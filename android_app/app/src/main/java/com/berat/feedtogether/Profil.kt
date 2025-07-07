package com.berat.feedtogether

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profil.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profil : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_profil, container, false)
        
        // Çıkış butonunu ayarla
        val cikisBtn = view.findViewById<ImageView>(R.id.cikis_btn)
        cikisBtn.setOnClickListener {
            showConfirmationDialog(requireContext())
        }
        
        // Kullanıcı bilgilerini al ve göster
        arguments?.getString("KULLANICI_ADI")?.let { username ->
            val userDetails = dbHelper.getUserDetails(username)
            userDetails?.let { details ->
                // Profil adını göster - Tam ad ve soyad
                view.findViewById<TextView>(R.id.profile_name).text = details["name"].toString()
                
                // Diğer detayları göster
                view.findViewById<TextView>(R.id.cinsiyet_value).text = details["gender"].toString()
                view.findViewById<TextView>(R.id.yas_value).text = details["age"].toString()
                view.findViewById<TextView>(R.id.agirlik_value).text = details["current_weight"].toString()
                view.findViewById<TextView>(R.id.hedef_value).text = details["target_weight"].toString()
                view.findViewById<TextView>(R.id.boy_value).text = details["height"].toString()

                // VKİ hesapla ve göster
                val weight = (details["current_weight"] as Float).toDouble()
                val height = (details["height"] as Int).toDouble() / 100
                val bmi = weight / (height * height)
                view.findViewById<TextView>(R.id.vki_value).text = String.format(Locale.getDefault(), "%.1f", bmi)
                
                // Geçmiş ölçümleri göster
                val userId = dbHelper.getUserIdByUsername(username)
                userId?.let { id ->
                    showMeasurementHistory(view, id)
                }
            }
        }

        return view
    }

    private fun showMeasurementHistory(view: View, userId: Int) {
        val gecmisOlcumler = view.findViewById<LinearLayout>(R.id.gecmis_olcumler)
        val measurements = dbHelper.getMeasurementHistory(userId)
        
        // Türkçe ay isimleri
        val turkishMonths = arrayOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )
        
        measurements.forEach { measurement ->
            val createdAt = measurement["created_at"] as String
            val weight = measurement["weight"] as Float
            
            try {
                // Tarih formatını parse et (örnek: "2024-01-15 10:30:00")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(createdAt)
                
                date?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it
                    
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = turkishMonths[calendar.get(Calendar.MONTH)]
                    val year = calendar.get(Calendar.YEAR)
                    
                    // Her ölçüm için ayrı bir LinearLayout oluştur
                    val rowLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(0, 8, 0, 8)
                    }
                    
                    // Tarih TextView'ı (sola yaslı)
                    val dateTextView = TextView(requireContext()).apply {
                        text = "$day $month $year"
                        textSize = 16f
                        setTextColor(resources.getColor(android.R.color.black, null))
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f // Ağırlık 1.0 - sola yaslı
                        )
                    }
                    
                    // Ölçüm değeri TextView'ı (sağa yaslı)
                    val weightTextView = TextView(requireContext()).apply {
                        text = "${String.format(Locale.getDefault(), "%.1f", weight)} kg"
                        textSize = 16f
                        setTextColor(resources.getColor(android.R.color.black, null))
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    
                    rowLayout.addView(dateTextView)
                    rowLayout.addView(weightTextView)
                    gecmisOlcumler.addView(rowLayout)
                }
            } catch (e: Exception) {
                // Tarih parse edilemezse, orijinal string'i göster
                val rowLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, 8, 0, 8)
                }
                
                val dateTextView = TextView(requireContext()).apply {
                    text = createdAt
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f // Ağırlık 1.0 - sola yaslı
                    )
                }
                
                val weightTextView = TextView(requireContext()).apply {
                    text = "${String.format(Locale.getDefault(), "%.1f", weight)} kg"
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                
                rowLayout.addView(dateTextView)
                rowLayout.addView(weightTextView)
                gecmisOlcumler.addView(rowLayout)
            }
        }
    }

    private fun showConfirmationDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Çıkış")
            .setMessage("Çıkış yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { dialog, which ->
                performLogout()
            }
            .setNegativeButton("Hayır") { dialog, which ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun performLogout() {
        // SharedPreferences'tan kullanıcı bilgilerini temizle
        val sharedPreferences = requireActivity().getSharedPreferences("FeedTogetherPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // MainActivity'ye yönlendir
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = Profil()
    }
}