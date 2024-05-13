package com.example.practicadibujotablero.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.practicadibujotablero.R
import com.example.practicadibujotablero.ui.model.Tablero

class TableroView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var model: Tablero = Tablero()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val gemasImg: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_1),
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_2),
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_3),
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_4),
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_5),
        BitmapFactory.decodeResource(context?.resources, R.drawable.gema_6),
        BitmapFactory.decodeResource(context?.resources, R.drawable.cubo),
        BitmapFactory.decodeResource(context?.resources, R.drawable.rayo),
        BitmapFactory.decodeResource(context?.resources, R.drawable.fuegoo)
    )


    private var gemaSeleccionadaFila = -1
    private var gemaSeleccionadaColumna = -1

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val board = model
        val ancho = width / board.columnas
        val alto = height / board.filas
        for (i in 0 until board.filas) {
            for (j in 0 until board.columnas) {
                val gema = board.matriz[i][j]
                if (gema in 1..9) {
                    val scaledBitmap = Bitmap.createScaledBitmap(gemasImg[gema - 1], ancho, alto, false)
                    canvas.drawBitmap(
                        scaledBitmap,
                        (j * ancho).toFloat(),
                        (i * alto).toFloat(),
                        paint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (model.juegoFinalizado) {
            return false
        }
        //posicion de la gema seleccionada
        val x = event.x.toInt()
        val y = event.y.toInt()
        val columna = x / (width / model.columnas)
        val fila = y / (height / model.filas)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Registra la gema
                gemaSeleccionadaFila = fila
                gemaSeleccionadaColumna = columna

            }
            MotionEvent.ACTION_UP -> {
                // verifica si el usuario selecciono una gema
                if (gemaSeleccionadaFila != -1 && gemaSeleccionadaColumna != -1) {
                    val direccion = if (fila == gemaSeleccionadaFila) {
                        if (columna > gemaSeleccionadaColumna) "derecha" else "izquierda"
                    } else {
                        if (fila > gemaSeleccionadaFila) "abajo" else "arriba"
                    }
                    model.intercambiarGemas(gemaSeleccionadaFila, gemaSeleccionadaColumna, fila, columna, direccion)
                    model.eliminarGemas()
                    invalidate() // Redibuja

                    Handler().postDelayed({
                        model.realizarCascada()
                        invalidate()
                    }, 1000) // Retraso de 1 segundo


                    gemaSeleccionadaFila = -1
                    gemaSeleccionadaColumna = -1
                }
            }
        }
        return true
    }

}