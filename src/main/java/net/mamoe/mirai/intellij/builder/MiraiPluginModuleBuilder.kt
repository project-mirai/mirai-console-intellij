package net.mamoe.mirai.intellij.builder

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.util.containers.toArray
import net.mamoe.mirai.intellij.module.MiraiConsolePluginModule.Companion.instance
import net.mamoe.mirai.intellij.ui.PMSetupStep
import net.mamoe.mirai.intellij.ui.PluginSetupStep

class MiraiPluginModuleBuilder : ModuleBuilder() {
    @Throws(ConfigurationException::class)
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val project = modifiableRootModel.project
        val root = createRoot()?:throw ConfigurationException("创建ROOT文件失败","项目创建失败")
        modifiableRootModel.addContentEntry(root)

        if (moduleJdk != null) {
            modifiableRootModel.sdk = moduleJdk
        }
        this.createDic(root)
    }

    override fun getModuleType(): ModuleType<*> {
        return instance
    }

    override fun createWizardSteps(
        wizardContext: WizardContext,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return arrayOf(PMSetupStep())
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep? {
        return PluginSetupStep()
    }
}