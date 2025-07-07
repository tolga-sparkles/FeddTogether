package com.berat.feedtogether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomappbar.BottomAppBar
import java.util.Calendar
import android.util.Log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SuveAdim.newInstance] factory method to
 * create an instance of this fragment.
 */
class SuveAdim : Fragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arayüz nesnelerini değişkene atamak için
        val icilenSu: TextView = view.findViewById(R.id.icilen_su)
        val atilanAdim: TextView = view.findViewById(R.id.atilan_adim)

        // Kullanıcı bilgilerini al ve göster
        arguments?.getString("KULLANICI_ADI")?.let { username ->
            val userDetails = dbHelper.getUserDetails(username)
            userDetails?.let { details ->
                icilenSu.text = details["daily_water"].toString()
                atilanAdim.text = details["daily_steps"].toString()
            }
        }

        // Yemek menüsü TextView'larını tanımla
        val anaYemek: TextView = view.findViewById(R.id.anaYemek)
        val aperitif: TextView = view.findViewById(R.id.aperitif)
        val salataCorba: TextView = view.findViewById(R.id.salataCorba)
        val tatliMeyve: TextView = view.findViewById(R.id.tatliMeyve)
        val ayKcal: TextView = view.findViewById(R.id.ay_kcal)
        val aKcal: TextView = view.findViewById(R.id.a_kcal)
        val scKcal: TextView = view.findViewById(R.id.sc_kcal)
        val tmKcal: TextView = view.findViewById(R.id.tm_kcal)
        val toplamKcal: TextView = view.findViewById(R.id.toplam_kcal)

        // Bugünün gününü al
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        
        Log.d("SuveAdim", "Bugünün günü: $dayOfMonth")

        // Günlük yemek menüsünü al ve TextView'lara yaz
        val dailyMenu = dbHelper.getDailyMenu(dayOfMonth)
        Log.d("SuveAdim", "getDailyMenu sonucu: $dailyMenu")
        
        dailyMenu?.let { menu ->
            Log.d("SuveAdim", "Menü verisi bulundu, TextView'lara yazılıyor")
            
            anaYemek.text = menu["anaYemek"].toString()
            Log.d("SuveAdim", "anaYemek TextView'ına yazıldı: ${menu["anaYemek"]}")
            
            aperitif.text = menu["aperitif"].toString()
            Log.d("SuveAdim", "aperitif TextView'ına yazıldı: ${menu["aperitif"]}")
            
            salataCorba.text = menu["salataCorba"].toString()
            Log.d("SuveAdim", "salataCorba TextView'ına yazıldı: ${menu["salataCorba"]}")
            
            tatliMeyve.text = menu["tatliMeyve"].toString()
            Log.d("SuveAdim", "tatliMeyve TextView'ına yazıldı: ${menu["tatliMeyve"]}")
            
            ayKcal.text = menu["ay_kcal"].toString()
            Log.d("SuveAdim", "ayKcal TextView'ına yazıldı: ${menu["ay_kcal"]}")
            
            aKcal.text = menu["a_kcal"].toString()
            Log.d("SuveAdim", "aKcal TextView'ına yazıldı: ${menu["a_kcal"]}")
            
            scKcal.text = menu["sc_kcal"].toString()
            Log.d("SuveAdim", "scKcal TextView'ına yazıldı: ${menu["sc_kcal"]}")
            
            tmKcal.text = menu["tm_kcal"].toString()
            Log.d("SuveAdim", "tmKcal TextView'ına yazıldı: ${menu["tm_kcal"]}")
            
            toplamKcal.text = menu["toplam_kcal"].toString()
            Log.d("SuveAdim", "toplamKcal TextView'ına yazıldı: ${menu["toplam_kcal"]}")
        } ?: run {
            Log.e("SuveAdim", "Günlük menü verisi null döndü")
        }

        //Arayüz nesnelerinden veri almak için
        var icilenSuValue = icilenSu.text.toString().toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_su_ve_adim, container, false)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            SuveAdim().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}