package tech.mappie.ir.reporting

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.Name

data class KotlinStringBuilder(val level: Int = 0) {
    private val builder = StringBuilder()

    fun print() = builder.toString()

    fun indented(block: KotlinStringBuilder.() -> KotlinStringBuilder) =
        string { block(KotlinStringBuilder(level = level + 4)).print() }

    fun indent(): KotlinStringBuilder =
        apply { builder.append((0..< level).joinToString(separator = "") { " " }) }

    fun string(block: () -> CharSequence): KotlinStringBuilder =
        apply {
            val string = block()
            if (string.isNotBlank()) {
                builder.append(string)
            }
        }

    fun char(char: Char): KotlinStringBuilder =
        apply { builder.append(char) }

    fun newline(): KotlinStringBuilder =
        apply { builder.appendLine() }

    fun dot() = char('.')
    fun space() = char(' ')

    fun line(block: () -> CharSequence): KotlinStringBuilder =
        apply {
            val string = block()
            if (string.isNotBlank()) {
                indent()
                builder.appendLine(string)
            }
        }

    fun curlyOpen() = char('{')
    fun curlyClose() = char('}')

    fun <T> commas(
        list: List<T>,
        prefix: String = "",
        postfix: String = "",
        block: KotlinStringBuilder.(T) -> KotlinStringBuilder,
    ) =
        strings(list, separator = ", ", prefix = prefix, postfix = postfix, block)

    fun <T> strings(
        list: List<T>,
        separator: String,
        prefix: String = "",
        postfix: String = "",
        block: KotlinStringBuilder.(T) -> KotlinStringBuilder
    ): KotlinStringBuilder =
        apply {
            string { prefix }
            list.forEachIndexed { index, item ->
                KotlinStringBuilder(level).block(item).print().let {
                    if (it.isNotBlank()) { string { "$it${if (index != list.lastIndex) separator else ""}" } }
                }
            }
            string { postfix }
        }

    fun <T> lines(list: List<T>, block: KotlinStringBuilder.(T) -> KotlinStringBuilder): KotlinStringBuilder =
        apply { list.forEach { line { KotlinStringBuilder(level).block(it).print() } } }
}

class PrettyPrinter : IrVisitor<KotlinStringBuilder, KotlinStringBuilder>() {

