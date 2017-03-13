package com.whiterabbit.pisabike

import java.io.*


private fun getFileFromPath(obj: Any, fileName: String): File {
    val classLoader = obj.javaClass.classLoader
    val resource = classLoader.getResource(fileName)
    return File(resource.path)
}

@Throws(Exception::class)
internal fun convertStreamToString(input: InputStream): String {
    val reader = BufferedReader(InputStreamReader(input))
    with(reader) {
        val res = reader.lineSequence().reduce { r, s ->  r + s + "\n"}
        close()
        return res
    }
}

@Throws(Exception::class)
fun getStringFromFile(obj:Any, filePath: String): String {
    val fl = getFileFromPath(obj, filePath)
    val fin = FileInputStream(fl)
    val ret = convertStreamToString(fin)
    fin.close()
    return ret
}
