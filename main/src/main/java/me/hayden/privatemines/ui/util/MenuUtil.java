package me.hayden.privatemines.ui.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static me.hayden.privatemines.utils.ChatUtil.HEX_PATTERN;

public class MenuUtil {

    public static String color(String textToTranslate) {

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }

    public static List<String> colorList(List<String> list) {
        List<String> toReturn = new ArrayList<>();
        for (String s : list) {
            toReturn.add(color(s));
        }
        return toReturn;
    }

    public static Material getMaterial(String materialName) {
        Material modern = Material.getMaterial(materialName);
        Material legacy = Material.getMaterial(materialName, true);
        if (modern != null) {
            return modern;
        }
        return legacy;
    }

}
