package com.example.computergraphics.geometry

import com.example.computergraphics.base.Coords
import java.nio.FloatBuffer

/**
 *
 */
data class Triangle(
    val first: Point,
    val second: Point,
    val third: Point,
    val normalVector: Coords
): Figure() {

    override val colorBuffer: FloatBuffer
        get() = colorArray.buildFloatBuffer()

    override val coordsBuffer: FloatBuffer
        get() = coordsArray.buildFloatBuffer()


    override val normalBuffer: FloatBuffer
        get() = normalArray.buildFloatBuffer()

    private val points = listOf(first, second, third)

    private val colorArray = points
        .map { it.color.toFloatArray() }
        .reduce { l, r -> l + r }

    private val coordsArray = points
        .map { it.coords.toFloatArray() }
        .reduce { l, r -> l + r }

    private val normalArray = points
        .map { normalVector.toFloatArray() }
        .reduce { l, r -> l + r }
}