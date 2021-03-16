package com.example.computergraphics

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyClassRenderer(  // интерфейс GLSurfaceView.Renderer содержит
    // три метода onDrawFrame, onSurfaceChanged, onSurfaceCreated
    // которые должны быть переопределены
    // текущий контекст
    private val context: Context
) : GLSurfaceView.Renderer {
    //координаты камеры
    private val xCamera: Float
    private val yCamera: Float
    private val zCamera: Float

    //координаты источника света
    private val xLightPosition = -0.5f
    private val yLightPosition = 0.9f
    private val zLightPosition = 0.5f

    //матрицы
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    //буфер для координат вершин
    private val vertexBuffer: FloatBuffer
    private val vertexBuffer1: FloatBuffer
    private val vertexBuffer2: FloatBuffer
    private val vertexBuffer3: FloatBuffer
    private val vertexBuffer4: FloatBuffer

    //буфер для нормалей вершин
    private val normalBuffer: FloatBuffer
    private val normalBuffer1: FloatBuffer? = null

    //буфер для цветов вершин
    private val colorBuffer: FloatBuffer
    private val colorBuffer1: FloatBuffer
    private val colorBuffer2: FloatBuffer
    private val colorBuffer4: FloatBuffer

    //шейдерный объект
    private var mShader: Shader? = null
    private var mShader1: Shader? = null
    private var mShader2: Shader? = null
    private var mShader3: Shader? = null
    private var mShader4: Shader? = null

    //метод, который срабатывает при изменении размеров экрана
    //в нем мы получим матрицу проекции и матрицу модели-вида-проекции
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

    //метод, который срабатывает при создании экрана
    //здесь мы создаем шейдерный объект
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        //включаем тест глубины
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //включаем отсечение невидимых граней
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        //включаем сглаживание текстур, это пригодится в будущем
        GLES20.glHint(
            GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST
        )
        //записываем код вершинного шейдера в виде строки
        val vertexShaderCode = "uniform mat4 u_modelViewProjectionMatrix;" +
                "attribute vec3 a_vertex;" +
                "attribute vec3 a_normal;" +
                "attribute vec4 a_color;" +
                "varying vec3 v_vertex;" +
                "varying vec3 v_normal;" +
                "varying vec4 v_color;" +
                "void main() {" +
                "v_vertex=a_vertex;" +
                "vec3 n_normal=normalize(a_normal);" +
                "v_normal=n_normal;" +
                "v_color=a_color;" +
                "gl_Position = u_modelViewProjectionMatrix * vec4(a_vertex,1.0);" +
                "}"
        //записываем код фрагментного шейдера в виде строки
        val fragmentShaderCode = "precision mediump float;"+
                "uniform vec3 u_camera;"+
                "uniform vec3 u_lightPosition;"+
                "varying vec3 v_vertex;"+
                "varying vec3 v_normal;"+
                "varying vec4 v_color;"+
                "void main() {"+
                "vec3 n_normal=normalize(v_normal);"+
                "vec3 lightvector = normalize(u_lightPosition - v_vertex);"+
                "vec3 lookvector = normalize(u_camera - v_vertex);"+
                "float ambient=0.2;"+
                "float k_diffuse=0.9;"+
                "float k_specular=0.5;"+
                "float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);"+
                "vec3 reflectvector = reflect(-lightvector, n_normal);"+
                "float specular = k_specular * pow( max(dot(lookvector,reflectvector),0.0), 40.0 );"+
                "vec4 one=vec4(1.0,0.75,0.0,1.0);"+
                "vec4 lightColor = (ambient+diffuse+specular)*one;"+
                "gl_FragColor = mix(lightColor, v_color, 0.3);"+
                "}"
        //создадим шейдерный объект
        mShader = Shader(vertexShaderCode, fragmentShaderCode)
        //свяжем буфер вершин с атрибутом a_vertex в вершинном шейдере
        mShader!!.linkVertexBuffer(vertexBuffer)
        //свяжем буфер нормалей с атрибутом a_normal в вершинном шейдере
        mShader!!.linkNormalBuffer(normalBuffer)
        //свяжем буфер цветов с атрибутом a_color в вершинном шейдере
        mShader!!.linkColorBuffer(colorBuffer)
        //связь атрибутов с буферами сохраняется до тех пор,
        //пока не будет уничтожен шейдерный объект
        mShader1 = Shader(vertexShaderCode, fragmentShaderCode)
        mShader1!!.linkVertexBuffer(vertexBuffer1)
        mShader1!!.linkNormalBuffer(normalBuffer)
        mShader1!!.linkColorBuffer(colorBuffer1)
        mShader2 = Shader(vertexShaderCode, fragmentShaderCode)
        mShader2!!.linkVertexBuffer(vertexBuffer2)
        mShader2!!.linkNormalBuffer(normalBuffer)
        mShader2!!.linkColorBuffer(colorBuffer2)
        mShader3 = Shader(vertexShaderCode, fragmentShaderCode)
        mShader3!!.linkVertexBuffer(vertexBuffer3)
        mShader3!!.linkNormalBuffer(normalBuffer)
        mShader3!!.linkColorBuffer(colorBuffer2)
        mShader4 = Shader(vertexShaderCode, fragmentShaderCode)
        mShader4!!.linkVertexBuffer(vertexBuffer4)
        mShader4!!.linkNormalBuffer(normalBuffer)
        mShader4!!.linkColorBuffer(colorBuffer4)
    }

    //метод, в котором выполняется рисование кадра
    override fun onDrawFrame(unused: GL10) {
        //очищаем кадр
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //передаем в шейдерный объект матрицу модели-вида-проекции
        mShader!!.useProgram()
        mShader!!.linkVertexBuffer(vertexBuffer)
        mShader!!.linkColorBuffer(colorBuffer)
        mShader!!.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader!!.linkCamera(xCamera, yCamera, zCamera)
        mShader!!.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        mShader1!!.useProgram()
        mShader1!!.linkVertexBuffer(vertexBuffer1)
        mShader1!!.linkColorBuffer(colorBuffer1)
        mShader1!!.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader1!!.linkCamera(xCamera, yCamera, zCamera)
        mShader1!!.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        mShader2!!.useProgram()
        mShader2!!.linkVertexBuffer(vertexBuffer2)
        mShader2!!.linkColorBuffer(colorBuffer2)
        mShader2!!.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader2!!.linkCamera(xCamera, yCamera, zCamera)
        mShader2!!.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        mShader3!!.useProgram()
        mShader3!!.linkVertexBuffer(vertexBuffer3)
        mShader3!!.linkColorBuffer(colorBuffer2)
        mShader3!!.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader3!!.linkCamera(xCamera, yCamera, zCamera)
        mShader3!!.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        mShader4!!.useProgram()
        mShader4!!.linkVertexBuffer(vertexBuffer4)
        mShader4!!.linkColorBuffer(colorBuffer4)
        mShader4!!.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader4!!.linkCamera(xCamera, yCamera, zCamera)
        mShader4!!.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    //конструктор
    init {
        // запомним контекст
        // он нам понадобится в будущем для загрузки текстур

        //координаты точечного источника света


        //мы не будем двигать объекты
        //поэтому сбрасываем модельную матрицу на единичную
        Matrix.setIdentityM(modelMatrix, 0)
        //координаты камеры
        xCamera = 0.0f
        yCamera = 0.0f
        zCamera = 3.0f
        //пусть камера смотрит на начало координат
        //и верх у камеры будет вдоль оси Y
        //зная координаты камеры получаем матрицу вида
        Matrix.setLookAtM(
            viewMatrix, 0, xCamera, yCamera, zCamera, 0f, 0f, 0f, 0f, 1f, 0f
        )
        // умножая матрицу вида на матрицу модели
        // получаем матрицу модели-вида
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        //координаты вершины 1
        val x1 = -1f
        val y1 = -0.35f
        val z1 = 0.0f
        //координаты вершины 2
        val x2 = -1f
        val y2 = -1.5f
        val z2 = 0.0f
        //координаты вершины 3
        val x3 = 1f
        val y3 = -0.35f
        val z3 = 0.0f
        //координаты вершины 4
        val x4 = 1f
        val y4 = -1.5f
        val z4 = 0.0f
        //запишем координаты всех вершин в единый массив
        val vertexArray = floatArrayOf(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4)
        //coordinates for sky
        val vertexArray1 =
            floatArrayOf(-1.0f, 1.5f, 0.0f, -1.0f, -0.35f, 0.0f, 1.0f, 1.5f, 0.0f, 1.0f, -0.35f, 0f)
        //coordinates for main sail
        val vertexArray2 = floatArrayOf(-0.5f, -0.45f, 0.4f, 0.0f, -0.45f, 0.4f, 0.0f, 0.5f, 0.4f)
        //coordinates for small sail
        val vertexArray3 = floatArrayOf(0.05f, -0.45f, 0.4f, 0.22f, -0.5f, 0.4f, 0.0f, 0.25f, 0.4f)
        //coordinates for boat
        val vertexArray4 = floatArrayOf(
            -0.5f,
            -0.5f,
            0.4f,
            -0.5f,
            -0.6f,
            0.4f,
            0.22f,
            -0.5f,
            0.4f,
            0.18f,
            -0.6f,
            0.4f
        )

        //создадим буфер для хранения координат вершин
        val vertex = ByteBuffer.allocateDirect(vertexArray.size * 4)
        vertex.order(ByteOrder.nativeOrder())
        vertexBuffer = vertex.asFloatBuffer()
        vertexBuffer.position(0)
        val vertex1 = ByteBuffer.allocateDirect(vertexArray1.size * 4)
        vertex1.order(ByteOrder.nativeOrder())
        vertexBuffer1 = vertex1.asFloatBuffer()
        vertexBuffer1.position(0)
        val vertex2 = ByteBuffer.allocateDirect(vertexArray2.size * 4)
        vertex2.order(ByteOrder.nativeOrder())
        vertexBuffer2 = vertex2.asFloatBuffer()
        vertexBuffer2.position(0)
        val vertex3 = ByteBuffer.allocateDirect(vertexArray3.size * 4)
        vertex3.order(ByteOrder.nativeOrder())
        vertexBuffer3 = vertex3.asFloatBuffer()
        vertexBuffer3.position(0)
        val vertex4 = ByteBuffer.allocateDirect(vertexArray4.size * 4)
        vertex4.order(ByteOrder.nativeOrder())
        vertexBuffer4 = vertex4.asFloatBuffer()
        vertexBuffer4.position(0)

        //перепишем координаты вершин из массива в буфер
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)
        vertexBuffer1.put(vertexArray1)
        vertexBuffer1.position(0)
        vertexBuffer2.put(vertexArray2)
        vertexBuffer2.position(0)
        vertexBuffer3.put(vertexArray3)
        vertexBuffer3.position(0)
        vertexBuffer4.put(vertexArray4)
        vertexBuffer4.position(0)
        //вектор нормали перпендикулярен плоскости квадрата
        //и направлен вдоль оси Z
        val nx = 0f
        val ny = 0f
        val nz = 1f
        //нормаль одинакова для всех вершин квадрата,
        //поэтому переписываем координаты вектора нормали в массив 4 раза
        val normalArray = floatArrayOf(nx, ny, nz, nx, ny, nz, nx, ny, nz, nx, ny, nz)
        val normalArray1 = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f)
        //создадим буфер для хранения координат векторов нормали
        val normal = ByteBuffer.allocateDirect(normalArray.size * 4)
        normal.order(ByteOrder.nativeOrder())
        normalBuffer = normal.asFloatBuffer()
        normalBuffer.position(0)

        //перепишем координаты нормалей из массива в буфер
        normalBuffer.put(normalArray)
        normalBuffer.position(0)

        //разукрасим вершины квадрата, зададим цвета для вершин
        val red1 = 0f
        val green1 = 1f
        val blue1 = 1f
        //цвет второй вершины
        val red2 = 0f
        val green2 = 0f
        val blue2 = 1f
        //цвет третьей вершины
        val red3 = 0f
        val green3 = 1f
        val blue3 = 1f
        //цвет четвертой вершины
        val red4 = 0f
        val green4 = 0f
        val blue4 = 1f
        //перепишем цвета вершин в массив
        //четвертый компонент цвета (альфу) примем равным единице
        val colorArray = floatArrayOf(
            red1, green1, blue1, 1f,
            red2, green2, blue2, 1f,
            red3, green3, blue3, 1f,
            red4, green4, blue4, 1f
        )
        val colorArray1 = floatArrayOf(
            0.2f, 0.2f, 0.8f, 1f,
            0.5f, 0.5f, 1f, 1f,
            0.2f, 0.2f, 0.8f, 1f,
            0.5f, 0.5f, 1f, 1f
        )
        val colorArray2 = floatArrayOf(1f, 0.1f, 0.1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.1f, 0.1f, 1f)
        val colorArray4 = floatArrayOf(
            1f, 1f, 1f, 1f,
            0.2f, 0.2f, 0.2f, 1f, 1f, 1f, 1f, 1f,
            0.2f, 0.2f, 0.2f, 1f
        )

        //создадим буфер для хранения цветов вершин
        val color = ByteBuffer.allocateDirect(colorArray.size * 4)
        color.order(ByteOrder.nativeOrder())
        colorBuffer = color.asFloatBuffer()
        colorBuffer.position(0)
        //перепишем цвета вершин из массива в буфер
        colorBuffer.put(colorArray)
        colorBuffer.position(0)
        val color1 = ByteBuffer.allocateDirect(colorArray1.size * 4)
        color1.order(ByteOrder.nativeOrder())
        colorBuffer1 = color1.asFloatBuffer()
        colorBuffer1.position(0)
        colorBuffer1.put(colorArray1)
        colorBuffer1.position(0)
        val color2 = ByteBuffer.allocateDirect(colorArray1.size * 4)
        color2.order(ByteOrder.nativeOrder())
        colorBuffer2 = color2.asFloatBuffer()
        colorBuffer2.position(0)
        colorBuffer2.put(colorArray2)
        colorBuffer2.position(0)
        val color4 = ByteBuffer.allocateDirect(colorArray4.size * 4)
        color4.order(ByteOrder.nativeOrder())
        colorBuffer4 = color4.asFloatBuffer()
        colorBuffer4.position(0)
        colorBuffer4.put(colorArray4)
        colorBuffer4.position(0)
    } //конец конструктора
}