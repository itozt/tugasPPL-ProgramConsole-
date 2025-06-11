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
