// ReportController.java

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;




public class ReportController {

    private List<Measurement> measurementList; // Menyimpan daftar pengukuran yang sedang ditampilkan (hasil dari load)
    private List<Measurement> filteredMeasurementList; // Menyimpan daftar pengukuran setelah filter
    
    // Atribut filter untuk laporan utama
    private String currentReportFilterDepartment;
    private String currentReportFilterAttributeType; // e.g., "criterion_id", "job_position", "severity_level", "severity_score"
    private String currentReportFilterAttributeValue; // Nilai filter untuk atribut
    private Integer currentReportFilterParticipantId; // Digunakan untuk filter per participant di viewMeasurementsAndAnalysisForSpecificParticipant

    public ReportController() {
        this.measurementList = new ArrayList<>();
        this.filteredMeasurementList = new ArrayList<>();
        this.currentReportFilterDepartment = null;
        this.currentReportFilterAttributeType = null;
        this.currentReportFilterAttributeValue = null;
        this.currentReportFilterParticipantId = null;
    }

    // Metode utama untuk Use Case 'View Report List' (yang diakses Observer dari main dashboard)
    public void handleReportMenu(User observerUser, Scanner scanner) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
            System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        loadMeasurementsFromDatabase(null, observerCompanyId); // Muat semua laporan perusahaan ke measurementList
        this.filteredMeasurementList.clear();
        this.filteredMeasurementList.addAll(this.measurementList); 
        resetReportFilters(); // Pastikan filter direset di awal setiap masuk menu utama ini

