/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.mappie.testing.compilation

import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.JVMAssertionsMode
import org.jetbrains.kotlin.config.JvmTarget
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.*
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.text.Regex.Companion.escape

data class PluginOption(val pluginId: PluginId, val optionName: OptionName, val optionValue: OptionValue)

typealias PluginId = String
typealias OptionName = String
typealias OptionValue = String

fun compile(directory: File, verbose: Boolean = false, dsl: CompilationDsl.() -> Unit): CompilationAssertionDsl =
	KotlinCompilation(directory).let {
		it.verbose = verbose
		dsl.invoke(CompilationDsl(it))
		CompilationAssertionDsl(it.compile())
	}

class CompilationDsl(private val compilation: KotlinCompilation) {
	fun file(name: String, @Language("kotlin") contents: String, trimIndent: Boolean = true, isMultiplatformCommonSource: Boolean = false) {
		compilation.sources.add(kotlin(name, contents, trimIndent, isMultiplatformCommonSource))
	}
}

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
			Pattern.compile("e: file://.+\\.kt:${line}.+ ${escape(messageOf(message, suggestions))}")
		)
	}

	fun hasWarningMessage(line: Int, message: String, suggestions: List<String> = emptyList()) {
		assertThat(result.messages).containsPattern(
			Pattern.compile("w: file://.+\\.kt:${line}:.+${escape(messageOf(message, suggestions))}")
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

@Suppress("MemberVisibilityCanBePrivate", "unused")
class KotlinCompilation(workingDir: File) : AbstractKotlinCompilation<K2JVMCompilerArguments>(workingDir) {

	/** Include Kotlin runtime in to resulting .jar */
	var includeRuntime: Boolean = false

	/** Name of the generated .kotlin_module file */
	var moduleName: String? = null

	/** Target version of the generated JVM bytecode */
	var jvmTarget: String = JvmTarget.DEFAULT.description

	/** Generate metadata for Java 1.8 reflection on method parameters */
	var javaParameters: Boolean = false

	/** Use the old JVM backend */
	var useOldBackend: Boolean = false

	/** Paths where to find Java 9+ modules */
	var javaModulePath: Path? = null

	/**
	 * Root modules to resolve in addition to the initial modules,
	 * or all modules on the module path if <module> is ALL-MODULE-PATH
	 */
	var additionalJavaModules: MutableList<File> = mutableListOf()

	/** Don't generate not-null assertions for arguments of platform types */
	var noCallAssertions: Boolean = false

	/** Don't generate not-null assertion for extension receiver arguments of platform types */
	var noReceiverAssertions: Boolean = false

	/** Don't generate not-null assertions on parameters of methods accessible from Java */
	var noParamAssertions: Boolean = false

	/** Disable optimizations */
	var noOptimize: Boolean = false

	/** Assert calls behaviour {always-enable|always-disable|jvm|legacy} */
	var assertionsMode: String? = JVMAssertionsMode.DEFAULT.description

	/** Path to the .xml build file to compile */
	var buildFile: File? = null

	/** Compile multifile classes as a hierarchy of parts and facade */
	var inheritMultifileParts: Boolean = false

	/** Use type table in metadata serialization */
	var useTypeTable: Boolean = false

	/** Suppress the \"cannot access built-in declaration\" error (useful with -no-stdlib) */
	var suppressMissingBuiltinsError: Boolean = false

	/** Script resolver environment in key-value pairs (the value could be quoted and escaped) */
	var scriptResolverEnvironment: MutableMap<String, String> = mutableMapOf()

	/** Java compiler arguments */
	var javacArguments: MutableList<String> = mutableListOf()

	/** Package prefix for Java files */
	var javaPackagePrefix: String? = null

	/**
	 * Specify behavior for Checker Framework compatqual annotations (NullableDecl/NonNullDecl).
	 * Default value is 'enable'
	 */
	var supportCompatqualCheckerFrameworkAnnotations: String? = null


	/** Allow to use '@JvmDefault' annotation for JVM default method support.
	 * {disable|enable|compatibility}
	 * */
	var jvmDefault: String = "disable"

	/** Generate metadata with strict version semantics (see kdoc on Metadata.extraInt) */
	var strictMetadataVersionSemantics: Boolean = false

	/**
	 * Transform '(' and ')' in method names to some other character sequence.
	 * This mode can BREAK BINARY COMPATIBILITY and is only supposed to be used as a workaround
	 * of an issue in the ASM bytecode framework. See KT-29475 for more details
	 */
	var sanitizeParentheses: Boolean = false

	/** Paths to output directories for friend modules (whose internals should be visible) */
	var friendPaths: List<File> = emptyList()

	/**
	 * Path to the JDK to be used
	 *
	 * If null, no JDK will be used with kotlinc (option -no-jdk)
	 * and the system java compiler will be used with empty bootclasspath
	 * (on JDK8) or --system none (on JDK9+). This can be useful if all
	 * the JDK classes you need are already on the (inherited) classpath.
	 * */
	var jdkHome: File? by default { processJdkHome }

	/**
	 * Path to the kotlin-stdlib.jar
	 * If none is given, it will be searched for in the host
	 * process' classpaths
	 */
	var kotlinStdLibJar: File? by default {
		HostEnvironment.kotlinStdLibJar
	}

	/**
	 * Path to the kotlin-stdlib-jdk*.jar
	 * If none is given, it will be searched for in the host
	 * process' classpaths
	 */
	var kotlinStdLibJdkJar: File? by default {
		HostEnvironment.kotlinStdLibJdkJar
	}

	/**
	 * Path to the kotlin-reflect.jar
	 * If none is given, it will be searched for in the host
	 * process' classpaths
	 */
	var kotlinReflectJar: File? by default {
		HostEnvironment.kotlinReflectJar
	}

	/**
	 * Path to the kotlin-script-runtime.jar
	 * If none is given, it will be searched for in the host
	 * process' classpaths
	 */
	var kotlinScriptRuntimeJar: File? by default {
		HostEnvironment.kotlinScriptRuntimeJar
	}

	// *.class files, Jars and resources (non-temporary) that are created by the
	// compilation will land here
	val classesDir get() = workingDir.resolve("classes")

	/** ExitCode of the entire Kotlin compilation process */
	enum class ExitCode {
		OK, INTERNAL_ERROR, COMPILATION_ERROR, SCRIPT_EXECUTION_ERROR
	}

	/** Result of the compilation */
	inner class Result(
		/** The exit code of the compilation */
		val exitCode: ExitCode,
		/** Messages that were printed by the compilation */
		val messages: String
	) {
		/** class loader to load the compile classes */
		val classLoader = URLClassLoader(
			// Include the original classpaths and the output directory to be able to load classes from dependencies.
			classpaths.plus(outputDirectory).map { it.toURI().toURL() }.toTypedArray(),
			this::class.java.classLoader
		)

		/** The directory where only the final output class and resources files will be */
		val outputDirectory: File get() = classesDir

		/**
		 * Compiled class and resource files that are the final result of the compilation.
		 */
		val compiledClassAndResourceFiles: List<File> = outputDirectory.listFilesRecursively()

		/**
		 * The class, resource and intermediate source files generated during the compilation.
		 */
		val generatedFiles: Collection<File> = compiledClassAndResourceFiles
	}


	// setup common arguments for the two kotlinc calls
	private fun commonK2JVMArgs() = commonArguments(K2JVMCompilerArguments()) { args ->
		args.destination = classesDir.absolutePath
		args.classpath = commonClasspaths().joinToString(separator = File.pathSeparator)

		if (jdkHome != null) {
			args.jdkHome = jdkHome!!.absolutePath
		}
		else {
			log("Using option -no-jdk. Kotlinc won't look for a JDK.")
			args.noJdk = true
		}

		args.includeRuntime = includeRuntime

		// the compiler should never look for stdlib or reflect in the
		// kotlinHome directory (which is null anyway). We will put them
		// in the classpath manually if they're needed
		args.noStdlib = true
		args.noReflect = true

		if (moduleName != null)
			args.moduleName = moduleName

		args.jvmTarget = jvmTarget
		args.javaParameters = javaParameters
		args.useOldBackend = useOldBackend

		if (javaModulePath != null)
			args.javaModulePath = javaModulePath!!.toString()

		args.additionalJavaModules = additionalJavaModules.map(File::getAbsolutePath).toTypedArray()
		args.noCallAssertions = noCallAssertions
		args.noParamAssertions = noParamAssertions
		args.noReceiverAssertions = noReceiverAssertions

		args.noOptimize = noOptimize

		if (assertionsMode != null)
			args.assertionsMode = assertionsMode

		if (buildFile != null)
			args.buildFile = buildFile!!.toString()

		args.inheritMultifileParts = inheritMultifileParts
		args.useTypeTable = useTypeTable

		if (javacArguments.isNotEmpty())
			args.javacArguments = javacArguments.toTypedArray()

		if (supportCompatqualCheckerFrameworkAnnotations != null)
			args.supportCompatqualCheckerFrameworkAnnotations = supportCompatqualCheckerFrameworkAnnotations

		args.jvmDefault = jvmDefault
		args.strictMetadataVersionSemantics = strictMetadataVersionSemantics
		args.sanitizeParentheses = sanitizeParentheses

		if (friendPaths.isNotEmpty())
			args.friendPaths = friendPaths.map(File::getAbsolutePath).toTypedArray()

		if (scriptResolverEnvironment.isNotEmpty())
			args.scriptResolverEnvironment = scriptResolverEnvironment.map { (key, value) -> "$key=\"$value\"" }.toTypedArray()

        args.javaPackagePrefix = javaPackagePrefix
        args.suppressMissingBuiltinsError = suppressMissingBuiltinsError
		args.disableStandardScript = disableStandardScript
	}

	/** Performs the 3rd compilation step to compile Kotlin source files */
	private fun compileJvmKotlin(): ExitCode {
		val sources = sourcesWithPath.map { it.path }
		return compileKotlin(sources, K2JVMCompiler(), commonK2JVMArgs())
	}

	/** Runs the compilation task */
	fun compile(function: Result.() -> Unit = { }): Result {
		// make sure all needed directories exist
		sourcesDir.mkdirs()
		classesDir.mkdirs()

		pluginClasspaths.forEach { filepath ->
			if (!filepath.exists()) {
				error("Plugin $filepath not found")
				return makeResult(ExitCode.INTERNAL_ERROR)
			}
		}

		return withSystemProperty("idea.use.native.fs.for.win", "false") {
			makeResult(compileJvmKotlin())
		}.also(function)
	}

	private fun makeResult(exitCode: ExitCode): Result {
		val messages = internalMessageBuffer.readUtf8()

		if (exitCode != ExitCode.OK)
			searchSystemOutForKnownErrors(messages)

		return Result(exitCode, messages)
	}

	private fun commonClasspaths() = mutableListOf<File>().apply {
		addAll(classpaths)
		addAll(listOfNotNull(kotlinStdLibJar,  kotlinStdLibCommonJar, kotlinStdLibJdkJar,
            kotlinReflectJar, kotlinScriptRuntimeJar
        ))

		if (inheritClassPath) {
			addAll(hostClasspaths)
			log("Inheriting classpaths:  " + hostClasspaths.joinToString(File.pathSeparator))
		}
	}.distinct()
}
