import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

group = "org.f14a.fatin2"
version = rootProject.findProperty("version_demoplugin") as String

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Plugin main project dependency.
    compileOnly(project(":"))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("demoplugin")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")

    destinationDirectory.set(rootProject.file("run/plugins"))

    manifest {
        attributes(
            "Name" to "DemoPlugin",
            "Version" to project.version.toString(),
            "Plugin-Main-Class" to "org.f14a.demoplugin.Main"
        )
    }

    doFirst {
        destinationDirectory.get().asFile.mkdirs()
    }
}

tasks.register("buildDemoplugin") {
    group = "demoplugin"
    description = "Build the demo plugin shadow JAR"
    dependsOn(":demoplugin:shadowJar")
}

