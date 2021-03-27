package com.example.computergraphics.geometry

import com.example.computergraphics.base.Color
import com.example.computergraphics.base.Coords
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Преобразование объекта координат в массив [FloatArray]
 */
fun Coords.toFloatArray(): FloatArray {
    return floatArrayOf(x, y, z)
}

/**
 * Преобразование объекта цвета в массив [FloatArray]
 */
fun Color.toFloatArray(): FloatArray {
    return floatArrayOf(red, green, blue, alpha)
}

/**
 * Преобразование [FloatArray] к [FloatBuffer]
 */
fun FloatArray.buildFloatBuffer(): FloatBuffer {
    val buffer = ByteBuffer.allocateDirect(size * 4)
    buffer.order(ByteOrder.nativeOrder())
    return buffer.asFloatBuffer().apply {
        position(0)
        put(this@buildFloatBuffer)
        position(0)
    }
}