package com.example.computergraphics

import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstOpenGLProjectRenderer : GLSurfaceView.Renderer {
    private val mModelMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    /** Store our model data in a float buffer.  */
    private val mTriangle1Vertices: FloatBuffer
    private val mSquareVertices: FloatBuffer
    private var mColorHandle = 0

    /** How many bytes per float.  */
    private val mBytesPerFloat = 4
    private var mPositionHandle = 0

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
    private val x = 0f

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        GLES10.glClearColor(0.5f, 0.0f, 0.5f, 1.0f)
        val vertexShader = """attribute vec4 a_Position;     
attribute vec4 a_Color;        
varying vec4 v_Color;          
void main()                    
{                              
   v_Color = a_Color;          
   gl_Position =  a_Position; 
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

            // Bind attributes
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
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color")

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle)
    }

    // Set the OpenGL viewport to fill the entire surface.
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        GLES10.glViewport(0, 0, width, height)
    }

    // Clear the rendering surface.
    override fun onDrawFrame(glUnused: GL10) {
        //   glClear(GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        // Draw the triangle facing straight on.
        //Matrix.setIdentityM(mModelMatrix, 0);
        // Matrix.translateM(mModelMatrix, 0, x, 0.0f, 0.0f);
        drawTriangle(mTriangle1Vertices)
        drawTriangle(mSquareVertices)
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
            // (which currently contains model * view).
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

            // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
            // (which now contains model * view * projection).
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0); */

        // GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
    }

    init {
        // Define points for equilateral triangles.

        // This triangle is white_blue.First sail is mainsail
        val triangle1VerticesData = floatArrayOf(
            // X, Y, Z,
            // R, G, B, A
            -0.5f, -0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.0f, -0.25f, 0.0f,
            0.8f, 0.8f, 1.0f, 1.0f,

            0.0f, 0.56f, 0.0f,
            0.8f, 0.8f, 1.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.0f, -0.25f, 0.0f,
            0.8f, 0.8f, 1.0f, 1.0f,

            0.0f, 0.56f, 0.0f,
            0.8f, 0.8f, 1.0f, 1.0f,
        )

        val squareVerticesData = floatArrayOf(
            0.0f, -0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.0f, 0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.5f, 0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.0f, -0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.5f, -0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            0.5f, 0.25f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f
        )

        mSquareVertices = ByteBuffer.allocateDirect(squareVerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mSquareVertices.put(squareVerticesData).position(0)

        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.size * mBytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTriangle1Vertices.put(triangle1VerticesData).position(0)
    }
}