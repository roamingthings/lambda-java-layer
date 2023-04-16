subprojects {
    group = "de.roamingthings"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

val jdkVersion = project.properties["jdkVersion"] ?: "17"

tasks.register<Exec>("buildJre") {
    doFirst {
        environment["JDK_VERSION"] = jdkVersion
        commandLine("./build-jre.sh")
    }
}

tasks.register<Exec>("prepareLayer") {
    dependsOn("buildJre")
    doFirst {
        environment["JDK_VERSION"] = jdkVersion
        commandLine("./prepare-layer.sh")
    }
}

tasks.register<Zip>("buildLayer") {
    dependsOn("prepareLayer")
    archiveFileName.set("java${jdkVersion}layer.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distribution"))
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    from(layout.buildDirectory.dir("layer/${jdkVersion}"))
}

tasks.register<Delete>("clean") {
    delete.add("${buildDir}")
}
