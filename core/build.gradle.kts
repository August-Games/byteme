plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val ver = "1.0.0"
group = "games.august.byteme"
version = ver

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
kotlin {
    jvmToolchain(11)
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//
//            groupId = "games.august.byteme"
//            artifactId = "core"
//            version = "1.0.0"
//
//            pom {
//                name.set("ByteMe")
//                description.set("A Kotlin DSL for writing bytes to a byte array")
//                url.set("https://github.com/August-Games/byteme")
//
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//
//                developers {
//                    developer {
//                        id.set("dylan")
//                        name.set("Dylan")
//                        email.set("dylan@august.games")
//                    }
//                }
//
//                scm {
//                    connection.set("scm:git:git://github.com/August-Games/byteme.git")
//                    developerConnection.set("scm:git:ssh://github.com:August-Games/byteme.git")
//                    url.set("https://github.com/August-Games/byteme")
//                }
//            }
//        }
//    }
//
//    repositories {
//        maven {
//            name = "sonatype"
//            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
//            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//            url = uri(if (ver.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
//
//            credentials {
//                username = System.getenv("OSSRH_USERNAME")
//                password = System.getenv("OSSRH_PASSWORD")
//            }
//        }
//    }
//}

