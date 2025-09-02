package tech.mappie.testing.compilation

import io.github.classgraph.ClassGraph
import java.io.File

/**
 * Utility object to provide everything we might discover from the host environment.
 */
object HostEnvironment {
    val classpath by lazy {
        getHostClasspaths()
    }

    val kotlinStdLibJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("(kotlin-stdlib|kotlin-runtime)"))
    }

    val kotlinStdLibCommonJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-stdlib-common"))
    }

    val kotlinStdLibJdkJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-stdlib-jdk[0-9]+"))
    }

    val kotlinReflectJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-reflect"))
    }

    val kotlinScriptRuntimeJar: File? by lazy {
        findInClasspath(kotlinDependencyRegex("kotlin-script-runtime"))
    }

    private fun kotlinDependencyRegex(prefix: String): Regex {
        return Regex("$prefix(-[0-9]+\\.[0-9]+(\\.[0-9]+)?)([-0-9a-zA-Z]+)?(\\.jar|\\.klib)")
    }

    private fun findInClasspath(regex: Regex): File? =
        classpath.firstOrNull { classpath -> classpath.name.matches(regex) }

    private fun getHostClasspaths(): List<File> {
        val classGraph = ClassGraph()
            .enableSystemJarsAndModules()
            .removeTemporaryFilesAfterScan()

        val classpaths = classGraph.classpathFiles
        val modules = classGraph.modules.mapNotNull { it.locationFile }
        val klibs = System.getProperty("java.class.path")
            .split(File.pathSeparator)
            .filter { it.endsWith(".klib") }
            .map(::File)

        return (classpaths + modules + klibs).distinctBy(File::getAbsolutePath)
    }
}

