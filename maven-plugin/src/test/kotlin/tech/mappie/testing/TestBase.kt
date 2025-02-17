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
import java.io.File
import java.util.*

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var request: InvocationRequest

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
                            </configuration>
                            <dependencies>
                                <dependency>
                                    <groupId>tech.mappie</groupId>
                                    <artifactId>mappie-maven-plugin</artifactId>
                                    <version>1.1.0</version>
                                </dependency>
                            </dependencies>
                        </plugin>                    
                    </plugins>
                </build>
                <dependencies>
                    <dependency>
                        <groupId>tech.mappie</groupId>
                        <artifactId>mappie-api-jvm</artifactId>
                        <version>1.1.0</version>
                    </dependency>
                    <dependency>
                        <groupId>org.jetbrains.kotlin</groupId>
                        <artifactId>kotlin-stdlib</artifactId>
                        <version>2.1.10</version>
                    </dependency>
                    <dependency>
                        <groupId>org.jetbrains.kotlin</groupId>
                        <artifactId>kotlin-test-junit5</artifactId>
                        <version>2.1.10</version>
                        <scope>test</scope>
                    </dependency>
                </dependencies>
            </project>
            """.trimIndent()
        )

        request = DefaultInvocationRequest().apply {
            pomFile = pom
            goals = listOf("compile", "test")

            // TODO
            mavenHome = File("C:\\Maven\\bin")
            mavenExecutable = File("C:\\Maven\\bin\\mvn")
        }
    }

    protected fun execute(): InvocationResult = DefaultInvoker().execute(request)

    protected fun ObjectAssert<InvocationResult>.isSuccessful(): AbstractObjectAssert<*, *> =
        extracting { it.exitCode }.isEqualTo(0)


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
