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
7. Coba jalannkan program di terminal Virtual Studio Code
   ```
   javac MainApp.java
   java -cp ".;lib/postgresql-42.7.6.jar" MainApp.java
   ```
