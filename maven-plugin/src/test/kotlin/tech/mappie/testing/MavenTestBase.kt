package tech.mappie.testing

import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationRequest
import org.apache.maven.shared.invoker.InvocationResult
import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.ObjectAssert
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.lang.System.lineSeparator
import java.util.*

abstract class MavenTestBase {

    @TempDir
    protected lateinit var directory: File

    protected val logs = StringBuilder()

    protected open val mappieOptions: Map<String, String> = emptyMap()

    private lateinit var request: InvocationRequest

    @BeforeEach
    fun setup() {
        directory.resolve("src/main/kotlin").mkdirs()
        directory.resolve("src/test/kotlin").mkdirs()

        val pom: File = xml("pom.xml",
            """
            <project xmlns="http://maven.apache.org/POM/4.0.0" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <artifactId>test</artifactId>
                <groupId>test</groupId>
                <version>1.0.0</version>
    
                <properties>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                </properties>
    
                <repositories>
                    <repository>
                        <id>mavenCentral</id>
                        <url>https://repo1.maven.org/maven2/</url>
                    </repository>
                </repositories>
                
                <build>
                    <sourceDirectory>src/main/kotlin</sourceDirectory>
                    <testSourceDirectory>src/test/kotlin</testSourceDirectory>
                    <plugins>
                          <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-surefire-plugin</artifactId>
                            <version>3.5.2</version>
                        </plugin>
                        <plugin>
                            <groupId>org.jetbrains.kotlin</groupId>
                            <artifactId>kotlin-maven-plugin</artifactId>
                            <version>2.1.10</version>
                            
                            <executions>
                                <execution>
                                    <id>compile</id>
                                    <goals>
                                        <goal>compile</goal>
                                    </goals>
                                </execution>
                                <execution>
                                    <id>test-compile</id>
                                    <goals>
                                        <goal>test-compile</goal>
                                    </goals>
                                </execution>
                            </executions>
                            <configuration>
                                <compilerPlugins>
                                    <compilerPlugin>mappie</compilerPlugin>
                                </compilerPlugins>
                                <pluginOptions>
                                    ${mappieOptions.map { "<option>mappie:${it.key}=${it.value}</option>" }.joinToString(separator = lineSeparator())}
                                </pluginOptions>
                            </configuration>
                            <dependencies>
                                <dependency>
                                    <groupId>tech.mappie</groupId>
                                    <artifactId>mappie-maven-plugin</artifactId>
                                    <version>$version</version>
                                </dependency>
                            </dependencies>
                        </plugin>                    
                    </plugins>
                </build>
                <dependencies>
                    <dependency>
                        <groupId>tech.mappie</groupId>
                        <artifactId>mappie-api-jvm</artifactId>
                        <version>$version</version>
                    </dependency>
                    <dependency>
                        <groupId>org.jetbrains.kotlin</groupId>
                        <artifactId>kotlin-stdlib</artifactId>
                        <version>2.1.10</version>
                    </dependency>
                    <dependency>
                        <groupId>org.testng</groupId>
                        <artifactId>testng</artifactId>
                        <version>6.9.8</version>
                        <scope>test</scope>
                    </dependency>
                </dependencies>
            </project>
            """.trimIndent()
        )

        request = DefaultInvocationRequest().apply {
            mavenHome = File("./src/test/resources/maven")
            mavenExecutable = File("../mvnw")
            pomFile = pom
            goals = listOf("compile", "test")
            setOutputHandler { logs.appendLine(it) }
            setInputStream(ByteArrayInputStream(ByteArray(0)))
        }
    }

    protected fun execute(): InvocationResult = DefaultInvoker().execute(request)

    protected fun ObjectAssert<InvocationResult>.isSuccessful(): AbstractObjectAssert<*, *> =
        extracting { it.exitCode }
            .withFailMessage("Expected successful invocation result but failed." + lineSeparator() + logs.lines().joinToString(separator = lineSeparator()))
            .isEqualTo(0)

    protected fun ObjectAssert<InvocationResult>.isFailure(): AbstractObjectAssert<*, *> =
        extracting { it.exitCode }
            .withFailMessage("Expected failure invocation result but succeeded." + lineSeparator() + logs.lines().joinToString(separator = lineSeparator()))
            .isNotEqualTo(0)

    protected fun xml(file: String, @Language("XML") code: String): File {
        return directory.resolve(file).apply {
            appendText(code)
        }
    }

    protected fun kotlin(file: String, @Language("kotlin") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }

    companion object {
        private val version = javaClass.classLoader.getResourceAsStream("mappie.properties").use {
            Properties().apply { load(it) }.getProperty("VERSION")
        }

        @BeforeAll
        @JvmStatic
        fun start() {
            println("Using mappie version $version")
        }
    }
}
