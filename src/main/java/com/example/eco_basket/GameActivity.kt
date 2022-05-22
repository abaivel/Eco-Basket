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
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class GameActivity : AppCompatActivity() {
    private lateinit var objet : ImageView
    private lateinit var poubelleVerte : ImageView
    private lateinit var poubelleMarron : ImageView
    private lateinit var poubelleGrise : ImageView
    private lateinit var poubelleJaune : ImageView
    private lateinit var faux : ImageView
    private lateinit var vrai : ImageView
    private lateinit var ville : String
    private val infos = mutableMapOf<String,String>()
    private var objets = listOf("Bouteille d'eau en plastique","Canette de soda","Brique de jus","Carton","Boite de conserve","Thon en conserve","Pot de confiture","Feuille de papier","Reste de pomme","Epluchures","Bouteille d'eau en verre","Pot de yaourt en plastique","Pot de yaourt en verre","Barquette en plastique","Flacon de shampoing","Flacon de parfum")
    private var d=0
    private var yObjet=0
    private var poubelles = mutableListOf<ImageView>()
    private var objetsUse = listOf("Bouteille d'eau en plastique", "Carton" , "Reste de pomme")
    private var nomObjetPhotos = mapOf("Bouteille d'eau en plastique" to R.drawable.bouteille, "Carton" to R.drawable.carton, "Reste de pomme" to R.drawable.pomme)
    private var photosObjet = listOf(R.drawable.bouteille,R.drawable.carton,R.drawable.pomme)
    //private var objetsPoubelles: Map<Int, String> =mapOf(R.drawable.bouteille to "poubelleJaune",R.drawable.carton to "poubelleJaune",R.drawable.pomme to "poubelleVerte")
    private var idPoubelles = mutableMapOf<String, ImageView>()
    private var objetsPoubelles: MutableMap<Int, ImageView> = mutableMapOf<Int, ImageView>()
    private var nomObjet = R.drawable.bouteille
    private var num=0
    private val SHARED_CITY_INFO = "SHARED_CITY_INFO"
    private val SHARED_CITY_NAME = "SHARED_CITY_NAME"
    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables", "WrongViewCast")
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
        /*findViewById<ImageView>(R.id.poubelleVerte).also { poubelleVerte = it }
        findViewById<ImageView>(R.id.poubelleMarron).also { poubelleMarron = it }
        findViewById<ImageView>(R.id.poubelleGrise).also { poubelleGrise = it }
        findViewById<ImageView>(R.id.poubelleJaune).also { poubelleJaune = it }*/
        findViewById<ImageView>(R.id.faux).also { faux = it }
        findViewById<ImageView>(R.id.vrai).also { vrai = it }
        idPoubelles= mutableMapOf("Bac au couvercle jaune" to findViewById(R.id.poubelleJaune), "Bac au couvercle gris" to findViewById(R.id.poubelleGrise), "Bac au couvercle vert" to findViewById(R.id.poubelleVerte), "Bac au couvercle marron" to findViewById(R.id.poubelleMarron),
            "Compost" to findViewById(R.id.compost),"Bac au couvercle bleu" to findViewById(R.id.poubelleBleu),"Bac vert au couvercle jaune" to findViewById(R.id.poubelleVerteJaune),"Bac vert au couvercle vert" to findViewById(R.id.poubelleVerteCompost),
            "Bac au couvercle blanc" to findViewById(R.id.poubelleBlanche),"Bac au convercle rouge" to findViewById(R.id.poubelleRouge),"Point de collecte vert" to findViewById(R.id.bacVert),"Point de collecte bleu" to findViewById(R.id.bacBleu));
        //poubelles = mutableListOf(poubelleMarron, poubelleJaune, poubelleGrise, poubelleVerte)
        objet.post {
            d = (height - (objet.y + objet.height)).toInt()
            yObjet = objet.y.toInt()
            println("d = $d")
            completePoubelles(ville)
            completeObjetsPoubelles()
        }
        objet.setOnLongClickListener {
            println("appui")
            true
        }
        objet.setOnTouchListener { v, event ->
            //@SuppressLint("ClickableViewAccessibility")
            if (event?.action == MotionEvent.ACTION_UP) { //l'objet est laché
                for (p in poubelles) {
                    if (((p.x) <= objet.x && objet.x <= (p.x + p.width - objet.width)) && ((p.y) <= objet.y && objet.y <= (p.y + p.height - objet.height))) {
                        println(p.tag)
                        println(objetsPoubelles[nomObjet])
                        if (p == objetsPoubelles[nomObjet]) {
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
            } else {//mouvement
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
        println(objets)
        for (o in objets){
            val m = getSharedPreferences(SHARED_CITY_INFO, MODE_PRIVATE).getString(
                o,
                null)!!
            infos[o] = m
        }
    }

    private fun completePoubelles(ville : String){
        val inputStream: InputStream = resources.openRawResource(R.raw.couleurs_poubelles)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        val refPoubelles = listOf<ImageView>(findViewById(R.id.poubelleRefNO),findViewById(R.id.poubelleRefNE),findViewById(R.id.poubelleRefSO),findViewById(R.id.poubelleRefSE))
        var j=0
        reader.readLines().forEach {

            //get a string array of all items in this line
            val items = it.split(",")
            if (items[0]==ville) {
                println(items.subList(1, items.size))
                for (i in items.subList(1, items.size)) {
                    idPoubelles[i]?.visibility = View.VISIBLE;
                    idPoubelles[i]?.let { it1 -> poubelles.add(it1) }
                    idPoubelles[i]?.x=refPoubelles[j].x
                    idPoubelles[i]?.y=refPoubelles[j].y + (refPoubelles[j].height - idPoubelles[i]!!.height)/2
                    j+=1
                }
            }
        }
    }
    private fun completeObjetsPoubelles(){
        for (o in objetsUse){
            var p: String? = infos[o]
            var op=nomObjetPhotos[o]
            idPoubelles[p]?.let {
                if (op != null) {
                    objetsPoubelles.put(op, it)
                }
            }
        }
    }
}