package tech.mappie.testing.compilation

import java.io.*
import javax.lang.model.SourceVersion

internal fun getJavaHome(): File {
    val path = System.getProperty("java.home")
        ?: System.getenv("JAVA_HOME")
        ?: throw IllegalStateException("no java home found")

    return File(path).also { check(it.isDirectory) }
}

internal val processJdkHome by lazy {
    if(isJdk9OrLater())
        getJavaHome()
    else
        getJavaHome().parentFile
}

/** Checks if the JDK of the host process is version 9 or later */
internal fun isJdk9OrLater(): Boolean =
    SourceVersion.latestSupported() > SourceVersion.RELEASE_8

internal fun File.listFilesRecursively(): List<File> {
    return (listFiles() ?: throw RuntimeException("listFiles() was null. File is not a directory or I/O error occurred"))
        .flatMap { file ->
        if(file.isDirectory)
            file.listFilesRecursively()
        else
            listOf(file)
    }
}

internal fun File.hasKotlinFileExtension() = hasFileExtension(listOf("kt", "kts"))

internal fun File.hasFileExtension(extensions: List<String>)
    = extensions.any{ it.equals(extension, ignoreCase = true) }

internal inline fun <T> withSystemProperty(key: String, value: String, f: () -> T): T
        = withSystemProperties(mapOf(key to value), f)


internal inline fun <T> withSystemProperties(properties: Map<String, String>, f: () -> T): T {
    val previousProperties = mutableMapOf<String, String?>()

    for ((key, value) in properties) {
        previousProperties[key] = System.getProperty(key)
        System.setProperty(key, value)
    }

    try {
        return f()
    } finally {
        for ((key, value) in previousProperties) {
            if (value != null)
                System.setProperty(key, value)
        }
    }
}
