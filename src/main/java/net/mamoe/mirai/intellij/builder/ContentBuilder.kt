package net.mamoe.mirai.intellij.builder

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import net.mamoe.mirai.intellij.CreateConfig
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
    root:VirtualFile
){
    val sourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/java")
    val resourceDirectory = VfsUtil.createDirectories(root.path + "/src/main/resources")
    val testSourceDirectory = VfsUtil.createDirectories(root.path + "/src/test/java")
    val testResourceDirectory = VfsUtil.createDirectories(root.path + "/src/test/resources")


    var fileName = ""
    val path = CreateConfig.mainClassPath!!.split("/").toMutableList().also {
        it.removeAt(it.size - 1)
    }.joinToString("/")

    root.findOrCreateChildData(this, "build.gradle")
    root.findOrCreateChildData(this, "gradle.properties")
    val sourcePackageDirectory =  VfsUtil.createDirectories(root.path + "/src/main/java/" + path)

}
