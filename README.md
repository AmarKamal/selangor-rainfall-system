# 🌧️ Selangor Rainfall Monitoring System

Sistem pemantauan taburan hujan bagi daerah-daerah di Selangor menggunakan **Spring Boot 3**, **MySQL (XAMPP)**, dan **Open-Meteo API**. Sistem ini berupaya menarik data sejarah hujan secara automatik dan melakukan pengiraan *Rolling Sum* untuk analisis risiko banjir atau tahap ketepuan tanah.

---

## 🚀 Ciri-Ciri Utama

- **Automated Data Fetching**: Menarik data hujan 31 hari ke belakang secara automatik dari API Open-Meteo
- **Rolling Sum Analysis**: Mengira jumlah hujan bagi 1-hari (semasa), 3-hari (tepu tanah), dan 30-hari (analisis bulanan)
- **Multi-District Support**: Menyokong penyinkronan data untuk semua 9 daerah utama di Selangor secara serentak
- **Historical Date Picker**: Membolehkan pengguna menyemak data hujan pada mana-mana tarikh yang lalu melalui antaramuka web
- **Duplicate Prevention**: Menggunakan kekunci unik (`UNIQUE KEY`) dalam MySQL untuk memastikan data tarikh yang sama tidak bertindih

---

## 🛠️ Teknologi Yang Digunakan

- **Backend**: Java 21, Spring Boot 3.x
- **Database**: MySQL 8.0 (melalui XAMPP)
- **ORM**: Spring Data JPA (Hibernate)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript (Fetch API)
- **External API**: [Open-Meteo Weather API](https://open-meteo.com/)

---

## 📂 Struktur Projek
```
src/main/java/com/amar/rainfall/
├── controller/
│   └── RainController.java       # Logik API, Sinkronisasi & Pengiraan
├── dao/
│   ├── RainData.java             # Entity Database (Table Mapping)
│   └── RainRepository.java       # Interface Query MySQL
├── dto/
│   └── RainSummaryDTO.java       # Objek Data untuk paparan Frontend
└── RainfallSystemApplication.java

src/main/resources/
├── static/
│   └── index.html                # Dashboard UI (Frontend)
└── application.properties        # Konfigurasi Database & Spring
```

---

## ⚙️ Panduan Penyediaan (Setup)

### 1. Konfigurasi MySQL (XAMPP)

Sistem ini memerlukan pangkalan data MySQL yang aktif untuk menyimpan sejarah hujan.

1. **Jalankan XAMPP**: Buka XAMPP Control Panel dan tekan butang **Start** pada modul **Apache** dan **MySQL**

2. **Cipta Database**:
   - Buka pelayar (browser) dan pergi ke `http://localhost/phpmyadmin`
   - Klik tab **New**
   - Nama Database: `rainfall_db`
   - Klik **Create**

3. **Cipta Table (Manual)**: 
   - Klik pada database `rainfall_db`
   - Pilih tab **SQL**
   - Jalankan kod berikut:
```sql
CREATE TABLE rain_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_name VARCHAR(255),
    record_date DATE,
    precipitation DOUBLE,
    UNIQUE KEY unique_loc_date (location_name, record_date)
);
```

### 2. Konfigurasi Spring Boot

Pastikan fail `src/main/resources/application.properties` anda mengikut tetapan berikut:
```properties
spring.application.name=Malaysia Rainfall System

# MySQL Configuration (XAMPP Default)
spring.datasource.url=jdbc:mysql://localhost:3306/rainfall_db?useSSL=false&serverTimezone=Asia/Kuala_Lumpur
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🏃 Cara Menjalankan Aplikasi

1. **Clone/Download**: Muat turun kod sumber ke komputer anda
2. **Import IDE**: Buka projek menggunakan IntelliJ IDEA, Eclipse, atau VS Code
3. **Run**: Jalankan fail utama `RainfallSystemApplication.java`
4. **Akses UI**: Buka pelayar dan taip: `http://localhost:8080/index.html`

---

## 💻 Cara Penggunaan

1. **Pilih Tarikh**: Gunakan date picker untuk memilih tarikh semakan (Default: Hari ini)
2. **Sync Data**: Klik butang **Sync All Districts** untuk menarik data bagi 9 daerah di Selangor
3. **Lihat Hasil**: Table akan memaparkan jumlah hujan 1-hari, 3-hari, dan 30-hari secara automatik

---

## 📊 Indikator Analisis

| Indikator | Penerangan |
|-----------|------------|
| **1-Day** | Jumlah hujan hari ini (mm) |
| **3-Day Rolling** | Digunakan untuk memantau risiko banjir kilat. Jika nilai tinggi (cth: >60mm), risiko banjir kilat meningkat |
| **30-Day Rolling** | Memantau taburan hujan bulanan untuk tujuan analisis cuaca jangka panjang |

---

## 📝 Nota

Projek ini dibangunkan untuk tujuan pembelajaran dan pemantauan cuaca domestik.

---
