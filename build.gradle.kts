plugins {
    kotlin("jvm") version "1.9.21"
    application
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}
