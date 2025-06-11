// ObserverController.java

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

// Pastikan Observer.java, Company.java, DatabaseUtil.java, dan User.java sudah ada

public class ObserverController {

    private List<Observer> observerList;
    private String currentSearchKeyword;
    private String currentFilterGender;
    private String currentFilterPosition;
    private Integer currentFilterCompanyId;

    public ObserverController() {
        this.observerList = new ArrayList<>();
        this.currentSearchKeyword = "";
        this.currentFilterGender = null;
        this.currentFilterPosition = null;
        this.currentFilterCompanyId = null;
    }

    public void addNewObserver(int companyIdForNewObserver, Scanner scanner) {
        System.out.println("\n--- Tambah Observer Baru ---");
        System.out.println("Untuk Perusahaan ID: " + companyIdForNewObserver + " (" + getCompanyNameById(companyIdForNewObserver) + ")");
        
        String username;
        String password;
        String name;
        String address;
        String mobilePhone;
        String emailAddress;
        String gender;
        String position;

        // 1. Input data untuk User (login) dan validasi username unik & tidak kosong
        while (true) {
            System.out.print("Masukkan Username baru untuk Observer: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username tidak boleh kosong. Mohon isi.");
                continue; // Minta input username lagi
            }

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username '" + username + "' sudah ada. Mohon gunakan username lain.");
                } else {
                    break; // Username unik dan tidak kosong
                }
            } catch (SQLException e) {
                System.err.println("Error saat memeriksa username: " + e.getMessage());
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
                return; // Batalkan jika ada masalah DB
            }
        }

        System.out.print("Masukkan Password baru untuk Observer: ");
        password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password tidak boleh kosong. Pembatalan penambahan.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        // 2. Input data untuk Observer (detail) dan validasi tidak kosong
        while (true) {
            System.out.print("Nama Observer: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Nama tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Alamat: ");
            address = scanner.nextLine().trim();
            if (address.isEmpty()) { System.out.println("Alamat tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Nomor Telepon: ");
            mobilePhone = scanner.nextLine().trim();
            if (mobilePhone.isEmpty()) { System.out.println("Nomor Telepon tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Email: ");
            emailAddress = scanner.nextLine().trim();
            if (emailAddress.isEmpty()) { System.out.println("Email tidak boleh kosong. Mohon isi."); continue; }

            System.out.print("Gender (Male/Female): ");
            gender = scanner.nextLine().trim();
            if (gender.isEmpty()) { System.out.println("Gender tidak boleh kosong. Mohon isi."); continue; }
            if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")) {
                System.out.println("Gender tidak valid. Gunakan 'Male' atau 'Female'. Mohon isi ulang.");
                continue;
            }

            System.out.print("Posisi (Staff/Manager): ");
            position = scanner.nextLine().trim();
            if (position.isEmpty()) { System.out.println("Posisi tidak boleh kosong. Mohon isi."); continue; }
            if (!position.equalsIgnoreCase("Staff") && !position.equalsIgnoreCase("Manager")) {
                System.out.println("Posisi tidak valid. Gunakan 'Staff' atau 'Manager'. Mohon isi ulang.");
                continue;
            }
            break; // Semua input detail observer valid, keluar dari loop
        }

        // ... (Simulasi Progress Bar dan Logika Insert Database seperti sebelumnya) ...

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // --- 1. Insert ke tabel users ---
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'observer') RETURNING user_id";
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

            // --- 2. Insert ke tabel observers ---
            String observerSql = "INSERT INTO observers (user_id, company_id, name, address, mobile_phone, email_address, gender, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement obsPstmt = conn.prepareStatement(observerSql)) {
                obsPstmt.setInt(1, newUserId);
                obsPstmt.setInt(2, companyIdForNewObserver);
                obsPstmt.setString(3, name);
                obsPstmt.setString(4, address);
                obsPstmt.setString(5, mobilePhone);
                obsPstmt.setString(6, emailAddress);
                obsPstmt.setString(7, gender);
                obsPstmt.setString(8, position);

                obsPstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Observer baru dengan username '" + username + "' berhasil ditambahkan.");

            System.out.println("\n[MessageBox] Observer dengan username '" + username + "' berhasil dibentuk.");
            System.out.print("Tekan ENTER untuk OK...");
            scanner.nextLine();

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan observer baru karena masalah database: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException rollbackEx) { System.err.println("Error saat rollback: " + rollbackEx.getMessage()); }
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan tak terduga: " + e.getMessage());
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException autoCommitEx) { System.err.println("Error mengembalikan auto-commit: " + autoCommitEx.getMessage()); }
            if (conn != null) { try { conn.close(); } catch (SQLException closeEx) { System.err.println("Error menutup koneksi: " + closeEx.getMessage()); }}
        }

        System.out.println("Progress Bar ditutup.");
    }
    
    public void handleObserverMenu(User userManagerUser, Scanner scanner) {
        String choice;
        do {
            boolean dataFound = displayObserverListConcise();

            if (!dataFound) {
                System.out.println("Tidak ada data observer yang ditemukan.");
                System.out.println("\nTekan ENTER untuk kembali ke Dashboard User Manager...");
                scanner.nextLine();
                return;
            }

            System.out.println("\n--- Opsi Observer ---");
            System.out.println("1. View Observer Detail");
            System.out.println("2. Search Observers");
            System.out.println("3. Kembali ke Dashboard User Manager");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewObserverDetail(scanner);
                    break;
                case "2":
                    searchObserversMenu(scanner);
                    break;
                case "3":
                    System.out.println("Kembali ke dashboard User Manager.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("3"));
    }
    
    // --- Metode handleObserverMenuForCompany (PERBAIKAN DI SINI) ---
    public void handleObserverMenuForCompany(int companyIdToFilter, User userManagerUser, Scanner scanner) {
        this.currentFilterCompanyId = companyIdToFilter; 
        
        System.out.println("\n--- Observer untuk Perusahaan: " + getCompanyNameById(companyIdToFilter) + " ---");
        
        boolean dataFound = displayObserverListConcise(); 
        
        if (!dataFound) {
            System.out.println("Tidak ada observer yang ditemukan untuk perusahaan ini.");
        }
        this.currentFilterCompanyId = null; 
    }

    private boolean displayObserverListConcise() {
        System.out.println("\n--- Daftar Observer ---");
        observerList.clear();

        // --- AWAL BAGIAN PERBAIKAN FILTER COMPANY ID ---
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT o.observer_id, o.user_id, o.company_id, o.name, o.address, o.mobile_phone, o.email_address, o.gender, o.position, c.name AS company_name " +
            "FROM observers o JOIN companies c ON o.company_id = c.company_id "
        );
        List<Object> params = new ArrayList<>();

        if (currentFilterCompanyId != null) {
            sqlBuilder.append("WHERE o.company_id = ? "); // Tambahkan kondisi WHERE
            params.add(currentFilterCompanyId);
        }
        sqlBuilder.append("ORDER BY o.name");


        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) { // Gunakan sqlBuilder

            // Set parameter untuk PreparedStatement
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean headerPrinted = false;

                while (rs.next()) {
                    if (!headerPrinted) {
                        System.out.printf("%-5s %-25s %-25s%n", "ID", "Nama Observer", "Perusahaan");
                        System.out.println("------------------------------------------------------------------");
                        headerPrinted = true;
                    }

                    Observer observer = new Observer(
                        rs.getInt("observer_id"), rs.getInt("user_id"), rs.getInt("company_id"),
                        rs.getString("name"), rs.getString("address"), rs.getString("mobile_phone"),
                        rs.getString("email_address"), rs.getString("gender"), rs.getString("position")
                    );
                    observerList.add(observer);

                    System.out.printf("%-5d %-25s %-25s%n",
                        observer.getObserverId(),
                        observer.getName(),
                        rs.getString("company_name")
                    );
                }

                return headerPrinted;
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data observer dari database: " + e.getMessage());
            return false;
        }
    }

    private void viewObserverDetail(Scanner scanner) {
        System.out.println("\n--- View Observer Detail ---");
        System.out.print("Masukkan ID Observer yang ingin dilihat detailnya: ");
        int observerIdToView;
        try {
            observerIdToView = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID Observer tidak valid. Mohon masukkan angka.");
            System.out.println("\nTekan ENTER untuk melanjutkan..."); // Jeda karena error
            scanner.nextLine();
            return;
        }

        Optional<Observer> foundObserver = observerList.stream()
                                                      .filter(obs -> obs.getObserverId() == observerIdToView)
                                                      .findFirst();

        if (foundObserver.isPresent()) {
            Observer observer = foundObserver.get();
            String companyName = getCompanyNameById(observer.getCompanyId());

            System.out.println("\n--- Detail Observer ---");
            System.out.println("ID Observer     : " + observer.getObserverId());
            System.out.println("Nama            : " + observer.getName());
            System.out.println("Perusahaan      : " + companyName);
            System.out.println("Alamat          : " + observer.getAddress());
            System.out.println("Nomor Telepon   : " + observer.getMobilePhone());
            System.out.println("Email           : " + observer.getEmailAddress());
            System.out.println("Gender          : " + observer.getGender());
            System.out.println("Posisi          : " + observer.getPosition());
            System.out.println("ID User         : " + observer.getUserId());
            
            System.out.println("\n--- Opsi Detail Observer ---");
            System.out.println("1. Edit Data");
            System.out.println("2. Kembali");
            System.out.print("Pilih opsi: ");
            String detailChoice = scanner.nextLine();

            if (detailChoice.equals("1")) {
                editObserverData(observer, scanner);
            } else if (detailChoice.equals("2")) {
                // Kembali ke menu utama Observer, tidak perlu jeda.
            } else {
                System.out.println("Opsi tidak valid.");
                System.out.println("\nTekan ENTER untuk melanjutkan..."); // Jeda karena error
                scanner.nextLine();
            }

        } else {
            System.out.println("Observer dengan ID " + observerIdToView + " tidak ditemukan dalam daftar.");
            System.out.println("\nTekan ENTER untuk melanjutkan..."); // Jeda karena tidak ditemukan
            scanner.nextLine();
        }
    }

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
            System.err.println("Error saat mengambil nama perusahaan: " + e.getMessage());
        }
        return companyName;
    }

    private void editObserverData(Observer observerToEdit, Scanner scanner) {
        System.out.println("\n--- Edit Data Observer ---");
        System.out.println("Anda akan mengedit Observer ID: " + observerToEdit.getObserverId());
        System.out.println("Tekan ENTER jika tidak ingin mengubah data.");

        System.out.print("Nama (" + observerToEdit.getName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            observerToEdit.setName(newName);
        }

        System.out.print("Alamat (" + observerToEdit.getAddress() + "): ");
        String newAddress = scanner.nextLine();
        if (!newAddress.isEmpty()) {
            observerToEdit.setAddress(newAddress);
        }

        System.out.print("Nomor Telepon (" + observerToEdit.getMobilePhone() + "): ");
        String newMobilePhone = scanner.nextLine();
        if (!newMobilePhone.isEmpty()) {
            observerToEdit.setMobilePhone(newMobilePhone);
        }

        System.out.print("Email (" + observerToEdit.getEmailAddress() + "): ");
        String newEmail = scanner.nextLine();
        if (!newEmail.isEmpty()) {
            observerToEdit.setEmailAddress(newEmail);
        }

        System.out.print("Gender (" + observerToEdit.getGender() + "): ");
        String newGender = scanner.nextLine();
        if (!newGender.isEmpty()) {
            observerToEdit.setGender(newGender);
        }

        System.out.print("Posisi (" + observerToEdit.getPosition() + "): ");
        String newPosition = scanner.nextLine();
        if (!newPosition.isEmpty()) {
            observerToEdit.setPosition(newPosition);
        }

        System.out.println("\nMembuat Progress Bar...");
        System.out.println("Memperbarui data di database...");

        try {
            String sql = "UPDATE observers SET name = ?, address = ?, mobile_phone = ?, email_address = ?, gender = ?, position = ? WHERE observer_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, observerToEdit.getName());
                pstmt.setString(2, observerToEdit.getAddress());
                pstmt.setString(3, observerToEdit.getMobilePhone());
                pstmt.setString(4, observerToEdit.getEmailAddress());
                pstmt.setString(5, observerToEdit.getGender());
                pstmt.setString(6, observerToEdit.getPosition());
                pstmt.setInt(7, observerToEdit.getObserverId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Data Observer berhasil diperbarui di database.");
                } else {
                    System.out.println("Gagal memperbarui data Observer di database. ID mungkin tidak ditemukan.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui data Observer: " + e.getMessage());
        }

        System.out.println("Progress Bar ditutup.");
        // Tidak perlu jeda di sini, akan kembali ke menu
    }

    // --- Metode BARU: searchObserversMenu (Revisi Alur Filter) ---
    private void searchObserversMenu(Scanner scanner) {
        String searchChoice;
        do {
            System.out.println("\n--- Search Observers ---");
            
            // --- Alur Baru: Terapkan Filter Dulu, Lalu Masukkan Keyword ---
            applyFilterMenu(scanner); // Panggil menu untuk menerapkan filter di awal
            displayCurrentFilters();  // Tampilkan filter yang sedang aktif setelah user selesai set filter

            System.out.print("Masukkan Keyword Nama (kosongkan untuk tidak mencari): ");
            currentSearchKeyword = scanner.nextLine().trim(); // Simpan keyword setelah filter

            // Langsung tampilkan hasil pencarian setelah keyword diinput
            displaySearchResults();

            System.out.println("\n--- Opsi Pencarian Lanjutan ---");
            System.out.println("1. Ubah Keyword / Filter Lagi"); // Gabungkan opsi
            System.out.println("2. Reset Filter & Keyword");
            System.out.println("3. Kembali ke Menu Observer");
            System.out.print("Pilih opsi: ");
            searchChoice = scanner.nextLine();

            switch (searchChoice) {
                case "1":
                    // Loop akan otomatis kembali ke awal (do-while) untuk minta filter/keyword lagi
                    break;
                case "2":
                    resetFilters();
                    System.out.println("Filter dan keyword telah direset.");
                    // Jeda agar user bisa membaca pesan reset
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println("Kembali ke menu Observer.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    // Jeda di sini untuk error
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!searchChoice.equals("3"));
    }

    // --- Metode applyFilterMenu (Tidak ada perubahan signifikan pada logikanya, hanya perilaku) ---
    private void applyFilterMenu(Scanner scanner) {
        String filterChoice;
        do {
            System.out.println("\n--- Terapkan/Ubah Filter ---");
            displayCurrentFilters(); // Tampilkan filter aktif di awal menu filter
            System.out.println("1. Filter by Gender (Saat ini: " + (currentFilterGender == null ? "Semua" : currentFilterGender) + ")");
            System.out.println("2. Filter by Position (Saat ini: " + (currentFilterPosition == null ? "Semua" : currentFilterPosition) + ")");
            System.out.println("3. Filter by Company (Saat ini: " + (currentFilterCompanyId == null ? "Semua" : getCompanyNameById(currentFilterCompanyId != null ? currentFilterCompanyId : 0)) + ")");
            System.out.println("4. Hapus Semua Filter");
            System.out.println("5. Selesai Filter");
            System.out.print("Pilih atribut filter: ");
            filterChoice = scanner.nextLine();

            switch (filterChoice) {
                case "1":
                    System.out.print("Masukkan Gender (Male/Female/kosong untuk semua): ");
                    String genderInput = scanner.nextLine().trim();
                    currentFilterGender = genderInput.isEmpty() ? null : (genderInput.equalsIgnoreCase("Male") ? "Male" : (genderInput.equalsIgnoreCase("Female") ? "Female" : null));
                    if (currentFilterGender == null && !genderInput.isEmpty()) {
                        System.out.println("Gender tidak valid. Gunakan 'Male' atau 'Female'. Filter tidak diterapkan.");
                    }
                    break;
                case "2":
                    System.out.print("Masukkan Posisi (Staff/Manager/kosong untuk semua): ");
                    String positionInput = scanner.nextLine().trim();
                    currentFilterPosition = positionInput.isEmpty() ? null : (positionInput.equalsIgnoreCase("Staff") ? "Staff" : (positionInput.equalsIgnoreCase("Manager") ? "Manager" : null));
                     if (currentFilterPosition == null && !positionInput.isEmpty()) {
                        System.out.println("Posisi tidak valid. Gunakan 'Staff' atau 'Manager'. Filter tidak diterapkan.");
                    }
                    break;
                case "3":
                    System.out.print("Masukkan ID Perusahaan (angka/kosong untuk semua): ");
                    String companyIdInput = scanner.nextLine().trim();
                    if (companyIdInput.isEmpty()) {
                        currentFilterCompanyId = null;
                        System.out.println("Filter Perusahaan direset ke Semua.");
                    } else {
                        try {
                            Integer companyId = Integer.parseInt(companyIdInput);
                            if (getCompanyNameById(companyId) != "Tidak Diketahui" && getCompanyNameById(companyId) != "Semua") { // Pastikan ID Company valid
                                currentFilterCompanyId = companyId;
                            } else {
                                System.out.println("ID Perusahaan tidak valid atau tidak ditemukan. Filter tidak diterapkan.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Input ID Perusahaan tidak valid. Mohon masukkan angka.");
                        }
                    }
                    break;
                case "4":
                    resetFilters();
                    System.out.println("Semua filter telah dihapus.");
                    filterChoice = "5"; // Otomatis keluar dari menu filter
                    break;
                case "5":
                    System.out.println("Selesai menerapkan filter.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
            }
            // Jeda di sini hanya jika tidak keluar dari menu filter
            if (!filterChoice.equals("5")) {
                 System.out.println("\nTekan ENTER untuk melanjutkan...");
                 scanner.nextLine();
            }
        } while (!filterChoice.equals("5"));
    }

    // --- Metode displaySearchResults (Tidak ada perubahan pada logikanya) ---
    private void displaySearchResults() {
        System.out.println("\n--- Hasil Pencarian Observer ---");

        if (observerList.isEmpty()) {
            System.out.println("Tidak ada data observer di memori untuk dicari.");
            return;
        }

        List<Observer> filteredAndSearchedObservers = observerList.stream()
            .filter(obs -> currentSearchKeyword.isEmpty() || obs.getName().toLowerCase().startsWith(currentSearchKeyword.toLowerCase()))
            .filter(obs -> currentFilterGender == null || (obs.getGender() != null && obs.getGender().equalsIgnoreCase(currentFilterGender)))
            .filter(obs -> currentFilterPosition == null || (obs.getPosition() != null && obs.getPosition().equalsIgnoreCase(currentFilterPosition)))
            .filter(obs -> currentFilterCompanyId == null || obs.getCompanyId() == currentFilterCompanyId)
            .collect(Collectors.toList());

        if (filteredAndSearchedObservers.isEmpty()) {
            System.out.println("Tidak ada observer yang cocok dengan kriteria pencarian/filter saat ini.");
        } else {
            System.out.printf("%-5s %-25s %-25s%n", "ID", "Nama Observer", "Perusahaan");
            System.out.println("------------------------------------------------------------------");
            for (Observer observer : filteredAndSearchedObservers) {
                String companyName = getCompanyNameById(observer.getCompanyId());
                System.out.printf("%-5d %-25s %-25s%n",
                    observer.getObserverId(),
                    observer.getName(),
                    companyName
                );
            }
        }
    }

    // --- Metode resetFilters (Tidak ada perubahan pada logikanya) ---
    private void resetFilters() {
        this.currentSearchKeyword = "";
        this.currentFilterGender = null;
        this.currentFilterPosition = null;
        this.currentFilterCompanyId = null;
    }

    // --- Metode displayCurrentFilters (Tidak ada perubahan pada logikanya) ---
    private void displayCurrentFilters() {
        System.out.println("Filter Aktif:");
        System.out.println("  Keyword Nama   : " + (currentSearchKeyword.isEmpty() ? "Tidak Ada" : currentSearchKeyword));
        System.out.println("  Gender         : " + (currentFilterGender == null ? "Semua" : currentFilterGender));
        System.out.println("  Posisi         : " + (currentFilterPosition == null ? "Semua" : currentFilterPosition));
        String companyFilterName = "Semua";
        if (currentFilterCompanyId != null) {
            companyFilterName = getCompanyNameById(currentFilterCompanyId);
        }
        System.out.println("  Perusahaan     : " + companyFilterName);
    }
}