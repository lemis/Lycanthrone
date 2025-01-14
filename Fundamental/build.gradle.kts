import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    antlr
    idea
    war
}

repositories { jcenter() }

dependencies {
    implementation(project(":Core"))
    annotationProcessor(project(":Core"))

   implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.rabbitmq:amqp-client:5.7.1")
    implementation("org.apache.pdfbox:pdfbox:2.0.16")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation(group = "org.snmp4j", name = "snmp4j", version = "3.4.0")

    val antlrV = "4.7.2"
    `antlr`("org.antlr:antlr4:$antlrV")
    implementation("org.antlr:antlr4:$antlrV")
    implementation("org.antlr:antlr4-runtime:$antlrV")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M2")
    implementation("com.google.apis:google-api-services-drive:v3-rev165-1.25.0")

    val googleApiV = "1.30.1"
    implementation("com.google.api-client:google-api-client:$googleApiV")
    implementation("com.google.oauth-client:google-oauth-client-jetty:$googleApiV")
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments.addAll(listOf("-visitor"))
}

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    doLast { print("Finished compiling Kotlin source code") }
}

val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    doLast { print("Finished compiling Kotlin source code for testing") }
}
