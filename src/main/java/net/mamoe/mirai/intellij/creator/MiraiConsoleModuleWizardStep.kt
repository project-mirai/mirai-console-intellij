package net.mamoe.mirai.intellij.creator

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class MiraiConsoleModuleWizardStep : ModuleWizardStep() {
    private lateinit var pluginNameField: JTextField
    private lateinit var pluginVersionField: JTextField
    private lateinit var mainClassField: JTextField
    private lateinit var descriptionField: JTextField
    private lateinit var authorsField: JTextField
    private lateinit var websiteField: JTextField
    private lateinit var dependField: JTextField
    private lateinit var title: JLabel

    var panel: JPanel? = null

    override fun getComponent(): JComponent {
        return panel!!
    }

    override fun updateDataModel() {}

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        if (pluginNameField.text.isBlank()) {
            throw ConfigurationException("请填写插件名称", "新建项目失败")
        }
        if (pluginVersionField.text.isBlank()) {
            throw ConfigurationException("没有填写插件版本", "新建项目失败")
        }
        if (mainClassField.text.isBlank()) {
            throw ConfigurationException("没有填写主类", "新建项目失败")
        }
        if (authorsField.text.isBlank()) {
            throw ConfigurationException("没有填写开发者姓名", "新建项目失败")
        }
        return true
    }

    override fun _init() {
        super._init()
    }

    override fun onStepLeaving() {
        CreateConfig.author = authorsField!!.text
        CreateConfig.depends = dependField!!.text.replace("，", ",").split(",").map(String::trim)
        CreateConfig.info = descriptionField!!.text
        if (!websiteField!!.text.isBlank()) {
            CreateConfig.info += " Web: " + websiteField.text
        }
        CreateConfig.mainClassPath = mainClassField!!.text
        CreateConfig.version = pluginVersionField!!.text
        if (!CreateConfig.version!!.toLowerCase().startsWith("v")) {
            CreateConfig.version = "V" + CreateConfig.version
        }
        CreateConfig.pluginName = pluginNameField!!.text
    }
}