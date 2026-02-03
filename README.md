# 🌧️ Selangor Rainfall Monitoring System

Sistem pemantauan taburan hujan bagi daerah-daerah di Selangor menggunakan **Spring Boot 3**, **MySQL**, dan **Open-Meteo API**. Sistem ini berupaya menarik data sejarah hujan dan melakukan pengiraan *Rolling Sum* untuk analisis risiko banjir atau tepu tanah.

## 🚀 Ciri-Ciri Utama
* **Automated Data Fetching**: Menarik data hujan 31 hari ke belakang secara automatik dari API Open-Meteo.
* **Rolling Sum Analysis**: Mengira jumlah hujan bagi 1-hari (semasa), 3-hari (tepu tanah), dan 30-hari (analisis bulanan).
* **Multi-District Support**: Menyokong penyinkronan data untuk semua 9 daerah utama di Selangor secara serentak.
* **Historical Date Picker**: Membolehkan pengguna menyemak data hujan pada mana-mana tarikh yang lalu.
* **Duplicate Prevention**: Menggunakan `UNIQUE KEY` dan semakan logik Java untuk mengelakkan pertindihan data dalam MySQL.

## 🛠️ Teknologi Yang Digunakan
* **Backend**: Java 21, Spring Boot 3.5.x
* **Database**: MySQL 8.0 (XAMPP)
* **ORM**: Spring Data JPA (Hibernate)
* **Frontend**: HTML5, CSS3, Vanilla JavaScript (Fetch API)
* **External API**: [Open-Meteo Weather API](https://open-meteo.com/)

## 📂 Struktur Projek
```text
src/main/java/com/amar/rainfall/
├── controller/
│   └── RainController.java    # Logik API & Pengiraan Rolling
├── dao/
│   ├── RainData.java          # Entity Database
│   └── RainRepository.java    # Interface Query MySQL
├── dto/
│   └── RainSummaryDTO.java    # Objek JSON untuk Frontend
└── RainfallSystemApplication.java

src/main/resources/
├── static/
│   └── index.html             # Dashboard Frontend
└── application.properties     # Konfigurasi MySQL