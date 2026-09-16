package tech.mappie.ir.analysis

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryN
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.ir.IrDiagnosticReporter
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.util.file
import tech.mappie.ir.analysis.Problem.Problem0
import tech.mappie.ir.util.firstRealParent

sealed interface Problem {
    val problem: KtDiagnosticFactoryN
    val file: IrFile
    val location: IrElement

    val severity: Severity
        get() = problem.severity

    data class Problem0(
        override val problem: KtDiagnosticFactory0,
        override val file: IrFile,
        override val location: IrElement,
    ): Problem

    data class Problem1<A : Any>(
        override val problem: KtDiagnosticFactory1<A>,
        override val file: IrFile,
        override val location: IrElement,
        val first: A,
    ): Problem {
        constructor(problem: KtDiagnosticFactory1<A>, declaration: IrDeclaration, first: A)
            : this(problem, declaration.file, firstRealParent(declaration), first)
    }

    data class Problem2<A : Any, B : Any>(
        override val problem: KtDiagnosticFactory2<A, B>,
        override val file: IrFile,
        override val location: IrElement,
        val first: A,
        val second: B,
    ): Problem {
        constructor(problem: KtDiagnosticFactory2<A, B>, declaration: IrDeclaration, first: A, second: B)
            : this(problem, declaration.file, firstRealParent(declaration), first, second)
    }
}

fun IrDiagnosticReporter.IrDiagnosticContext.report(problem: Problem) {
    when (problem) {
        is Problem0 -> report(problem.problem)
        is Problem.Problem1<*> -> report(problem.problem, problem.first)
        is Problem.Problem2<*, *> -> report(problem.problem, problem.first, problem.second)
    }
}