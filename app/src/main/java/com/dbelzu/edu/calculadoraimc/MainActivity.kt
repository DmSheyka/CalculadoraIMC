package com.dbelzu.edu.calculadoraimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbelzu.edu.calculadoraimc.ui.theme.CalculadoraIMCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraIMCTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppCalculadorsIMC()
                }
            }
        }
    }
}

enum class SistemaUnidades{
    Metrico, Imperial
}
@Composable
fun AppCalculadorsIMC() {
    var pesoIngresado by remember { mutableStateOf("") }
    var alturaIngresada by remember { mutableStateOf("") }
    var sistemaUnidades by remember { mutableStateOf(SistemaUnidades.Metrico) }

    var IMC by remember { mutableStateOf(0.0) }

    val unidadPeso =
        if (sistemaUnidades == SistemaUnidades.Imperial) stringResource(id = R.string.unidadPesoIngles) else stringResource(
            id = R.string.unidadPeso)
    val unidadAltura =
        if (sistemaUnidades == SistemaUnidades.Imperial) stringResource(id = R.string.unidadAlturaIngles) else stringResource(
            id = R.string.unidadAltura
        )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(id = R.string.titulo_calculadora_IMC),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CampodeDatos(
                textoEtiqueta = stringResource(id = R.string.peticionPeso) + " ($unidadPeso)",
                textoValor = pesoIngresado,
                onValorCambiado = { pesoIngresado = it },
                opcionesTeclado = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(30.dp))
            CampodeDatos(
                textoEtiqueta = stringResource(R.string.peticionAltura) + " ($unidadAltura)",
                textoValor = alturaIngresada,
                onValorCambiado = { alturaIngresada = it },
                opcionesTeclado = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.unidadMedida))
            Switch(
                checked = sistemaUnidades == SistemaUnidades.Imperial,
                onCheckedChange = { sistemaUnidades = if (it) SistemaUnidades.Imperial else SistemaUnidades.Metrico }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                IMC = calcularIMC(
                    pesoIngresado.toDoubleOrNull() ?: 0.0,
                    alturaIngresada.toDoubleOrNull() ?: 0.0,
                    sistemaUnidades
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.texto_calcular))
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (IMC != 0.0) {
            Text(
                text = stringResource(id = R.string.valorIMC, IMC),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = when {
                    IMC < 18.5 -> Color.Red
                    IMC < 25 -> Color.Green
                    IMC < 30 -> Color.Yellow
                    else -> Color.Red
                }
            )
        }
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .padding(horizontal = 12.dp, vertical = 12.dp)
        )
        Card(
            modifier = Modifier
                .shadow(12.dp)
                .fillMaxWidth(0.8f),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(width = 1.dp, color = Color.Gray),
        ) {
            Text(
                text = stringResource(R.string.bajoPeso) + "\n"
                        + stringResource(R.string.normalPeso) + "\n"
                        + stringResource(R.string.sobrePeso) + "\n"
                        + stringResource(R.string.obesidad),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
@Composable
fun CampodeDatos(
    textoEtiqueta: String,
    textoValor: String,
    onValorCambiado: (String) -> Unit,
    opcionesTeclado: KeyboardOptions
) {
    TextField(
        value = textoValor,
        onValueChange = onValorCambiado,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = textoEtiqueta) },
        keyboardOptions = opcionesTeclado,
        singleLine = true
    )
}

private fun calcularIMC(peso: Double, altura: Double, sistemaUnidades: SistemaUnidades): Double {
    return when (sistemaUnidades) {
        SistemaUnidades.Metrico -> peso / (altura * altura)
        SistemaUnidades.Imperial -> (peso * 703) / (altura * altura)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculadoraIMCTheme {
        AppCalculadorsIMC()
    }
}