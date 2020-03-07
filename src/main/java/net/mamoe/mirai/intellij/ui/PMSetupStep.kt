package net.mamoe.mirai.intellij.ui

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.ui.Messages
import net.mamoe.mirai.intellij.CreateConfig
import javax.swing.*

class PMSetupStep: ModuleWizardStep() {

    var panel: JPanel? = null
    lateinit var helpField: JButton
    lateinit var pmField: JComboBox<String>
    lateinit var pmExtraField: JComboBox<String>
    lateinit var groupIdField: JTextField
    lateinit var artifactIdField: JTextField


    override fun getComponent(): JComponent {
        return panel!!
    }

    override fun validate(): Boolean {
        if(groupIdField.text.isNullOrBlank()){
            throw ConfigurationException("groupID不能为空", "新建项目失败")
        }
        if(artifactIdField.text.isNullOrBlank()){
            throw ConfigurationException("artifactID不能为空", "新建项目失败")
        }
        if(pmField.selectedItem == "Maven"){
            throw ConfigurationException("当前插件版本不支持maven项目自动创建, 请使用gradle", "TODO")
        }
        return true
    }

    override fun updateDataModel() {
        CreateConfig.groupId = groupIdField.text
        CreateConfig.artifactId = artifactIdField.text
        if(pmField.selectedItem == "Maven"){
            CreateConfig.buildTool = CreateConfig.BUILD_MAVEN
        }else{
            if(pmExtraField.selectedItem == "Groovy DSL"){
                CreateConfig.buildTool = CreateConfig.BUILD_GRADLE_GROOVY
            }else{
                CreateConfig.buildTool = CreateConfig.BUILD_GRADLE_KOTLIN
            }
        }
    }

    override fun updateStep() {
        helpField.addActionListener {
            Messages.showMessageDialog(
                "如不清楚, 推荐使用gradle + groovy\n如需寻求更多插件帮助请访问github\nhttps://github.com/mamoe/mirai-console",
                "帮助",
                Messages.getInformationIcon()
            )
        }
        pmField.addActionListener {
            if(pmField.selectedItem == "Maven"){
                pmExtraField.addItem("Default")
                pmExtraField.removeItem("Kotlin DSL")
                pmExtraField.removeItem("Groovy DSL")
            }else{
                pmExtraField.removeItem("Default")
                pmExtraField.addItem("Kotlin DSL")
                pmExtraField.addItem("Groovy DSL")
            }
        }
    }

}