    override fun visitElement(element: IrElement, data: KotlinStringBuilder): KotlinStringBuilder {
        error("This should never happen for ${element::class} ${element.dumpKotlinLike()}")
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "${declaration.name.pretty()}: ${declaration.type.dumpKotlinLike()}" }
        }
    }

    override fun visitClass(declaration: IrClass, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "${declaration.visibility.pretty()} " }
            string { declaration.kind.pretty() }
            space()
            string { declaration.name.pretty() }

            if (declaration.typeParameters.isNotEmpty()) {
                commas(declaration.typeParameters, prefix = "<", postfix = ">") {
                    element(it)
                }
            }

            if (declaration.kind in listOf(ClassKind.CLASS, ClassKind.ENUM_CLASS, ClassKind.ANNOTATION_CLASS)) {
                declaration.primaryConstructor?.let { constructor ->

                    string { " ${declaration.visibility.pretty()}" }
                    string { " constructor" }
                    commas(constructor.parameters, prefix = "(", postfix = ")") { parameter ->
                        val property = declaration.properties.singleOrNull { it.name == parameter.name }
                        if (property != null) {
                            string { property.visibility.pretty() }
                            string { if (property.isVar) " var " else " val " }
                        }

                        string { parameter.name.pretty() }
                        string { ": ${parameter.type.dumpKotlinLike()}"}
                        parameter.defaultValue?.let {
                            space()
                            element(it)
                        } ?: this
                    }
                }
            }

            if (declaration.superTypes.isNotEmpty()) {
                string { ": " }
                declaration.superTypes.forEach {
                    string { it.dumpKotlinLike() }
                    if (!it.isInterface()) {
                        string { "()" } // TODO: arguments
                    }
                }
            }

            space()

            curlyOpen()
            newline()
            indented {
                lines(declaration.declarations) {
                    element(it)
                }
            }
            indent(); curlyClose()
        }
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "init " }
            element(declaration.body)
        }
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { declaration.name.pretty() }
            if (declaration.superTypes.isNotEmpty()) {
                string { " : " }
                commas(declaration.superTypes) { string { it.dumpKotlinLike() } }
            }
        }
    }
    override fun visitConstructor(declaration: IrConstructor, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            if (!declaration.isPrimary) {
                string { declaration.visibility.pretty() }
                string { " constructor" }
                commas(declaration.parameters, "(", ") ") {
                    element(it)
                }
                declaration.body?.let {
                    element(it)
                }
            }
        }
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitEnumEntry(declaration, data)
    }

    override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitErrorDeclaration(declaration, data)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitLocalDelegatedProperty(declaration, data)
    }

    override fun visitProperty(declaration: IrProperty, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            if (!declaration.isFakeOverride && declaration.parentAsClass.primaryConstructor?.parameters?.none { it.name == declaration.name } == true) {
                string { declaration.visibility.pretty() }
                if (declaration.isLateinit) string { " lateinit" }
                if (declaration.isConst) string { " const" }
                string { if (declaration.isVar) " var " else " val " }
                string { declaration.name.pretty() }

                declaration.backingField?.initializer?.let {
                    string { ": " }
                    string { it.expression.type.dumpKotlinLike() }
                    string { " = " }
                    element(it.expression)
                }

                declaration.getter?.let {
                    if (it.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
                        newline()
                        indented {
                            indent()
                            string { "get " }
                            it.symbol.owner.body?.let { element(it) } ?: this
                        }
                    }
                }

                declaration.setter?.let {
                    if (it.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
                        newline()
                        indented {
                            indent()
                            string { "set " }
                            it.symbol.owner.body?.let { element(it) } ?: this
                        }
                    }
                }
            }
        }
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            if (!declaration.isFakeOverride) {
                if (declaration.overriddenSymbols.isNotEmpty()) string { "override " }
                if (declaration.isInfix) string { "infix " }
                if (declaration.isOperator) string { "operator " }

                string { "fun " }
                if (declaration.typeParameters.isNotEmpty()) {
                    commas(declaration.typeParameters, prefix = "<", postfix = "> ") {
                        element(it)
                    }
                }
                string { declaration.name.pretty() }
                commas(declaration.parameters.filter { it.kind == IrParameterKind.Regular }, prefix = "(", postfix = ")") {
                    element(it)
                }
                string { ": ${declaration.returnType.dumpKotlinLike()} "}
                declaration.body?.let { element(it) }
            }
        }
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitTypeAlias(declaration, data)
    }

    override fun visitVariable(declaration: IrVariable, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { if (declaration.isVar) "var" else "val" }
            string { " ${declaration.name.pretty()}: ${declaration.type.dumpKotlinLike()}" }
            declaration.initializer?.let {
                string { " = " }
                element(it)
            }
        }
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "= " }
            element(body.expression)
        }
    }

    override fun visitBlockBody(body: IrBlockBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            curlyOpen()
            newline()
            indented {
                lines(body.statements) {
                    element(it)
                }
            }
            indent(); curlyClose()
        }
    }

    override fun visitDeclarationReference(expression: IrDeclarationReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDeclarationReference(expression, data)
    }

    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitMemberAccess(expression, data)
    }

    override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitFunctionAccess(expression, data)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.type.dumpKotlinLike() }
            var i = 0
            val printer: KotlinStringBuilder.(IrElement?) -> KotlinStringBuilder = {
                if (it != null) {
                    string { "${expression.symbol.owner.parameters[i].name.pretty()} = " }
                    element(it)
                    if (i != expression.symbol.owner.parameters.lastIndex) {
                        string { ", " }
                    }
                }
                i++
                this
            }
            if (expression.arguments.size > 2) {
                string { "(" }
                newline()
                indented {
                    lines(expression.arguments, printer)
                }
                line { ")" }
            } else {
                strings(expression.arguments, "", "(", ")", printer)
            }
        }
    }

    override fun visitSingletonReference(expression: IrGetSingletonValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSingletonReference(expression, data)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.pretty() }
        }
    }

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.parentAsClass.name.pretty() + "." + expression.symbol.owner.name.pretty() }
        }
    }

    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string {
                "${expression.type.dumpKotlinLike()}::${expression.symbol.owner.name.pretty()}"
            }
        }
    }

    override fun visitContainerExpression(expression: IrContainerExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitContainerExpression(expression, data)
    }

    override fun visitBlock(expression: IrBlock, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            curlyOpen()
            newline()
            indented {
                lines(expression.statements) {
                    element(it)
                }
            }
            indent(); curlyClose()
        }
    }

    override fun visitComposite(expression: IrComposite, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            curlyOpen()
            newline()
            indented {
                lines(expression.statements) {
                    element(it)
                }
            }
            indent(); curlyClose()
        }
    }

    override fun visitReturnableBlock(expression: IrReturnableBlock, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitReturnableBlock(expression, data)
    }

    override fun visitInlinedFunctionBlock(inlinedBlock: IrInlinedFunctionBlock, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitInlinedFunctionBlock(inlinedBlock, data)
    }

    override fun visitSyntheticBody(body: IrSyntheticBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSyntheticBody(body, data)
    }

    override fun visitBreakContinue(jump: IrBreakContinue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitBreakContinue(jump, data)
    }

    override fun visitBreak(jump: IrBreak, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "break" }
        }
    }

    override fun visitContinue(jump: IrContinue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply { string { "continue" } }
    }

    override fun visitCall(expression: IrCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            when {
                expression.symbol.owner.isPropertyAccessor -> {
                    element(expression.arguments.first()!!)
                    dot()
                    string { expression.symbol.owner.name.pretty().split("-").last() }
                }
                expression.symbol.owner.name.asString() == "CHECK_NOT_NULL" -> {
                    element(expression.arguments[0]!!)
                    string { "!!" }
                }
                expression.symbol.owner.name.asString() in SYMBOL_OPERATORS -> {
                    char('(')
                    element(expression.arguments[0]!!)
                    string { " ${expression.symbol.owner.name.operatorName()} " }
                    element(expression.arguments[1]!!)
                    char(')')
                }
                expression.symbol.owner.isInfix -> {
                    element(expression.dispatchReceiver!!)
                    string { " ${expression.symbol.owner.name.pretty()} " }
                    element(expression.arguments[0]!!)
                }
                else -> {
                    expression.dispatchReceiver?.let {
                        element(it)
                        dot()
                    }
                    expression.extensionReceiver?.let {
                        element(it)
                        dot()
                    }
                    if (expression.symbol.owner.isStatic) {
                        string { expression.symbol.owner.parentAsClass.name.pretty() }
                        dot()
                    }
                    string { expression.symbol.owner.name.pretty() }
                    commas(expression.valueArguments.filterNotNull(), prefix = "(", postfix = ")") {
                        element(it)
                    }
                }
            }
        }
    }

    override fun visitCallableReference(expression: IrCallableReference<*>, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitCallableReference(expression, data)
    }

    override fun visitFunctionReference(expression: IrFunctionReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            expression.symbol.owner.parentClassOrNull?.let {
                string { it.name.pretty() }
            }
            string { "::${expression.symbol.owner.name.pretty()}" }
        }
    }

    override fun visitPropertyReference(expression: IrPropertyReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.parentAsClass.name.pretty() }
            string { "::" }
            string { expression.symbol.owner.name.pretty() }
        }
    }

    override fun visitLocalDelegatedPropertyReference(
        expression: IrLocalDelegatedPropertyReference,
        data: KotlinStringBuilder,
    ): KotlinStringBuilder {
        return super.visitLocalDelegatedPropertyReference(expression, data)
    }

    override fun visitRichFunctionReference(expression: IrRichFunctionReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitRichFunctionReference(expression, data)
    }

    override fun visitRichPropertyReference(expression: IrRichPropertyReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitRichPropertyReference(expression, data)
    }

    override fun visitClassReference(expression: IrClassReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "${expression.classType.dumpKotlinLike()}::class" }
        }
    }

    override fun visitConst(expression: IrConst, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string {
                when (expression.value) {
                    is String -> "\"${expression.value}\""
                    is Char -> "'${expression.value}'"
                    is Long -> "${expression.value}L"
                    is Float -> "${expression.value}F"
                    null -> "null"
                    else -> expression.value.toString()
                }
            }
        }
    }

    override fun visitConstantPrimitive(expression: IrConstantPrimitive, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            element(expression.value)
        }
    }

    override fun visitConstantObject(expression: IrConstantObject, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitConstantObject(expression, data)
    }

    override fun visitConstantArray(expression: IrConstantArray, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            commas(expression.elements, prefix = "[", postfix = "]") {
                element(it)
            }
        }
    }

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.parentAsClass.name.pretty()}
            commas(expression.arguments.filterNotNull(), prefix = "(", postfix = ")") {
                element(it)
            }
        }
    }

    override fun visitDynamicExpression(expression: IrDynamicExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDynamicExpression(expression, data)
    }

    override fun visitDynamicOperatorExpression(expression: IrDynamicOperatorExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDynamicOperatorExpression(expression, data)
    }

    override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDynamicMemberExpression(expression, data)
    }

    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitEnumConstructorCall(expression, data)
    }

    override fun visitErrorExpression(expression: IrErrorExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitErrorExpression(expression, data)
    }

    override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitErrorCallExpression(expression, data)
    }

    override fun visitFieldAccess(expression: IrFieldAccessExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitFieldAccess(expression, data)
    }

    override fun visitGetField(expression: IrGetField, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.pretty() }
        }
    }

    override fun visitSetField(expression: IrSetField, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.pretty() }
            string { " = " }
            element(expression.value)
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            curlyOpen()
            if (expression.function.parameters.isNotEmpty()) {
                commas(expression.function.parameters, prefix = "", postfix = " -> ") {
                    element(it)
                }
            }
            strings(expression.function.body!!.statements, separator = "; ") {
                element(it)
            }
            curlyClose()
        }
    }

    override fun visitGetClass(expression: IrGetClass, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            element(expression.argument)
            string { "::class" }
        }
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitInstanceInitializerCall(expression, data)
    }

    override fun visitLoop(loop: IrLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitLoop(loop, data)
    }

    override fun visitWhileLoop(loop: IrWhileLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "while (" }
            element(loop.condition)
            string { ") " }
            loop.body?.let { element(it) }
        }
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "do " }
            loop.body?.let { element(it) }
            string { " while (" }
            element(loop.condition)
            string { ")" }
        }
    }

    override fun visitReturn(expression: IrReturn, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply { Long.MAX_VALUE
            // TODO: labeled returns
            if ((expression.returnTargetSymbol.owner as IrFunction).origin != IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA) {
                string { "return " }
            }
            element(expression.value)
        }
    }

    override fun visitStringConcatenation(expression: IrStringConcatenation, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            strings(expression.arguments, " + ") {
                element(it)
            }
        }
    }

    override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSuspensionPoint(expression, data)
    }

    override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSuspendableExpression(expression, data)
    }

    override fun visitThrow(expression: IrThrow, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "throw "}
            element(expression.value)
        }
    }

    override fun visitTry(aTry: IrTry, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "try " }
            element(aTry.tryResult)
            lines(aTry.catches) {
                newline()
                indent()
                element(it)
            }
            aTry.finallyExpression?.let {
                newline()
                indent()
                string { "finally " }
                element(it)
            }
        }
    }

    override fun visitCatch(aCatch: IrCatch, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "catch (" }
            element(aCatch.catchParameter)
            string { ") " }
            element(aCatch.result)
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            element(expression.argument)
            when (expression.operator) {
                IrTypeOperator.CAST -> string { " as ${expression.type.dumpKotlinLike()}"  }
                IrTypeOperator.IMPLICIT_CAST -> this
                IrTypeOperator.IMPLICIT_NOTNULL -> this
                IrTypeOperator.IMPLICIT_COERCION_TO_UNIT -> this
                IrTypeOperator.IMPLICIT_INTEGER_COERCION -> this
                IrTypeOperator.SAFE_CAST -> string { " as? ${expression.type.dumpKotlinLike()}"  }
                IrTypeOperator.INSTANCEOF -> string { " is ${expression.type.dumpKotlinLike()}"  }
                IrTypeOperator.NOT_INSTANCEOF -> string { " !is ${expression.type.dumpKotlinLike()}"  }
                IrTypeOperator.SAM_CONVERSION -> this
                IrTypeOperator.IMPLICIT_DYNAMIC_CAST -> this
                IrTypeOperator.REINTERPRET_CAST -> this
            }
        }
    }

    override fun visitValueAccess(expression: IrValueAccessExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitValueAccess(expression, data)
    }

    override fun visitGetValue(expression: IrGetValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.pretty() }
        }
    }

    override fun visitSetValue(expression: IrSetValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.pretty() }
            string { " = " }
            element(expression.value)
        }
    }

    override fun visitVararg(expression: IrVararg, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitVararg(expression, data)
    }

    override fun visitSpreadElement(spread: IrSpreadElement, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSpreadElement(spread, data)
    }

    override fun visitWhen(expression: IrWhen, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "when" }
            space()
            curlyOpen()
            newline()
            indented {
                lines(expression.branches) {
                    element(it)
                }
            }
            indent(); curlyClose()
        }
    }

    override fun visitBranch(branch: IrBranch, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            element(branch.condition)
            string { " -> " }
            element(branch.result)
        }
    }

    override fun visitElseBranch(branch: IrElseBranch, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitElseBranch(branch, data)
    }

    private fun KotlinStringBuilder.element(element: IrElement) =
        element.accept(this@PrettyPrinter, this)

    private fun ClassKind.pretty() = when (this) {
        ClassKind.CLASS -> "class"
        ClassKind.INTERFACE -> "interface"
        ClassKind.ENUM_CLASS -> "enum class"
        ClassKind.ENUM_ENTRY -> ""
        ClassKind.ANNOTATION_CLASS -> "annotation class"
        ClassKind.OBJECT -> "object"
    }

    private fun DescriptorVisibility.pretty(): String =
        /*if (delegate == Visibilities.Public) "" else*/ delegate.name

    private fun Name.pretty(): String =
        asStringStripSpecialMarkers()

    private fun Name.operatorName(): String =
        SYMBOL_OPERATORS[this.asString()] ?: pretty()

    companion object {
        private val SYMBOL_OPERATORS = mapOf(
            "EQEQEQ" to "===",
            "EQEQ" to "==",
            "plus" to "+",
            "minus" to "-",
            "times" to "*",
            "div" to "/",
            "rem" to "%",
            "less" to "<",
            "lessOrEqual" to "<=",
            "greater" to ">",
            "greaterOrEqual" to ">=",
            "rangeTo" to "..",
            "rangeUntil" to "..<",
        )
    }
}
