package net.mamoe.mirai.intellij.creator.module

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.util.PlatformIcons
import net.mamoe.mirai.intellij.creator.MiraiPluginModuleBuilder
import javax.swing.Icon

class MiraiConsolePluginModule : ModuleType<MiraiPluginModuleBuilder>(ID) {
    override fun createModuleBuilder(): MiraiPluginModuleBuilder {
        return MiraiPluginModuleBuilder()
    }

    override fun getName(): String {
        return "Mirai Console Plugin"
    }

    override fun getDescription(): String {
        return "Create a mirai console plugin"
    }

    override fun getNodeIcon(b: Boolean): Icon {
        return PlatformIcons.ABSTRACT_METHOD_ICON
    }

    override fun createWizardSteps(wizardContext: WizardContext,
                                   moduleBuilder: MiraiPluginModuleBuilder,
                                   modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)
    }

    companion object {
        private const val ID = "MIRAI_CONSOLE_PLUGIN"

        @JvmStatic
        val instance: MiraiConsolePluginModule
            get() = ModuleTypeManager.getInstance().findByID(ID) as MiraiConsolePluginModule
    }
}