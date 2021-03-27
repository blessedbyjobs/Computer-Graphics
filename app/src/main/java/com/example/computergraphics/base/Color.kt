package com.example.computergraphics.base

/**
 * Объект цвета
 */
data class Color(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1f
) {

    constructor(
        red: Int,
        green: Int,
        blue: Int,
        alpha: Float = 1f
    ): this(
        red = red / 255f,
        green = green / 255f,
        blue = blue / 255f,
        alpha = alpha
    )
}