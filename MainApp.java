// Jalankan dengan
// javac MainApp.java Measurement.java Participant.java PerformanceCriteria.java SendController.java User.java UserRepository.java AuthManager.java DatabaseUtil.java Observer.java Company.java ObserverController.java
// java -cp ".;lib/postgresql-42.7.6.jar" MainApp

import java.util.Scanner;

public class MainApp {
    private Scanner scanner;
    private AuthManager authManager;
    private SendController sendController;
    private ObserverController observerController;
    private CompanyController companyController;
    private ReportController reportController;     // Deklarasi ReportController
    private ParticipantController participantController; // Deklarasi ParticipantController
    

    public MainApp() {
        scanner = new Scanner(System.in);
        authManager = new AuthManager();
        this.sendController = new SendController();
        this.observerController = new ObserverController();
        this.companyController = new CompanyController(this.observerController); // CompanyController butuh ObserverController
        // --- PENAMBAHAN DI SINI ---
        this.reportController = new ReportController(); // Inisialisasi ReportController
        // ParticipantController butuh ReportController
        this.participantController = new ParticipantController(this.reportController);
           
    }

    public void start() {
        boolean loggedIn = false;
        while (!loggedIn) {
            displayLoginScreen();
            System.out.print("Masukkan Username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan Password: ");
            String password = scanner.nextLine();

            User authenticatedUser = authManager.authenticate(username, password);

            if (authenticatedUser != null) {
                loggedIn = true;
                handleUserDashboard(authenticatedUser);
            } else {
                System.out.println("Username tidak tersedia atau Password salah. Silakan coba lagi.");
            }
        }
        scanner.close(); // Tutup scanner setelah aplikasi utama selesai
    }

    private void displayLoginScreen() {
        System.out.println("\n--- Sistem PVT Login ---");
        System.out.println("-------------------------");
    }

    private void handleUserDashboard(User user) {
        System.out.println("\nSelamat datang, " + user.getUsername() + "!");
        switch (user.getRole()) {
            case "participant":
                System.out.println("Anda masuk sebagai PARTISIPAN.");
                runMobilePVT(user); // Panggil metode dashboard partisipan
                break;
            case "observer":
                System.out.println("Anda masuk sebagai OBSERVER.");
                runPVTwebForObserver(user); // Panggil metode dashboard observer
                break;
            case "user_manager":
                System.out.println("Anda masuk sebagai USER MANAGER.");
                runPVTwebForUserManager(user); // Panggil metode dashboard user manager
                break;
            default:
                System.out.println("Peran tidak dikenali.");
        }
    }

    // --- Metode Dashboard Partisipan (Sudah Benar) ---
    private void runMobilePVT(User participantUser) {
        String choice;
        do {
            System.out.println("\n=== Dashboard Mobile PVT (Partisipan) ===");
            System.out.println("1. Kirim Data Pengukuran");
            System.out.println("2. Logout");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    sendController.handleSendMeasurement(participantUser, scanner);
                    break;
                case "2":
                    System.out.println("Logout berhasil.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
            }
        } while (!choice.equals("2"));
    }

    // --- Metode Dashboard Observer (Tetap Placeholder) ---
    private void runPVTwebForObserver(User observerUser) {
        String choice;
        do {
            System.out.println("\n=== Dashboard PVTweb (Observer) ===");
            System.out.println("1. Lihat Daftar Partisipan"); // Opsi baru
            System.out.println("2. Lihat Daftar Laporan");    // Opsi asli 'View Report List'
            System.out.println("3. Logout");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    participantController.handleParticipantMenu(observerUser, scanner); // Panggil ParticipantController
                    break;
                case "2":
                    reportController.handleReportMenu(observerUser, scanner); // Panggil ReportController
                    break;
                case "3":
                    System.out.println("Logout berhasil.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("3"));
    }

    // --- Metode Dashboard User Manager (Perbaikan Lengkap) ---
     private void runPVTwebForUserManager(User userManagerUser) {
        String choice;
        do {
            System.out.println("\n=== Dashboard PVTweb (User Manager) ===");
            System.out.println("1. Lihat Daftar Perusahaan");
            System.out.println("2. Lihat Daftar Observer");
            System.out.println("3. Logout");
            System.out.print("Pilih opsi: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1": // Pilihan untuk "Lihat Daftar Perusahaan"
                    companyController.handleCompanyMenu(userManagerUser, scanner); // Panggil CompanyController
                    break;
                case "2": // Pilihan untuk "Lihat Daftar Observer"
                    observerController.handleObserverMenu(userManagerUser, scanner);
                    break;
                case "3":
                    System.out.println("Logout berhasil.");
                    break;
                default:
                    System.out.println("Opsi tidak valid. Silakan coba lagi.");
                    System.out.println("\nTekan ENTER untuk melanjutkan...");
                    scanner.nextLine();
            }
        } while (!choice.equals("3"));
    }


    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.start();
    }
}