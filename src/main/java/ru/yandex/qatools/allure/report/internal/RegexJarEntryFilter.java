package ru.yandex.qatools.allure.report.internal;

import ru.yandex.qatools.clay.utils.archive.JarEntryFilter;

import java.util.jar.JarEntry;
import java.util.regex.Pattern;

/**
 * eroshenkoam
 * 5/29/14
 */
public class RegexJarEntryFilter implements JarEntryFilter {

    private String pattern;

    public RegexJarEntryFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(JarEntry entry) {
        return Pattern.matches(pattern, entry.getName());
    }

    @Override
    public String getOutputFilePath(JarEntry entry) {
        return entry.getName();
    }

    public static RegexJarEntryFilter filterByRegex(String pattern) {
        return new RegexJarEntryFilter(pattern);
    }
}