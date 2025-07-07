# FeedTogether

Beslenme danışmanlarının danışanlarına çevrimiçi destek verebilmeleri için geliştirilmiş; mobil uygulama, sunucu, SMS/Whatsapp bildirimleri ve otomasyon altyapısını bir araya getiren hibrit bir platform.

---

## 📋 İçindekiler

- [Proje Hakkında](#-proje-hakkında)  
- [Özellikler](#-özellikler)  
- [Mimari](#-mimari)  
- [Teknolojiler](#%EF%B8%8F-teknolojiler)  
- [Kurulum & Çalıştırma](#-kurulum--çalıştırma)  
- [Diyetisyen Komutları](#-diyetisyen-komutları)  
- [Gelecek Planları](#-gelecek-planları)  
- [Katkıda Bulunanlar](#-katk%C4%B1da-bulunanlar)  
- [Lisans](#-lisans)

---

## ℹ️ Proje Hakkında

FeedTogether, beslenme alanındaki profesyonellerin (diyetisyenlerin) danışan takibini kolaylaştırmak üzere geliştirilmiş bütünleşik bir sistemdir. 

- Danışan, **Kotlin** tabanlı mobil uygulama üzerinden:
  - Günlük kalori alımını takip edebilir,
  - Vücut kitle indeksi (BMI) hesaplayabilir,
  - Diyetisyen tarafından atanan haftalık/aylık beslenme planlarına erişebilir,
  - Randevu talebi oluşturabilir.  

- Sistem, **Raspberry Pi 5** üzerinde kesintisiz çalışan arka uç sunucusu ile:
  - Tüm danışan verilerini SQL veri tabanında saklar,
  - Otomatik bildirim, SMS/WhatsApp ve arama planlaması yapar,
  - Adım sayar (pedometre) ve yapay zeka entegrasyonlarını destekler.

- Diyetisyen paneli, basit komutlarla tüm danışanların listesini ve bireysel profillerini sorgular; haftalık özel yemek listelerini tek komutla danışana gönderir.

---

## 🚀 Özellikler

- **Gerçek Zamanlı Bildirimler**  
  Motivasyon mesajları, su hatırlatmaları ve günlük özet bildirimleri  
- **Otomasyon & Planlama**  
  Telefon aramaları, temel seviye randevu planlaması, otomatik SMS/WhatsApp  
- **Mobil Uygulama**  
  Kotlin + Android: kullanıcı dostu arayüz, biyometrik giriş, veri görselleştirme  
- **Adım Sayar Entegrasyonu**  
  Kullanıcının hareket verilerini toplayarak kalori yakımını izleme  
- **Yapay Zeka Destekli Öneriler**  
  Beslenme analizi ve kişiye özel öneri modülü  
- **Kolay Diyetisyen Komutları**  
  - `!danışan_liste` → Tüm danışanların isimleri  
  - `!danışan_üye <isim>` → Belirli danışanın profili ve portfolyosu  
  - `!haftalık_liste` → Haftalık yemek planı (kalori, miktar)  

---

## 🏗️ Mimari

![FeedTogether Mimari Diyagramı](feedTogether.png)

1. **Raspberry Pi 5**  
   - Sürekli çalışan sunucu  
   - Docker, Node.js, Python, SQL  
2. **Veri Tabanı**  
   - Danışan bilgileri, bildirim geçmişi, adım verileri  
3. **API Katmanı**  
   - Mobil uygulama ve otomasyon servisi için RESTful uç noktalar  
4. **Mobil Uygulama (Kotlin/Android)**  
   - Kullanıcı arayüzü ve offline senkronizasyon  
5. **Otomasyon ve Bildirim Modülü**  
   - SMS/WhatsApp, mesajlaşma botları, arama planlayıcı  
6. **Diyetisyen CLI**  
   - Basit komutlarla danışan yönetimi ve raporlama  

---

## 🛠️ Teknolojiler

| Katman               | Teknoloji           |
| -------------------- | ------------------- |
| Sunucu OS            | Raspberry Pi OS     |
| Konteynerizasyon     | Docker              |
| Backend              | Node.js, Python     |
| Veri Tabanı          | PostgreSQL / MySQL  |
| Mobil Uygulama       | Kotlin, Android SDK |
| Bildirim Servisleri  | Twilio, WhatsApp API|
| Yapay Zeka            | TensorFlow / PyTorch (opsiyonel) |

---

## ⚙️ Kurulum & Çalıştırma

1. Repoyu klonlayın:  
   ```bash
   git clone https://github.com/kullanici_adiniz/FeedTogether.git
   cd FeedTogether

# FeedTogether

FeedTogether is a hybrid platform that brings together a mobile app, server backend, SMS/WhatsApp notifications and automation infrastructure to enable dietitians to provide online support to their clients.

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)  
- [Features](#-features)  
- [Architecture](#-architecture)  
- [Technologies](#-technologies)  
- [Installation & Running](#-installation--running)  
- [Dietitian Commands](#-dietitian-commands)  
- [Roadmap](#-roadmap)  
- [Contributors](#-contributors)  
- [License](#-license)

---

## ℹ️ About the Project

FeedTogether is an integrated system designed to streamline client tracking for nutrition professionals (dietitians).

- **Client app** (Kotlin-based mobile):
  - Track daily calorie intake  
  - Calculate Body Mass Index (BMI)  
  - View weekly/monthly meal plans assigned by the dietitian  
  - Request appointments  

- **Backend** (Raspberry Pi 5):
  - Stores all client data in an SQL database  
  - Schedules automated notifications, SMS/WhatsApp messages, and calls  
  - Supports pedometer integration and AI-powered recommendations  

- **Dietitian panel**:
  - Query the full client list or individual profiles with simple commands  
  - Send weekly customized meal plans to clients with a single command  

---

## 🚀 Features

- **Real-Time Notifications**  
  Motivation messages, hydration reminders, and daily summaries  
- **Automation & Scheduling**  
  Phone calls, appointment planning, automated SMS/WhatsApp  
- **Mobile Application**  
  Kotlin + Android: user-friendly UI, biometric login, data visualization  
- **Pedometer Integration**  
  Collects activity data to estimate calories burned  
- **AI-Powered Recommendations**  
  Nutrition analysis and personalized suggestions  
- **Easy Dietitian Commands**  
  - `!client_list` → List all clients  
  - `!client_profile <name>` → Show a specific client’s profile  
  - `!weekly_plan` → Send weekly meal plan (calories, portions)  

---

## 🏗️ Architecture

![FeedTogether Architecture Diagram](feedTogether.png)

1. **Raspberry Pi 5**  
   - Always-on server  
   - Docker, Node.js, Python, SQL  
2. **Database**  
   - Client info, notification history, activity data  
3. **API Layer**  
   - RESTful endpoints for the mobile app and automation services  
4. **Mobile App (Kotlin/Android)**  
   - User interface and offline sync  
5. **Automation & Notification Module**  
   - SMS/WhatsApp bots, call scheduler  
6. **Dietitian CLI**  
   - Simple commands for client management and reporting  

---

## 🛠️ Technologies

| Layer                 | Technology                       |
| --------------------- | -------------------------------- |
| Operating System      | Raspberry Pi OS                  |
| Containerization      | Docker                           |
| Backend               | Node.js, Python                  |
| Database              | PostgreSQL / MySQL               |
| Mobile App            | Kotlin, Android SDK              |
| Notification Services | Twilio, WhatsApp API             |
| AI/ML                 | TensorFlow / PyTorch (optional)  |

---

## ⚙️ Installation & Running

1. Clone the repository:  
   ```bash
   git clone https://github.com/your_username/FeedTogether.git
   cd FeedTogether

