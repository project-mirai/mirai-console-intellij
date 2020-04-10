buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
        classpath("org.jsoup:jsoup:1.12.1")
    }
}
plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    kotlin("jvm") version "1.3.31" // 与 IDEA bundled 的 kotlin 一致
}

group = "net.mamoe"
version = "1.1.2"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1"
}

tasks.withType<org.jetbrains.intellij.tasks.PatchPluginXmlTask>() {
    setChangeNotes("""Update to 2020.1""".trimIndent())
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


dependencies {

}