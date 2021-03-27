package com.example.computergraphics.assets

import android.content.Context
import java.io.IOException
import java.io.InputStream

object AssetReader {

    fun Context.readAsset(fileName: String): String {
        return runCatching {
            val stream: InputStream = assets.open(fileName)
            val buffer = ByteArray(stream.available())
            stream.read(buffer)
            stream.close()
            String(buffer)
        }.getOrNull().orEmpty()
    }
}