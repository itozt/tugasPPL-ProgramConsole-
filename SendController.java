import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class SendController {

    public SendController() {
        // Tidak ada inisialisasi Repository lain di sini, karena semua logika DB ada di dalam kelas ini
    }

    // --- Metode Akses Data Internal untuk Partisipan ---
    private Participant findParticipantByUserId(int userId) throws SQLException {
        // Pastikan SELECT statement mengambil SEMUA kolom yang dibutuhkan konstruktor
        String sql = "SELECT participant_id, user_id, company_id, name, department, job_position, email_address, mobile_phone, date_of_last_measurement, last_fatique_level FROM participants WHERE user_id = ?";
        Participant participant = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Gunakan konstruktor yang lengkap (10 argumen)
                // Pastikan Anda membaca semua kolom yang sesuai
                participant = new Participant(
                    rs.getInt("participant_id"),
                    rs.getInt("user_id"),
                    rs.getInt("company_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("job_position"),
                    rs.getString("email_address"),
                    rs.getString("mobile_phone"),
                    rs.getTimestamp("date_of_last_measurement"),
                    // Mengambil nilai last_fatique_level dan menangani kasus null
                    rs.getObject("last_fatique_level") != null ? rs.getInt("last_fatique_level") : null
                );
            }
        }
        return participant;
    }

    // --- Metode Akses Data Internal untuk Kriteria Kinerja ---
    private PerformanceCriteria findPerformanceCriteriaById(int criterionId) throws SQLException {
        String sql = "SELECT criterion_id, name, description, max_score, is_active FROM performance_criteria WHERE criterion_id = ?";
        PerformanceCriteria criteria = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, criterionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                criteria = new PerformanceCriteria(
                    rs.getInt("criterion_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("max_score"),
                    rs.getBoolean("is_active")
                );
            }
        }
        return criteria;
    }

    // --- Metode Akses Data Internal untuk Pengukuran (SAVE) ---
    private void saveMeasurement(Measurement measurement) throws SQLException {
        String sql = "INSERT INTO measurement_data (participant_id, criterion_id, measurement_datetime, job_position, severity_level, severity_score, measurement_location, measured_scores) VALUES (?, ?, ?, ?, ?, ?, POINT(?, ?), ?::REAL[])";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, measurement.getParticipantId());
            pstmt.setInt(2, measurement.getCriterionId());
            pstmt.setTimestamp(3, measurement.getMeasurementDatetime());
            pstmt.setString(4, measurement.getJobPosition());
            pstmt.setString(5, measurement.getSeverityLevel());
            pstmt.setInt(6, measurement.getSeverityScore());
            
            String[] locParts = measurement.getMeasurementLocation().split(",");
            if (locParts.length == 2) {
                pstmt.setDouble(7, Double.parseDouble(locParts[0].trim()));
                pstmt.setDouble(8, Double.parseDouble(locParts[1].trim()));
            } else {
                throw new SQLException("Format lokasi pengukuran salah. Harus 'X,Y'.");
            }
            
            String measuredScoresPgArray = "{" + Arrays.stream(measurement.getMeasuredScores())
                                                       .mapToObj(String::valueOf)
                                                       .reduce((a, b) -> a + "," + b)
                                                       .orElse("") + "}";
            pstmt.setString(9, measuredScoresPgArray);

            pstmt.executeUpdate();
            System.out.println("Data pengukuran berhasil disimpan ke database.");
        }
    }

    // --- Metode Handle Utama untuk Mengirim Pengukuran ---
    public void handleSendMeasurement(User participantUser, Scanner scanner) {
        System.out.println("\n--- Silakan Masukkan Data Pengukuran ---");

        try {
            // 1. Dapatkan participant_id dari user yang login
            Participant participant = findParticipantByUserId(participantUser.getUserId());
            if (participant == null) {
                System.out.println("Error: Data partisipan tidak ditemukan untuk user ini. Tidak dapat mengirim pengukuran.");
                return;
            }
            int participantId = participant.getParticipantId();
            String jobPosition = participant.getJobPosition(); // Ambil job_position dari data partisipan

            // 2. Minta input dari user dan validasi
            int criterionId = -1;
            PerformanceCriteria criteria = null;
            while (criteria == null) {
                System.out.print("Masukkan Criterion ID (1-5): ");
                try {
                    criterionId = Integer.parseInt(scanner.nextLine());
                    criteria = findPerformanceCriteriaById(criterionId); // Panggil metode internal
                    if (criteria == null) {
                        System.out.println("Criterion ID " + criterionId + " tidak ditemukan. Mohon masukkan ID yang valid.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input Criterion ID tidak valid. Mohon masukkan angka.");
                } catch (SQLException e) {
                    System.out.println("Error saat memeriksa Criterion ID: " + e.getMessage());
                    return;
                }
            }

            System.out.print("Masukkan Waktu Pengukuran (YYYY-MM-DD HH:MM:SS): ");
            String dateTimeStr = scanner.nextLine();
            Timestamp measurementDatetime;
            try {
                measurementDatetime = Timestamp.valueOf(LocalDateTime.parse(dateTimeStr.replace(" ", "T")));
            } catch (DateTimeParseException e) {
                System.out.println("Format waktu tidak valid. Gunakan YYYY-MM-DD HH:MM:SS. Pengiriman dibatalkan.");
                return;
            }

            System.out.print("Masukkan Tingkat Keparahan (Severity Level - misal: Rendah, Sedang, Tinggi): ");
            String severityLevel = scanner.nextLine();

            int severityScore = -1;
            while (true) {
                System.out.print("Masukkan Skor Keparahan (Severity Score - maks " + criteria.getMaxScore() + "): ");
                try {
                    severityScore = Integer.parseInt(scanner.nextLine());
                    if (severityScore < 0 || severityScore > criteria.getMaxScore()) {
                        System.out.println("Skor keparahan tidak valid. Harus antara 0 dan " + criteria.getMaxScore() + ".");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input Skor Keparahan tidak valid. Mohon masukkan angka.");
                }
            }
            
            System.out.print("Masukkan Lokasi Pengukuran (format X.Y, misal: 10.5,20.3): ");
            String measurementLocation = scanner.nextLine();
            if (!measurementLocation.matches("\\d+(\\.\\d+)?,\\d+(\\.\\d+)?")) {
                System.out.println("Format lokasi tidak valid. Gunakan X.Y,Y.X. Pengiriman dibatalkan.");
                return;
            }

            System.out.print("Masukkan Skor Pengukuran (Measured Scores, pisahkan dengan koma, misal: 1.2,3.4,5.6): ");
            String measuredScoresStr = scanner.nextLine();
            double[] measuredScores;
            try {
                measuredScores = Arrays.stream(measuredScoresStr.split(","))
                                        .map(String::trim)
                                        .mapToDouble(Double::parseDouble)
                                        .toArray();
            } catch (NumberFormatException e) {
                System.out.println("Format skor pengukuran tidak valid. Mohon masukkan angka dipisah koma. Pengiriman dibatalkan.");
                return;
            }

            // 3. Buat objek Measurement
            Measurement newMeasurement = new Measurement(
                participantId,
                criterionId,
                measurementDatetime,
                jobPosition,
                severityLevel,
                severityScore,
                measurementLocation,
                measuredScores
            );

            // 4. Simpan ke database (menggunakan metode internal SendController ini)
            saveMeasurement(newMeasurement);
            System.out.println("Data pengukuran berhasil dikirim!");

        } catch (SQLException e) {
            System.err.println("Gagal mengirim data pengukuran karena masalah database: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan tak terduga: " + e.getMessage());
        }
    }
}