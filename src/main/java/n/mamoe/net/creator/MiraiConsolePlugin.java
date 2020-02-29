package n.mamoe.net.creator;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MiraiConsolePlugin extends ModuleType<MiraiPluginModuleBuilder> {
    private static final String ID = "MIRAI_CONSOLE_PLUGIN";

    public MiraiConsolePlugin() {
        super(ID);
    }

    public static MiraiConsolePlugin getInstance() {
        return (MiraiConsolePlugin) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public MiraiPluginModuleBuilder createModuleBuilder() {
        return new MiraiPluginModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Mirai Console Plugin";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Create a mirai console plugin";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return PlatformIcons.ABSTRACT_METHOD_ICON;
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext,
                                                @NotNull MiraiPluginModuleBuilder moduleBuilder,
                                                @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }



}
