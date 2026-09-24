plugins {
    id("lifebalance.java-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
    constraints {
        implementation(libs.findLibrary("tomcat-embed-core").get())
        implementation(libs.findLibrary("tomcat-embed-el").get())
        implementation(libs.findLibrary("tomcat-embed-websocket").get())
    }
    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
}
