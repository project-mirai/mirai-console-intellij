package net.mamoe.mirai.intellij

object CreateConfig {
    @JvmField
    var mainClassPath: String? = null
    @JvmField
    var pluginName: String? = null
    @JvmField
    var author: String? = null
    @JvmField
    var info: String? = null
    @JvmField
    var depends: List<String>? = null
    @JvmField
    var version: String? = null
    @JvmField
    var isKotlinProject: Boolean? = null

}