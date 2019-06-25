plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.21"
}

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:3.4.1")
}

repositories {
    google()
    jcenter()
}