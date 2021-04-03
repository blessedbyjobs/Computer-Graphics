package com.example.computergraphics

import android.opengl.Matrix
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

infix fun Camera.setupOn(viewMatrix: FloatArray) {
    Matrix.setLookAtM(
        viewMatrix,
        0,
        position.x, position.y, position.z,
        direction.x, direction.y, direction.z,
        upVector.x, upVector.y, upVector.z
    )
}