package com.example.computergraphics

import com.example.computergraphics.base.Coords

/**
 * Объект координат камеры
 *
 * @property position - координаты позиции камеры
 * @property direction - координаты направления камеры
 */
data class Camera(
    val position: Coords,
    val direction: Coords,
    val upVector: Coords
)