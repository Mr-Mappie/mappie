package tech.mappie.testing.compilation

import java.io.File
import java.net.URLClassLoader
import java.util.regex.Pattern
import kotlin.collections.plus

class CompilationResult(
    private val kotlinCompilation: KotlinCompilation,
    val exitCode: KotlinCompilation.ExitCode,
    private val messages: String,
) {
    val logs = Logs(messages)

    val classLoader = URLClassLoader(
        kotlinCompilation.classpaths.plus(outputDirectory).map { it.toURI().toURL() }.toTypedArray(),
        this::class.java.classLoader
    )

    val outputDirectory: File get() = kotlinCompilation.classesDir
}

data class Logs(val complete: String) {

    private val logs: Map<Log.Level, List<Log>> by lazy {
        complete.split(Pattern.compile("(?m)(?=^(w:|e:|i:))"))
            .filter(String::isNotBlank)
            .map(Log::parse)
            .groupBy { it.level }
    }

    val warnings: List<Log> get() = logs[Log.Level.WARNING] ?: emptyList()
    val errors: List<Log> get() = logs[Log.Level.ERROR] ?: emptyList()
}

data class Log(val level: Level, val line: Int?, val message: String, val suggestions: List<String> = emptyList()) {

    enum class Level { ERROR, WARNING, INFO, UNKNOWN }

    companion object {
        private val regex = Regex(
            """^(i:|w:|e:)\s+(?:(file:///.+?):(\d+):\d+\s+)?(.+)$"""
        )

        fun parse(input: String): Log {
            val lines = input.lines().filter { it.isNotBlank() }

            val first = lines.first()
            val match = regex.matchEntire(first)
                ?: error("Cannot parse log line: $first")

            val (levelRaw, _, lineRaw, message) = match.destructured

            return Log(
                level = when (levelRaw) {
                    "i:" -> Level.INFO
                    "w:" -> Level.WARNING
                    "e:" -> Level.ERROR
                    else -> Level.UNKNOWN
                },
                line = lineRaw.takeIf { it.isNotEmpty() }?.toInt(),
                message = message.trim(),
                suggestions = lines.drop(1).map {
                    it.trim().substringAfter(" ")
                }
            )
        }
    }
}
