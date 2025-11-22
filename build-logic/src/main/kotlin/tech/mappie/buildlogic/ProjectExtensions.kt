package tech.mappie.buildlogic

import org.gradle.api.Project
import kotlin.math.max

internal fun Project.halfWorkerCount() = max(gradle.startParameter.maxWorkerCount, 1)
