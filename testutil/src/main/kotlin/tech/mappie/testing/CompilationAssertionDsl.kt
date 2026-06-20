package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.compilation.CompilationResult
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.Log

class CompilationAssertionDsl(private val result: CompilationResult) {

	val classLoader = result.classLoader

	infix fun satisfies(dsl: CompilationAssertionDsl.() -> Unit) =
		dsl(this)

	fun isOk() {
		assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
	}

	fun isCompilationError() {
		assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
	}

	fun hasNoWarningsOrErrors() {
		assertThat(result.logs.warnings).isEmpty()
		assertThat(result.logs.errors).isEmpty()
	}

	fun hasErrorMessages(vararg logs: Pair<Int, String>) {
		assertThat(result.logs.errors).containsExactlyInAnyOrder(*logs
			.map { Log(Log.Level.ERROR, it.first, it.second, emptyList()) }
			.toTypedArray()
		)
	}

	fun hasErrorMessages(vararg logs: Triple<Int, String, List<String>>) {
		assertThat(result.logs.errors).containsExactlyInAnyOrder(*logs
			.map { Log(Log.Level.ERROR, it.first, it.second, it.third) }
			.toTypedArray()
		)
	}

	fun hasSingleErrorMessage(line: Int, message: String, suggestions: List<String> = emptyList()) {
		assertThat(result.logs.errors).containsExactly(Log(Log.Level.ERROR, line, message, suggestions))
	}

	fun hasSingleWarningMessage(line: Int, message: String, suggestions: List<String> = emptyList()) {
		assertThat(result.logs.warnings).containsExactly(Log(Log.Level.WARNING, line, message, suggestions))
	}

	fun assertHasLogMessage(message: String) {
		assertThat(result.logs.complete.lines().map { it.trimEnd() })
			.containsAll(message.lines().map { it.trimEnd() })
	}
}