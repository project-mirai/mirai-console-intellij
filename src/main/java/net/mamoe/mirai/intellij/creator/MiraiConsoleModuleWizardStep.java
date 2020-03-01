package net.mamoe.mirai.intellij.creator;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiraiConsoleModuleWizardStep extends ModuleWizardStep {

    private JTextField pluginNameField;
    private JTextField pluginVersionField;
    private JTextField mainClassField;
    private JTextField descriptionField;
    private JTextField authorsField;
    private JTextField websiteField;
    private JTextField dependField;
    private JLabel title;

    public JPanel panel;

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void updateDataModel() {

    }

    public boolean validate() throws ConfigurationException {
        if(pluginNameField.getText().isBlank()){
            throw new ConfigurationException("没有填写插件名称","新建项目失败");
        }
        if(pluginVersionField.getText().isBlank()){
            throw new ConfigurationException("没有填写插件版本","新建项目失败");
        }
        if(mainClassField.getText().isBlank()){
            throw new ConfigurationException("没有填写主类","新建项目失败");
        }
        if(authorsField.getText().isBlank()){
            throw new ConfigurationException("没有填写开发者姓名","新建项目失败");
        }
        return true;
    }

    @Override
    public void onStepLeaving() {
        CreateConfig.author = authorsField.getText();
        CreateConfig.depends = Stream.of(dependField.getText().replace("，", ",").split(",")).map(String::trim).collect(Collectors.toList());
        CreateConfig.info = descriptionField.getText();
        if(!websiteField.getText().isBlank()){
            CreateConfig.info+=" Web: " + websiteField.getText();
        }
        CreateConfig.mainClassPath = mainClassField.getText();
        CreateConfig.version = pluginVersionField.getText();
        if(!CreateConfig.version.toLowerCase().startsWith("v")){
            CreateConfig.version = "V" + CreateConfig.version;
        }
        CreateConfig.pluginName = pluginNameField.getText();
    }
}

