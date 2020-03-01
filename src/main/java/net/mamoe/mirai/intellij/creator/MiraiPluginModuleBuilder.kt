package net.mamoe.mirai.intellij.creator

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import net.mamoe.mirai.intellij.creator.module.MiraiConsolePluginModule.Companion.instance

class MiraiPluginModuleBuilder : ModuleBuilder() {
    @Throws(ConfigurationException::class)
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
    }

    override fun getModuleType(): ModuleType<*> {
        return instance
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
        return MiraiConsoleModuleWizardStep()
    }
}