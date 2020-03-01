buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    }
}

plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    kotlin("jvm") version "1.3.31" // 与 IDEA bundled 的 kotlin 一致
}

group = "net.mamoe"
version = "1.0.0"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.3.1"
}

tasks.withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask>() {
    setChangeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""".trimIndent())
}

allprojects {
    repositories {
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven") }
        jcenter()
        mavenCentral()
        google()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
    }
}