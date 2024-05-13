package com.example.practicadibujotablero.ui.model

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Tablero {
    var filas = 8
    var columnas = 8
    var juegoFinalizado: Boolean = false


    // matriz llena de números del 1 al 6
    var matriz: Array<Array<Int>> = arrayOf(
        arrayOf(2, 1, 5, 2, 5, 2, 1, 2),
        arrayOf(3, 3, 1, 1, 6, 3, 1, 3),
        arrayOf(3, 1, 5, 6, 3, 2, 4, 3),
        arrayOf(4, 1, 5, 2, 5, 1, 3, 5),
        arrayOf(5, 6, 1, 2, 1, 3, 5, 3),
        arrayOf(6, 3, 2, 3, 4, 5, 6, 3),
        arrayOf(1, 1, 3, 4, 5, 6, 1, 2),
        arrayOf(2, 3, 4, 5, 3, 1, 2, 3)
    )

    fun eliminarGemas(): Boolean {
        var huboEliminacion = false
        //recorren todas las gemas del tablero
        for (i in 0 until filas) {
            for (j in 0 until columnas) {
                //posicion actual de la gema
                val gema = matriz[i][j]
                //hay gemas del mismo tipo la fila  y columna
                if (gema in 1..6) {
                    var countFila = 0
                    while (j + countFila < columnas && matriz[i][j + countFila] == gema) {
                        countFila++
                    }

                    var countColumna = 0
                    while (i + countColumna < filas && matriz[i + countColumna][j] == gema) {
                        countColumna++
                    }

                    // combinación en forma de T
                    if (countFila >= 3 && countColumna >= 3) {
                        for (k in 0 until countFila) {
                            matriz[i][j + k] = 0
                        }
                        for (k in 0 until countColumna) {
                            matriz[i + k][j] = 0
                        }
                        matriz[i][j] = 8 // Gema de rayo
                        huboEliminacion = true
                    } else if (countFila >= 3) {
                        for (k in 0 until countFila) {
                            matriz[i][j + k] = 0
                        }
                        if (countFila >= 5) {
                            matriz[i][j] = 7 // Gema de cubo
                        } else if (countFila >= 4) {
                            matriz[i][j] = 9 // Gema de fuego
                        }
                        huboEliminacion = true
                    } else if (countColumna >= 3) {
                        for (k in 0 until countColumna) {
                            matriz[i + k][j] = 0
                        }
                        if (countColumna >= 5) {
                            matriz[i][j] = 7 // Gema de cubo
                        } else if (countColumna >= 4) {
                            matriz[i][j] = 9 // Gema de fuego
                        }
                        huboEliminacion = true
                    }
                }
            }
        }
        if (huboEliminacion) {
            realizarCascada()
        }
        return huboEliminacion
    }

    fun realizarCascada() {
        //recorre todas las columnas del tablero
        for (j in 0 until columnas) {
            for (i in filas - 1 downTo 0) {
                if (matriz[i][j] == 0) {
                    var k = i - 1
                    while (k >= 0) {
                        if (matriz[k][j] != 0) {
                            matriz[i][j] = matriz[k][j]
                            matriz[k][j] = 0
                            break
                        }
                        k--
                    }
                }
            }
            //genera nuevas gemas en la parte superior de la columna
            for (i in 0 until filas) {
                if (matriz[i][j] == 0) {
                    var nuevaGema: Int
                    do {
                        nuevaGema = Random.nextInt(1, 7)
                    } while ((i > 1 && matriz[i - 1][j] == nuevaGema && matriz[i - 2][j] == nuevaGema) ||
                        (j > 1 && i > 0 && matriz[i - 1][j] == nuevaGema && matriz[i - 1][j - 1] == nuevaGema && matriz[i - 1][j - 2] == nuevaGema)
                    )
                    matriz[i][j] = nuevaGema
                }
            }
        }
        while (eliminarGemas()) {
        }
    }

    fun intercambiarGemas(fila1: Int, columna1: Int, fila2: Int, columna2: Int, direccion: String) {
        // Verificar si las gemas están adyacentes y en la dirección correcta
        val esAdyacente = when (direccion) {
            "derecha" -> fila1 == fila2 && columna2 == columna1 + 1
            "izquierda" -> fila1 == fila2 && columna2 == columna1 - 1
            "abajo" -> columna1 == columna2 && fila2 == fila1 + 1
            "arriba" -> columna1 == columna2 && fila2 == fila1 - 1
            else -> false
        }

        if (!esAdyacente) {
            return
        }
        // Intercambia las gemas
        val temp = matriz[fila1][columna1]
        val temp2 = matriz[fila2][columna2]

        println("Intercambiando gema en ($fila1, $columna1) con gema en ($fila2, $columna2)")

        // Realiza el intercambio de gemas
        matriz[fila1][columna1] = temp2
        matriz[fila2][columna2] = temp

        // Si una de las gemas es un cubo
        if (temp == 7 || temp2 == 7) {
            val tipoGema = if (temp == 7) temp2 else temp
            eliminarGemasDelMismoTipo(tipoGema, fila2, columna2)
        }

        // fuego
        if (temp == 9 || temp2 == 9) {
            println("Gema de fuego detectada en ($fila2, $columna2)")
            eliminarGemasAlrededor(fila2, columna2)
        }

        // rayo
        if (temp == 8 || temp2 == 8) {
            println("Rayo detectado en ($fila2, $columna2)")
            borrarFila(fila2)
            borrarColumna(columna2)
        }
    }

    fun eliminarGemasAlrededor(fila: Int, columna: Int) {
        val filas = matriz.size
        val columnas = matriz[0].size
        for (i in max(0, fila - 1)..min(filas - 1, fila + 1)) {
            for (j in max(0, columna - 1)..min(columnas - 1, columna + 1)) {
                matriz[i][j] = 0
            }
        }
    }

    fun borrarFila(fila: Int) {
        //recorre todas las columnas
        for (j in 0 until columnas) {
            matriz[fila][j] = 0
        }
    }

    fun borrarColumna(columna: Int) {
        for (i in 0 until filas) {
            matriz[i][columna] = 0
        }
    }

    fun eliminarGemasDelMismoTipo(tipo: Int, fila: Int, columna: Int) {
        for (i in 0 until filas) {
            for (j in 0 until columnas) {
                if (matriz[i][j] == tipo || (i == fila && j == columna)) {
                    matriz[i][j] = 0
                }
            }
        }
    }

}


