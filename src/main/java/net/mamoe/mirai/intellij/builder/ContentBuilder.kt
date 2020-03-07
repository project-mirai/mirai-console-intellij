package net.mamoe.mirai.intellij.builder

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.writeChild
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.intellij.CreateConfig
import net.mamoe.mirai.intellij.CreateConfig.consoleVersion
import net.mamoe.mirai.intellij.CreateConfig.coreVersion
import org.intellij.lang.annotations.Language
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


fun MiraiPluginModuleBuilder.createRoot(): VirtualFile? {
    val temp = contentEntryPath ?: return null

    val path = FileUtil.toSystemIndependentName(temp)

    return try {
        Files.createDirectories(Paths.get(path))
        LocalFileSystem.getInstance().refreshAndFindFileByPath(path)
    } catch (e: IOException) {
        null
    }
}

fun MiraiPluginModuleBuilder.createDic(
    root: VirtualFile
) {
    val sourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/kotlin")
    VfsUtil.createDirectories(root.path + "/src/main/resources")
    VfsUtil.createDirectories(root.path + "/src/test/kotlin")
    VfsUtil.createDirectories(root.path + "/src/test/resources")
    //val resourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/resources")
    //val testSourceDirectory = VfsUtil.createDirectories(root.path + "/src/test/kotlin")
    //val testResourceDirectory = VfsUtil.createDirectories(root.path + "/src/test/resources")

    val buildToolFiles: List<String> = when (CreateConfig.buildTool) {
        CreateConfig.BUILD_GRADLE_KOTLIN -> Template.gradleCommon + Template.gradleKotlinDsl
        CreateConfig.BUILD_GRADLE_GROOVY -> Template.gradleCommon + Template.gradleGroovyDsl
        CreateConfig.BUILD_MAVEN -> Template.mavenJava
        else -> error("not exhaustive when")
    }

    fun getResource(relativePath: String): String {
        return this::class.java.classLoader.getResourceAsStream(relativePath)?.readBytes()?.let { String(it) }
            ?: error("cannot find resource : $relativePath")
    }

    runBlocking { joinAll(consoleVersion(), coreVersion()) }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun String.replaceTemplateVariables(): String {
        return runBlocking {
           this@replaceTemplateVariables.replace("<GROUP>", CreateConfig.groupId)
                .replace("<PROJECT_NAME>", CreateConfig.artifactId)
                .replace("<VERSION>", CreateConfig.version)
                .replace("<MIRAI_CONSOLE_VERSION>", consoleVersion().await())
                .replace("<MIRAI_CORE_VERSION>", coreVersion().await())
        }//this should not take time since there are version cache!!
    }

    buildToolFiles.forEach {
        root.writeChild(it.substringAfter('/').substringAfter('/'), getResource(it).replaceTemplateVariables())
    }

    val pluginBaseClassName: String = CreateConfig.mainClassQualifiedName.substringAfterLast('.')

    val packageName: String = CreateConfig.mainClassQualifiedName.substringBeforeLast('.')

    when (CreateConfig.language) {
        CreateConfig.LANGUAGE_KOTLIN -> {
            sourceDirectory.writeChild(
                CreateConfig.mainClassQualifiedName.replace('.', '/') + ".kt",
                Template.pluginBaseKotlin.replace(
                    "MAIN_CLASS_NAME",
                    pluginBaseClassName
                ).replace("PACKAGE", packageName)
            )
        }
        CreateConfig.LANGUAGE_JAVA -> {
            sourceDirectory.writeChild(
                CreateConfig.mainClassQualifiedName.replace('.', '/') + ".java",
                Template.pluginBaseJava.replace(
                    "MAIN_CLASS_NAME",
                    pluginBaseClassName
                ).replace("PACKAGE", packageName)
            )
        }
    }

    root.writeChild("src/main/resources/plugin.yml", Template.pluginYml
        .replace("NAME", CreateConfig.pluginName)
        .replace("VERSION", CreateConfig.version)
        .replace("AUTHOR", CreateConfig.author)
        .replace("MAIN", CreateConfig.mainClassQualifiedName)
        .replace("INFO", CreateConfig.info)
        .replace("DEPENDS", CreateConfig.depends.takeIf { it.isNotEmpty() }?.joinToString { "\n  -$it" } ?: "[]")
    )
}


object Template {
    val gradleCommon: List<String> = listOf(
        "template/gradle-common/gradle/wrapper/gradle-wrapper.jar",
        "template/gradle-common/gradle/wrapper/gradle-wrapper.properties",
        "template/gradle-common/gradlew",
        "template/gradle-common/gradlew.bat"
    )

    val gradleKotlinDsl: List<String> = listOf(
        "template/gradle-kotlin-dsl/build.gradle.kts",
        "template/gradle-kotlin-dsl/settings.gradle.kts",
        "template/gradle-kotlin-dsl/gradle.properties"
    )

    val gradleGroovyDsl: List<String> = listOf(
        "template/gradle-groovy-dsl/build.gradle",
        "template/gradle-groovy-dsl/settings.gradle",
        "template/gradle-groovy-dsl/gradle.properties"
    )

    val mavenJava: List<String> = listOf(
        "template/maven-kotlin/pom.xml"
    )


    @Suppress("SimpleRedundantLet") // bug
    @Language("kotlin")
    val pluginBaseKotlin: String = """
        package PACKAGE
        
        import net.mamoe.mirai.console.plugins.PluginBase
        import net.mamoe.mirai.event.events.MessageRecallEvent
        import net.mamoe.mirai.event.subscribeAlways
        import net.mamoe.mirai.event.subscribeMessages
        import net.mamoe.mirai.utils.info

        object MAIN_CLASS_NAME : PluginBase() {
            override fun onLoad() {
                super.onLoad()
            }

            override fun onEnable() {
                super.onEnable()

                logger.info { "Plugin loaded!" }

                subscribeMessages {
                    "greeting" reply { "Hello \$\{sender.nick}" }
                }

                subscribeAlways<MessageRecallEvent> { event ->
                    logger.info { "\$\{event.authorId} 的消息被撤回了" }
                }
            }
        }
    """.trimIndent().let { it.replace("\\\$\\{", "\${") } // bug also

    @Language("java")
    val pluginBaseJava: String = """
        
    """.trimIndent()


    @Language("yaml")
    val pluginYml: String = """
        name: "NAME"
        author: "AUTHOR"
        version: "VERSION"
        main: "MAIN"
        info: "INFO"
        depends: DEPENDS
    """.trimIndent()
}