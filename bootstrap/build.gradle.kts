plugins {
    id("lifebalance.spring-boot-application")
    `jacoco-report-aggregation`
}

val singlePageApplication = configurations.dependencyScope("singlePageApplication")
val singlePageApplicationFiles = configurations.resolvable("singlePageApplicationFiles") {
    extendsFrom(singlePageApplication.get())
}

dependencies {
    singlePageApplication(project(path = ":frontend", configuration = "singlePageApplication"))
    implementation(project(":application"))
    implementation(project(":adapters:persistence"))
    implementation(project(":adapters:web"))
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.security.test)
    testImplementation(libs.spring.boot.starter.security.oauth2.resource.server)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.archunit.junit5)
}

tasks.processResources {
    from(singlePageApplicationFiles) {
        into("static")
    }
}

tasks.bootJar {
    archiveFileName = "life-balance.jar"
}

tasks.jar {
    enabled = false
}

tasks.named<JacocoReport>("testCodeCoverageReport") {
    reports {
        xml.required = true
        html.outputLocation = rootProject.layout.buildDirectory.dir("reports/jacoco/test/html")
    }
}

tasks.check {
    dependsOn(tasks.named("testCodeCoverageReport"))
}
