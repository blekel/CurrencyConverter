plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.squareup.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.core)
}
