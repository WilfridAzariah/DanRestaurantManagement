Readme
RESTAURANT QUEUE MANAGEMENT SYSTEM
oleh kelompok DAN :
1. 13217006 Johannes Felix Rimbun
2. 13217018 Ryan Dharma Chandra
3. 13217071 Wilfrid Azariah

Video demonstrasi terdapat pada demoRestoMgmtDAN.mp4
Laporan terdapat pada file laporanRestoMgmtDAN.pdf

Terdapat tiga buah program yang harus dijalankan, masing-masing terdapat pada directory /server/, /restaurant_client/, dan /Android/. Selain itu, databse backup konfigurasi mySQL juga terdapat pada directory /SQLdatabase/.

Untuk menyiapkan server, dilakukan beberapa langkah sebagai berikut.
1. Pastikan bahwa pada bagian “server preparation”, IP Address dan Port yang tertulis ada sama dengan IP Address pada komputer untuk menjalankan program server ini. Misal, “192.168.100.8”.
2. Tetapkan nilai port. Pada proyek ini, digunakan port=3456.
3. Pada bagian “Constant untuk mySQL”, pastikan bahwa IP Address yang tertulis adalah sama dengan IP Address pada komputer yang menjalankan database mySQL tersebut. Misal, “192.168.100.6”.
4. Pastikan bahwa certificate untuk verifikasi SSL, yaitu “server.key”, “server.pem”, dan “client.pem” telah berada pada satu directory dengan program server.py ini.
5. Pastikan bahwa database mySQL telah dijalankan terlebih dahulu sebelum menjalankan program server ini.
6. Untuk menjalankan program server ini, masukkan perintah pada command line : server.py.

Untuk membuat dan menjalankan database mySQL, dilakukan beberapa prosedur sebagai berikut.
1. Pastikan Mysql telah terinstall dengan baik
2. Load database yang sudah ada sesuai pada directory. Apabila database telah berhasil dimuat, instruksi nomor 7 dan 10-12 tidak perlu dijalankan. Sedangkan, apabila database gagal dimuat, ikut seluruh instruksi.
2. Jalankan [mysqld] pada cmd
3. Login ke mysql dengan [mysql -u root -p]
4. Masukan password
5. Buat user untuk server dengan [CREATE USER 'root'@'192.168.100.8' IDENTIFIED BY 'root';]
6. Buat database dengan [CREATE DATABASE restaurant;]
7. Grant akses server ke database dengan [GRANT ALL PRIVILEGES ON restaurant.* TO 'root'@'localhost';]
8. Jalankan [Flush privileges;]
9. Jalankan [use restaurant;] untuk persiapan pembuatan tabel pada database
10. Jalankan [create table if not exists ordertable(orderno INT(10) NOT NULL  PRIMARY KEY AUTO_INCREMENT, paket VARCHAR(30), quantity VARCHAR(30), status VARCHAR(10), doo date, totalHarga INT(30), username  VARCHAR(30), notes VARCHAR(30))AUTO_INCREMENT=1;]
11. Jalankan [create table if not exists usertable(Id INT(10) NOT NULL  PRIMARY KEY AUTO_INCREMENT, username  VARCHAR(30), password VARCHAR(65), saldo INT(30))AUTO_INCREMENT=1;]

Untuk menyiapkan client pada kasir, dilakukan beberapa langkah sebagai berikut.
1. Pastikan bahwa pada bagian “Konfigurasi Socket”, IP Address dan Port yang tertulis ada sama dengan IP Address pada komputer untuk menjalankan program server. Misal, “192.168.100.8”.
2. Tetapkan nilai port. Pada proyek ini, digunakan port=3456.
3. Pastikan bahwa certificate untuk verifikasi SSL, yaitu “client.key”, “client.pem”, dan “server.pem” telah berada pada satu directory dengan program login.py dan main.py.
4. Pastikan program server telah dijalankan terlebih dahulu sebelum menjalankan program client ini.
5. Untuk menjalankan program client ini, masukkan perintah pada command line : restaurant_client.py.
6. Untuk menyiapkan client pada Android, dilakuka`n beberapa langkah sebagai berikut.
7. Buka project Android menggunakan Android Studio.
8. Pada browser, terdapat file Constant.java. Pada file tersebut, pastikan bahwa IP Address dan Port server sesuai dengan IP Address yang tertulis pada konstanta server_addr dan server_port. Dalam hal ini, digunakan server_addr=”192.168.100.8” dan server_port=3456.
9. Untuk menjalankan aplikasi ini, dapat digunakan menu Run → Run App. Selain itu, aplikasi ini juga dapat diekspor menjadi APK dengan Build → Build Bundle(s)/Apk(s) → Build Apk(s)
