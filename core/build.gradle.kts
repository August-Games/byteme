import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val groupName = "games.august"
val artifactName = "byteme-core"
val versionName = "1.1.1"
group = groupName
version = versionName

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.1.4")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(11)
}

val privateProperties = Properties()
val privatePropertiesFile = rootProject.file("private.properties")
if (privatePropertiesFile.exists()) {
    privateProperties.load(FileInputStream(privatePropertiesFile))
} else {
    privateProperties.setProperty(
        "sonatypeUsername",
        System.getenv("SONATYPE_USERNAME") ?: "MISSING"
    )
    privateProperties.setProperty(
        "sonatypePassword",
        System.getenv("SONATYPE_PASSWORD") ?: "MISSING"
    )

    privateProperties.setProperty(
        "signingKeyId",
        System.getenv("SIGNING_KEY_ID") ?: "MISSING"
    )
    privateProperties.setProperty(
        "signingKeyPassword",
        System.getenv("SIGNING_KEY_PASSWORD") ?: "MISSING"
    )
    privateProperties.setProperty(
        "signingKeyLocation",
        System.getenv("SIGNING_KEY_LOCATION") ?: "MISSING"
    )
}

extraProperties["signing.keyId"] = privateProperties["signingKeyId"]
extraProperties["signing.password"] = privateProperties["signingKeyPassword"]
extraProperties["signing.secretKeyRingFile"] = privateProperties["signingKeyLocation"]

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = groupName
            artifactId = artifactName
            version = versionName

            pom {
                name.set(artifactName)
                description.set("A Kotlin DSL for writing bytes to a byte array")
                url.set("https://github.com/August-Games/byteme")

                licenses {
                    license {
                        name.set("ByteMe License")
                        url.set("https://github.com/August-Games/byteme/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("dylan")
                        name.set("Dylan")
                        email.set("dylan@august.games")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/August-Games/byteme.git")
                    developerConnection.set("scm:git:ssh://github.com:August-Games/byteme.git")
                    url.set("https://github.com/August-Games/byteme")
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            url = uri(
                if (versionName.endsWith("SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
            credentials {
                username = privateProperties["sonatypeUsername"] as String? ?: System.getenv("SONATYPE_USERNAME")
                password = privateProperties["sonatypePassword"] as String? ?: System.getenv("SONATYPE_PASSWORD")
            }
        }
        mavenLocal()
    }

    signing {
        sign(publishing.publications)
    }
}

