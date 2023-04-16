plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.aws.cdk.core)
    implementation(libs.aws.cdk.constructs)
    implementation(libs.bundles.jackson.full)

    testImplementation(libs.bundles.snapshotTesting)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

application {
    mainClass.set("de.roamingthings.javalayer.infrastructure.PipelineApp")
}

tasks.register<Exec>("synth") {
    commandLine("npx", "cdk", "synth", "--require-approval", "never")
}

tasks.register<Exec>("deploy") {
    commandLine("npx", "cdk", "deploy", "--require-approval", "never")
}

tasks.register<Exec>("destroy") {
    commandLine("npx", "cdk", "destroy", "--force")
}

tasks.clean {
    delete.add("cdk.out")
}
