import java.io.File
import java.io.FileInputStream
import java.util.*

fun getLocalProperty(key: String): String {
    val prop = Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }
    return prop.getProperty(key)
}