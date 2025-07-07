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
