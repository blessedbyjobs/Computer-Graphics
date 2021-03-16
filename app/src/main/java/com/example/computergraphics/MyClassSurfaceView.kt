package com.example.computergraphics

import android.content.Context
import android.opengl.GLSurfaceView


//Опишем наш класс MyClassSurfaceView расширяющий GLSurfaceView
class MyClassSurfaceView(context: Context) : GLSurfaceView(context) {
    //создадим ссылку для хранения экземпляра нашего класса рендерера
//    private val renderer: MyClassRenderer

    // конструктор
    init {
        // вызовем конструктор родительского класса GLSurfaceView
        setEGLContextClientVersion(2)
        // создадим экземпляр нашего класса MyClassRenderer
//        renderer = MyClassRenderer(context)
        // запускаем рендерер
        setRenderer(MyClassRenderer(context))
        // установим режим циклического запуска метода onDrawFrame
        renderMode = RENDERMODE_CONTINUOUSLY
        // при этом запускается отдельный поток
        // в котором циклически вызывается метод onDrawFrame
        // т.е. бесконечно происходит перерисовка кадров
    }
}

