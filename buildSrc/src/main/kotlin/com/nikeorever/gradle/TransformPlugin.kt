package com.nikeorever.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

class TransformPlugin : Plugin<Project>{

    override fun apply(project: Project) {
        val appExtension = project.extensions.findByType<AppExtension>()
        appExtension?.registerTransform(MyTransformer(project))
    }
}