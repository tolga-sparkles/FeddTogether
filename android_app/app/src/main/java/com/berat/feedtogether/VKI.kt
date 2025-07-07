package com.berat.feedtogether

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.res.ResourcesCompat.getColorStateList
import androidx.fragment.app.Fragment
import org.w3c.dom.Text
import java.io.FileOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VKI.newInstance] factory method to
 * create an instance of this fragment.
 */
class VKI : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        dbHelper = DatabaseHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_v_k_i, container, false)

        val textV33: TextView = view.findViewById(R.id.textView33)

        val vkiBilgiMetni = """
    ❓ VÜCUT KİTLE İNDEKSİ (VKİ) NEDİR?
    Vücut Kitle İndeksi (VKİ), bir kişinin kilosunun boyuna göre 
    uygun olup olmadığını değerlendiren basit bir hesaplama yöntemidir.
    
    
    ➗ NASIL HESAPLANIR?
    VKİ = Ağırlık (kg) / (Boy (m) × Boy (m))
    
    Örnek: Boyu 1.75 m, kilosu 70 kg olan biri için:
    70 / (1.75 × 1.75) = 22.86
    
    
    📊 VKİ DEĞER ARALIKLARI: 
     • 18.5 altı: Zayıf
     • 18.5 - 24.9: Normal kilolu
     • 25.0 - 29.9: Fazla kilolu
     • 30.0 ve üzeri: Obez
    
    
    ❗ ÖNEMLİ UYARI:
    VKİ, kas kütlesi, yağ dağılımı, kemik yoğunluğu gibi faktörleri 
    dikkate almaz. Profesyonel sporcular ve hamileler için doğru 
    sonuç vermeyebilir. Kesin teşhis için doktorunuza danışınız.
    
    
    🍅 SAĞLIKLI YAŞAM ÖNERİLERİ:
     • Düzenli egzersiz yapın
     • Dengeli beslenin
     • Yeterli su tüketin
     • Düzenli uyuyun
     • Stres yönetimine özen gösterin
""".trimIndent()

// TextView'e atama örneği
        textV33.text = vkiBilgiMetni

        // Kullanıcı bilgilerini al ve VKİ hesapla
        arguments?.getString("KULLANICI_ADI")?.let { username ->
            val userDetails = dbHelper.getUserDetails(username)
            userDetails?.let { details ->
                val weight = (details["current_weight"] as Float).toDouble()
                val height = (details["height"] as Int).toDouble() / 100 // cm'yi metreye çevir
                val bmi = weight / (height * height)
                
                // BMI değerini göster ve renkleri ayarla
                val bmiTextView = view.findViewById<TextView>(R.id.bmi_value)
                val progressBar = view.findViewById<android.widget.ProgressBar>(R.id.progressBar)
                
                bmiTextView.text = String.format("%.1f", bmi)
                
                // BMI değerine göre renk ve progress ayarla
                when {
                    bmi < 18.5 -> {
                        val color = Color.parseColor("#FFA500") // Turuncu
                        bmiTextView.setTextColor(color)
                        progressBar.progress = 25
                        progressBar.progressTintList = ColorStateList.valueOf(color)
                    }
                    bmi < 25 -> {
                        val color = Color.parseColor("#4CAF50") // Yeşil
                        bmiTextView.setTextColor(color)
                        progressBar.progress = 50
                        progressBar.progressTintList = ColorStateList.valueOf(color)
                    }
                    bmi < 30 -> {
                        val color = Color.parseColor("#FF0000") // Kırmızı
                        bmiTextView.setTextColor(color)
                        progressBar.progress = 75
                        progressBar.progressTintList = ColorStateList.valueOf(color)
                    }
                    else -> {
                        val color = Color.parseColor("#800000") // Bordo
                        bmiTextView.setTextColor(color)
                        progressBar.progress = 100
                        progressBar.progressTintList = ColorStateList.valueOf(color)
                    }
                }
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VKI.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VKI().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}