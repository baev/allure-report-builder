package ru.yandex.qatools.allure.report.utils;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * eroshenkoam
 * 5/29/14
 */
public class JarUtils {

    public static void unpackJar(File from, File to, JarEntryFilter filter) throws IOException {
        unpackJar(new JarFile(from), to, filter);
    }

    public static void unpackJar(JarFile jar, File to, JarEntryFilter filter) throws IOException {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entryToCopy = entries.nextElement();
            if (filter.accept(entryToCopy)) {
                File outputFile = new File(to, filter.getOutputFilePath(entryToCopy));
                if (entryToCopy.isDirectory()) {
                    FileUtils.forceMkdir(outputFile);
                } else {
                    FileUtils.copyInputStreamToFile(jar.getInputStream(entryToCopy), outputFile);
                }
            }
        }
    }
}
