package com.berat.feedtogether

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var kullaniciAdiEditText: EditText
    private lateinit var sifreEditText: EditText
    private lateinit var girisyapBtn: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.ft_beyaz)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        try {
            dbHelper = DatabaseHelper(this)
            sharedPreferences = getSharedPreferences("FeedTogetherPrefs", Context.MODE_PRIVATE)

            // Son giriş yapan kullanıcıyı kontrol et
            val sonKullaniciAdi = sharedPreferences.getString("son_kullanici_adi", null)
            val sonKullaniciSifre = sharedPreferences.getString("son_kullanici_sifre", null)

            if (sonKullaniciAdi != null && sonKullaniciSifre != null) {
                // Otomatik giriş yap
                val kullaniciAdiSoyadi = dbHelper.checkUser(sonKullaniciAdi, sonKullaniciSifre)
                if (kullaniciAdiSoyadi != null) {
                    girisYap(sonKullaniciAdi, kullaniciAdiSoyadi)
                    return
                }
            }

            setupUI()
        } catch (e: Exception) {
            Log.e("MainActivity", "Başlatma hatası", e)
            Toast.makeText(this, "Uygulama başlatılırken bir hata oluştu", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUI() {
        kullaniciAdiEditText = findViewById(R.id.kullanici_text)
        sifreEditText = findViewById(R.id.password_text)
        girisyapBtn = findViewById(R.id.girisyap_btn)

        girisyapBtn.setOnClickListener {
            val kullaniciAdi = kullaniciAdiEditText.text.toString().trim()
            val sifre = sifreEditText.text.toString().trim()

            if (kullaniciAdi.isNotEmpty() && sifre.isNotEmpty()) {
                try {
                    val kullaniciAdiSoyadi = dbHelper.checkUser(kullaniciAdi, sifre)
                    if (kullaniciAdiSoyadi != null) {
                        // Kullanıcı bilgilerini kaydet
                        sharedPreferences.edit().apply {
                            putString("son_kullanici_adi", kullaniciAdi)
                            putString("son_kullanici_sifre", sifre)
                            apply()
                        }
                        
                        girisYap(kullaniciAdi, kullaniciAdiSoyadi)
                    } else {
                        Toast.makeText(this, "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Giriş hatası", e)
                    Toast.makeText(this, "Giriş yapılırken bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        setupSocialMediaButtons()
    }

    private fun setupSocialMediaButtons() {
        //Instagram yönlendirme
        val social_insta: ImageView = findViewById(R.id.insta_icon)
        social_insta.setOnClickListener {
            val username = "diyetisyenhaticenurege"
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val webUri = Uri.parse("http://instagram.com/_u/$username")
                startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        }

        //Whatsapp Yönlendirme
        val social_wp: ImageView = findViewById(R.id.wp_icon)
        social_wp.setOnClickListener {
            val phoneNumber = "905549649358"
            val message = "Merhaba, bu bir test mesajıdır!"
            val uri = Uri.parse(
                "https://api.whatsapp.com/send?phone=$phoneNumber&text=${
                    URLEncoder.encode(
                        message,
                        "UTF-8"
                    )
                }"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val webUri = Uri.parse(
                    "https://web.whatsapp.com/send?phone=$phoneNumber&text=${
                        URLEncoder.encode(
                            message,
                            "UTF-8"
                        )
                    }"
                )
                startActivity(Intent(Intent.ACTION_VIEW, webUri))
            }
        }
    }

    private fun girisYap(kullaniciAdi: String, kullaniciAdiSoyadi: String) {
        val intent = Intent(this, AnaSayfa::class.java)
        intent.putExtra("KULLANICI_ADI", kullaniciAdi)
        intent.putExtra("KULLANICI_ADI_SOYADI", kullaniciAdiSoyadi)
        startActivity(intent)
        finish()
    }
}