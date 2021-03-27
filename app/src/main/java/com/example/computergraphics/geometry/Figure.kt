package com.example.computergraphics.geometry

import java.nio.FloatBuffer


/**
 * Некоторая абстрактная фигура.
 * Является эдаким адаптером
 * от координатного представления фигуры
 * к буферному представлению для рендера
 */
abstract class Figure {

    abstract val colorBuffer: FloatBuffer

    abstract val normalBuffer: FloatBuffer

    abstract val coordsBuffer: FloatBuffer
}