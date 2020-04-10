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
    val sourceDirectory: VirtualFile
    val testDirectory: VirtualFile
    when (CreateConfig.language) {
        CreateConfig.LANGUAGE_KOTLIN -> {
            sourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/kotlin")
            testDirectory = VfsUtil.createDirectories(root.path + "/src/test/kotlin")
        }
        CreateConfig.LANGUAGE_JAVA -> {
            sourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/java")
            testDirectory = VfsUtil.createDirectories(root.path + "/src/test/java")
        }
        else -> error("internal error: illegal language: ${CreateConfig.language}")
    }
    VfsUtil.createDirectories(root.path + "/src/main/resources")
    VfsUtil.createDirectories(root.path + "/src/test/resources")

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
            testDirectory.writeChild(
                "mirai/RunMirai.kt",
                Template.testConsoleRunnerKotlin
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
            testDirectory.writeChild(
                "mirai/RunMirai.java",
                Template.testConsoleRunnerJava
            )
        }
    }

    root.writeChild("src/main/resources/plugin.yml", Template.pluginYml
        .replace("NAME", CreateConfig.pluginName)
        .replace("VERSION", CreateConfig.version)
        .replace("AUTHOR", CreateConfig.author)
        .replace("MAIN", CreateConfig.mainClassQualifiedName)
        .replace("INFO", CreateConfig.info)
        .replace(
            "DEPENDS",
            CreateConfig.depends.filter { it.isNotBlank() }.takeIf { it.isNotEmpty() }?.joinToString { "\n  -$it" }
                ?: "[]")
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

                logger.info("Plugin loaded!")

                subscribeMessages {
                    "greeting" reply { "Hello \$\{sender.nick}" }
                }

                subscribeAlways<MessageRecallEvent> { event ->
                    logger.info { "\$\{event.authorId} 的消息被撤回了" }
                }
            }
        }
    """.trimIndent().let { it.replace("\\\$\\{", "\${") } // bug also


    @Language("kotlin")
    val testConsoleRunnerKotlin: String = """
        package mirai

        import kotlinx.coroutines.runBlocking
        import net.mamoe.mirai.console.command.CommandManager
        import net.mamoe.mirai.console.pure.MiraiConsolePureLoader

        object RunMirai {

            // 执行 gradle task: runMiraiConsole 来自动编译, shadow, 复制, 并启动 pure console.

            @JvmStatic
            fun main(args: Array<String>) {
                // 默认在 /test 目录下运行

                MiraiConsolePureLoader.load(args[0], args[1]) // 启动 console

                runBlocking { CommandManager.join() } // 阻止主线程退出
            }
        }
    """.trimIndent()

    @Language("java")
    val pluginBaseJava: String = """
        package PACKAGE;

        import net.mamoe.mirai.console.plugins.PluginBase;

        class ExamplePluginMain extends PluginBase {
            
            public void onLoad(){
                Events.subscribeAlways(GroupMessage.class, (GroupMessage event) -> {

                    if (event.getMessage().contains("reply")) {
                        // 引用回复
                        final QuoteReplyToSend quote = MessageUtils.quote(event.getMessage(), event.getSender());
                        event.getGroup().sendMessage(quote.plus("引用回复"));

                    } else if (event.getMessage().contains("at")) {
                        // at
                        event.getGroup().sendMessage(new At(event.getSender()));

                    } else if (event.getMessage().contains("permission")) {
                        // 成员权限
                        event.getGroup().sendMessage(event.getPermission().toString());

                    } else if (event.getMessage().contains("mixed")) {
                        // 复合消息, 通过 .plus 连接两个消息
                        event.getGroup().sendMessage(
                                MessageUtils.newImage("{01E9451B-70ED-EAE3-B37C-101F1EEBF5B5}.png") // 演示图片, 可能已过期
                                        .plus("Hello") // 文本消息
                                        .plus(new At(event.getSender())) // at 群成员
                                        .plus(AtAll.INSTANCE) // at 全体成员
                        );

                    } else if (event.getMessage().contains("recall1")) {
                        event.getGroup().sendMessage("你看不到这条消息").recall();
                        // 发送消息马上就撤回. 因速度太快, 客户端将看不到这个消息.

                    } else if (event.getMessage().contains("recall2")) {
                        final Job job = event.getGroup().sendMessage("3秒后撤回").recallIn(3000);

                        // job.cancel(new CancellationException()); // 可取消这个任务

                    } else if (event.getMessage().contains("上传图片")) {
                        File file = new File("myImage.jpg");
                        if (file.exists()) {
                            final Image image = event.getGroup().uploadImage(new File("myImage.jpg"));
                            // 上传一个图片并得到 Image 类型的 Message

                            final String imageId = image.getImageId(); // 可以拿到 ID
                            final Image fromId = MessageUtils.newImage(imageId); // ID 转换得到 Image

                            event.getGroup().sendMessage(image); // 发送图片
                        }

                    } else if (event.getMessage().contains("friend")) {
                        final Future<MessageReceipt<? extends Contact>> future = event.getSender().sendMessageAsync("Async send"); // 异步发送
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            
            public void onEnable(){
                logger.info("Plugin loaded!");
            }
            
        }        
    """.trimIndent()

    @Language("java")
    val testConsoleRunnerJava: String = """
        package mirai;

        import kotlinx.coroutines.BuildersKt;
        import kotlinx.coroutines.GlobalScope;
        import net.mamoe.mirai.console.command.CommandManager;
        import net.mamoe.mirai.console.pure.MiraiConsolePureLoader;

        public class RunMirai {
           
            // 执行 gradle task: runMiraiConsole 来自动编译, shadow, 复制, 并启动 pure console.

            public static void main(String[] args) throws InterruptedException {
                // 默认在 /test 目录下运行

                MiraiConsolePureLoader.load(args[0], args[1]); // 启动 console

                // 阻止主线程退出
                BuildersKt.runBlocking(GlobalScope.INSTANCE.getCoroutineContext(), (coroutineScope, continuation) -> CommandManager.INSTANCE.join(continuation));
            }
        }
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