package com.example.computergraphics

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.computergraphics.assets.AssetReader.readAsset
import com.example.computergraphics.geometry.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Рендерер для лабораторных работ
 */
class LabRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    //матрицы
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private val picture: Picture = HousePicture
    private val shaders = mutableListOf<Shader>()

    /**
     * метод, который срабатывает при изменении размеров экрана
     * в нем мы получим матрицу проекции и матрицу модели-вида-проекции
     */
    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // устанавливаем glViewport
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val k = 0.055f
        val left = -k * ratio
        val right = k * ratio
        val bottom = -k
        val near = 0.1f
        val far = 10.0f
        // получаем матрицу проекции
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, k, near, far)
        // матрица проекции изменилась,
        // поэтому нужно пересчитать матрицу модели-вида-проекции
        Matrix.multiplyMM(
            modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0
        )
    }

    /**
     * метод, который срабатывает при создании экрана
     * здесь мы создаем шейдерный объект
     */
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        //включаем тест глубины
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //включаем отсечение невидимых граней
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        //включаем сглаживание текстур, это пригодится в будущем
        GLES20.glHint(
            GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST
        )
        val vertexShaderCode = context.readAsset("vertex.shader")
        val fragmentShaderCode = context.readAsset("fragment.shader")

        picture.figures.map { figure ->
            Shader(vertexShaderCode, fragmentShaderCode).apply {
                linkVertexBuffer(figure.coordsBuffer)
                linkColorBuffer(figure.colorBuffer)
                linkNormalBuffer(figure.normalBuffer)
            }
        }.let { shaders.addAll(it) }
    }

    /**
     * Метод, в котором выполняется рисование кадра
     */
    override fun onDrawFrame(unused: GL10) {
        //очищаем кадр
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val cameraPosition = picture.camera.position
        val lightPosition = picture.lightPosition

        picture.figures.forEachIndexed { index, figure ->
            val shader = shaders[index]
            shader.apply {
                useProgram()
                linkVertexBuffer(figure.coordsBuffer)
                linkColorBuffer(figure.colorBuffer)
                linkNormalBuffer(figure.normalBuffer)
                linkModelViewProjectionMatrix(modelViewProjectionMatrix)
                linkCamera(cameraPosition.x, cameraPosition.y, cameraPosition.z)
                linkLightSource(lightPosition.x, lightPosition.y, lightPosition.z)
            }
            if (figure is Triangle) {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
            } else {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            }
        }
    }

    init {
        //мы не будем двигать объекты
        //поэтому сбрасываем модельную матрицу на единичную
        Matrix.setIdentityM(modelMatrix, 0)
        with(picture.camera) {
            Matrix.setLookAtM(
                viewMatrix, 0,
                position.x, position.y, position.z,
                direction.x, direction.y, direction.z,
                upVector.x, upVector.y, upVector.z
            )
        }
        // умножая матрицу вида на матрицу модели
        // получаем матрицу модели-вида
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    }
}