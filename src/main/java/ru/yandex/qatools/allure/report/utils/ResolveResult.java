package ru.yandex.qatools.allure.report.utils;

import org.eclipse.aether.resolution.ArtifactResult;

import java.util.List;

/**
 * eroshenkoam
 * 5/28/14
 */
public class ResolveResult {
    private final String classPath;
    private final List<ArtifactResult> artifactResults;

    public ResolveResult(String classPath, List<ArtifactResult> artifactResults) {
        this.artifactResults = artifactResults;
        this.classPath = classPath;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public List<ArtifactResult> getArtifactResults() {
        return this.artifactResults;
    }
}
