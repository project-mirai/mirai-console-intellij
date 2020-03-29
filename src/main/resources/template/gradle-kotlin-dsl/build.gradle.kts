plugins {
    kotlin("jvm") version "1.4-M1"
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "<GROUP>"
version = "<VERSION>"

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://mirrors.huaweicloud.com/repository/maven")
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.mamoe:mirai-core-jvm:<MIRAI_CORE_VERSION>")
    compileOnly("net.mamoe:mirai-console:<MIRAI_CONSOLE_VERSION>")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}