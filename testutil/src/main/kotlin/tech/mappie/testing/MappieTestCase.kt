package tech.mappie.testing

import org.junit.jupiter.api.io.TempDir
import tech.mappie.api.EnumMappie
import tech.mappie.api.ObjectMappie
import tech.mappie.api.ObjectMappie2
import tech.mappie.api.ObjectMappie3
import tech.mappie.api.ObjectMappie5
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.compilation.CompilationDsl
import java.io.File

abstract class MappieTestCase {

    @TempDir
    protected lateinit var directory: File

    protected fun compile(verbose: Boolean = false, dsl: CompilationDsl.() -> Unit): CompilationAssertionDsl =
        compile(directory, verbose, dsl)

    protected fun <FROM : Enum<*>, TO> CompilationAssertionDsl.enumMappie(name: String = "Mapper", vararg args: Any?): EnumMappie<FROM, TO> =
        classLoader.loadEnumMappieClass<FROM, TO>(name)
            .constructors
            .first()
            .call(*args)

    protected fun <FROM, TO> CompilationAssertionDsl.objectMappie(name: String = "Mapper", vararg args: Any?): ObjectMappie<FROM, TO> =
        classLoader.loadObjectMappieClass<FROM, TO>(name)
            .constructors
            .first()
            .call(*args)

    protected fun <FROM1, FROM2, TO> CompilationAssertionDsl.objectMappie2(name: String = "Mapper", vararg args: Any?): ObjectMappie2<FROM1, FROM2, TO> =
        classLoader.loadObjectMappie2Class<FROM1, FROM2, TO>(name)
            .constructors
            .first()
            .call(*args)

    protected fun <FROM1, FROM2, FROM3, TO> CompilationAssertionDsl.objectMappie3(name: String = "Mapper", vararg args: Any?): ObjectMappie3<FROM1, FROM2, FROM3, TO> =
        classLoader.loadObjectMappie3Class<FROM1, FROM2, FROM3, TO>(name)
            .constructors
            .first()
            .call(*args)

    protected fun <FROM1, FROM2, FROM3, FROM4, FROM5, TO> CompilationAssertionDsl.objectMappie5(name: String = "Mapper", vararg args: Any?): ObjectMappie5<FROM1, FROM2, FROM3, FROM4, FROM5, TO> =
        classLoader.loadObjectMappie5Class<FROM1, FROM2, FROM3, FROM4, FROM5, TO>(name)
            .constructors
            .first()
            .call(*args)
}