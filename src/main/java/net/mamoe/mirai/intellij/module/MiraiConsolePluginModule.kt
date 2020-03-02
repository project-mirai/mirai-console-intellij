package net.mamoe.mirai.intellij.module

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.util.PlatformIcons
import net.mamoe.mirai.intellij.CreateConfig
import net.mamoe.mirai.intellij.builder.MiraiPluginModuleBuilder
import javax.swing.Icon

class MiraiConsolePluginModule : ModuleType<MiraiPluginModuleBuilder>(ID) {
    init {
        CreateConfig // start core version getter
    }

    override fun createModuleBuilder(): MiraiPluginModuleBuilder {
        return MiraiPluginModuleBuilder()
    }

    override fun getName(): String = "Mirai Console Plugin"
    override fun getDescription(): String = "Create a mirai console plugin"
    override fun getNodeIcon(b: Boolean): Icon = PlatformIcons.ABSTRACT_METHOD_ICON

    companion object {
        private const val ID = "MIRAI_CONSOLE_PLUGIN"

        @JvmStatic
        val instance: MiraiConsolePluginModule
            get() = ModuleTypeManager.getInstance().findByID(ID) as MiraiConsolePluginModule
    }
}