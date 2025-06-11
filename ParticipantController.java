// ParticipantController.java

// HAPUS SEMUA BARIS PACKAGE DAN IMPORT UNTUK CLASS LOKAL ANDA
// (Contoh: import com.pvt.system.report.ReportController; HAPUS INI)
// (Contoh: import com.pvt.system.DatabaseUtil; HAPUS INI)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

// Catatan: Jika semua file .java berada di folder yang sama dan tidak menggunakan 'package ...'
// maka Anda TIDAK memerlukan import untuk kelas-kelas seperti DatabaseUtil, User, Participant, ReportController, dll.
// Mereka secara otomatis terlihat.

public class ParticipantController {

    private List<Participant> participantList;
    private ReportController reportController; // Referensi ke ReportController
    
    // Atribut filter untuk pencarian partisipan
    private String currentSearchKeyword;
    private String currentFilterDepartment;
    private String currentFilterJobPosition;

    public ParticipantController(ReportController reportController) {
        this.participantList = new ArrayList<>();
        this.reportController = reportController;
        this.currentSearchKeyword = "";
        this.currentFilterDepartment = null;
        this.currentFilterJobPosition = null;
    }

    // Metode utama untuk menangani menu Partisipan
    public void handleParticipantMenu(User observerUser, Scanner scanner) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
            System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        String choice;
        do {
            boolean dataFound = loadParticipantsFromDatabase(observerCompanyId);

            if (!dataFound) {
                System.out.println("Tidak ada data partisipan yang ditemukan untuk perusahaan Anda.");
                System.out.println("\nTekan ENTER untuk kembali ke Dashboard Observer...");
                scanner.nextLine();
                return;
            }
            
            displayParticipantListConcise();

            System.out.println("\n--- Opsi Partisipan ---");
            System.out.println("1. View Participant Detail");
            System.out.println("2. Search Participants");
            System.out.println("3. Add New Participant");
            System.out.println("4. Edit Participant Data");
            System.out.println("5. View Participant Measurement");
            System.out.println("6. Kembali ke Dashboard Observer");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewParticipantDetail(observerUser, scanner);
                    break;
                case "2":
                    searchParticipantsMenu(observerUser, scanner);
                    break;
                case "3":
                    addNewParticipant(observerUser, scanner);
                    break;
                case "4":
                    System.out.print("Masukkan ID Partisipan yang ingin diedit: ");
                    int participantIdToEdit;
                    try {
                        participantIdToEdit = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("ID Partisipan tidak valid. Mohon masukkan angka.");
                        System.out.println("\nTekan ENTER untuk melanjutkan...");
                        scanner.nextLine();
                        break;
                    }
                    
                    Optional<Participant> pToEdit = participantList.stream()
                                                                .filter(p -> p.getParticipantId() == participantIdToEdit)
                                                                .findFirst();
                    
                    if (pToEdit.isPresent()) {
                        editParticipantData(pToEdit.get(), observerUser, scanner);
                    } else {
                        System.out.println("Partisipan dengan ID " + participantIdToEdit + " tidak ditemukan.");
                        System.out.println("\nTekan ENTER untuk melanjutkan...");
                        scanner.nextLine();
                    }
                    break;
                case "5":
                    viewParticipantMeasurement(observerUser, scanner);
                    break;
                case "6":
                    System.out.println("Kembali ke dashboard Observer.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("6"));
    }

    // Metode internal untuk memuat data partisipan dari database (difilter oleh company_id)
    private boolean loadParticipantsFromDatabase(int companyIdFilter) {
        participantList.clear();
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT participant_id, user_id, company_id, name, department, job_position, email_address, mobile_phone, date_of_last_measurement, last_fatique_level " +
            "FROM participants WHERE company_id = ? ORDER BY name"
        );
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            pstmt.setInt(1, companyIdFilter);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Handle null untuk last_fatique_level karena INT bisa null di DB tapi Integer di Java
                    Integer lastFatiqueLevel = rs.getObject("last_fatique_level") != null ? rs.getInt("last_fatique_level") : null;

                    Participant participant = new Participant(
                        rs.getInt("participant_id"),
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("job_position"),
                        rs.getString("email_address"),
                        rs.getString("mobile_phone"),
                        rs.getTimestamp("date_of_last_measurement"),
                        lastFatiqueLevel
                    );
                    participantList.add(participant);
                }
            }
            return !participantList.isEmpty(); // Mengembalikan true jika ada data
        } catch (SQLException e) {
            System.err.println("Gagal memuat data partisipan dari database: " + e.getMessage());
            return false;
        }
    }

    // Metode untuk menampilkan daftar partisipan dalam format ringkas (Logika sama)
    private void displayParticipantListConcise() {
        System.out.println("\n--- Daftar Partisipan ---");
        if (participantList.isEmpty()) {
            System.out.println("Tidak ada partisipan yang ditemukan untuk perusahaan ini.");
            return;
        }

        System.out.printf("%-5s %-25s %-20s %-20s%n", "ID", "Nama Partisipan", "Departemen", "Posisi Kerja");
        System.out.println("------------------------------------------------------------------------");
        for (Participant p : participantList) {
            System.out.printf("%-5d %-25s %-20s %-20s%n",
                p.getParticipantId(),
                p.getName(),
                p.getDepartment(),
                p.getJobPosition()
            );
        }
    }

    // --- Metode: View Participant Detail ---
    private void viewParticipantDetail(User observerUser, Scanner scanner) {
        System.out.println("\n--- View Participant Detail ---");
        System.out.print("Masukkan ID Partisipan yang ingin dilihat detailnya: ");
        int participantIdToView;
        try {
            participantIdToView = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID Partisipan tidak valid. Mohon masukkan angka.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        Optional<Participant> foundParticipant = participantList.stream()
                                                            .filter(p -> p.getParticipantId() == participantIdToView)
                                                            .findFirst();

        if (foundParticipant.isPresent()) {
            Participant p = foundParticipant.get();
            // Validasi Keamanan: Pastikan partisipan ada di perusahaan observer
            int observerCompanyId = getCompanyIdForObserver(observerUser);
            if (observerCompanyId == -1 || p.getCompanyId() != observerCompanyId) {
                System.out.println("Akses ditolak: Partisipan tidak ditemukan atau bukan dari perusahaan Anda.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
                return;
            }

            System.out.println("\n--- Detail Partisipan ---");
            System.out.println("ID Partisipan     : " + p.getParticipantId());
            System.out.println("Nama              : " + p.getName());
            System.out.println("Departemen        : " + p.getDepartment());
            System.out.println("Posisi Kerja      : " + p.getJobPosition());
            System.out.println("Email             : " + p.getEmailAddress());
            System.out.println("Nomor Telepon     : " + p.getMobilePhone());
            System.out.println("Pengukuran Terakhir: " + (p.getDateOfLastMeasurement() != null ? p.getDateOfLastMeasurement() : "-"));
            System.out.println("Tingkat Kelelahan Terakhir: " + (p.getLastFatiqueLevel() != null ? p.getLastFatiqueLevel() : "-"));
            System.out.println("ID User           : " + p.getUserId());
            System.out.println("ID Perusahaan     : " + p.getCompanyId());
            
            System.out.println("\n--- Opsi Detail Partisipan ---");
            System.out.println("1. View Measurement History");
            System.out.println("2. Edit Data (Belum Diimplementasikan)");
            System.out.println("3. Kembali");
            System.out.print("Pilih opsi: ");
            String detailChoice = scanner.nextLine();

            if (detailChoice.equals("1")) {
                // Panggil ReportController untuk menampilkan pengukuran partisipan ini
                // Perbaikan: Panggil metode yang tepat di ReportController
                reportController.viewMeasurementsAndAnalysisForSpecificParticipant(p.getParticipantId(), observerUser, scanner);

            } else if (detailChoice.equals("2")) {
                System.out.println("Fitur 'Edit Data Partisipan' belum diimplementasikan.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
            } else if (detailChoice.equals("3")) {
                // Kembali
            } else {
                System.out.println("Opsi tidak valid.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
            }

        } else {
            System.out.println("Partisipan dengan ID " + participantIdToView + " tidak ditemukan dalam daftar.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        }
    }

    // --- Metode: Search Participants ---
    private void searchParticipantsMenu(User observerUser, Scanner scanner) {
        String searchChoice;
        do {
            int observerCompanyId = getCompanyIdForObserver(observerUser);
            if (observerCompanyId == -1) {
                System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
                return; // Keluar jika ada masalah observer
            }
            // Pastikan participantList di-refresh setiap kali masuk menu pencarian
            loadParticipantsFromDatabase(observerCompanyId);
            
            System.out.println("\n--- Search Participants ---");
            displayCurrentFilters();
            
            System.out.print("Masukkan Keyword Nama (kosongkan untuk tidak mencari): ");
            currentSearchKeyword = scanner.nextLine().trim();

            displaySearchResults();

            System.out.println("\n--- Opsi Pencarian Lanjutan ---");
            System.out.println("1. Terapkan/Ubah Filter Departemen/Posisi"); 
            System.out.println("2. Ubah Keyword Lagi");
            System.out.println("3. Reset Filter & Keyword");
            System.out.println("4. Kembali ke Menu Partisipan");
            System.out.print("Pilih opsi: ");
            searchChoice = scanner.nextLine();

            switch (searchChoice) {
                case "1":
                    applyFilterMenu(scanner);
                    break;
                case "2":
                    break;
                case "3":
                    resetFilters();
                    System.out.println("Filter dan keyword telah direset.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
                    break;
                case "4":
                    System.out.println("Kembali ke menu Partisipan.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!searchChoice.equals("4"));
    }

    // --- Metode: applyFilterMenu ---
    private void applyFilterMenu(Scanner scanner) {
        String filterChoice;
        do {
            System.out.println("\n--- Terapkan/Ubah Filter Partisipan ---");
            displayCurrentFilters();
            System.out.println("1. Filter by Department (Saat ini: " + (currentFilterDepartment == null ? "Semua" : currentFilterDepartment) + ")");
            System.out.println("2. Filter by Job Position (Saat ini: " + (currentFilterJobPosition == null ? "Semua" : currentFilterJobPosition) + ")");
            System.out.println("3. Hapus Semua Filter");
            System.out.println("4. Selesai Filter");
            System.out.print("Pilih atribut filter: ");
            filterChoice = scanner.nextLine();

            switch (filterChoice) {
                case "1":
                    System.out.print("Masukkan Departemen (kosongkan untuk semua): ");
                    String deptInput = scanner.nextLine().trim();
                    currentFilterDepartment = deptInput.isEmpty() ? null : deptInput;
                    break;
                case "2":
                    System.out.print("Masukkan Posisi Kerja (kosongkan untuk semua): ");
                    String jobPosInput = scanner.nextLine().trim();
                    currentFilterJobPosition = jobPosInput.isEmpty() ? null : jobPosInput;
                    break;
                case "3":
                    resetFilters();
                    System.out.println("Semua filter telah dihapus.");
                    filterChoice = "4"; // Otomatis keluar dari menu filter
                    break;
                case "4":
                    System.out.println("Selesai menerapkan filter.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
            }
            if (!filterChoice.equals("4")) {
                 System.out.println("\nTekan ENTER untuk melanjutkan...");
                 scanner.nextLine();
            }
        } while (!filterChoice.equals("4"));
    }

    // Metode: displaySearchResults
    private void displaySearchResults() {
        System.out.println("\n--- Hasil Pencarian Partisipan ---");

        if (participantList.isEmpty()) {
            System.out.println("Tidak ada data partisipan di memori untuk dicari.");
            return;
        }

        List<Participant> filteredAndSearchedParticipants = participantList.stream()
            .filter(p -> currentSearchKeyword.isEmpty() || p.getName().toLowerCase().startsWith(currentSearchKeyword.toLowerCase()))
            .filter(p -> currentFilterDepartment == null || (p.getDepartment() != null && p.getDepartment().equalsIgnoreCase(currentFilterDepartment)))
            .filter(p -> currentFilterJobPosition == null || (p.getJobPosition() != null && p.getJobPosition().toLowerCase().contains(currentFilterJobPosition.toLowerCase())))
            .collect(Collectors.toList());

        if (filteredAndSearchedParticipants.isEmpty()) {
            System.out.println("Tidak ada partisipan yang cocok dengan kriteria pencarian/filter saat ini.");
        } else {
            System.out.printf("%-5s %-25s %-20s %-20s%n", "ID", "Nama Partisipan", "Departemen", "Posisi Kerja");
            System.out.println("------------------------------------------------------------------------");
            for (Participant p : filteredAndSearchedParticipants) {
                System.out.printf("%-5d %-25s %-20s %-20s%n",
                    p.getParticipantId(),
                    p.getName(),
                    p.getDepartment(),
                    p.getJobPosition()
                );
            }
        }
    }

    // --- Metode: resetFilters ---
    private void resetFilters() {
        this.currentSearchKeyword = "";
        this.currentFilterDepartment = null;
        this.currentFilterJobPosition = null;
    }
    
    // --- Metode: displayCurrentFilters ---
    private void displayCurrentFilters() {
        System.out.println("Filter Aktif:");
        System.out.println("  Keyword Nama   : " + (currentSearchKeyword.isEmpty() ? "Tidak Ada" : currentSearchKeyword));
        System.out.println("  Departemen     : " + (currentFilterDepartment == null ? "Semua" : currentFilterDepartment));
        System.out.println("  Posisi Kerja   : " + (currentFilterJobPosition == null ? "Semua" : currentFilterJobPosition));
    }

    // Metode untuk mendapatkan company_id dari Observer (diperlukan karena ObserverUser adalah tipe User)
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
        return -1; // Mengembalikan -1 jika tidak ditemukan atau error
    }

    // --- Metode untuk mendapatkan nama perusahaan berdasarkan ID (Disalin dari ObserverController/ReportController) ---
    private String getCompanyNameById(int companyId) {
        if (companyId == 0) { // Menangani kasus filter "Semua" jika ada
            return "Semua";
        }
        String companyName = "Tidak Diketahui"; // Default value
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

    // --- Metode: viewParticipantMeasurement ---
    private void viewParticipantMeasurement(User observerUser, Scanner scanner) {
        reportController.handleViewMeasurement(observerUser, scanner);
    }

    // --- Metode: addNewParticipant ---
    public void addNewParticipant(User observerUser, Scanner scanner) {
        int observerCompanyId = getCompanyIdForObserver(observerUser);
        if (observerCompanyId == -1) {
            System.out.println("Error: Data observer tidak ditemukan untuk pengguna ini. Tidak dapat menambah partisipan.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        System.out.println("\n--- Tambah Partisipan Baru ---");
        System.out.println("Untuk Perusahaan ID: " + observerCompanyId + " (" + getCompanyNameById(observerCompanyId) + ")");

        String username;
        while (true) {
            System.out.print("Masukkan Username baru untuk Partisipan: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username tidak boleh kosong. Pembatalan penambahan.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
                return;
            }

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username '" + username + "' sudah ada. Mohon gunakan username lain.");
                } else {
                    break;
                }
            } catch (SQLException e) {
                System.err.println("Error saat memeriksa username: " + e.getMessage());
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
                return;
            }
        }

        System.out.print("Masukkan Password baru untuk Partisipan: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password tidak boleh kosong. Pembatalan penambahan.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        String name;
        String department;
        String jobPosition;
        String emailAddress;
        String mobilePhone;

        while (true) {
            System.out.print("Nama Partisipan: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Nama partisipan tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Departemen: ");
            department = scanner.nextLine().trim();
            if (department.isEmpty()) { System.out.println("Departemen tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Posisi Kerja: ");
            jobPosition = scanner.nextLine().trim();
            if (jobPosition.isEmpty()) { System.out.println("Posisi Kerja tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Email: ");
            emailAddress = scanner.nextLine().trim();
            if (emailAddress.isEmpty()) { System.out.println("Email tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Nomor Telepon: ");
            mobilePhone = scanner.nextLine().trim();
            if (mobilePhone.isEmpty()) { System.out.println("Nomor Telepon tidak boleh kosong. Mohon isi."); continue; }
            
            break;
        }

        System.out.println("\nMembuat Progress Bar...");
        System.out.println("Menambahkan data ke database...");

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'participant') RETURNING user_id";
            int newUserId = -1;
            try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                userPstmt.setString(1, username);
                userPstmt.setString(2, password);
                ResultSet rs = userPstmt.executeQuery();

                if (rs.next()) {
                    newUserId = rs.getInt("user_id");
                } else {
                    throw new SQLException("Gagal mendapatkan user_id yang baru dibuat.");
                }
            }

            String participantSql = "INSERT INTO participants (user_id, company_id, name, department, job_position, email_address, mobile_phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement partPstmt = conn.prepareStatement(participantSql)) {
                partPstmt.setInt(1, newUserId);
                partPstmt.setInt(2, observerCompanyId);
                partPstmt.setString(3, name);
                partPstmt.setString(4, department);
                partPstmt.setString(5, jobPosition);
                partPstmt.setString(6, emailAddress);
                partPstmt.setString(7, mobilePhone);

                partPstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Partisipan baru dengan username '" + username + "' berhasil ditambahkan.");

            System.out.println("\n[MessageBox] Partisipan dengan username '" + username + "' berhasil dibentuk.");
            System.out.print("Tekan ENTER untuk OK...");
            scanner.nextLine();

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan partisipan baru karena masalah database: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException rollbackEx) { System.err.println("Error saat rollback: " + rollbackEx.getMessage()); }
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan tak terduga: " + e.getMessage());
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                System.err.println("Error mengembalikan auto-commit: " + autoCommitEx.getMessage());
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error menutup koneksi: " + closeEx.getMessage());
                }
            }
        }

        System.out.println("Progress Bar ditutup.");
    }

    // --- Metode: editParticipantData ---
    private void editParticipantData(Participant participantToEdit, User observerUser, Scanner scanner) {
        System.out.println("\n--- Edit Data Partisipan ---");
        System.out.println("Anda akan mengedit Partisipan ID: " + participantToEdit.getParticipantId());
        System.out.println("Tekan ENTER jika tidak ingin mengubah data.");

        String newName;
        String newDepartment;
        String newJobPosition;
        String newEmailAddress;
        String newMobilePhone;

        while(true) {
            System.out.print("Nama (" + participantToEdit.getName() + "): ");
            newName = scanner.nextLine().trim();
            if (newName.isEmpty()) { newName = participantToEdit.getName(); }

            System.out.print("Departemen (" + participantToEdit.getDepartment() + "): ");
            newDepartment = scanner.nextLine().trim();
            if (newDepartment.isEmpty()) { newDepartment = participantToEdit.getDepartment(); }

            System.out.print("Posisi Kerja (" + participantToEdit.getJobPosition() + "): ");
            newJobPosition = scanner.nextLine().trim();
            if (newJobPosition.isEmpty()) { newJobPosition = participantToEdit.getJobPosition(); }

            System.out.print("Email (" + participantToEdit.getEmailAddress() + "): ");
            newEmailAddress = scanner.nextLine().trim();
            if (newEmailAddress.isEmpty()) { newEmailAddress = participantToEdit.getEmailAddress(); }

            System.out.print("Nomor Telepon (" + participantToEdit.getMobilePhone() + "): ");
            newMobilePhone = scanner.nextLine().trim();
            if (newMobilePhone.isEmpty()) { newMobilePhone = participantToEdit.getMobilePhone(); }

            if (newName.isEmpty() || newDepartment.isEmpty() || newJobPosition.isEmpty() || newEmailAddress.isEmpty() || newMobilePhone.isEmpty()) {
                System.out.println("Data wajib (Nama, Departemen, Posisi Kerja, Email, Telepon) tidak boleh kosong. Mohon isi ulang.");
            } else {
                break;
            }
        }
        
        participantToEdit.setName(newName);
        participantToEdit.setDepartment(newDepartment);
        participantToEdit.setJobPosition(newJobPosition);
        participantToEdit.setEmailAddress(newEmailAddress);
        participantToEdit.setMobilePhone(newMobilePhone);

        System.out.println("\nMembuat Progress Bar...");
        System.out.println("Memperbarui data di database...");

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            
            // Validasi keamanan: Pastikan partisipan yang diedit berasal dari perusahaan observer yang login
            int observerCompanyId = getCompanyIdForObserver(observerUser);
            if (observerCompanyId == -1 || participantToEdit.getCompanyId() != observerCompanyId) {
                System.out.println("Akses ditolak: Partisipan bukan dari perusahaan Anda.");
                return;
            }

            String sql = "UPDATE participants SET name = ?, department = ?, job_position = ?, email_address = ?, mobile_phone = ? WHERE participant_id = ? AND company_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, participantToEdit.getName());
                pstmt.setString(2, participantToEdit.getDepartment());
                pstmt.setString(3, participantToEdit.getJobPosition());
                pstmt.setString(4, participantToEdit.getEmailAddress());
                pstmt.setString(5, participantToEdit.getMobilePhone());
                pstmt.setInt(6, participantToEdit.getParticipantId());
                pstmt.setInt(7, observerCompanyId); // Filter keamanan

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Data Partisipan berhasil diperbarui di database.");
                } else {
                    System.out.println("Gagal memperbarui data Partisipan di database. ID tidak ditemukan atau bukan dari perusahaan Anda.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui data Partisipan: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error menutup koneksi: " + closeEx.getMessage());
                }
            }
        }

        System.out.println("Progress Bar ditutup.");
    }
}