package tech.mappie.ir.reporting

import org.jetbrains.kotlin.descriptors.ClassKind
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

    fun indented(block: (KotlinStringBuilder) -> CharSequence) =
        block(KotlinStringBuilder(level = level + 4))

    fun indent() =
        builder.append((0..< level).joinToString(separator = "") { " " })

    fun string(block: () -> CharSequence) =
        block().let { if (it.isNotBlank()) { builder.append(it) } }

    fun <T> commas(list: List<T>, prefix: String = "", postfix: String = "", block: (T) -> CharSequence) =
        strings(list, separator = ", ", prefix = prefix, postfix = postfix, block)

    fun <T> strings(list: List<T>, separator: String, prefix: String = "", postfix: String = "", block: (T) -> CharSequence) {
        string { prefix }
        list.forEachIndexed { index, item -> block(item).let {
            if (it.isNotBlank()) { string { "$it${if (index != list.lastIndex) separator else ""}" } } }
        }
        string { postfix }
    }

    fun <T> lines(list: List<T>, prefix: String = "", postfix: String = "", block: (T) -> CharSequence?) {
        line { prefix }
        list.forEach { line { block(it) } }
        line { postfix }
    }

    fun newline() =
        apply { builder.appendLine() }

    fun line(block: () -> CharSequence?) {
        block()?.let {
            if (it.isNotBlank()) {
                indent()
                builder.appendLine(it)
            }
        }
    }
}

class PrettyPrinter : IrVisitor<KotlinStringBuilder, KotlinStringBuilder>() {

    override fun visitElement(element: IrElement, data: KotlinStringBuilder): KotlinStringBuilder {
        error("This should never happen for ${element::class} ${element.dumpKotlinLike()}")
    }
    
