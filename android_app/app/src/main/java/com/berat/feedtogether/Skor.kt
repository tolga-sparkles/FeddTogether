package com.berat.feedtogether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Skor.newInstance] factory method to
 * create an instance of this fragment.
 */
class Skor : Fragment() {
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

    private fun formatName(fullName: String): String {
        val parts = fullName.split(" ")
        return if (parts.size > 1) {
            "${parts[0]} ${parts[1][0]}."
        } else {
            fullName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_skor, container, false)

        try {
            // İlk 10 kişiyi al ve göster
            val topTen = dbHelper.getTopTenByBMI()
            
            if (topTen.isNotEmpty()) {
                // İlk üç kişinin isimlerini üst kısımda göster
                if (topTen.size >= 1) {
                    view.findViewById<TextView>(R.id.textView7)?.text = formatName(topTen[0]["name"].toString())
                }
                if (topTen.size >= 2) {
                    view.findViewById<TextView>(R.id.textView9)?.text = formatName(topTen[1]["name"].toString())
                }
                if (topTen.size >= 3) {
                    view.findViewById<TextView>(R.id.textView8)?.text = formatName(topTen[2]["name"].toString())
                }

                // Tüm listeyi alt kısımda göster
                val container = view.findViewById<LinearLayout>(R.id.skor_list_container)
                topTen.forEachIndexed { index, user ->
                    val cardView = CardView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(3, 8, 3, 8)
                        }
                        radius = 5 * resources.displayMetrics.density // 10dp'yi piksel cinsine çeviriyoruz
                    }

                    val innerLayout = LinearLayout(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(32, 32, 32, 32)
                    }

                    // Sıra numarası
                    val numberText = TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = (index + 1).toString()
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.ft_siyah))
                        textSize = 16f
                    }

                    // İsim
                    val nameText = TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        ).apply {
                            marginStart = 32
                        }
                        text = formatName(user["name"].toString())
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.ft_siyah))
                        textSize = 16f
                    }

                    // VKİ değeri
                    val bmiText = TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = String.format("%.1f", user["bmi"])
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.ft_siyah))
                        textSize = 16f
                    }

                    innerLayout.addView(numberText)
                    innerLayout.addView(nameText)
                    innerLayout.addView(bmiText)
                    cardView.addView(innerLayout)
                    container.addView(cardView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
         * @return A new instance of fragment Skor.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = Skor()
    }
}