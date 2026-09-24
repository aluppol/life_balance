plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.pitest.gradle.plugin)
}

tasks.jar {
    destinationDirectory = layout.buildDirectory.dir("jars")
}
