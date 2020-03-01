package net.mamoe.mirai.intellij.creator;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiraiPluginModuleBuilder extends ModuleBuilder{
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {

    }

    public ModuleType<?> getModuleType() {
        return MiraiConsolePlugin.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new MiraiConsoleModuleWizardStep();
    }
}
