package com.example.computergraphics

import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer : GLSurfaceView.Renderer {
    // матрица модели
    private val mModelMatrix = FloatArray(16)

    // видовая матрица
    private val mViewMatrix = FloatArray(16)

    // модельновидовая матрица
    private val mMVPMatrix = FloatArray(16)

    // проекционная матрица
    private val mProjectionMatrix = FloatArray(16)

    /** буфер VBO.  */
    private val roofVertices: FloatBuffer
    private val doorVertices: FloatBuffer
    private val buildingVertices: FloatBuffer
    private val windowVertices: FloatBuffer

    // переменная матрицы трансформации
    private var mMVPMatrixHandle = 0

    // переменная для model position данных
    private var mPositionHandle = 0
    private var mColorHandle = 0

    /** How many bytes per float.  */
    private val mBytesPerFloat = 4

    /** How many elements per vertex.  */
    private val mStrideBytes = 7 * mBytesPerFloat

    /** Offset of the position data.  */
    private val mPositionOffset = 0

    /** Size of the position data in elements.  */
    private val mPositionDataSize = 3

    /** Offset of the color data.  */
    private val mColorOffset = 3

    /** Size of the color data in elements.  */
    private val mColorDataSize = 4
    private var x = 0f
    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        GLES10.glClearColor(1f, 1f, 1f, 1.0f)

        // Позиция камеры за объектом
        val eyeX = 0.0f
        val eyeY = 0.0f
        val eyeZ = 1.5f

        // Определяем напрвление камеры
        val lookX = 0.0f
        val lookY = 0.0f
        val lookZ = -5.0f

        // Устанавливаем позицию up-вектора камеры. This is where our head would be pointing were we holding the camera.
        val upX = 0.0f
        val upY = 1.0f
        val upZ = 0.0f

        //установление камеры (матрицы просмотра)
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ)
        val vertexShader = """uniform mat4 u_MVPMatrix;      
attribute vec4 a_Position;     
attribute vec4 a_Color;        
varying vec4 v_Color;          
void main()                    
{                              
   v_Color = a_Color;          
   gl_Position = u_MVPMatrix * a_Position; 
}                              
""" // normalized screen coordinates.
        val fragmentShader = """precision mediump float;       
varying vec4 v_Color;          
void main()                    
{                              
   gl_FragColor = v_Color;     
}                              
"""

        // Load in the vertex shader.
        var vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        if (vertexShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader)

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle)
                vertexShaderHandle = 0
            }
        }
        if (vertexShaderHandle == 0) {
            throw RuntimeException("Error creating vertex shader.")
        }

        // Load in the fragment shader shader.
        var fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        if (fragmentShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader)

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle)

            // Get the compilation status.
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShaderHandle)
                fragmentShaderHandle = 0
            }
        }
        if (fragmentShaderHandle == 0) {
            throw RuntimeException("Error creating fragment shader.")
        }

        // Create a program object and store the handle to it.
        var programHandle = GLES20.glCreateProgram()
        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle)

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)

            //  Привязка атрибутов
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position")
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color")

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw RuntimeException("Error creating program.")
        }

