package com.example.computergraphics

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.example.computergraphics.ShaderUtils.createProgram
import com.example.computergraphics.ShaderUtils.createShader
import com.example.computergraphics.TextureUtils.loadTexture
import com.example.computergraphics.base.Coords
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class TextureLabOpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var vertexData: FloatBuffer? = null
    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0
    private var programId = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)
    private var texture = 0
    private var texture1 = 0
    private var texture2 = 0

    override fun onSurfaceCreated(arg0: GL10, arg1: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        createAndUseProgram()
        locateVariables()
        prepareData()
        bindData()
        createViewMatrix()
    }

    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }

    private fun prepareData() {
        val vertices = floatArrayOf(
            //coordinates for sky
            -2f, 4f, 0f,     0f, 0f,
            -2f, 0f, 0f,     0f, 0.5f,
            2f,  4f, 0f,     0.5f, 0f,
            2f,  0f, 0f,     0.5f, 0.5f,

            //coordinates for sea
            -2f,  0f, 0f,      0.5f, 0f,
            -2f, -1f, 2f,      0.5f, 0.5f,
            2f,   0f, 0f,      1f,   0f,
            2f,  -1f, 2f,      1f,   0.5f,

            //coordinates for dolphin
            -1f,  1f, 0.5f,      0f,   0.5f,
            -1f, -1f, 0.5f,      0f,   1f,
            1f,   1f, 0.5f,      0.5f, 0.5f,
            1f,  -1f, 0.5f,      0.5f, 1f,
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply { put(vertices) }

        texture = loadTexture(context, R.drawable.textures1)
        texture1 = loadTexture(context, R.drawable.textures1)
        texture2 = loadTexture(context, R.drawable.textures1)
    }

    private fun createAndUseProgram() {
        val vertexShaderId = context.createShader(GLES20.GL_VERTEX_SHADER, "vertex.shader")
        val fragmentShaderId = context.createShader(GLES20.GL_FRAGMENT_SHADER, "fragment.shader")
        programId = createProgram(vertexShaderId, fragmentShaderId)
        GLES20.glUseProgram(programId)
    }

    private fun locateVariables() {
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
        aTextureLocation = GLES20.glGetAttribLocation(programId, "a_Texture")
        uTextureUnitLocation = GLES20.glGetUniformLocation(programId, "u_TextureUnit")
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix")
    }

    private fun bindData() {
        // координаты вершин
        vertexData!!.position(0)
        GLES20.glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, vertexData
        )
        GLES20.glEnableVertexAttribArray(aPositionLocation)


        // координаты текстур
        vertexData!!.position(POSITION_COUNT)
        GLES20.glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, vertexData
        )
        GLES20.glEnableVertexAttribArray(aTextureLocation)


        // помещаем текстуру в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture);


        // помещаем текстуру1 в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture1);

        // помещаем текстуру2 в target 2D юнита 0

        //glActiveTexture(GL_TEXTURE0);
        //glBindTexture(GL_TEXTURE_2D, texture2);

        // юнит текстуры
        //glUniform1i(uTextureUnitLocation, 0);
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -0.5f
        var right = 0.5f
        var bottom = -0.5f
        var top = 0.5f
        val near = 2f
        val far = 12f
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
    }

    private fun createViewMatrix() {
        Camera(
            position = Coords(0f, 2f, 7f),
            direction = Coords(0f, 1f, 0f),
            upVector = Coords(0f, 1f, 0f)
        ) setupOn mViewMatrix
    }

    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0)
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    override fun onDrawFrame(arg0: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

//сбрасываем model матрицу
        Matrix.setIdentityM(mModelMatrix, 0)
        bindMatrix()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2)
        Matrix.setIdentityM(mModelMatrix, 0)
        setModelMatrix()
        bindMatrix()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4)
    }

    private fun setModelMatrix() {
        Matrix.translateM(mModelMatrix, 0, 0f, -0.5f, 0f)
        //В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.
        val angle = (-(SystemClock.uptimeMillis() % TIME)).toFloat() / TIME * 360
        //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
        Matrix.rotateM(mModelMatrix, 0, angle, 0f, 0f, 1f)
        Matrix.translateM(mModelMatrix, 0, -0.8f, 0f, 0f)
    }

    companion object {
        private const val POSITION_COUNT = 3
        private const val TEXTURE_COUNT = 2
        private const val STRIDE = (POSITION_COUNT
                + TEXTURE_COUNT) * 4
        private const val TIME = 10000L
    }
}