plugins {
    `java-library`
    `maven-publish`
}

group = "com.3ll3d00d.renault-java"
version = "0.1.0"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("com.squareup.okhttp3:okhttp:5.4.0")
    api("tools.jackson.core:jackson-databind:3.2.0")
    implementation("org.apache.logging.log4j:log4j-api:2.26.0")

    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.4.0")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:2.26.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
