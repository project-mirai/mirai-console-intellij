package net.mamoe.mirai.intellij.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public abstract class Assets {
    public Icon loadIcon(String path){
        return IconLoader.getIcon(path, Assets.class);
    }
}
