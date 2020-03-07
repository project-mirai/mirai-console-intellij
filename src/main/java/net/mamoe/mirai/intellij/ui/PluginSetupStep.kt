package net.mamoe.mirai.intellij.ui

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.intellij.CreateConfig
import javax.swing.*
import javax.xml.bind.JAXBElement

class PluginSetupStep : ModuleWizardStep() {
    private lateinit var pluginNameField: JTextField
    private lateinit var pluginVersionField: JTextField
    private lateinit var mainClassField: JTextField
    private lateinit var descriptionField: JTextField
    private lateinit var authorsField: JTextField
    private lateinit var websiteField: JTextField
    private lateinit var dependField: JTextField
    //private lateinit var buildToolField: JComboBox<String>
    //private lateinit var languageField: JComboBox<String>
    private lateinit var consoleVersionField: JTextField
    private lateinit var coreVersionField: JTextField
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
        CreateConfig.mainClassQualifiedName = mainClassField.text
        CreateConfig.version = pluginVersionField.text
        if (!CreateConfig.version.toLowerCase().startsWith("v")) {
            CreateConfig.version = "V" + CreateConfig.version
        }
        CreateConfig.pluginName = pluginNameField.text
        CreateConfig.language = languageField.selectedItem?.toString()?:"Java"
       // CreateConfig.language = languageField.selectedItem?.toString().orEmpty()
       //CreateConfig.buildTool = buildToolField.selectedItem?.toString().orEmpty()
        println("I received")
    }

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        if(consoleVersionField.text == "获取中..." || coreVersionField.text == "获取中..."){
            throw ConfigurationException("正在获取最新的Core/Console版本", "信息获取中")
        }
        if (pluginNameField.text.isBlank()) {
            throw ConfigurationException("请填写插件名称", "新建项目失败")
        }
        if (pluginVersionField.text.isBlank()) {
            throw ConfigurationException("请填写插件版本", "新建项目失败")
        }
        if (mainClassField.text.isBlank()) {
            throw ConfigurationException("请填写主类", "新建项目失败")
        }
        if (authorsField.text.isBlank()) {
            throw ConfigurationException("请填写开发者姓名", "新建项目失败")
        }
        return true
    }

    override fun updateStep() {

        /*
        languageField.addItem(CreateConfig.LANGUAGE_JAVA)
        languageField.addItem(CreateConfig.LANGUAGE_KOTLIN)

        languageField.addActionListener {
            if (languageField.selectedItem == CreateConfig.LANGUAGE_KOTLIN) {
                buildToolField.removeAllItems()
                buildToolField.addItem(CreateConfig.BUILD_GRADLE_KOTLIN)
                buildToolField.addItem(CreateConfig.BUILD_GRADLE_GROOVY)
            } else {
                buildToolField.removeAllItems()
                buildToolField.addItem(CreateConfig.BUILD_GRADLE_GROOVY)
                buildToolField.addItem(CreateConfig.BUILD_MAVEN)
                buildToolField.addItem(CreateConfig.BUILD_GRADLE_KOTLIN)
            }
        }

         */
        consoleVersionField.isEditable = false
        coreVersionField.isEditable = false
        GlobalScope.launch {
            coreVersionField.text = CreateConfig.coreVersion().await()
        }
        GlobalScope.launch {
            consoleVersionField.text = CreateConfig.consoleVersion().await()
        }
    }

    override fun onStepLeaving() {

    }
}