package com.orto.crud

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var contenedor: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etVida   = findViewById<EditText>(R.id.etVida)
        val etDano   = findViewById<EditText>(R.id.etDano)
        val btnCrear = findViewById<Button>(R.id.button)
        contenedor   = findViewById(R.id.contenedorCards)
        ApiMongo.api.listar("cartas", "cartas").enqueue(object : retrofit2.Callback<List<Carta>> {
            override fun onResponse(
                call: retrofit2.Call<List<Carta>>,
                response: retrofit2.Response<List<Carta>>
            ) {
                val lista = response.body() ?: emptyList()
                contenedor.removeAllViews()
                for (c in lista) {
                    val nombre = c.nombre ?: ""
                    val vida = c.vida ?: 0
                    val dano = c.dano ?: 0
                    addCard(nombre, vida, dano)
                }
            }
            override fun onFailure(call: retrofit2.Call<List<Carta>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al listar", Toast.LENGTH_SHORT).show()
            }
        })
        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val vida   = etVida.text.toString().trim().toIntOrNull()
            val dano   = etDano.text.toString().trim().toIntOrNull()
            if (nombre.isEmpty() || vida == null || dano == null) {
                Toast.makeText(this, "Rellena datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val body = CrearBody(
                dataSource = "Cluster0",
                database   = "cartas",
                collection = "cartas",
                document   = mapOf("nombre" to nombre, "vida" to vida, "dano" to dano)
            )

            ApiMongo.api.crear(body).enqueue(object : retrofit2.Callback<CrearResponse> {
                override fun onResponse(
                    call: retrofit2.Call<CrearResponse>,
                    response: retrofit2.Response<CrearResponse>
                ) {
                    if (response.isSuccessful) {
                        addCard(nombre, vida, dano)
                        etNombre.setText("")
                        etVida.setText("")
                        etDano.setText("")
                        Toast.makeText(this@MainActivity, "Creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Error al crear", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: retrofit2.Call<CrearResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun addCard(nombre: String, vida: Int, dano: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_carta, contenedor, false)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvVida   = view.findViewById<TextView>(R.id.tvVida)
        val tvDano   = view.findViewById<TextView>(R.id.tvDano)
        val btnEditar   = view.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        tvNombre.text = nombre
        tvVida.text   = "Vida: $vida"
        tvDano.text   = "Daño: $dano"
        btnEditar.setOnClickListener {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            val pad = (12 * resources.displayMetrics.density).toInt()
            layout.setPadding(pad, pad, pad, pad)
            val etN = EditText(this)
            etN.hint = "Nombre"
            etN.setText(tvNombre.text.toString())
            val etV = EditText(this)
            etV.hint = "Vida (número)"
            etV.inputType = InputType.TYPE_CLASS_NUMBER
            etV.setText(vida.toString())
            val etD = EditText(this)
            etD.hint = "Daño (número)"
            etD.inputType = InputType.TYPE_CLASS_NUMBER
            etD.setText(dano.toString())
            layout.addView(etN)
            layout.addView(etV)
            layout.addView(etD)
            AlertDialog.Builder(this)
                .setTitle("editar")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar") { _, _ ->
                    val nuevoNombre = etN.text.toString().trim()
                    val nuevaVida   = etV.text.toString().trim().toIntOrNull()
                    val nuevoDano   = etD.text.toString().trim().toIntOrNull()

                    if (nuevoNombre.isEmpty() || nuevaVida == null || nuevoDano == null) {
                        Toast.makeText(this, "Campos inválidos", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val body = hashMapOf<String, Any>(
                        "nombre" to tvNombre.text.toString(),
                        "nuevoNombre" to nuevoNombre,
                        "vida" to nuevaVida,
                        "dano" to nuevoDano
                    )
                    ApiMongo.api.actualizar(body).enqueue(object : retrofit2.Callback<Map<String, Any>> {
                        override fun onResponse(
                            call: retrofit2.Call<Map<String, Any>>,
                            response: retrofit2.Response<Map<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                tvNombre.text = nuevoNombre
                                tvVida.text   = "Vida: $nuevaVida"
                                tvDano.text   = "Daño: $nuevoDano"
                                Toast.makeText(this@MainActivity, "ACTUALIZADA !", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: retrofit2.Call<Map<String, Any>>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "red", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .show()
        }
        btnEliminar.setOnClickListener {
            ApiMongo.api.borrar(mapOf("nombre" to tvNombre.text.toString()))
                .enqueue(object : retrofit2.Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: retrofit2.Call<Map<String, Any>>,
                        response: retrofit2.Response<Map<String, Any>>
                    ) {
                        contenedor.removeView(view)
                        Toast.makeText(this@MainActivity, "borrada", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: retrofit2.Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        contenedor.addView(view)
    }
}
