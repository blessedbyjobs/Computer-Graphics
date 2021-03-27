package com.example.computergraphics.geometry

import com.example.computergraphics.base.Coords
import java.nio.FloatBuffer

data class Rectangle(
    val leftTop: Point,
    val leftBottom: Point,
    val rightBottom: Point,
    val rightTop: Point,
    val normalVector: Coords
): Figure() {

    override val colorBuffer: FloatBuffer
        get() = colorArray.buildFloatBuffer()

    override val coordsBuffer: FloatBuffer
        get() = coordsArray.buildFloatBuffer()


    override val normalBuffer: FloatBuffer
        get() = normalArray.buildFloatBuffer()

    private val orderedPoints = listOf(
        leftTop, leftBottom, rightTop, rightBottom
    )

    private val colorArray = orderedPoints
        .map { it.color.toFloatArray() }
        .reduce { l, r -> l + r }

    private val coordsArray = orderedPoints
        .map { it.coords.toFloatArray() }
        .reduce { l, r -> l + r }

    private val normalArray = orderedPoints
        .map { normalVector.toFloatArray() }
        .reduce { l, r -> l + r }
}