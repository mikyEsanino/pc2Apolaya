package com.example.pc2apolaya.presentation.Screen
import androidx.compose.material.icons.filled.Refresh
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConversionScreen(navController: NavController) {
    val context = LocalContext.current
    val monedas = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    var monto by remember { mutableStateOf("") }
    var monedaOrigen by remember { mutableStateOf("USD") }
    var monedaDestino by remember { mutableStateOf("EUR") }
    var resultado by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Conversor de Divisas", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Moneda origen
        Text("Moneda de origen")
        DropdownMenuSelector(monedas, monedaOrigen) { monedaOrigen = it }

        Spacer(modifier = Modifier.height(16.dp))

// Botón para intercambiar monedas
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            IconButton(onClick = {
                // Intercambia las monedas
                val temp = monedaOrigen
                monedaOrigen = monedaDestino
                monedaDestino = temp
            }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Intercambiar monedas"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Moneda destino
        Text("Moneda de destino")
        DropdownMenuSelector(monedas, monedaDestino) { monedaDestino = it }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(context, "No autenticado", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val montoDouble = monto.toDoubleOrNull()
            if (montoDouble == null || montoDouble <= 0) {
                Toast.makeText(context, "Monto inválido", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Consultar tasa en Firestore
            val rateRef = db.collection("rates").document(monedaOrigen)
            rateRef.get().addOnSuccessListener { doc ->
                val tasa = doc.getDouble(monedaDestino)
                if (tasa != null) {
                    val resultadoCalc = montoDouble * tasa
                    resultado = "$montoDouble $monedaOrigen equivalen a %.2f $monedaDestino".format(resultadoCalc)

                    // Guardar conversión
                    val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    val data = hashMapOf(
                        "uid" to uid,
                        "fechaHora" to fecha,
                        "monto" to montoDouble,
                        "from" to monedaOrigen,
                        "to" to monedaDestino,
                        "resultado" to resultadoCalc
                    )
                    db.collection("conversions").add(data)

                } else {
                    Toast.makeText(context, "Tasa no encontrada", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Error al obtener tasa", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error rates: ${it.message}")
            }

        }, modifier = Modifier.fillMaxWidth()) {
            Text("Convertir")
        }

        Spacer(modifier = Modifier.height(24.dp))

        resultado?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DropdownMenuSelector(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Seleccionar") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir")
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSelected(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
