package net.mamoe.mirai.intellij

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object CreateConfig {
    @JvmField
    val LANGUAGE_JAVA = "Java"
    @JvmField
    val LANGUAGE_KOTLIN = "Kotlin"


    @JvmField
    val MANAGE_MAVEN = "Maven"
    @JvmField
    val MANAGE_GRADLE_GROOVY = "Gradle (Groovy DSL)"
    @JvmField
    val MANAGE_GRADLE_KOTLIN = "Gradle (Kotlin DSL)"







    @JvmField
    var mainClassPath: String? = null
    @JvmField
    var pluginName: String? = null
    @JvmField
    var author: String? = null
    @JvmField
    var info: String? = null
    @JvmField
    var depends: List<String>? = null
    @JvmField
    var version: String? = null
    @JvmField
    var isKotlinProject: Boolean? = null


    suspend fun getNewestCoreVersion():String{
        return withContext(Dispatchers.IO) {
            Jsoup.connect("https://bintray.com/package/generalTab?pkgPath=/him188moe/mirai/mirai-core").post().body().getElementById("versions").getElementsByClass("tr")[0].getElementsByClass("td")[0].getElementsByTag("a")[0].text()
        }
    }

    suspend fun getNewestConsoleVersion(): String{
        return withContext(Dispatchers.IO) {
            Jsoup.connect("https://bintray.com/package/generalTab?pkgPath=/him188moe/mirai/mirai-console").post().body().getElementById("versions").getElementsByClass("tr")[0].getElementsByClass("td")[0].getElementsByTag("a")[0].text()
        }
    }


}

