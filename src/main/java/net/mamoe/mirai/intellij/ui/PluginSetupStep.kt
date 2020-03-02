package net.mamoe.mirai.intellij.ui

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.intellij.CreateConfig
import org.jsoup.Jsoup
import javax.swing.*

class PluginSetupStep : ModuleWizardStep() {
    private lateinit var pluginNameField: JTextField
    private lateinit var pluginVersionField: JTextField
    private lateinit var mainClassField: JTextField
    private lateinit var descriptionField: JTextField
    private lateinit var authorsField: JTextField
    private lateinit var websiteField: JTextField
    private lateinit var dependField: JTextField
    private lateinit var manageField: JComboBox<String>
    private lateinit var languageField: JComboBox<String>
    private lateinit var title: JLabel





    var panel: JPanel? = null

    override fun getComponent(): JComponent {
        return panel!!
    }

    override fun updateDataModel() {
        CreateConfig.author = authorsField.text
        CreateConfig.depends = dependField.text.replace("，", ",").split(",").map(String::trim)
        CreateConfig.info = descriptionField.text
        if (!websiteField.text.isBlank()) {
            CreateConfig.info += " Web: " + websiteField.text
        }
        CreateConfig.mainClassPath = mainClassField.text
        CreateConfig.version = pluginVersionField.text
        if (!CreateConfig.version!!.toLowerCase().startsWith("v")) {
            CreateConfig.version = "V" + CreateConfig.version
        }
        CreateConfig.pluginName = pluginNameField.text
        println("I received")
    }

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

    override fun updateStep() {

        languageField.addItem(CreateConfig.LANGUAGE_JAVA)
        languageField.addItem(CreateConfig.LANGUAGE_KOTLIN)

        manageField.addItem(CreateConfig.MANAGE_MAVEN)
        manageField.addItem(CreateConfig.MANAGE_GRADLE_GROOVY)

        languageField.addActionListener{
            if(languageField.selectedItem == CreateConfig.LANGUAGE_KOTLIN){
                manageField.addItem(CreateConfig.MANAGE_GRADLE_KOTLIN)
                manageField.removeItem(CreateConfig.MANAGE_MAVEN)
            }else{
                manageField.addItem(CreateConfig.MANAGE_MAVEN)
                manageField.removeItem(CreateConfig.MANAGE_GRADLE_KOTLIN)
            }
        }
    }

    override fun onStepLeaving() {

    }
}