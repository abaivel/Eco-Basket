package com.example.eco_basket
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameActivity2 : AppCompatActivity() {
    private lateinit var objet : ImageView
    private lateinit var poubelleVerte : ImageView
    private lateinit var poubelleMarron : ImageView
    private lateinit var poubelleGrise : ImageView
    private lateinit var poubelleJaune : ImageView
    private lateinit var faux : ImageView
    private lateinit var vrai : ImageView
    private lateinit var ville : String
    private val infos = mutableMapOf<String,List<String>>()
    private var objets = listOf("Bouteille d'eau en plastique"," Canette de soda"," Brique de jus"," Carton"," Boite de conserve"," Thon en conserve"," Pot de confiture"," Feuille de papier"," Reste de pomme"," Epluchures"," Bouteille d'eau en verre"," Pot de yaourt en plastique"," Pot de yaourt en verre"," Barquette en plastique"," Flacon de shampoing"," Flacon de parfum")
    private var d=0
    private var yObjet=0
    private var poubelles = mutableListOf<ImageView>()
    private var photosObjet = listOf(R.drawable.bouteille,R.drawable.carton,R.drawable.pomme)
    private var objetsPoubelles =mapOf(R.drawable.bouteille to "poubelleJaune",R.drawable.carton to "poubelleJaune",R.drawable.pomme to "poubelleVerte")
    private var nomObjet = R.drawable.bouteille
    private var num=0
    private val SHARED_CITY_INFO = "SHARED_CITY_INFO"
    private val SHARED_CITY_NAME = "SHARED_CITY_NAME"
    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        supportActionBar?.hide()
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        recup_ville()
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        println("largeur=" + width + "  hauteur=" + height)
        findViewById<ImageView>(R.id.objet).also { objet = it }
        findViewById<ImageView>(R.id.poubelleVerte).also { poubelleVerte = it }
        findViewById<ImageView>(R.id.poubelleMarron).also { poubelleMarron = it }
        findViewById<ImageView>(R.id.poubelleGrise).also { poubelleGrise = it }
        findViewById<ImageView>(R.id.poubelleJaune).also { poubelleJaune = it }
        findViewById<ImageView>(R.id.faux).also { faux = it }
        findViewById<ImageView>(R.id.vrai).also { vrai = it }
        poubelles = mutableListOf(poubelleMarron, poubelleJaune, poubelleGrise, poubelleVerte)
        objet.post {
            d = (height - (objet.y + objet.height)).toInt()
            yObjet = objet.y.toInt()
            println("d = $d")
        }
        objet.setOnLongClickListener {
            println("appui")
            true
        }
        objet.setOnTouchListener { v, event ->

            //@SuppressLint("ClickableViewAccessibility")
            if (event?.action == MotionEvent.ACTION_UP) {
                for (p in poubelles) {
                    if (((p.x) <= objet.x && objet.x <= (p.x + p.width - objet.width)) && ((p.y) <= objet.y && objet.y <= (p.y + p.height - objet.height))) {
                        println(p.tag)
                        println(objetsPoubelles[nomObjet])
                        if (p.tag == objetsPoubelles[nomObjet]) {
                            vrai.visibility = View.VISIBLE
                            Handler().postDelayed({ vrai.visibility = View.INVISIBLE }, 1000)
                            num += 1
                            if (num < photosObjet.size) {
                                nomObjet = photosObjet[num]
                                objet.background = resources.getDrawable(nomObjet)
                                objet.x = ((width - objet.width) / 2).toFloat()
                                //objet.y=(height-objet.y-d).toFloat()
                                objet.y = (yObjet).toFloat()
                            } else {
                                objet.visibility = View.INVISIBLE
                            }
                        } else {
                            faux.visibility = View.VISIBLE
                            Handler().postDelayed({ faux.visibility = View.INVISIBLE }, 1000)
                            objet.x = ((width - objet.width) / 2).toFloat()
                            //objet.y= (height-objet.y-d)
                            objet.y = (yObjet).toFloat()
                        }
                    }
                }
            } else {
                val x = event?.rawX?.toInt()
                val y = event?.rawY?.toInt()
                if (y != null) {
                    objet.y = y.toFloat() - objet.height / 2 - getStatusBarHeight()
                }
                if (x != null) {
                    objet.x = x.toFloat() - objet.width / 2
                }
                val nx=objet.x
                val ny =objet.y
                println("$nx   $ny")
                println("$x $y")
            }
            v?.onTouchEvent(event) ?: true
        }
    }
    private fun getStatusBarHeight(): Int{
        val rectangle = Rect()
        val window = window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight: Int = rectangle.top
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        val titleBarHeight = contentViewTop - statusBarHeight
        println("taille barre $statusBarHeight")
        return statusBarHeight
    }

    private fun recup_ville(){
        ville = getSharedPreferences(SHARED_CITY_INFO, MODE_PRIVATE).getString(
            SHARED_CITY_NAME,
            null)!!
        for (o in objets){
            val m = getSharedPreferences(SHARED_CITY_INFO, MODE_PRIVATE).getString(
                o,
                null)!!
            infos[o] = m.split(" ou ")
        }

    }
}