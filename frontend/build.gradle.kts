import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    alias(libs.plugins.node)
}

node {
    version = libs.versions.nodejs.get()
    download = true
    npmInstallCommand = "ci"
}

val frontendSources = fileTree(projectDir) {
    exclude("node_modules/**", "dist/**", "coverage/**", "reports/**", ".stryker-tmp/**", "build/**", ".gradle/**")
}

val lintFrontend = tasks.register<NpmTask>("lintFrontend") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "lint")
    inputs.files(frontendSources)
}

val typecheckFrontend = tasks.register<NpmTask>("typecheckFrontend") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "typecheck")
    inputs.files(frontendSources)
}

val testFrontend = tasks.register<NpmTask>("testFrontend") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "test")
    inputs.files(frontendSources)
    outputs.dir("coverage")
}

val buildFrontend = tasks.register<NpmTask>("buildFrontend") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "build")
    inputs.files(frontendSources)
    outputs.dir("dist")
}

tasks.check {
    dependsOn(lintFrontend, typecheckFrontend, testFrontend)
}

tasks.assemble {
    dependsOn(buildFrontend)
}

tasks.clean {
    delete("dist", "coverage")
}

configurations.consumable("singlePageApplication")

artifacts {
    add("singlePageApplication", layout.projectDirectory.dir("dist")) {
        builtBy(buildFrontend)
    }
}
