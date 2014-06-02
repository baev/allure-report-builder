package ru.yandex.qatools.allure.report.utils;

import java.util.jar.JarEntry;

/**
 * eroshenkoam
 * 5/29/14
 */
public interface JarEntryFilter {

    boolean accept(JarEntry entry);

    String getOutputFilePath(JarEntry entry);
}