        String choice;
        do {
            System.out.println("\n--- Laporan Kinerja Perusahaan: " + getCompanyNameById(observerCompanyId) + " ---");
            displayCurrentReportFilters(); // Tampilkan filter aktif sebelum daftar
            applyFiltersToMeasurementList(); // Terapkan filter ke list sebelum ditampilkan
            printMeasurementListDetailed(this.filteredMeasurementList); // Gunakan nama metode yang benar untuk tampilan detail

            System.out.println("\n--- Opsi Laporan Kinerja ---");
            System.out.println("1. Tampilkan Analisis Kinerja Perusahaan");     // Analisis Keseluruhan Perusahaan
            System.out.println("2. Atur Filter Laporan");
            System.out.println("3. View Measurement & Analysis (Partisipan Spesifik)"); // BARU: Tambahkan opsi ini
            System.out.println("4. Kembali ke Dashboard Observer"); // Opsi Kembali digeser ke 4
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAnalysisReportOverall(observerUser);
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
                    break;
                case "2":
                    filterReportsMenu(scanner, observerCompanyId);
                    break;
                case "3": // BARU: Case untuk View Measurement & Analysis Partisipan Spesifik
                    System.out.print("Masukkan ID Partisipan untuk melihat pengukuran dan analisisnya: ");
                    int participantIdToView;
                    try {
                        participantIdToView = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ID Partisipan tidak valid. Mohon masukkan angka.");
                        System.out.println("\nTekan ENTER untuk melanjutkan...");
                        scanner.nextLine();
                        break;
                    }
                    viewMeasurementsAndAnalysisForSpecificParticipant(participantIdToView, observerUser, scanner);
                    break;
                case "4": // Opsi Kembali
                    System.out.println("Kembali ke dashboard.");
                    resetReportFilters();
                    break;
                default:
                    System.out.println("Opsi tidak valid.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("4")); // Loop sampai user memilih "Kembali"
    }

    private void displayAllReportsForCompany(User observerUser) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
             System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
             return;
        }
        System.out.println("\n--- Laporan Kinerja untuk " + getCompanyNameById(observerCompanyId) + " ---");
        // Gunakan filteredMeasurementList untuk tampilan laporan yang difilter
        printMeasurementListDetailed(this.filteredMeasurementList); // Ganti dengan nama metode yang benar
    }

    private void printMeasurementListDetailed(List<Measurement> listToDisplay) {
        if (listToDisplay.isEmpty()) {
            System.out.println("Tidak ada data pengukuran yang ditemukan sesuai kriteria.");
            return;
        }

        // Header tabel dengan lebih banyak detail
        System.out.printf("%-5s %-15s %-15s %-20s %-15s %-15s %-10s %-15s %-15s%n",
            "ID", "Partisipan ID", "Kriteria ID", "Waktu Pengukuran", "Posisi Kerja", 
            "Tingkat Sev.", "Skor Sev.", "Lokasi", "Nilai Ukur");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Measurement m : listToDisplay) {
            String measuredScoresStr = Arrays.toString(m.getMeasuredScores());
            if (measuredScoresStr.length() > 15) {
                measuredScoresStr = measuredScoresStr.substring(0, 12) + "...";
            }
            String locationStr = m.getMeasurementLocation();
             if (locationStr != null && locationStr.length() > 15) {
                locationStr = locationStr.substring(0, 12) + "...";
            } else if (locationStr == null) {
                locationStr = "-";
            }


            System.out.printf("%-5d %-15d %-15d %-20s %-15s %-15s %-10d %-15s %-15s%n",
                m.getMeasurementId(),
                m.getParticipantId(),
                m.getCriterionId(),
                m.getMeasurementDatetime().toString().substring(0, 19),
                m.getJobPosition(),
                m.getSeverityLevel(),
                m.getSeverityScore(),
                locationStr,
                measuredScoresStr
            );
        }
    }
    
    // Metode utama baru yang dipanggil dari ParticipantController (opsi 5)
    // Ini akan menampilkan daftar semua pengukuran untuk perusahaan observer (tanpa filter participantId awal)
    // Kemudian memberikan opsi untuk Analisis Global atau Lihat Detail Pengukuran Partisipan.
    public void handleViewMeasurement(User observerUser, Scanner scanner) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
            System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
            System.out.println("\nTekan ENTER untuk kembali...");
            scanner.nextLine();
            return;
        }

        this.currentReportFilterParticipantId = null; // Reset filter partisipan sebelum menampilkan semua laporan perusahaan
        loadMeasurementsFromDatabase(null, observerCompanyId); // Muat semua laporan untuk perusahaan

        String choice;
        do {
            System.out.println("\n--- Daftar Semua Pengukuran di Perusahaan Anda ---");
            displayDetailedMeasurementList(this.measurementList); // Menampilkan daftar semua pengukuran
            // Note: filteredMeasurementList tidak digunakan di sini karena ini adalah tampilan awal semua data perusahaan

            System.out.println("\n--- Opsi Laporan Pengukuran ---");
            System.out.println("1. Tampilkan Analisis (Keseluruhan Perusahaan)");
            System.out.println("2. View Measurement & Analysis (Partisipan Spesifik)");
            System.out.println("3. Kembali");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAnalysisReportOverall(observerUser);
                    System.out.println("\nTekan ENTER untuk kembali...");
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.print("Masukkan ID Partisipan untuk melihat pengukuran dan analisisnya: ");
                    int participantIdToView;
                    try {
                        participantIdToView = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ID Partisipan tidak valid. Mohon masukkan angka.");
                        System.out.println("\nTekan ENTER untuk kembali...");
                        scanner.nextLine();
                        break;
                    }
                    viewMeasurementsAndAnalysisForSpecificParticipant(participantIdToView, observerUser, scanner);
                    break;
                case "3":
                    System.out.println("Kembali.");
                    break;
                default:
                    System.out.println("Opsi tidak valid.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("3"));
    }

    // Metode internal untuk memuat data pengukuran dari database
    private void loadMeasurementsFromDatabase(Integer participantIdFilter, int companyIdFilter) {
        measurementList.clear(); // Selalu kosongkan measurementList utama
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT md.measurement_id, md.participant_id, md.criterion_id, md.measurement_datetime, " +
            "md.job_position, md.severity_level, md.severity_score, md.measurement_location, md.measured_scores " +
            "FROM measurement_data md " +
            "JOIN participants p ON md.participant_id = p.participant_id " +
            "WHERE p.company_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(companyIdFilter);

        if (participantIdFilter != null) {
            sqlBuilder.append("AND md.participant_id = ? ");
            params.add(participantIdFilter);
        }
        
        sqlBuilder.append("ORDER BY md.measurement_datetime DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] pgArray = (Object[]) rs.getArray("measured_scores").getArray();
                    double[] measuredScores = new double[pgArray.length];
                    for (int i = 0; i < pgArray.length; i++) {
                        measuredScores[i] = ((Number) pgArray[i]).doubleValue();
                    }

                    Measurement measurement = new Measurement(
                        rs.getInt("measurement_id"),
                        rs.getInt("participant_id"),
                        rs.getInt("criterion_id"),
                        rs.getTimestamp("measurement_datetime"),
                        rs.getString("job_position"),
                        rs.getString("severity_level"),
                        rs.getInt("severity_score"),
                        rs.getString("measurement_location"),
                        measuredScores
                    );
                    measurementList.add(measurement);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data pengukuran dari database: " + e.getMessage());
        }
    }

    // Metode BARU: displayDetailedMeasurementList (untuk mencetak semua atribut)
    private void displayDetailedMeasurementList(List<Measurement> listToDisplay) {
        if (listToDisplay.isEmpty()) {
            System.out.println("Tidak ada data pengukuran yang ditemukan sesuai kriteria.");
            return;
        }

        // Header tabel dengan lebih banyak detail
        System.out.printf("%-5s %-15s %-15s %-20s %-15s %-15s %-10s %-15s %-15s%n",
            "ID", "Partisipan ID", "Kriteria ID", "Waktu Pengukuran", "Posisi Kerja", 
            "Tingkat Sev.", "Skor Sev.", "Lokasi", "Nilai Ukur");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Measurement m : listToDisplay) {
            String measuredScoresStr = Arrays.toString(m.getMeasuredScores());
            // Memastikan string tidak terlalu panjang untuk tampilan
            if (measuredScoresStr.length() > 15) {
                measuredScoresStr = measuredScoresStr.substring(0, 12) + "...";
            }
            String locationStr = m.getMeasurementLocation();
             if (locationStr != null && locationStr.length() > 15) {
                locationStr = locationStr.substring(0, 12) + "...";
            } else if (locationStr == null) {
                locationStr = "-";
            }


            System.out.printf("%-5d %-15d %-15d %-20s %-15s %-15s %-10d %-15s %-15s%n",
                m.getMeasurementId(),
                m.getParticipantId(),
                m.getCriterionId(),
                m.getMeasurementDatetime().toString().substring(0, 19),
                m.getJobPosition(),
                m.getSeverityLevel(),
                m.getSeverityScore(),
                locationStr,
                measuredScoresStr
            );
        }
    }

    // Metode BARU: filterReportsMenu
    private void filterReportsMenu(Scanner scanner, int observerCompanyId) {
        String filterChoice;
        do {
            System.out.println("\n--- Atur Filter Laporan ---");
            displayCurrentReportFilters();
            System.out.println("1. Filter berdasarkan Departemen");
            System.out.println("2. Filter berdasarkan Atribut");
            System.out.println("3. Reset Semua Filter");
            System.out.println("4. Selesai Filter"); // Akan kembali ke handleReportMenu dan tampilkan laporan
            System.out.println("5. Kembali ke Dashboard Laporan (Reset Filter)"); // Kembali tanpa menerapkan filter
            System.out.print("Pilih opsi: ");
            filterChoice = scanner.nextLine();

            switch (filterChoice) {
                case "1":
                    applyDepartmentFilter(scanner, observerCompanyId);
                    break;
                case "2":
                    applyAttributeFilter(scanner, observerCompanyId);
                    break;
                case "3":
                    resetReportFilters();
                    System.out.println("Semua filter laporan telah direset.");
                    break;
                case "4":
                    System.out.println("Menerapkan filter dan menampilkan laporan...");
                    break; // Keluar dari loop filterReportsMenu
                case "5":
                    System.out.println("Kembali ke Dashboard Laporan tanpa menerapkan filter.");
                    resetReportFilters(); // Reset filter jika user memilih kembali tanpa menerapkan
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
            }
            if (!filterChoice.equals("4") && !filterChoice.equals("5")) {
                 System.out.println("\nTekan ENTER untuk melanjutkan...");
                 scanner.nextLine();
            }
        } while (!filterChoice.equals("4") && !filterChoice.equals("5"));
        // --- HAPUS PANGGILAN INI DARI SINI ---
        // applyFiltersToMeasurementList();
        // --- AKHIR HAPUS ---
    }
    
    // Metode BARU: applyDepartmentFilter
    private void applyDepartmentFilter(Scanner scanner, int observerCompanyId) {
        System.out.println("\n--- Filter Laporan berdasarkan Departemen ---");
        Set<String> uniqueDepartments = getUniqueDepartmentsInCompany(observerCompanyId);
        if (uniqueDepartments.isEmpty()) {
            System.out.println("Tidak ada departemen yang ditemukan di perusahaan ini.");
            return;
        }
        System.out.println("Departemen yang tersedia: " + uniqueDepartments);
        System.out.print("Masukkan Departemen (kosongkan untuk semua): ");
        String deptInput = scanner.nextLine().trim();
        currentReportFilterDepartment = deptInput.isEmpty() ? null : deptInput;
        System.out.println("Filter Departemen diterapkan: " + (currentReportFilterDepartment == null ? "Semua" : currentReportFilterDepartment));
    }

    // Metode BARU: applyAttributeFilter
    private void applyAttributeFilter(Scanner scanner, int observerCompanyId) {
        System.out.println("\n--- Filter Laporan berdasarkan Atribut ---");
        displayCurrentReportFilters(); // Tampilkan filter aktif sebelum pilihan
        System.out.println("Pilih atribut untuk filter:");
        System.out.println("1. Criterion ID (Saat ini: " + (currentReportFilterAttributeType != null && currentReportFilterAttributeType.equals("criterion_id") ? currentReportFilterAttributeValue : "Semua") + ")");
        System.out.println("2. Job Position (Saat ini: " + (currentReportFilterAttributeType != null && currentReportFilterAttributeType.equals("job_position") ? currentReportFilterAttributeValue : "Semua") + ")");
        System.out.println("3. Severity Level (Saat ini: " + (currentReportFilterAttributeType != null && currentReportFilterAttributeType.equals("severity_level") ? currentReportFilterAttributeValue : "Semua") + ")");
        System.out.println("4. Severity Score (Saat ini: " + (currentReportFilterAttributeType != null && currentReportFilterAttributeType.equals("severity_score") ? currentReportFilterAttributeValue : "Semua") + ")");
        System.out.println("5. Hapus Filter Atribut ini");
        System.out.print("Pilih opsi atribut: ");
        String attrChoice = scanner.nextLine();

        String attrType = null;
        String attrValue = null;

        switch (attrChoice) {
            case "1":
                attrType = "criterion_id";
                System.out.print("Masukkan Criterion ID (angka): ");
                attrValue = scanner.nextLine().trim();
                break;
            case "2":
                attrType = "job_position";
                System.out.print("Masukkan Posisi Kerja: ");
                attrValue = scanner.nextLine().trim();
                break;
            case "3":
                attrType = "severity_level";
                System.out.print("Masukkan Severity Level (misal: Rendah, Sedang, Tinggi): ");
                attrValue = scanner.nextLine().trim();
                break;
            case "4":
                attrType = "severity_score";
                System.out.print("Masukkan Severity Score (angka): ");
                attrValue = scanner.nextLine().trim();
                break;
            case "5":
                resetAttributeFilter();
                System.out.println("Filter atribut dihapus.");
                return;
            default:
                System.out.println("Opsi tidak valid. Filter atribut tidak diterapkan.");
                return;
        }

        if (attrValue.isEmpty()) {
            resetAttributeFilter();
            System.out.println("Nilai filter kosong. Filter atribut dihapus.");
        } else {
            currentReportFilterAttributeType = attrType;
            currentReportFilterAttributeValue = attrValue;
            System.out.println("Filter atribut diterapkan: " + attrType + " = " + attrValue);
        }
    }

    // Metode BARU: resetReportFilters
    private void resetReportFilters() {
        this.currentReportFilterDepartment = null;
        this.currentReportFilterAttributeType = null;
        this.currentReportFilterAttributeValue = null;
    }

    // Metode BARU: resetAttributeFilter
    private void resetAttributeFilter() {
        this.currentReportFilterAttributeType = null;
        this.currentReportFilterAttributeValue = null;
    }

    // Metode BARU: displayCurrentReportFilters
    private void displayCurrentReportFilters() {
        System.out.println("Filter Laporan Aktif:");
        System.out.println("  Departemen     : " + (currentReportFilterDepartment == null ? "Semua" : currentReportFilterDepartment));
        String attrFilterStatus = "Tidak Ada";
        if (currentReportFilterAttributeType != null && currentReportFilterAttributeValue != null) {
            attrFilterStatus = currentReportFilterAttributeType + " = " + currentReportFilterAttributeValue;
        }
        System.out.println("  Filter Atribut : " + attrFilterStatus);
    }

    // Metode BARU: applyFiltersToMeasurementList (menerapkan filter ke filteredMeasurementList)
    private void applyFiltersToMeasurementList() {
        filteredMeasurementList.clear();
        filteredMeasurementList.addAll(measurementList.stream()
            // Filter by Department
            .filter(m -> currentReportFilterDepartment == null || 
                         (getParticipantDepartmentById(m.getParticipantId()) != null && 
                          getParticipantDepartmentById(m.getParticipantId()).equalsIgnoreCase(currentReportFilterDepartment)))
            // Filter by Attribute (Criterion ID, Job Position, Severity Level, Severity Score)
            .filter(m -> {
                if (currentReportFilterAttributeType == null) return true; // Tidak ada filter atribut

                String attrType = currentReportFilterAttributeType;
                String attrValue = currentReportFilterAttributeValue;

                if (attrType.equals("criterion_id")) {
                    try { return m.getCriterionId() == Integer.parseInt(attrValue); } catch (NumberFormatException e) { return false; }
                } else if (attrType.equals("job_position")) {
                    return m.getJobPosition() != null && m.getJobPosition().toLowerCase().contains(attrValue.toLowerCase());
                } else if (attrType.equals("severity_level")) {
                    return m.getSeverityLevel() != null && m.getSeverityLevel().equalsIgnoreCase(attrValue);
                } else if (attrType.equals("severity_score")) {
                    try { return m.getSeverityScore() == Integer.parseInt(attrValue); } catch (NumberFormatException e) { return false; }
                }
                return true; // Tidak ada filter yang cocok atau tipe tidak valid
            })
            .collect(Collectors.toList()));
    }

    // Metode BARU: getUniqueDepartmentsInCompany (mengambil daftar departemen unik dari DB)
    private Set<String> getUniqueDepartmentsInCompany(int companyId) {
        Set<String> departments = new TreeSet<>();
        String sql = "SELECT DISTINCT department FROM participants WHERE company_id = ? AND department IS NOT NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil daftar departemen: " + e.getMessage());
        }
        return departments;
    }

    // Metode BARU: getParticipantDepartmentById (untuk mendapatkan departemen partisipan)
    private String getParticipantDepartmentById(int participantId) {
        String department = null;
        String sql = "SELECT department FROM participants WHERE participant_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, participantId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                department = rs.getString("department");
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil departemen partisipan: " + e.getMessage());
        }
        return department;
    }

    // Metode internal untuk mencetak daftar pengukuran ke konsol
    private void printMeasurementList() {
        if (measurementList.isEmpty()) {
            System.out.println("Tidak ada data pengukuran yang ditemukan sesuai kriteria.");
            return;
        }

        System.out.printf("%-5s %-15s %-15s %-20s %-10s %-10s%n",
            "ID", "Partisipan ID", "Kriteria ID", "Waktu Pengukuran", "Skor Sev.", "Nilai Ukur");
        System.out.println("-------------------------------------------------------------------------------");

        for (Measurement m : measurementList) {
            String measuredScoresStr = Arrays.toString(m.getMeasuredScores());
            if (measuredScoresStr.length() > 20) {
                measuredScoresStr = measuredScoresStr.substring(0, 17) + "...";
            }

            System.out.printf("%-5d %-15s %-15d %-20s %-10d %-10s%n",
                m.getMeasurementId(),
                m.getParticipantId(),
                m.getCriterionId(),
                m.getMeasurementDatetime().toString().substring(0, 19),
                m.getSeverityScore(),
                measuredScoresStr
            );
        }
    }

    // Metode BARU: viewMeasurementsAndAnalysisForSpecificParticipant
    public void viewMeasurementsAndAnalysisForSpecificParticipant(int participantIdToView, User observerUser, Scanner scanner) { // Parameter participantIdToView ditambahkan di sini
        System.out.println("\n--- View Measurement & Analysis Partisipan ---");
        
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
            System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        // Muat pengukuran hanya untuk partisipan spesifik ini (juga filter companyId)
        loadMeasurementsFromDatabase(participantIdToView, observerCompanyId); // Gunakan participantIdToView yang baru diterima

        if (measurementList.isEmpty()) {
            System.out.println("Tidak ada data pengukuran ditemukan untuk partisipan ID " + participantIdToView + " di perusahaan Anda.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        // Tampilkan daftar pengukuran untuk partisipan ini
        System.out.println("\n--- Daftar Pengukuran untuk Partisipan ID: " + participantIdToView + " ---");
        printMeasurementList();

        // Langsung tampilkan analisis untuk partisipan ini
        System.out.println("\n--- Analisis Kinerja Partisipan ID: " + participantIdToView + " ---");
        performAnalysisOnCurrentMeasurementList(); // Lakukan analisis pada measurementList yang sudah dimuat

        System.out.println("\nTekan ENTER untuk kembali ke menu sebelumnya...");
        scanner.nextLine();
    }
    
    // Metode BARU: performAnalysisOnCurrentMeasurementList (untuk analisis dari list yang sudah dimuat)
    private void performAnalysisOnCurrentMeasurementList() {
        if (measurementList.isEmpty()) {
            System.out.println("Tidak ada data pengukuran untuk dianalisis di list saat ini.");
            return;
        }

        Map<Integer, Double> criterionTotalScore = new HashMap<>();
        Map<Integer, Integer> criterionCount = new HashMap<>();
        Map<Integer, String> criterionNames = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT criterion_id, name FROM performance_criteria")) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                criterionNames.put(rs.getInt("criterion_id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil nama kriteria untuk analisis: " + e.getMessage());
            return;
        }

        for (Measurement m : measurementList) {
            int criterionId = m.getCriterionId();
            int severityScore = m.getSeverityScore();

            criterionTotalScore.put(criterionId, criterionTotalScore.getOrDefault(criterionId, 0.0) + severityScore);
            criterionCount.put(criterionId, criterionCount.getOrDefault(criterionId, 0) + 1);
        }

        System.out.println("\n--- Rata-rata Skor per Kriteria ---");
        List<Integer> sortedCriterionIds = criterionNames.keySet().stream().sorted().collect(Collectors.toList());

        for (int criterionId : sortedCriterionIds) {
            String criterionName = criterionNames.getOrDefault(criterionId, "Kriteria Tidak Dikenal");
            double totalScore = criterionTotalScore.getOrDefault(criterionId, 0.0);
            int count = criterionCount.getOrDefault(criterionId, 0);

            if (count > 0) {
                double averageScore = totalScore / count;
                System.out.printf("Rata-rata skor dari kriteria %d (%s) = %.2f%n", criterionId, criterionName, averageScore);
            } else {
                System.out.printf("Kriteria %d (%s) belum memiliki data pengukuran yang relevan.%n", criterionId, criterionName);
            }
        }

        System.out.println("\n--- Kesimpulan ---");
        System.out.println("Analisis rata-rata skor kinerja menunjukkan area kekuatan dan area yang perlu ditingkatkan.");
    }


    // Metode BARU: showAnalysisReportOverall (Analisis untuk Seluruh Perusahaan)
    public void showAnalysisReportOverall(User observerUser) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
             System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
             return;
        }
        System.out.println("\n--- Analisis Kinerja Perusahaan: " + getCompanyNameById(observerCompanyId) + " ---");

        // Muat semua pengukuran untuk perusahaan ini (tanpa filter partisipan spesifik)
        loadMeasurementsFromDatabase(null, observerCompanyId); 

        performAnalysisOnCurrentMeasurementList(); // Lakukan analisis pada list yang sudah dimuat (sekarang ini untuk seluruh perusahaan)
    }


    // Metode untuk mendapatkan company_id dari Observer
    private int getCompanyIdForObserver(User observerUser) {
        String sql = "SELECT company_id FROM observers WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, observerUser.getUserId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("company_id");
            }
        } catch (SQLException e) {
            System.err.println("Error: Gagal mendapatkan company_id untuk observer: " + e.getMessage());
        }
        return -1;
    }

    // Metode helper untuk mendapatkan nama perusahaan
    private String getCompanyNameById(int companyId) {
        String companyName = "Tidak Diketahui";
        String sql = "SELECT name FROM companies WHERE company_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                companyName = rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil nama perusahaan (ID: " + companyId + "): " + e.getMessage());
        }
        return companyName;
    }
}