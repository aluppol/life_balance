plugins {
    id("lifebalance.java-conventions")
    id("info.solidsoft.pitest")
}

val libs = the<VersionCatalogsExtension>().named("libs")

pitest {
    pitestVersion = libs.findVersion("pitest").get().requiredVersion
    junit5PluginVersion = libs.findVersion("pitest-junit5").get().requiredVersion
    targetClasses = setOf("com.luppol.lifebalance.*")
    threads = 4
    outputFormats = setOf("HTML", "XML")
    timestampedReports = false
    mutationThreshold = 85
}

tasks.check {
    dependsOn(tasks.named("pitest"))
}
