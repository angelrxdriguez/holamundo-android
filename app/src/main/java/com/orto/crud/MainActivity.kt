package com.orto.crud

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etVida   = findViewById<TextInputEditText>(R.id.etVida)
        val etDano   = findViewById<TextInputEditText>(R.id.etDano)
        val btnCrear = findViewById<Button>(R.id.button)

        btnCrear.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            val vida   = etVida.text?.toString()?.trim()?.toIntOrNull()
            val dano   = etDano.text?.toString()?.trim()?.toIntOrNull()

            if (nombre.isEmpty() || vida == null || dano == null) {
                Toast.makeText(this, "Rellena nombre, vida y daño (números)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val doc = mapOf("nombre" to nombre, "vida" to vida, "dano" to dano)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val body = InsertOneBody(
                        dataSource = "Cluster0",     // ajusta si tu data source se llama distinto
                        database   = "veraleza",     // tu DB
                        collection = "cards",        // tu colección
                        document   = doc
                    )
                    val res = ApiMongo.api.insertOne(body)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Insertado id=${res.insertedId ?: "?"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        etNombre.setText(""); etVida.setText(""); etDano.setText("")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error al insertar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
