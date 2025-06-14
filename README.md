# Tugas PPL - Program Console
<br>

# Langkah - Langkah Pengerjaan
1. Pastikan Postgre dan pgAdmin4 sudah terinstal. Gunakan versi postgres `17`
2. Simpan dan unzip file `Tugas Akhir (Program di Console).zip` pada direktori yang diinginkan.
3. Buka Command Prompt, lalu buat database bernama `PVT` di username `postgres` dengan password sesuai dengan keinginan. <br>
   Berikut contoh kode untuk membuatnya :
   ``` cmd
   cd "C:\Program Files\PostgreSQL\17\bin"
   createdb -U postgres PVT
       --- lalu inputkan password yang diinginkan, kalau bisa jangan pakai spasi
   psql -U postgres -d PVT -f "D:\Kuliah\Semester 4\Perancangan Perangkat Lunak\Tugas Akhir (Program di Console)\backupPVT.sql"
       --- lalu masukkan password untuk user `postgres`
       --- password ini didapat ketika menginstall postgres pertama kali
   ```
   Hasilnya : <br>
   ![Screenshot 2025-06-11 071919](https://github.com/user-attachments/assets/23e40b2f-32af-4524-bf1b-1d7722c08a05)
   
5. Lalu buka pgAdmin4 untuk memastikan bahwa database PVT berhasil dibentuk. <br>
   Hasilnya : <br>
   ![image](https://github.com/user-attachments/assets/b7550956-1651-4c14-8a41-03bde6b4e137)
6. Ubah kode pada file `DatabaseUtil.java` pada bagian
   ``` java
   private static final String URL = "jdbc:postgresql://localhost:5432/PVT";
   private static final String USER = "postgres";
   private static final String PASSWORD = "<sesuaikan dengan password database PVT tadi>";
   ```
7. Coba jalankan program di terminal Virtual Studio Code
   ```
   javac MainApp.java
   java -cp ".;lib/postgresql-42.7.6.jar" MainApp.java
   ```

# Login
Terdapat 3 macam role user, yaitu participant, observer, dan user manager. Setiap user, memiliki username dan password berbeda dan memiliki akses program yang berbeda. Berikut penjelasannya
## 👨 Participant
- Use Case yang bisa diakses :
  - Send Measurement
- Contoh Login :
  ```
  Username : part_jago_01, Password : participant123
  Username : part_bangun_01, Password : participant123
  ```
- Output :
  ```
  --- Sistem PVT Login ---
  -------------------------
  Masukkan Username: part_jago_01
  Masukkan Password: participant123
  Login berhasil sebagai participant!

  Selamat datang, part_jago_01!
  Anda masuk sebagai PARTISIPAN.

  === Dashboard Mobile PVT (Partisipan) ===
  1. Kirim Data Pengukuran
  2. Logout
  Pilih opsi: 
  ```
## 🧔‍♂️ Observer
- Use Case yang bisa diakses :
  - View Participant List
  - View Report List
- Contoh Login :
  ```
  Username : obs_jago_01, Password : observer123
  Username : obs_bangun_01, Password : observer123
  ```
- Output :
  ```
   --- Sistem PVT Login ---
   -------------------------
   Masukkan Username: obs_jago_01
   Masukkan Password: observer123
   Login berhasil sebagai observer!
  
   Selamat datang, obs_jago_01!
   Anda masuk sebagai OBSERVER.
   
   === Dashboard PVTweb (Observer) ===
   1. Lihat Daftar Partisipan
   2. Lihat Daftar Laporan
   3. Logout
   Pilih opsi: 
  ```

## 🙍‍♂️ User Manager
- Use Case yang bisa diakses :
  - View Company List
  - View Observers List
- Contoh Login :
  ```
  Username : manager_rudi, Password : manager123
  Username : manager_sinta, Password : manager123
  ```
- Output :
  ```
   --- Sistem PVT Login ---
   -------------------------
   Masukkan Username: manager_rudi
   Masukkan Password: manager123
   Login berhasil sebagai user_manager!

   Selamat datang, manager_rudi!
   Anda masuk sebagai USER MANAGER.

   === Dashboard PVTweb (User Manager) ===
   1. Lihat Daftar Perusahaan
   2. Lihat Daftar Observer
   3. Logout
   Pilih opsi:
  ```

# Desain Pattern
Berikut adalah pola desain utama yang kami identifikasi dan terapkan dalam perangkat lunak PVT ini :<br>
**1. Singleton Pattern (Pola Singleton)**
<p align='justify'><b>Tujuan</b> : Memastikan sebuah kelas hanya memiliki satu instansi dan menyediakan titik akses global ke instansi tersebut. <br>
<b>Penerapan dalam PVT</b> : Pola Singleton digunakan pada kelas DatabaseUtil. Kelas ini bertanggung jawab untuk mengelola koneksi ke database. Dengan menerapkan Singleton, kami memastikan bahwa hanya ada satu objek DatabaseUtil yang dibuat selama masa pakai aplikasi. Ini mencegah duplikasi koneksi database yang tidak perlu dan mengelola sumber daya database secara efisien, karena koneksi database adalah sumber daya yang seringkali mahal dan unik dalam sebuah aplikasi.</p>
<br>

**2. Command Pattern (Pola Perintah)**
<p align='justify'><b>Tujuan</b> : Mengkapsulasi sebuah permintaan (request) sebagai sebuah objek. Ini memungkinkan klien untuk memparametrisasi permintaan yang berbeda, mengantrekan atau mencatat permintaan, dan mendukung operasi yang dapat di-undo.<br>
<b>Penerapan dalam PVT</b> : Konsep dari Command Pattern terdapat dalam cara kami mendesain controller. Setiap tindakan pengguna (seperti "Send Measurement", "View Observer Detail", "Add New Participant") dienkapsulasi dalam metode-metode spesifik di dalam kelas Controller yang relevan. Metode-metode ini menerima parameter yang diperlukan dan mengeksekusi serangkaian operasi untuk memenuhi permintaan tersebut. Ini mirip dengan sebuah "perintah" yang dikerjakan oleh controller.</p>

**3. Observer Pattern (Pola Observer)**
<p align='justify'><b>Tujuan : </b>Mendefinisikan dependensi satu-ke-banyak antar objek, sehingga ketika satu objek berubah keadaan, semua dependensinya diberitahu dan diperbarui secara otomatis. Pola ini sering digunakan dalam framework Model-View-Controller (MVC).<br>
<b>Penerapan dalam PVT :</b> Konsep inti dari Observer Pattern ada dalam cara kami memastikan konsistensi data di memori. Saat data di database diubah (misalnya, melalui Edit Data Observer atau Add New Participant), Controller yang melakukan perubahan tersebut akan secara aktif me-refresh List data di memori (observerList, participantList, measurementList) dengan memuat ulang data dari database. Ini mensimulasikan mekanisme pembaruan otomatis: perubahan pada "Subject" (database) memicu pembaruan pada "Observer" (list data di memori yang digunakan oleh controller).</p>

**4. Factory Pattern (Pola Pabrik)** 
<p align='justify'><b>Tujuan</b> : Membuat objek tanpa mengekspos logika pembuatannya kepada klien dan merujuk pada objek yang baru dibuat menggunakan interface umum. Ini termasuk dalam kategori pola kreasi (creational pattern).<br>
<b>Penerapan dalam PVT :</b> Konsep dasar Factory Pattern terwujud secara implisit dalam konstruksi objek Model (seperti Participant, Observer, Measurement) di dalam kelas Controller. Ketika Controller membaca ResultSet dari database, ia bertanggung jawab untuk "membuat" objek Model yang sesuai. Ini mengkapsulasi logika pembuatan objek dari "klien" (yaitu, bagian lain dari controller yang hanya menerima objek Model yang sudah jadi).<br></p>
    
**5. Builder Pattern (Pola Pembangun)**
<p align='justify'><b>Tujuan</b> : Membangun objek kompleks secara bertahap menggunakan objek sederhana dan pendekatan langkah demi langkah. <br>
<b>Penerapan dalam PVT :</b> Pola Builder tidak secara eksplisit diimplementasikan dalam struktur kelas terpisah. Namun, konsepnya tercermin dalam cara kami meminta input data dari pengguna untuk objek baru (seperti Observer atau Participant). Daripada meminta semua input sekaligus, kami meminta atribut satu per satu (username, password, nama, alamat, dll.), memvalidasinya, dan kemudian menggunakan data tersebut untuk "membangun" objek Model langkah demi langkah sebelum menyimpannya ke database. Ini adalah bentuk sederhana dari konstruksi bertahap. </p>

**6. Adapter Pattern (Pola Adaptor)**
<p align='justify'><b>Tujuan</b> : Bertindak sebagai penerjemah yang mengadaptasi interface server untuk klien.<br>
<b>Penerapan dalam PVT :</b> Pola Adapter terwujud dalam peran kelas DatabaseUtil. Kelas ini berfungsi sebagai adaptor antara kode Java kami (yang menggunakan API JDBC standar) dan sistem manajemen database PostgreSQL yang spesifik. Kode Java tidak berinteraksi langsung dengan detail koneksi atau driver PostgreSQL; ia hanya memanggil DatabaseUtil.getConnection(). DatabaseUtil kemudian "mengadaptasi" permintaan ini ke driver JDBC PostgreSQL.</p>

# Arsitektur
Berikut adalah implementasi arsitektur MVC dalam program PVT:
## 1. Model
- **Definisi :** Model merepresentasikan logika bisnis dan data inti dari aplikasi. Bagian ini tidak bergantung pada tampilan atau cara data diinputkan. Model berfokus pada apa yang dilakukan sistem dan data apa yang dikelola.
- **Implementasi dalam PVT :**
  - **Kelas Entitas/Model Data :** Kelas-kelas seperti User, Company, Observer, Participant, Measurement, dan PerformanceCriteria merepresentasikan struktur data dari tabel yang sesuai di database dan berisi atribut serta metode untuk mengelola data tersebut (misalnya, getters dan setters). Kelas-kelas ini adalah representasi murni dari data domain aplikasi.
  - **Logika Bisnis di Model :** Untuk menjaga model tetap "murni" (POJO - Plain Old Java Object), logika bisnis yang kompleks tidak diletakkan langsung di dalamnya. Model hanya menyediakan data dan, jika ada, validasi dasar terkait format data yang melekat pada atributnya.
    
## 2. View
- **Definisi:** View bertanggung jawab untuk menampilkan data dari Model kepada pengguna dan menerima input dari pengguna. Dalam aplikasi GUI, ini adalah antarmuka grafis. Dalam aplikasi konsol, ini adalah representasi tekstual dari antarmuka.
- **Implementasi dalam PVT (Konsol-based View):**
  - **Simulasi Antarmuka Grafis:** Karena PVT sementara adalah aplikasi konsol, elemen View diinterpretasikan sebagai interaksi input/output berbasis teks di konsol. Tidak ada kelas Page, Button, TextField, MessageBox, atau ProgressBar yang terpisah dalam bentuk kode eksplisit, seperti yang akan ada dalam aplikasi GUI.
  - **Pencetakan Output :** Semua pesan, menu, daftar, dan detail data yang ditampilkan kepada pengguna (misalnya, System.out.println untuk menampilkan daftar partisipan atau laporan) adalah bagian dari View.
  - **Pembacaan Input :** Pembacaan input dari pengguna (misalnya, scanner.nextLine() untuk username, password, atau pilihan menu) juga merupakan bagian dari View.
  - **Integrasi View dengan Controller :** Fungsi-fungsi View ini tidak dienkapsulasi dalam kelas View terpisah, melainkan diintegrasikan langsung ke dalam metode-metode di dalam kelas Controller (atau MainApp sebagai entry point UI konsol). Controller secara langsung mencetak informasi dan membaca input, bertindak sebagai mediator antara Model dan View.

## 3. Controller
- **Definisi :** Controller bertindak sebagai penghubung antara Model dan View. Ia menerima input dari pengguna (melalui View), memprosesnya, berinteraksi dengan Model (untuk memanipulasi data atau menerapkan logika bisnis), dan kemudian memperbarui View jika diperlukan.
- **Implementasi dalam PVT :** Ini adalah komponen yang paling aktif dan sentral dalam arsitektur, terutama karena tidak menggunakan lapisan Service terpisah:
  - **MainApp :** Berfungsi sebagai controller utama yang mengelola alur login dan mendelegasikan kontrol ke controller spesifik sesuai dengan peran pengguna yang login.
  - **AuthManager :** Bertindak sebagai controller khusus untuk proses otentikasi pengguna, berinteraksi dengan User Model dan kemudian mengarahkan ke controller dashboard yang sesuai.
  - **SendController :** Mengelola use case "Send Measurement". Ia menerima input dari partisipan, melakukan validasi, dan secara langsung berinteraksi dengan database untuk menyimpan data pengukuran. Logika akses data untuk Participant dan PerformanceCriteria juga diimplementasikan secara internal di sini.
  - **ObserverController :** Mengelola use case "View Observer List". Ia menerima input dari User Manager, melakukan validasi, mengambil dan memanipulasi data Observer dan Company dari database, serta mengelola observerList di memori.
  - **CompanyController :** Mengelola use case "View Company List". Sama seperti ObserverController, ia menangani tampilan, pencarian, penambahan, pengeditan data Company, dan juga dapat memanggil ObserverController untuk melihat observer dari suatu perusahaan.
  - **ParticipantController :** Mengelola use case "View Participant List". Ia bertanggung jawab untuk mengambil, menampilkan, mencari, menambah, dan mengedit data Participant. Ia juga mendelegasikan tugas laporan/analisis ke ReportController.
  - **ReportController :** Mengelola use case "View Report List" dan semua extension laporan. Ia mengambil data Measurement, melakukan pemfilteran, menampilkan laporan detail, dan menghitung analisis baik untuk partisipan spesifik maupun keseluruhan perusahaan.
