package com.example.computergraphics

import android.content.Context
import android.opengl.GLSurfaceView

class LabSurfaceView(context: Context) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(2)
        setRenderer(LabRenderer(context))
        // установим режим циклического запуска метода onDrawFrame
        renderMode = RENDERMODE_CONTINUOUSLY
        // при этом запускается отдельный поток
        // в котором циклически вызывается метод onDrawFrame
        // т.е. бесконечно происходит перерисовка кадров
    }
}
