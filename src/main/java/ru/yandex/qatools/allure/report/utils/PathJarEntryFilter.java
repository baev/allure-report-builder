package ru.yandex.qatools.allure.report.utils;

import java.util.jar.JarEntry;

/**
 * eroshenkoam
 * 5/29/14
 */
public class PathJarEntryFilter implements JarEntryFilter {

    private String path;

    public PathJarEntryFilter(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean accept(JarEntry entry) {
        return entry.getName().startsWith(path);
    }

    @Override
    public String getOutputFilePath(JarEntry entry) {
        return entry.getName().replace(path, "");
    }

    public static PathJarEntryFilter filterByPath(String path) {
        return new PathJarEntryFilter(path);
    }
}
