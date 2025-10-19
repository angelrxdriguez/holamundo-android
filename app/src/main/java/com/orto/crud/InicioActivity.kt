package com.orto.crud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnBaraja = findViewById<Button>(R.id.button2)
        val btnSalir  = findViewById<Button>(R.id.button5)
        btnBaraja.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            // finish()
        }

        btnSalir.setOnClickListener {
            finishAffinity() // cierra la app
        }
    }
}
