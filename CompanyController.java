// CompanyController.java

// package com.pvt.system; // Sesuaikan dengan paket Anda

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Untuk Date
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors; // Untuk java.sql.Date

public class CompanyController {

    private List<Company> companyList;
    private String currentSearchKeyword;
    private ObserverController observerController; // Untuk memanggil View Observer List dari sini

    public CompanyController(ObserverController observerController) { // Menerima ObserverController
        this.companyList = new ArrayList<>();
        this.currentSearchKeyword = "";
        this.observerController = observerController; // Simpan referensi ObserverController
    }

    public void handleCompanyMenu(User userManagerUser, Scanner scanner) {
        String choice;
        do {
            boolean dataFound = displayCompanyListConcise(); // Tampilkan daftar perusahaan

            if (!dataFound) {
                System.out.println("Tidak ada data perusahaan yang ditemukan.");
                System.out.println("\nTekan ENTER untuk kembali ke Dashboard User Manager...");
                scanner.nextLine();
                return;
            }

            System.out.println("\n--- Opsi Perusahaan ---");
            System.out.println("1. View Company Detail");
            System.out.println("2. Search Companies");
            System.out.println("3. Add New Company");
            System.out.println("4. View Observers by Company"); 
            System.out.println("5. Kembali ke Dashboard User Manager");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewCompanyDetail(scanner);
                    break;
                case "2":
                    searchCompaniesMenu(scanner);
                    break;
                case "3":
                    addNewCompany(scanner);
                    break;
                case "4":
                    viewObserversByCompany(scanner, userManagerUser); // Memanggil ObserverController
                    break;
                case "5":
                    System.out.println("Kembali ke dashboard User Manager.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("5"));
    }

    // Metode untuk mengambil dan menampilkan daftar perusahaan dalam format ringkas
    private boolean displayCompanyListConcise() {
        System.out.println("\n--- Daftar Perusahaan ---");
        companyList.clear(); // Bersihkan list sebelumnya

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT company_id, name, address, date_registered, contact_person, mobile_phone, email_address " +
                 "FROM companies ORDER BY name");
             ResultSet rs = pstmt.executeQuery()) {

            boolean headerPrinted = false;

            while (rs.next()) {
                if (!headerPrinted) {
                    System.out.printf("%-5s %-30s %-40s%n", "ID", "Nama Perusahaan", "Alamat");
                    System.out.println("--------------------------------------------------------------------------------");
                    headerPrinted = true;
                }

                Company company = new Company(
                    rs.getInt("company_id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getDate("date_registered"),
                    rs.getString("contact_person"),
                    rs.getString("mobile_phone"),
                    rs.getString("email_address")
                );
                companyList.add(company); // Tambahkan ke list sementara

                System.out.printf("%-5d %-30s %-40s%n",
                    company.getCompanyId(),
                    company.getName(),
                    company.getAddress()
                );
            }

            return headerPrinted;
            
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data perusahaan dari database: " + e.getMessage());
            return false;
        }
    }

    // --- Metode: View Company Detail ---
    private void viewCompanyDetail(Scanner scanner) {
        System.out.println("\n--- View Company Detail ---");
        System.out.print("Masukkan ID Perusahaan yang ingin dilihat detailnya: ");
        int companyIdToView;
        try {
            companyIdToView = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID Perusahaan tidak valid. Mohon masukkan angka.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        Optional<Company> foundCompany = companyList.stream()
                                                    .filter(c -> c.getCompanyId() == companyIdToView)
                                                    .findFirst();

        if (foundCompany.isPresent()) {
            Company company = foundCompany.get();
            System.out.println("\n--- Detail Perusahaan ---");
            System.out.println("ID Perusahaan   : " + company.getCompanyId());
            System.out.println("Nama            : " + company.getName());
            System.out.println("Alamat          : " + company.getAddress());
            System.out.println("Tanggal Daftar  : " + company.getDateRegistered());
            System.out.println("Kontak Person   : " + company.getContactPerson());
            System.out.println("Nomor Telepon   : " + company.getMobilePhone());
            System.out.println("Email           : " + company.getEmailAddress());
            
            System.out.println("\n--- Opsi Detail Perusahaan ---");
            System.out.println("1. Edit Data");
            System.out.println("2. Kembali");
            System.out.print("Pilih opsi: ");
            String detailChoice = scanner.nextLine();

            if (detailChoice.equals("1")) {
                editCompanyData(company, scanner);
            } else if (detailChoice.equals("2")) {
                // Kembali ke menu sebelumnya
            } else {
                System.out.println("Opsi tidak valid.");
                System.out.println("\nTekan ENTER untuk melanjutkan...");
                scanner.nextLine();
            }

        } else {
            System.out.println("Perusahaan dengan ID " + companyIdToView + " tidak ditemukan dalam daftar.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
        }
    }
    private String getCompanyNameById(int companyId) {
        if (companyId == 0) {
            return "Semua";
        }
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
    
    // --- Metode: Search Companies ---
    private void searchCompaniesMenu(Scanner scanner) {
        String searchChoice;
        do {
            System.out.println("\n--- Search Companies ---");
            displayCurrentFilters(); // Tampilkan filter yang sedang aktif
            
            System.out.print("Masukkan Keyword Nama Perusahaan (kosongkan untuk tidak mencari): ");
            currentSearchKeyword = scanner.nextLine().trim();

            displaySearchResults(); // Tampilkan hasil pencarian

            System.out.println("\n--- Opsi Pencarian Lanjutan ---");
            System.out.println("1. Ubah Keyword Lagi");
            System.out.println("2. Reset Keyword");
            System.out.println("3. Kembali ke Menu Perusahaan");
            System.out.print("Pilih opsi: ");
            searchChoice = scanner.nextLine();

            switch (searchChoice) {
                case "1":
                    // Loop akan kembali ke awal untuk input keyword lagi
                    break;
                case "2":
                    resetFilters();
                    System.out.println("Keyword pencarian telah direset.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println("Kembali ke menu Perusahaan.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!searchChoice.equals("3"));
    }

    private void displaySearchResults() {
        System.out.println("\n--- Hasil Pencarian Perusahaan ---");

        if (companyList.isEmpty()) {
            System.out.println("Tidak ada data perusahaan di memori untuk dicari.");
            return;
        }

        List<Company> filteredAndSearchedCompanies = companyList.stream()
            .filter(c -> currentSearchKeyword.isEmpty() || c.getName().toLowerCase().startsWith(currentSearchKeyword.toLowerCase()))
            .collect(Collectors.toList());

        if (filteredAndSearchedCompanies.isEmpty()) {
            System.out.println("Tidak ada perusahaan yang cocok dengan kriteria pencarian saat ini.");
        } else {
            System.out.printf("%-5s %-30s %-40s%n", "ID", "Nama Perusahaan", "Alamat");
            System.out.println("--------------------------------------------------------------------------------");
            for (Company company : filteredAndSearchedCompanies) {
                System.out.printf("%-5d %-30s %-40s%n",
                    company.getCompanyId(),
                    company.getName(),
                    company.getAddress()
                );
            }
        }
    }

    private void resetFilters() {
        this.currentSearchKeyword = "";
    }

    private void displayCurrentFilters() {
        System.out.println("Filter Aktif:");
        System.out.println("  Keyword Nama Perusahaan: " + (currentSearchKeyword.isEmpty() ? "Tidak Ada" : currentSearchKeyword));
    }

    // --- Metode: Edit Company Data ---
    private void editCompanyData(Company companyToEdit, Scanner scanner) {
        System.out.println("\n--- Edit Data Perusahaan ---");
        System.out.println("Anda akan mengedit Perusahaan ID: " + companyToEdit.getCompanyId());
        System.out.println("Tekan ENTER jika tidak ingin mengubah data.");

        System.out.print("Nama (" + companyToEdit.getName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            companyToEdit.setName(newName);
        }

        System.out.print("Alamat (" + companyToEdit.getAddress() + "): ");
        String newAddress = scanner.nextLine();
        if (!newAddress.isEmpty()) {
            companyToEdit.setAddress(newAddress);
        }

        System.out.print("Kontak Person (" + companyToEdit.getContactPerson() + "): ");
        String newContactPerson = scanner.nextLine();
        if (!newContactPerson.isEmpty()) {
            companyToEdit.setContactPerson(newContactPerson);
        }

        System.out.print("Nomor Telepon (" + companyToEdit.getMobilePhone() + "): ");
        String newMobilePhone = scanner.nextLine();
        if (!newMobilePhone.isEmpty()) {
            companyToEdit.setMobilePhone(newMobilePhone);
        }

        System.out.print("Email (" + companyToEdit.getEmailAddress() + "): ");
        String newEmail = scanner.nextLine();
        if (!newEmail.isEmpty()) {
            companyToEdit.setEmailAddress(newEmail);
        }

        System.out.println("\nMembuat Progress Bar...");
        System.out.println("Memperbarui data di database...");

        try {
            String sql = "UPDATE companies SET name = ?, address = ?, contact_person = ?, mobile_phone = ?, email_address = ? WHERE company_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, companyToEdit.getName());
                pstmt.setString(2, companyToEdit.getAddress());
                pstmt.setString(3, companyToEdit.getContactPerson());
                pstmt.setString(4, companyToEdit.getMobilePhone());
                pstmt.setString(5, companyToEdit.getEmailAddress());
                pstmt.setInt(6, companyToEdit.getCompanyId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Data Perusahaan berhasil diperbarui di database.");
                } else {
                    System.out.println("Gagal memperbarui data Perusahaan di database. ID mungkin tidak ditemukan.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui data Perusahaan: " + e.getMessage());
        }

        System.out.println("Progress Bar ditutup.");
    }

    // --- Metode BARU: Add New Company ---
    private void addNewCompany(Scanner scanner) {
        System.out.println("\n--- Tambah Perusahaan Baru ---");
        System.out.println("Masukkan data perusahaan baru:");

        System.out.print("Nama Perusahaan: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("Nama perusahaan tidak boleh kosong. Pembatalan penambahan.");
            System.out.println("\nTekan ENTER untuk melanjutkan..."); // Jeda karena error/pembatalan
            scanner.nextLine();
            return;
        }

        System.out.print("Alamat: ");
        String address = scanner.nextLine();

        System.out.print("Tanggal Daftar (YYYY-MM-DD): ");
        String dateRegisteredStr = scanner.nextLine();
        Date dateRegistered;
        try {
            dateRegistered = Date.valueOf(LocalDate.parse(dateRegisteredStr));
        } catch (DateTimeParseException e) {
            System.out.println("Format tanggal tidak valid. Gunakanんですね-MM-DD. Pembatalan penambahan.");
            System.out.println("\nTekan ENTER untuk melanjutkan..."); // Jeda karena error
            scanner.nextLine();
            return;
        }

        System.out.print("Kontak Person: ");
        String contactPerson = scanner.nextLine();

        System.out.print("Nomor Telepon: ");
        String mobilePhone = scanner.nextLine();

        System.out.print("Email: ");
        String emailAddress = scanner.nextLine();

        Company newCompany = new Company(name, address, dateRegistered, contactPerson, mobilePhone, emailAddress);

        System.out.println("\nMembuat Progress Bar...");
        System.out.println("Menambahkan data ke database...");

        try {
            String sql = "INSERT INTO companies (name, address, date_registered, contact_person, mobile_phone, email_address) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, newCompany.getName());
                pstmt.setString(2, newCompany.getAddress());
                pstmt.setDate(3, newCompany.getDateRegistered());
                pstmt.setString(4, newCompany.getContactPerson());
                pstmt.setString(5, newCompany.getMobilePhone());
                pstmt.setString(6, newCompany.getEmailAddress());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Perusahaan baru berhasil ditambahkan.");
                    // Refresh companyList dari database setelah penambahan
                    displayCompanyListConcise(); // Panggil ini untuk memuat ulang daftar di memori
                } else {
                    System.out.println("Gagal menambahkan perusahaan baru.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan perusahaan baru: " + e.getMessage());
        }

        System.out.println("Progress Bar ditutup.");
    }

    // --- Metode BARU: View Observers by Company (Memanggil ObserverController) ---
    private void viewObserversByCompany(Scanner scanner, User userManagerUser) {
        System.out.println("\n--- View Observers by Company ---");
        System.out.print("Masukkan ID Perusahaan untuk melihat Observernya: ");
        int companyIdToView;
        try {
            companyIdToView = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID Perusahaan tidak valid. Mohon masukkan angka.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        String companyName = getCompanyNameById(companyIdToView);
        if (companyName.equals("Tidak Diketahui") || companyName.equals("Semua")) {
            System.out.println("Perusahaan dengan ID " + companyIdToView + " tidak ditemukan.");
            System.out.println("\nTekan ENTER untuk melanjutkan...");
            scanner.nextLine();
            return;
        }

        System.out.println("\n--- Observer di " + companyName + " ---");
        
        // Panggil metode di ObserverController untuk menampilkan observer berdasarkan companyId
        observerController.handleObserverMenuForCompany(companyIdToView, userManagerUser, scanner);
        
        String choice;
        do {
            // Setelah kembali dari ObserverController, atau setelah menambah/kembali
            System.out.println("\n--- Opsi Observer untuk Perusahaan Ini ---");
            System.out.println("1. Tambah Observer Baru"); // Opsi baru
            System.out.println("2. Kembali ke Menu Perusahaan");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // Panggil metode baru di ObserverController untuk menambahkan observer
                    observerController.addNewObserver(companyIdToView, scanner); 
                    break;
                case "2":
                    System.out.println("Kembali ke menu perusahaan.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("2"));
    }

}