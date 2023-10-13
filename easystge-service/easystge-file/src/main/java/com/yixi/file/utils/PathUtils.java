package com.yixi.file.utils;

import org.springframework.stereotype.Component;

/**
 * @author yixi
 * @date 2023/9/4
 * @apiNote
 */

public class PathUtils {

    /**
     * 判断b是否为a的子目录，如<br>
     * <code>isSubDir("/a/b/c", "/a/b/c/d")</code>为true<br>
     * <code>isSubDir("/a/b/c/d", "/a/b/c")</code>为false<br>
     * <code>isSubDir("/a/b/c", "a/b/c/d")</code>为true
     * @param a 目录A
     * @param b 目录B
     * @return 是子目录则为true，否则为false
     */
    public static boolean isSubDir(String a, String b) {
        if (a.charAt(0) != '/' && a.charAt(0) != '\\') {
            a = "/" + a;
        }
        if (b.charAt(0) != '/' && b.charAt(0) != '\\') {
            b = "/" + b;
        }
        a = a.replaceAll("//+|\\\\+", "/");
        b = b.replaceAll("//+|\\\\+", "/");
        return b.startsWith(a);
    }

    public static void main(String[] args) {
        System.out.println(isSubDir("/a/b/c/d/g", "/a/b/c/d/g/a"));

    }

}
