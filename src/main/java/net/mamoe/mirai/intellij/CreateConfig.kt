package net.mamoe.mirai.intellij

import kotlinx.coroutines.*
import org.jsoup.Jsoup

object CreateConfig {
    const val LANGUAGE_JAVA = "Java"
    const val LANGUAGE_KOTLIN = "Kotlin"

    const val BUILD_MAVEN = "Maven"
    const val BUILD_GRADLE_GROOVY = "Gradle (Groovy DSL)"
    const val BUILD_GRADLE_KOTLIN = "Gradle (Kotlin DSL)"


    lateinit var mainClassQualifiedName: String
    lateinit var pluginName: String
    lateinit var author: String
    lateinit var info: String
    lateinit var depends: List<String>
    lateinit var version: String
    lateinit var language: String
    lateinit var buildTool: String

    private suspend fun getNewestCoreVersion(): String {
        return withContext(Dispatchers.IO) {
            Jsoup.connect("https://bintray.com/package/generalTab?pkgPath=/him188moe/mirai/mirai-core").get().body().getElementById(
                "versions"
            ).getElementsByClass("tr")[0].getElementsByClass("td")[0].getElementsByTag("a")[0].text()
        }
    }

    private suspend fun getNewestConsoleVersion(): String {
        return withContext(Dispatchers.IO) {
            Jsoup.connect("https://bintray.com/package/generalTab?pkgPath=/him188moe/mirai/mirai-console").get().body().getElementById(
                "versions"
            ).getElementsByClass("tr")[0].getElementsByClass("td")[0].getElementsByTag("a")[0].text()
        }
    }

    val consoleVersion: Deferred<String> = GlobalScope.async {
        repeat(3) {
            try {
                return@async getNewestConsoleVersion()
            } catch (e: Exception) {
            }
        }

        @Suppress("UNREACHABLE_CODE")
        "UNKNOWN" // 类型推断 bug
    }
    val coreVersion: Deferred<String> = GlobalScope.async {
        repeat(3) {
            try {
                return@async getNewestCoreVersion()
            } catch (e: Exception) {
            }
        }

        @Suppress("UNREACHABLE_CODE")
        "UNKNOWN" // 类型推断 bug
    }
}

