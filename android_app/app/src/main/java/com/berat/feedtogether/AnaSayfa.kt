package com.berat.feedtogether

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.berat.feedtogether.databinding.ActivityAnaSayfaBinding
import com.google.android.material.bottomappbar.BottomAppBar

class AnaSayfa : AppCompatActivity() {

    private lateinit var binding: ActivityAnaSayfaBinding
    private var kullaniciAdi: String? = null
    private var kullaniciAdiSoyadi: String? = null
    private lateinit var dbHelper: DatabaseHelper
    private var userDetails: Map<String, Any>? = null
    private var currentFragment: Fragment? = null
    private var currentMenuItemId: Int = R.id.main_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        changeStatusBarColor(R.color.ft_textbox)
        binding = ActivityAnaSayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kullanıcı bilgilerini al
        kullaniciAdi = intent.getStringExtra("KULLANICI_ADI")
        kullaniciAdiSoyadi = intent.getStringExtra("KULLANICI_ADA_SOYADI")

        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val frameLayout: FrameLayout = findViewById(R.id.frame_layout)
        val bottomAppBar: BottomAppBar = findViewById(R.id.bottomAppBar)

        bottomAppBar.viewTreeObserver.addOnGlobalLayoutListener {
            val height = bottomAppBar.height
            val params = frameLayout.layoutParams as CoordinatorLayout.LayoutParams
            params.bottomMargin = height + 30
            frameLayout.layoutParams = params
        }

        binding.bottomNavigationView.selectedItemId = R.id.main_menu
        binding.bottomNavigationView.background = null

        // Kullanıcı detaylarını al
        kullaniciAdi?.let { username ->
            userDetails = dbHelper.getUserDetails(username)
        }

        // İlk fragment'ı yükle
        replaceFragment(AnaSayfaMenu(), AnimationType.FADE)

        val renkGoster: ImageView = findViewById(R.id.halka_image)
        renkGoster.visibility = View.VISIBLE

        binding.bottomNavigationView.setOnItemSelectedListener {
            renkGoster.visibility = View.INVISIBLE
            
            val newMenuItemId = it.itemId
            val animationType = getAnimationTypeForMenuTransition(currentMenuItemId, newMenuItemId)
            
            when(newMenuItemId) {
                R.id.water_menu -> replaceFragment(SuveAdim(), animationType)
                R.id.scoreboard_menu -> replaceFragment(Skor(), animationType)
                R.id.bmi_menu -> replaceFragment(VKI(), animationType)
                R.id.profile_menu -> replaceFragment(Profil(), animationType)
                else -> {}
            }
            
            currentMenuItemId = newMenuItemId
            true
        }

        binding.floatingActionButton.setOnClickListener {
            replaceFragment(AnaSayfaMenu(), AnimationType.FADE)
            binding.bottomNavigationView.selectedItemId = R.id.main_menu
            currentMenuItemId = R.id.main_menu
            renkGoster.visibility = View.VISIBLE
        }
    }

    enum class AnimationType {
        SLIDE_RIGHT, SLIDE_LEFT, FADE
    }

    private fun getAnimationTypeForMenuTransition(fromMenuItemId: Int, toMenuItemId: Int): AnimationType {
        // Menü pozisyonlarını tanımla (soldan sağa)
        val menuPositions = mapOf(
            R.id.water_menu to 0,
            R.id.scoreboard_menu to 1,
            R.id.main_menu to 2,
            R.id.bmi_menu to 3,
            R.id.profile_menu to 4
        )
        
        val fromPosition = menuPositions[fromMenuItemId] ?: 2
        val toPosition = menuPositions[toMenuItemId] ?: 2
        
        return when {
            toPosition > fromPosition -> AnimationType.SLIDE_RIGHT // Sağa doğru kayma
            toPosition < fromPosition -> AnimationType.SLIDE_LEFT  // Sola doğru kayma
            else -> AnimationType.FADE // Aynı pozisyon (fade)
        }
    }

    private fun replaceFragment(fragment: Fragment, animationType: AnimationType = AnimationType.SLIDE_RIGHT) {
        val bundle = Bundle()
        userDetails?.let { details ->
            bundle.putString("KULLANICI_ADI", kullaniciAdi)
            bundle.putString("KULLANICI_ADI_SOYADI", details["name"].toString())
            bundle.putString("CINSIYET", details["gender"].toString())
            bundle.putInt("YAS", details["age"] as Int)
            bundle.putFloat("AGIRLIK", details["current_weight"] as Float)
            bundle.putFloat("HEDEF_AGIRLIK", details["target_weight"] as Float)
            bundle.putInt("BOY", details["height"] as Int)
            bundle.putInt("GUNLUK_SU", details["daily_water"] as Int)
            bundle.putInt("GUNLUK_ADIM", details["daily_steps"] as Int)
        }
        fragment.arguments = bundle
        
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        
        // Animasyonları ayarla
        when (animationType) {
            AnimationType.SLIDE_RIGHT -> {
                fragmentTransition.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            AnimationType.SLIDE_LEFT -> {
                fragmentTransition.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            AnimationType.FADE -> {
                fragmentTransition.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            }
        }
        
        fragmentTransition.replace(R.id.frame_layout, fragment)
        fragmentTransition.addToBackStack(null) // Geri tuşu için
        fragmentTransition.commit()
        
        currentFragment = fragment
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            // Geri tuşuna basıldığında animasyonlu geçiş
            fragmentManager.popBackStack()
            
            // Bottom navigation'ı güncelle
            val currentFragment = fragmentManager.fragments.lastOrNull()
            when (currentFragment) {
                is AnaSayfaMenu -> {
                    binding.bottomNavigationView.selectedItemId = R.id.main_menu
                    currentMenuItemId = R.id.main_menu
                    findViewById<ImageView>(R.id.halka_image).visibility = View.VISIBLE
                }
                is SuveAdim -> {
                    binding.bottomNavigationView.selectedItemId = R.id.water_menu
                    currentMenuItemId = R.id.water_menu
                }
                is Skor -> {
                    binding.bottomNavigationView.selectedItemId = R.id.scoreboard_menu
                    currentMenuItemId = R.id.scoreboard_menu
                }
                is VKI -> {
                    binding.bottomNavigationView.selectedItemId = R.id.bmi_menu
                    currentMenuItemId = R.id.bmi_menu
                }
                is Profil -> {
                    binding.bottomNavigationView.selectedItemId = R.id.profile_menu
                    currentMenuItemId = R.id.profile_menu
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun changeStatusBarColor(colorResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, colorResId)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AnaSayfa()
    }
}