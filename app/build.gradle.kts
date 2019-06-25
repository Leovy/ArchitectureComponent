import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaCompilation


plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

//apply<TransformPlugin>()

configure<BaseAppModuleExtension> {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.nikeorever.architecturecomponent"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

tasks.withType<KotlinCompile<KotlinJvmOptions>>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
    }
}


dependencies {
    implementation(fileTree("libs") { include("*.jar") })
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("androidx.appcompat", "appcompat", "1.0.2")
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.appcompat:appcompat:1.0.2")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-M1")
    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.alibaba:fastjson:1.2.58")
}

tasks {
    val copyApkToDesktop by registering(Copy::class) {
        dependsOn("build")
        val appPlugin = plugins.getPlugin(AppPlugin::class)
        val baseAppModuleExtension = appPlugin.extension as BaseAppModuleExtension
        val packageAndroidArtifact = baseAppModuleExtension.applicationVariants
            .first { it.name == "release" }
            .packageApplicationProvider!!
            .get()
        val apk = packageAndroidArtifact.run {
            File(outputDirectory, apkNames.first())
        }

        from(apk)
        into("/home/xianxueliang/Desktop/")
    }
}
