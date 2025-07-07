package com.berat.feedtogether

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnaSayfaMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnaSayfaMenu : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var textView18: TextView
    private lateinit var tavsiyeText: TextView
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
        return inflater.inflate(R.layout.fragment_ana_sayfa_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView18 = view.findViewById(R.id.textView18)
        tavsiyeText = view.findViewById(R.id.tavsiye_text)

        arguments?.getString("KULLANICI_ADI")?.let { username ->
            // Kullanıcı adını göster
            arguments?.getString("KULLANICI_ADI_SOYADI")?.let { kullaniciAdiSoyadi ->
                val ad = kullaniciAdiSoyadi.split(" ")[0]
                textView18.text = ad
            }

            // Günlük tavsiyeyi göster
            showDailyAdvice(username)
        }
    }

    private fun showDailyAdvice(username: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("FeedTogetherPrefs", Context.MODE_PRIVATE)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastAdviceDate = sharedPreferences.getString("last_advice_date", "")
        val savedAdvice = sharedPreferences.getString("saved_advice", "")

        if (currentDate == lastAdviceDate && savedAdvice?.isNotEmpty() == true) {
            // Bugün için zaten tavsiye oluşturulmuş, onu göster
            tavsiyeText.text = savedAdvice
        } else {
            // Yeni tavsiye oluştur
            val userDetails = dbHelper.getUserDetails(username)
            userDetails?.let { details ->
                val gender = details["gender"].toString()
                val age = details["age"] as Int
                val currentWeight = details["current_weight"] as Float
                val targetWeight = details["target_weight"] as Float
                val height = details["height"] as Int
                val bmi = currentWeight / ((height / 100f) * (height / 100f))

                val advice = generateAdvice(gender, age, currentWeight, targetWeight, height, bmi)
                
                // Tavsiyeyi kaydet
                sharedPreferences.edit().apply {
                    putString("last_advice_date", currentDate)
                    putString("saved_advice", advice)
                    apply()
                }

                tavsiyeText.text = advice
            }
        }
    }

    private fun generateAdvice(
        gender: String,
        age: Int,
        currentWeight: Float,
        targetWeight: Float,
        height: Int,
        bmi: Float
    ): String {
        val weightDiff = currentWeight - targetWeight
        val weightStatus = when {
            bmi < 18.5 -> "zayıf"
            bmi < 25 -> "normal kilolu"
            bmi < 30 -> "kilolu"
            else -> "obez"
        }

        val sb = StringBuilder()
        
        // Başlık
        sb.append("🌟 Günlük Sağlık Tavsiyeleri 🌟\n\n")

        // Genel Durum
        sb.append("📊 Mevcut Durumunuz:\n")
        sb.append("• VKİ değeriniz: %.1f (%s)\n".format(bmi, weightStatus))
        sb.append("• Hedef kilonuza %.1f kg %s\n\n".format(
            abs(weightDiff),
            if (weightDiff > 0) "vermeniz gerekiyor" else "almanız gerekiyor"
        ))

        // Beslenme Önerileri
        sb.append("🍎 Beslenme Önerileri:\n")
        when {
            weightDiff > 0 -> {
                sb.append("• Günlük kalori alımınızı 500 kcal azaltın\n")
                sb.append("• Öğün atlamayın, 3 ana 2 ara öğün yapın\n")
                sb.append("• Protein ağırlıklı beslenin (yumurta, tavuk, balık)\n")
                sb.append("• Sebze ve meyve tüketimini artırın\n")
                sb.append("• Şekerli ve işlenmiş gıdalardan uzak durun\n")
            }
            weightDiff < 0 -> {
                sb.append("• Günlük kalori alımınızı 300-500 kcal artırın\n")
                sb.append("• Protein ve karbonhidrat dengeli beslenin\n")
                sb.append("• Sağlıklı yağları ihmal etmeyin (zeytinyağı, kuruyemişler)\n")
                sb.append("• Ara öğünlerde smoothie veya protein shake tüketin\n")
            }
            else -> {
                sb.append("• Mevcut beslenme düzeninizi koruyun\n")
                sb.append("• Öğün düzeninizi bozmayın\n")
                sb.append("• Dengeli ve çeşitli beslenmeye devam edin\n")
            }
        }
        sb.append("\n")

        // Egzersiz Önerileri
        sb.append("💪 Egzersiz Önerileri:\n")
        when (age) {
            in 18..30 -> {
                sb.append("• Haftada 4-5 gün egzersiz yapın\n")
                sb.append("• Kardiyo ve kuvvet antrenmanlarını dengeleyin\n")
                sb.append("• HIIT antrenmanları yapabilirsiniz\n")
            }
            in 31..50 -> {
                sb.append("• Haftada 3-4 gün egzersiz yapın\n")
                sb.append("• Orta şiddetli kardiyo tercih edin\n")
                sb.append("• Pilates veya yoga ile esnekliğinizi koruyun\n")
            }
            else -> {
                sb.append("• Haftada 2-3 gün hafif egzersiz yapın\n")
                sb.append("• Yürüyüş ve yüzme gibi düşük etkili aktiviteler seçin\n")
                sb.append("• Esneme hareketlerine önem verin\n")
            }
        }
        sb.append("\n")

        // Günlük İpuçları
        sb.append("💡 Günlük İpuçları:\n")
        sb.append("• Günde en az 2 litre su için\n")
        sb.append("• 7-8 saat uyku uyumaya özen gösterin\n")
        sb.append("• Stres yönetimi için meditasyon yapın\n")
        sb.append("• Düzenli olarak tansiyon ve nabız takibi yapın\n")
        
        // Not
        sb.append("\n\n⚠️ Not: Bu sağlık çizelgesi sadece size özel olarak hazırlanmıştır. Başkalarının kullanması uygun değildir. Sağlığınızla ilgili herhangi bir karar vermeden önce mutlaka bir sağlık profesyoneline danışınız.")

        return sb.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnaSayfaMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnaSayfaMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}