    override fun visitDeclaration(declaration: IrDeclarationBase, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDeclaration(declaration, data)
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "${declaration.name.asString()}: ${declaration.type.dumpKotlinLike()}" }
        }
    }

    override fun visitClass(declaration: IrClass, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            indent()
            string { declaration.visibility.delegate.externalDisplayName + " " }
            string {
                when (declaration.kind) {
                    ClassKind.CLASS -> "class"
                    ClassKind.INTERFACE -> "interface"
                    ClassKind.ENUM_CLASS -> "enum class"
                    ClassKind.ENUM_ENTRY -> ""
                    ClassKind.ANNOTATION_CLASS -> "annotation class"
                    ClassKind.OBJECT -> "object"
                }
            }

            string { " " + declaration.name.asString() }

            if (declaration.typeParameters.isNotEmpty()) {
                commas(declaration.typeParameters, prefix = "<", postfix = ">") {
                    it.pretty(data)
                }
            }

            if (declaration.kind in listOf(ClassKind.CLASS, ClassKind.ENUM_CLASS, ClassKind.ANNOTATION_CLASS)) {
                declaration.primaryConstructor?.let { constructor ->
                    commas(constructor.parameters, prefix = "(", postfix = ")") { parameter ->
                            val kind = if (!parameter.isPropertyField) {
                                "val " // TODO: might be var
                            } else {
                                ""
                            }
                            // TODO: visibility
                            // TODO: default argument
                            "$kind${parameter.name}: ${parameter.type.dumpKotlinLike()}"
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

            newline()

            lines(declaration.declarations, prefix = "{", postfix = "}") {
               indented { data -> it.pretty(data) }
            }
        }
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            line { "init" }
            string { declaration.body.pretty(data) }
        }
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { declaration.name.asString() }
            if (declaration.superTypes.isNotEmpty()) {
                string { " : " }
                commas(declaration.superTypes) { it.dumpKotlinLike() }
            }
        }
    }

    override fun visitFunction(declaration: IrFunction, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            if (!declaration.isFakeOverride) {
                newline()
                indent()
                string { "fun " }
                if (declaration.typeParameters.isNotEmpty()) {
                    commas(declaration.typeParameters, prefix = "<", postfix = "> ") {
                        it.pretty(data)
                    }
                }
                string { declaration.name.asString() }
                commas(declaration.parameters.filter { it.kind == IrParameterKind.Regular }, prefix = "(", postfix = ")") {
                    it.pretty(data)
                }
                string { ": ${declaration.returnType.dumpKotlinLike()}"}
                newline()
                declaration.body?.let { string { it.pretty(data) } }
            }
        }
    }

    // TODO
    override fun visitConstructor(declaration: IrConstructor, data: KotlinStringBuilder): KotlinStringBuilder {
        return if (declaration.isPrimary) {
            data
        } else {
            data
        }
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitEnumEntry(declaration, data)
    }

    override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitErrorDeclaration(declaration, data)
    }

    override fun visitField(declaration: IrField, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitField(declaration, data)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitLocalDelegatedProperty(declaration, data)
    }

    override fun visitModuleFragment(declaration: IrModuleFragment, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitModuleFragment(declaration, data)
    }

    override fun visitProperty(declaration: IrProperty, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            if (!declaration.isFakeOverride && declaration.isPropertyField) {
                string {
                    val kind = if (declaration.isVar) "var" else "val"

                    "$kind ${declaration.name.asString()}"
                }
            }
        }
    }

    override fun visitScript(declaration: IrScript, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitScript(declaration, data)
    }

    override fun visitReplSnippet(declaration: IrReplSnippet, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitReplSnippet(declaration, data)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSimpleFunction(declaration, data)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitTypeAlias(declaration, data)
    }

    override fun visitVariable(declaration: IrVariable, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { if (declaration.isVar) "var" else "val" }
            string { " ${declaration.name}: ${declaration.type.dumpKotlinLike()}" }
            declaration.initializer?.let {
                string { " = ${it.pretty(data)}" }
            }
        }
    }

    override fun visitPackageFragment(declaration: IrPackageFragment, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitPackageFragment(declaration, data)
    }

    override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitExternalPackageFragment(declaration, data)
    }

    override fun visitFile(declaration: IrFile, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitFile(declaration, data)
    }

    override fun visitExpression(expression: IrExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitExpression(expression, data)
    }

    override fun visitBody(body: IrBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitBody(body, data)
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitExpressionBody(body, data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            lines(body.statements, prefix = "{", postfix = "}") { statement ->
                indent(); statement.pretty(data)
            }
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
            commas(expression.arguments, prefix = "(", postfix = ")") {
                it?.pretty(data) ?: ""
            }
        }
    }

    override fun visitSingletonReference(expression: IrGetSingletonValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSingletonReference(expression, data)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.asString() }
        }
    }

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.parentAsClass.name.asString() + "." + expression.symbol.owner.name.asString() }
        }
    }

    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string {
                "${expression.type.dumpKotlinLike()}::${expression.symbol.owner.name.asString()}"
            }
        }
    }

    override fun visitContainerExpression(expression: IrContainerExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitContainerExpression(expression, data)
    }

    override fun visitBlock(expression: IrBlock, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            lines(expression.statements, prefix = "{", postfix = "}") {
                indented { data -> it.pretty(data) }
            }
        }
    }

    override fun visitComposite(expression: IrComposite, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitComposite(expression, data)
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
        return super.visitBreak(jump, data)
    }

    override fun visitContinue(jump: IrContinue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitContinue(jump, data)
    }

    override fun visitCall(expression: IrCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            when {
                expression.symbol.owner.isPropertyAccessor -> {
                    string { expression.arguments.first()!!.pretty(data) }
                    string { "." }
                    string { expression.symbol.owner.name.asStringStripSpecialMarkers().split("-").last() }
                }
                expression.symbol.owner.name.asString() == "CHECK_NOT_NULL" -> {
                    string { "${expression.arguments[0]!!.pretty(data)}!!" }
                }
                // TODO: doesn't account for pre/post-fix operators
                expression.symbol.owner.name.asString() == "EQEQ" || expression.symbol.owner.isOperator -> {
                    string { expression.arguments[0]!!.pretty(data) }
                    string { " ${expression.symbol.owner.name.operatorName()} " }
                    string { expression.arguments[1]!!.pretty(data) }
                }
                else -> {
                    expression.dispatchReceiver?.let {
                        string { "${it.pretty(data)}." }
                    }
                    expression.extensionReceiver?.let {
                        string { "${it.pretty(data)}." }
                    }
                    if (expression.symbol.owner.isStatic) {
                        string { "${expression.symbol.owner.parentAsClass.name.asString()}." }
                    }
                    string { expression.symbol.owner.name.asString() }
                    commas(expression.valueArguments, prefix = "(", postfix = ")") {
                        it?.pretty(data) ?: "TODO"
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
                string { it.name.asString() }
            }
            string { "::${expression.symbol.owner.name.asString()}" }
        }
    }

    override fun visitPropertyReference(expression: IrPropertyReference, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitPropertyReference(expression, data)
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
                    null -> "null"
                    else -> expression.value.toString()
                }
            }
        }
    }

    override fun visitConstantValue(expression: IrConstantValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitConstantValue(expression, data)
    }

    override fun visitConstantPrimitive(expression: IrConstantPrimitive, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.value.pretty(data) }
        }
    }

    override fun visitConstantObject(expression: IrConstantObject, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitConstantObject(expression, data)
    }

    override fun visitConstantArray(expression: IrConstantArray, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            commas(expression.elements, prefix = "[", postfix = "]") {
                it.pretty(data)
            }
        }
    }

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDelegatingConstructorCall(expression, data)
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
        return super.visitGetField(expression, data)
    }

    override fun visitSetField(expression: IrSetField, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSetField(expression, data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.function.body!!.pretty(data) }
        }
    }

    override fun visitGetClass(expression: IrGetClass, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "${expression.argument.pretty(data)}::class" }
        }
    }

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitInstanceInitializerCall(expression, data)
    }

    override fun visitLoop(loop: IrLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitLoop(loop, data)
    }

    override fun visitWhileLoop(loop: IrWhileLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitWhileLoop(loop, data)
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitDoWhileLoop(loop, data)
    }

    override fun visitReturn(expression: IrReturn, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            // TODO: last statement in lambda -> ommit "return"
            string { "return ${expression.value.pretty(data)}" }
        }
    }

    override fun visitStringConcatenation(expression: IrStringConcatenation, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitStringConcatenation(expression, data)
    }

    override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSuspensionPoint(expression, data)
    }

    override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSuspendableExpression(expression, data)
    }

    override fun visitThrow(expression: IrThrow, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "throw ${expression.value.pretty(data)}" }
        }
    }

    override fun visitTry(aTry: IrTry, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitTry(aTry, data)
    }

    override fun visitCatch(aCatch: IrCatch, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitCatch(aCatch, data)
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitTypeOperator(expression, data)
    }

    override fun visitValueAccess(expression: IrValueAccessExpression, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitValueAccess(expression, data)
    }

    override fun visitGetValue(expression: IrGetValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { expression.symbol.owner.name.asStringStripSpecialMarkers() }
        }
    }

    override fun visitSetValue(expression: IrSetValue, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSetValue(expression, data)
    }

    override fun visitVararg(expression: IrVararg, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitVararg(expression, data)
    }

    override fun visitSpreadElement(spread: IrSpreadElement, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitSpreadElement(spread, data)
    }

    override fun visitWhen(expression: IrWhen, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            string { "when" }; newline()
            lines(expression.branches, prefix = "    {", postfix = "    }") {
                indented { data -> it.pretty(data) }
            }
        }
    }

    override fun visitBranch(branch: IrBranch, data: KotlinStringBuilder): KotlinStringBuilder {
        return data.apply {
            indent()
            string { branch.condition.pretty(data) }
            string { " -> " }
            string { branch.result.pretty(data) }
        }
    }

    override fun visitElseBranch(branch: IrElseBranch, data: KotlinStringBuilder): KotlinStringBuilder {
        return super.visitElseBranch(branch, data)
    }

    private fun IrElement.pretty(builder: KotlinStringBuilder) =
        accept(this@PrettyPrinter, KotlinStringBuilder(level = builder.level)).print()
}


// TODO: expand operators
private fun Name.operatorName(): String = when (this.asString()) {
    "EQEQ" -> "=="
    "plus" -> "+"
    "minus" -> "-"
    else -> this.asString()
}
