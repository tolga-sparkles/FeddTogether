package com.berat.feedtogether

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wp_bot.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        try {
            createDatabase()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Veritabanı oluşturma hatası", e)
        }
    }

    private fun createDatabase() {
        val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath
        
        // Her seferinde veritabanını güncelle
        try {
            context.deleteDatabase(DATABASE_NAME)
            this.readableDatabase
            copyDatabase(dbPath)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Veritabanı güncelleme hatası", e)
        }
    }

    private fun copyDatabase(dbPath: String) {
        try {
            val inputStream: InputStream = context.assets.open(DATABASE_NAME)
            val outputStream: OutputStream = FileOutputStream(dbPath)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            Log.e("DatabaseHelper", "Veritabanı kopyalama hatası", e)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Veritabanı assets'ten kopyalandığı için burada bir şey yapmıyoruz
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Güncelleme gerektiğinde yapılacak işlemler
        if (newVersion > oldVersion) {
            context.deleteDatabase(DATABASE_NAME)
            createDatabase()
        }
    }

    // Kullanıcı bilgilerini doğrulamak için fonksiyon
    fun checkUser(kullaniciAdi: String, sifre: String): String? {
        val db = this.readableDatabase
        var kullaniciAdiSoyadi: String? = null

        try {
            val cursor = db.rawQuery(
                "SELECT u.name FROM users u INNER JOIN accounts a ON u.id = a.user_id WHERE a.username = ? AND a.password = ?",
                arrayOf(kullaniciAdi, sifre)
            )
            if (cursor.moveToFirst()) {
                kullaniciAdiSoyadi = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Kullanıcı kontrolü hatası", e)
        }
        
        db.close()
        return kullaniciAdiSoyadi
    }

    fun getUserDetails(username: String): Map<String, Any>? {
        val db = this.readableDatabase
        val result = mutableMapOf<String, Any>()
        
        try {
            val cursor = db.rawQuery(
                "SELECT u.name, u.surname, ud.* FROM users u INNER JOIN accounts a ON u.id = a.user_id INNER JOIN userDetail ud ON u.id = ud.user_id WHERE a.username = ?",
                arrayOf(username)
            )
            
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"))
                result["name"] = "$name $surname"
                result["gender"] = cursor.getString(cursor.getColumnIndexOrThrow("gender"))
                result["age"] = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
                result["current_weight"] = cursor.getFloat(cursor.getColumnIndexOrThrow("current_weight"))
                result["target_weight"] = cursor.getFloat(cursor.getColumnIndexOrThrow("target_weight"))
                result["height"] = cursor.getInt(cursor.getColumnIndexOrThrow("height"))
                result["daily_water"] = cursor.getInt(cursor.getColumnIndexOrThrow("daily_water"))
                result["daily_steps"] = cursor.getInt(cursor.getColumnIndexOrThrow("daily_steps"))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Kullanıcı detayları alma hatası", e)
        }
        
        db.close()
        return if (result.isNotEmpty()) result else null
    }

    fun getTopTenByBMI(): List<Map<String, Any>> {
        val db = this.readableDatabase
        val result = mutableListOf<Map<String, Any>>()
        
        try {
            val cursor = db.rawQuery("""
                SELECT 
                    u.name, 
                    u.surname,
                    ud.current_weight,
                    ud.height,
                    (ud.current_weight / ((ud.height / 100.0) * (ud.height / 100.0))) as bmi
                FROM users u 
                INNER JOIN userDetail ud ON u.id = ud.user_id 
                ORDER BY ABS(bmi - 22) ASC
                LIMIT 10
            """, null)
            
            while (cursor.moveToNext()) {
                val userMap = mutableMapOf<String, Any>()
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"))
                userMap["name"] = "$name $surname"
                userMap["bmi"] = cursor.getDouble(cursor.getColumnIndexOrThrow("bmi"))
                result.add(userMap)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Top 10 kullanıcı alma hatası", e)
        }
        
        db.close()
        return result
    }

    // Günlük yemek menüsünü getiren fonksiyon
    fun getDailyMenu(dayOfMonth: Int): Map<String, Any>? {
        val db = this.readableDatabase
        val result = mutableMapOf<String, Any>()
        
        Log.d("DatabaseHelper", "getDailyMenu çağrıldı, gün: $dayOfMonth")
        
        try {
            // Önce tablonun var olup olmadığını kontrol et
            val tableCheckCursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='yemek_menusu'",
                null
            )
            
            if (!tableCheckCursor.moveToFirst()) {
                Log.e("DatabaseHelper", "yemek_menusu tablosu bulunamadı!")
                tableCheckCursor.close()
                db.close()
                return null
            }
            tableCheckCursor.close()
            
            // Tablo yapısını kontrol et
            val structureCursor = db.rawQuery("PRAGMA table_info(yemek_menusu)", null)
            Log.d("DatabaseHelper", "yemek_menusu tablosu kolonları:")
            while (structureCursor.moveToNext()) {
                val columnName = structureCursor.getString(structureCursor.getColumnIndexOrThrow("name"))
                val columnType = structureCursor.getString(structureCursor.getColumnIndexOrThrow("type"))
                Log.d("DatabaseHelper", "Kolon: $columnName, Tip: $columnType")
            }
            structureCursor.close()
            
            // Veriyi çek
            val cursor = db.rawQuery(
                "SELECT * FROM yemek_menusu WHERE id = ?",
                arrayOf(dayOfMonth.toString())
            )
            
            Log.d("DatabaseHelper", "Sorgu sonucu satır sayısı: ${cursor.count}")
            
            if (cursor.moveToFirst()) {
                Log.d("DatabaseHelper", "Veri bulundu, kolonları okumaya başlıyorum")
                
                try {
                    result["anaYemek"] = cursor.getString(cursor.getColumnIndexOrThrow("AnaYemek"))
                    Log.d("DatabaseHelper", "AnaYemek: ${result["anaYemek"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "AnaYemek kolonu okunamadı", e)
                }
                
                try {
                    result["aperitif"] = cursor.getString(cursor.getColumnIndexOrThrow("Aperitif"))
                    Log.d("DatabaseHelper", "Aperitif: ${result["aperitif"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "Aperitif kolonu okunamadı", e)
                }
                
                try {
                    result["salataCorba"] = cursor.getString(cursor.getColumnIndexOrThrow("SalataCorba"))
                    Log.d("DatabaseHelper", "SalataCorba: ${result["salataCorba"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "SalataCorba kolonu okunamadı", e)
                }
                
                try {
                    result["tatliMeyve"] = cursor.getString(cursor.getColumnIndexOrThrow("TatliMeyve"))
                    Log.d("DatabaseHelper", "TatliMeyve: ${result["tatliMeyve"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "TatliMeyve kolonu okunamadı", e)
                }
                
                try {
                    result["ay_kcal"] = cursor.getInt(cursor.getColumnIndexOrThrow("ay_kcal"))
                    Log.d("DatabaseHelper", "ay_kcal: ${result["ay_kcal"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "ay_kcal kolonu okunamadı", e)
                }
                
                try {
                    result["a_kcal"] = cursor.getInt(cursor.getColumnIndexOrThrow("a_kcal"))
                    Log.d("DatabaseHelper", "a_kcal: ${result["a_kcal"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "a_kcal kolonu okunamadı", e)
                }
                
                try {
                    result["sc_kcal"] = cursor.getInt(cursor.getColumnIndexOrThrow("sc_kcal"))
                    Log.d("DatabaseHelper", "sc_kcal: ${result["sc_kcal"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "sc_kcal kolonu okunamadı", e)
                }
                
                try {
                    result["tm_kcal"] = cursor.getInt(cursor.getColumnIndexOrThrow("tm_kcal"))
                    Log.d("DatabaseHelper", "tm_kcal: ${result["tm_kcal"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "tm_kcal kolonu okunamadı", e)
                }
                
                try {
                    result["toplam_kcal"] = cursor.getInt(cursor.getColumnIndexOrThrow("toplam_kcal"))
                    Log.d("DatabaseHelper", "toplam_kcal: ${result["toplam_kcal"]}")
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "toplam_kcal kolonu okunamadı", e)
                }
            } else {
                Log.e("DatabaseHelper", "ID $dayOfMonth için veri bulunamadı")
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Günlük menü alma hatası", e)
        }
        
        Log.d("DatabaseHelper", "Sonuç map boyutu: ${result.size}")
        db.close()
        return if (result.isNotEmpty()) result else null
    }

    // Kullanıcının geçmiş ölçümlerini getiren fonksiyon
    fun getMeasurementHistory(userId: Int): List<Map<String, Any>> {
        val db = this.readableDatabase
        val result = mutableListOf<Map<String, Any>>()
        
        try {
            val cursor = db.rawQuery(
                "SELECT created_at, weight FROM measurements WHERE user_id = ? ORDER BY created_at DESC",
                arrayOf(userId.toString())
            )
            
            while (cursor.moveToNext()) {
                val measurementMap = mutableMapOf<String, Any>()
                measurementMap["created_at"] = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                measurementMap["weight"] = cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))
                result.add(measurementMap)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Geçmiş ölçümler alma hatası", e)
        }
        
        db.close()
        return result
    }

    // Kullanıcı ID'sini username'e göre getiren fonksiyon
    fun getUserIdByUsername(username: String): Int? {
        val db = this.readableDatabase
        var userId: Int? = null
        
        try {
            val cursor = db.rawQuery(
                "SELECT a.user_id FROM accounts a WHERE a.username = ?",
                arrayOf(username)
            )
            
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Kullanıcı ID alma hatası", e)
        }
        
        db.close()
        return userId
    }
}