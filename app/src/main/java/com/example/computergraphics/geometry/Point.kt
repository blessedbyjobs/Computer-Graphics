package com.example.computergraphics.geometry

import com.example.computergraphics.base.Color
import com.example.computergraphics.base.Coords

/**
 * Объект некоторой точки,
 * содержащей данные о позиции и цвете
 */
data class Point(
    val coords: Coords,
    val color: Color
)