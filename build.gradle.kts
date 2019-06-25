// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra["deps"] = mapOf(
        "android" to mapOf(
            "runtime" to "com.google.android:android:4.1.1.4",
            "gradlePlugin" to "com.android.tools.build:gradle:3.4.1"
        )
    )

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.4.1")
        classpath(kotlin("gradle-plugin", "1.3.40"))
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.withType<Wrapper>{
    gradleVersion = "5.4.1"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.create("clean", Delete::class.java){
    delete = setOf(rootProject.buildDir)
}