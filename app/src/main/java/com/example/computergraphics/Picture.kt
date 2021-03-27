package com.example.computergraphics

import com.example.computergraphics.base.Color
import com.example.computergraphics.base.Coords
import com.example.computergraphics.geometry.Figure
import com.example.computergraphics.geometry.Point
import com.example.computergraphics.geometry.Rectangle
import com.example.computergraphics.geometry.Triangle

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

/**
 * Картинка с домиком
 */
object HousePicture : Picture() {

    override val figures: List<Figure> = listOf(
        roof,
        building,
        door,
        window1,
        window2,
        sky,
        land
    )

    private val roof: Triangle
        get() {
            val roofColor = Color(254, 67, 0)
            val firstPosition = Coords(0.0f, 0.75f, 0.4f)
            val secondPosition = Coords(-0.85f, 0.125f, 0.4f)
            val thirdPosition = Coords(0.85f, 0.125f, 0.4f)
            return Triangle(
                first = Point(firstPosition, roofColor),
                second = Point(secondPosition, roofColor),
                third = Point(thirdPosition, roofColor),
                normalVector = defaultNormalVector
            )
        }

    private val building: Rectangle
        get() {
            val buildingColor = Color(253, 234, 83)
            val leftTopPosition = Coords(-0.6f, 0.125f, 0.4f)
            val leftBottomPosition = Coords(-0.6f, -0.8f, 0.4f)
            val rightBottomPosition = Coords(0.6f, -0.8f, 0.4f)
            val rightTopPosition = Coords(0.6f, 0.125f, 0.4f)
            return Rectangle(
                leftTop = Point(leftTopPosition, buildingColor),
                leftBottom = Point(leftBottomPosition, buildingColor),
                rightBottom = Point(rightBottomPosition, buildingColor),
                rightTop = Point(rightTopPosition, buildingColor),
                normalVector = defaultNormalVector
            )
        }

    private val door: Rectangle
        get() {
            val doorColor = Color(215, 200, 99)
            val leftTopPosition = Coords(-0.2f, -0.1f, 0.41f)
            val leftBottomPosition = Coords(-0.2f, -0.8f, 0.41f)
            val rightBottomPosition = Coords(0.15f, -0.8f, 0.41f)
            val rightTopPosition = Coords(0.15f, -0.1f, 0.41f)
            return Rectangle(
                leftTop = Point(leftTopPosition, doorColor),
                leftBottom = Point(leftBottomPosition, doorColor),
                rightBottom = Point(rightBottomPosition, doorColor),
                rightTop = Point(rightTopPosition, doorColor),
                normalVector = defaultNormalVector
            )
        }

    private val window1: Rectangle
        get() {
            val windowColor = Color(253, 241, 206)
            val leftTopPosition = Coords(-0.56f, -0.1f, 0.41f)
            val leftBottomPosition = Coords(-0.56f, -0.4f, 0.41f)
            val rightBottomPosition = Coords(-0.27f, -0.4f, 0.41f)
            val rightTopPosition = Coords(-0.27f, -0.1f, 0.41f)
            return Rectangle(
                leftTop = Point(leftTopPosition, windowColor),
                leftBottom = Point(leftBottomPosition, windowColor),
                rightBottom = Point(rightBottomPosition, windowColor),
                rightTop = Point(rightTopPosition, windowColor),
                normalVector = defaultNormalVector
            )
        }

    private val window2: Rectangle
        get() {
            val windowColor = Color(253, 241, 206)
            val leftTopPosition = Coords(0.23f, -0.1f, 0.41f)
            val leftBottomPosition = Coords(0.23f, -0.4f, 0.41f)
            val rightBottomPosition = Coords(0.52f, -0.4f, 0.41f)
            val rightTopPosition = Coords(0.52f, -0.1f, 0.41f)
            return Rectangle(
                leftTop = Point(leftTopPosition, windowColor),
                leftBottom = Point(leftBottomPosition, windowColor),
                rightBottom = Point(rightBottomPosition, windowColor),
                rightTop = Point(rightTopPosition, windowColor),
                normalVector = defaultNormalVector
            )
        }

    private val sky: Rectangle
        get() {
            val leftTopPosition = Coords(-1.0f, 1.5f, 0.0f)
            val leftBottomPosition = Coords(-1.0f, -0.35f, 0.0f)
            val rightBottomPosition = Coords(1.0f, 1.5f, 0.0f)
            val rightTopPosition = Coords(1.0f, -0.35f, 0f)
            val skyColor = Color(0,191,255)
            return Rectangle(
                leftTop = Point(leftTopPosition, skyColor),
                leftBottom = Point(leftBottomPosition, skyColor),
                rightTop = Point(rightBottomPosition, skyColor),
                rightBottom = Point(rightTopPosition, skyColor),
                normalVector = defaultNormalVector
            )
        }

    private val land: Rectangle
        get() {
            val leftTopPosition = Coords(-1.0f, -0.35f, 0.0f)
            val leftBottomPosition = Coords(-1.0f, -1.5f, 0.0f)
            val rightBottomPosition = Coords(1.0f, -0.35f, 0.0f)
            val rightTopPosition = Coords(1.0f, -1.5f, 0f)
            val landColor = Color(105, 147, 72)
            return Rectangle(
                leftTop = Point(leftTopPosition, landColor),
                leftBottom = Point(leftBottomPosition, landColor),
                rightTop = Point(rightBottomPosition, landColor),
                rightBottom = Point(rightTopPosition, landColor),
                normalVector = defaultNormalVector
            )
        }
}