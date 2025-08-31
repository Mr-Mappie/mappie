package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.compilation.KotlinCompilation
import java.util.regex.Pattern

class CompilationAssertionDsl(private val result: KotlinCompilation.Result) {

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
		assertThat(result.messages.lines())
			.noneMatch { it.startsWith("w:") || it.startsWith("e:") }
	}

	fun hasErrorMessage(line: Int, message: String, suggestions: List<String> = emptyList()) {
		assertThat(result.messages).containsPattern(
			Pattern.compile("e: file://.+\\.kt:${line}.+ ${Regex.escape(messageOf(message, suggestions))}")
		)
	}

	fun hasWarningMessage(line: Int, message: String, suggestions: List<String> = emptyList()) {
		assertThat(result.messages).containsPattern(
			Pattern.compile("w: file://.+\\.kt:${line}:.+${Regex.escape(messageOf(message, suggestions))}")
		)
	}

	fun hasOutputLines(message: String) {
		assertThat(result.messages.lines().map { it.trimEnd() })
			.containsAll(message.lines().map { it.trimEnd() })
	}

	private fun messageOf(message: String, suggestions: List<String>) =
		message + System.lineSeparator() + suggestions
			.mapIndexed { i, it -> i + 1 to it }
			.joinToString(separator = "") { "    ${it.first}. ${it.second}" + System.lineSeparator() }
}