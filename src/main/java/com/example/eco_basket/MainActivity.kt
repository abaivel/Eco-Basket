package com.example.eco_basket

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    private lateinit var bouton : Button
    private lateinit var ecran : ConstraintLayout
    private lateinit var info_button : ImageButton
    private lateinit var infos : TextView
    private lateinit var ville : String
    private lateinit var infos_ville : List<String>
    private lateinit var objets : List<String>
    private val SHARED_CITY_INFO = "SHARED_CITY_INFO"
    private val SHARED_CITY_NAME = "SHARED_CITY_NAME"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        bouton=findViewById(R.id.bouton_depart)
        bouton.setOnClickListener {
            enregistrer_ville(ville)
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        println(recup_data("Paris (75)").size)
        ecran = findViewById(R.id.ecran)
        val search = findViewById<SearchView>(R.id.search)
        val listView = findViewById<ListView>(R.id.listview)
        val cities = resources.getStringArray(R.array.cities)
        info_button = findViewById(R.id.button_info)
        infos = findViewById(R.id.info_villes)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1,resources.getStringArray(R.array.cities))
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener{ _, view, position, _ ->
            ville = adapter.getItem(position).toString()
            search.setQuery(ville, false)
            listView.visibility = View.INVISIBLE
            view.hideKeyboard()
            bouton.isEnabled=true

        }
        search.setOnClickListener { v ->
            when (v.id) {
                R.id.search -> search.onActionViewExpanded()
            }
        }
        info_button.setOnClickListener {
            if (infos.visibility == View.VISIBLE) {
                infos.visibility = View.INVISIBLE
            } else {
                infos.visibility = View.VISIBLE
            }
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search.clearFocus()
                if (cities.contains(p0)){
                    adapter.filter.filter(p0)
                }else{
                    Toast.makeText(applicationContext, "Item not found", Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                listView.visibility = View.VISIBLE
                bouton.isEnabled=false
                return false
            }

        })

    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    fun View.showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun enregistrer_ville(ville: String){
        getSharedPreferences(SHARED_CITY_INFO, MODE_PRIVATE)
            .edit()
            .putString(SHARED_CITY_NAME, ville)
            .apply()
        infos_ville = recup_data(ville)
        println(objets)
        if (infos_ville.size >0) {
            for (i in 0 until infos_ville.size) {
                getSharedPreferences(SHARED_CITY_INFO, MODE_PRIVATE)
                    .edit()
                    .putString(objets[i], infos_ville[i])
                    .apply()
            }
        }else{
            println("ERREUR POUR RECUPERER LES DONNEES")
        }
    }

    private fun recup_data(ville: String): List<String> {
        val inputStream: InputStream = resources.openRawResource(R.raw.donnees)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            //get a string array of all items in this line
            val items = it.split(",")
            println("taille="+items.size)
            if (items[0]=="Villes"){
                objets=items.subList(1,items.size)
            }
            if (items[0]==ville){
                return items.subList(1,items.size)
            }
            //do what you want with each item
        }
        return emptyList()
    }

}


