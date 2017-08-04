package io.github.iamyours.radapter;

/**
 * Created by yanxx on 2017/8/1.
 */

public class StringUtil {
    public static String captureName(String name) {
        if (name != null && name.length() > 0) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }
}