// Устанавливаем программные переменные в переменныен шейдора uniform and atribut
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color")

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle)
    }

    // Set the OpenGL viewport to fill the entire surface.
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Создаем новую перспективнро-поекционную матрицу. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = width.toFloat() / height
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f
        Matrix.frustumM(mProjectionMatrix, 0, left, ratio, bottom, top, near, far)
    }

    // Clear the rendering surface.
    override fun onDrawFrame(glUnused: GL10) {
        //   glClear(GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        // Draw the triangle facing straight on.
        //Matrix.setIdentityM(mModelMatrix, 0);
        // Matrix.translateM(mModelMatrix, 0, x, 0.0f, 0.0f);
        drawTriangle(roofVertices)

        // Draw triangle_2.
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.translateM(mModelMatrix, 0, x + 0.3f, 0.0f, 0.0f)
        drawTriangle(buildingVertices)

        // Draw triangle_2.
//        Matrix.setIdentityM(mModelMatrix, 0)
//        Matrix.translateM(mModelMatrix, 0, x + 0.3f, 0.0f, 0.0f)
        drawTriangle(doorVertices)

        // Draw triangle_2.
//        Matrix.setIdentityM(mModelMatrix, 0)
//        Matrix.translateM(mModelMatrix, 0, x + 0.3f, 0.0f, 0.0f)
        drawTriangle(windowVertices)
        x = if (x <= 1) {
            (x + 0.001).toFloat()
        } else {
            0f
        }
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    private fun drawTriangle(aTriangleBuffer: FloatBuffer) {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset)
        GLES20.glVertexAttribPointer(
            mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
            mStrideBytes, aTriangleBuffer
        )
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset)
        GLES20.glVertexAttribPointer(
            mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
            mStrideBytes, aTriangleBuffer
        )
        GLES20.glEnableVertexAttribArray(mColorHandle)

        /* This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
            // (which currently contains model * view).*/
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).*/
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 13)
    }

    init {
        // Define points for equilateral triangles.

        val roofColor = Coords(129, 138, 193)
        val windowColor = Coords(238, 193, 65)
        val roofVerticesData = floatArrayOf(
            // X, Y, Z,
            // R, G, B, A
            0.0f, 0.75f, 0.0f,
            roofColor.redColor, roofColor.greenColor, roofColor.blueColor, 1.0f,
            0.89f, 0.3125f, 0.0f,
            roofColor.redColor, roofColor.greenColor, roofColor.blueColor, 1.0f,
            -0.89f, 0.3125f, 0.0f,
            roofColor.redColor, roofColor.greenColor, roofColor.blueColor, 1.0f,
        )
        val buildingVerticesData = buildRectangleVertices(
            leftTop = Coords(-0.89f, 0.3125f),
            leftBottom = Coords(-0.89f, -0.75f),
            rightBottom = Coords(0.89f, -0.75f),
            rightTop = Coords(0.89f, 0.3125f),
            color = Coords(187, 197, 220)
        )

        val doorVerticesData = buildRectangleVertices(
            leftTop = Coords(-0.67f, -0.1875f),
            leftBottom = Coords(-0.67f, -0.75f),
            rightBottom = Coords(-0.11f, -0.75f),
            rightTop = Coords(-0.11f, -0.1875f),
            color = roofColor
        )

        val windowVerticesData = buildRectangleVertices(
            leftTop = Coords(0.167f, 0.125f),
            leftBottom = Coords(0.167f, -0.21875f),
            rightBottom = Coords(0.67f, -0.21875f),
            rightTop = Coords(0.67f, 0.125f),
            color = windowColor
        )


        roofVertices = ByteBuffer.allocateDirect(roofVerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        windowVertices = ByteBuffer.allocateDirect(windowVerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        doorVertices = ByteBuffer.allocateDirect(doorVerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        buildingVertices = ByteBuffer.allocateDirect(buildingVerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()

        roofVertices.put(roofVerticesData).position(0)
        doorVertices.put(doorVerticesData).position(0)
        windowVertices.put(windowVerticesData).position(0)
        buildingVertices.put(buildingVerticesData).position(0)
    }

    private fun buildRectangleVertices(
        leftTop: Coords,
        rightTop: Coords,
        rightBottom: Coords,
        leftBottom: Coords,
        color: Coords
    ) =
        floatArrayOf(
            // X, Y, Z,
            // R, G, B, A
            leftTop.x, leftTop.y, leftTop.z,
            color.redColor, color.greenColor, color.blueColor, 1f,
            rightTop.x, rightTop.y, rightTop.z,
            color.redColor, color.greenColor, color.blueColor, 1f,
            leftBottom.x, leftBottom.y, leftBottom.z,
            color.redColor, color.greenColor, color.blueColor, 1f,

            rightBottom.x, rightBottom.y, rightBottom.z,
            color.redColor, color.greenColor, color.blueColor, 1f,
            rightTop.x, rightTop.y, rightTop.z,
            color.redColor, color.greenColor, color.blueColor, 1f,
            leftBottom.x, leftBottom.y, leftBottom.z,
            color.redColor, color.greenColor, color.blueColor, 1f
        )
}

data class Coords(
    val x: Float,
    val y: Float,
    val z: Float = 0.0f
) {

    constructor(r: Int, g: Int, b: Int = 0): this(
        x = r.toFloat(),
        y = g.toFloat(),
        z = b.toFloat()
    )
}

val Coords.redColor: Float
    get() = x / 255

val Coords.greenColor: Float
    get() = y / 255

val Coords.blueColor: Float
    get() = z / 255
