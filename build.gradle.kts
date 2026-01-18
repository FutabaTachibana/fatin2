plugins {
    `java-library`
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    signing
    kotlin("jvm") version "2.3.0"
}

group = "org.f14a"
version = System.getenv("RELEASE_VERSION") ?: properties["version"] as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Annotations
    compileOnly("org.jetbrains:annotations:24.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // WebSocket client library
    implementation("org.java-websocket:Java-WebSocket:1.6.0")

    // JSON processing library
    api("com.google.code.gson:gson:2.13.2")

    // YAML processing library
    implementation("org.yaml:snakeyaml:2.5")

    // Logging library
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.21")

    // Tools
    implementation("org.apache.commons:commons-lang3:3.18.0")

    // Javalin Web
    implementation("io.javalin:javalin:6.7.0")

    // JWT
    implementation("com.auth0:java-jwt:4.5.0")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")

    // Kotlin
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("org.f14a.fatin2.Main")
}

// Exclude shadow runtime elements while publishing
val shadowRuntimeElements by configurations.getting
components.named<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(shadowRuntimeElements) {
        skip()
    }
}

tasks.jar {
    archiveBaseName.set("fatin2")
    archiveVersion.set(project.version.toString())
    // Output: fatin2-{version}.jar
    archiveClassifier.set("")

    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.shadowJar {
    archiveBaseName.set("fatin2")
    archiveVersion.set(project.version.toString())
    // Output: fatin2-{version}-all.jar
    archiveClassifier.set("all")

    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.build {
    dependsOn(tasks.jar, tasks.shadowJar)
}

tasks.register<JavaExec>("runBot") {
    group = "application"
    description = "Run the Fatin2 bot"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(application.mainClass.get())
    workingDir = file("$projectDir/run")

    doFirst {
        file("$projectDir/run").mkdirs()
    }
}

tasks.clean {
    val versionDemoplugin = properties["version_demoplugin"]
    delete("$projectDir/run/plugins/demoplugin-${versionDemoplugin}.jar")
}

tasks.test {
    doFirst {
        file("$projectDir/test").mkdirs()
    }
    workingDir = file("$projectDir/test")
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("fatin2")
                description.set("A quick and easy-to-use bot framework for Onebot v11.")
                url.set("https://github.com/f0xea/fatin2")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("f0xea")
                        name.set("f14a")
                        url.set("https://github.com/f0xea")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/f0xea/fatin2.git")
                    developerConnection.set("scm:git:ssh://git@github.com/f0xea/fatin2.git")
                    url.set("https://github.com/f0xea/fatin2")
                }
            }
        }
    }

    repositories {
        maven {
            name = "localStaging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    val signingKey = System.getenv("JRELEASER_GPG_SECRET_KEY") ?: System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("JRELEASER_GPG_PASSPHRASE") ?: System.getenv("GPG_PASSPHRASE")

    isRequired = !signingKey.isNullOrBlank()

    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

