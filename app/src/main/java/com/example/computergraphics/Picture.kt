package com.example.computergraphics

import com.example.computergraphics.base.Color
import com.example.computergraphics.base.Coords
import com.example.computergraphics.geometry.*

val defaultNormalVector = Coords(0f, 0f, 1f)

/**
 * Обертка над некоторым рисунком, составленным из фигур
 */
abstract class Picture {

    abstract val figures: List<Figure>

    open val camera = Camera(
        position = Coords(0f, 0f, 3f),
        direction = Coords(0f, 0f, 0f),
        upVector = Coords(0f, 1f, 0f)
    )

    open val lightPosition = Coords(-0.5f, 0.9f, 0.5f)
}

/**
 * Рисунок с лодкой в море
 */
object BoatPicture : Picture() {

    override val figures: List<Figure> = listOf(
        sea,
        boat,
        sky,
        mainSail,
        smallSail
    )

    private val boat
        get() = Rectangle(
            leftTop = Point(Coords(-0.5f, -0.5f, 0.4f), Color(1f, 1f, 1f, 1f)),
            leftBottom = Point(Coords(-0.5f, -0.6f, 0.4f), Color(0.2f, 0.2f, 0.2f, 1f)),
            rightTop = Point(Coords(0.22f, -0.5f, 0.4f), Color(1f, 1f, 1f, 1f)),
            rightBottom = Point(Coords(0.18f, -0.6f, 0.4f), Color(0.2f, 0.2f, 0.2f, 1f)),
            normalVector = defaultNormalVector
        )

    private val sky
        get() = Rectangle(
            leftTop = Point(Coords(-1.0f, 1.5f, 0.0f), Color(0.2f, 0.2f, 0.8f, 1f)),
            leftBottom = Point(Coords(-1.0f, -0.35f, 0.0f), Color(0.5f, 0.5f, 1f, 1f)),
            rightTop = Point(Coords(1.0f, 1.5f, 0.0f), Color(0.2f, 0.2f, 0.8f, 1f)),
            rightBottom = Point(Coords(1.0f, -0.35f, 0f), Color(0.5f, 0.5f, 1f, 1f)),
            normalVector = defaultNormalVector
        )

    private val sea
        get() = Rectangle(
            leftTop = Point(Coords(-1.0f, -0.35f, 0.0f), Color(0f, 1f, 1f, 1f)),
            leftBottom = Point(Coords(-1.0f, -1.5f, 0.0f), Color(0f, 0f, 1f, 1f)),
            rightTop = Point(Coords(1.0f, -0.35f, 0.0f), Color(0f, 1f, 1f, 1f)),
            rightBottom = Point(Coords(1.0f, -1.5f, 0f), Color(0f, 0f, 1f, 1f)),
            normalVector = defaultNormalVector
        )

    private val mainSail
        get() = Triangle(
            first = Point(Coords(-0.5f, -0.45f, 0.4f), Color(1f, 0.1f, 0.1f, 1f)),
            second = Point(Coords(0.0f, -0.45f, 0.4f), Color(1f, 1f, 1f, 1f)),
            third = Point(Coords(0.0f, 0.5f, 0.4f), Color(1f, 0.1f, 0.1f, 1f)),
            normalVector = defaultNormalVector
        )

    private val smallSail
        get() = Triangle(
            first = Point(Coords(0.05f, -0.45f, 0.4f), Color(1f, 0.1f, 0.1f, 1f)),
            second = Point(Coords(0.22f, -0.5f, 0.4f), Color(1f, 1f, 1f, 1f)),
            third = Point(Coords(0.0f, 0.25f, 0.4f), Color(1f, 0.1f, 0.1f, 1f)),
            normalVector = defaultNormalVector
        )
}