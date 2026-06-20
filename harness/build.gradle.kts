plugins {
    `java`
    application
}

dependencies {
    implementation(project(":api"))
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.26.0")
}

application {
    // default to CLI; override with -PmainClass=com.renault.harness.HarnessServer
    mainClass = providers.gradleProperty("mainClass")
        .orElse("com.renault.harness.HarnessCli")
}

tasks.named<JavaExec>("run") {
    // make fixture files resolvable at ../api/src/test/resources/fixtures
    workingDir = rootProject.projectDir
    standardInput = System.`in`
}
