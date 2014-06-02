package ru.yandex.qatools.allure.report;

import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * eroshenkoam
 * 5/29/14
 */
public class AllureArtifacts {

    public static final String ALLURE_GROUP_ID = "ru.yandex.qatools.allure";

    public static final String ALLURE_REPORT_DATA_ARTIFACT_ID = "allure-report-data";

    public static final String ALLURE_REPORT_COMMONS_ARTIFACT_ID = "allure-report-data";

    public static final String ALLURE_REPORT_FACE_ARTIFACT_ID = "allure-report-face";

    public static final String WAR = "war";

    public static final String JAR = "jar";

    public static DefaultArtifact getAllureReportDataArtifact(String version) {
        return new DefaultArtifact(ALLURE_GROUP_ID, ALLURE_REPORT_DATA_ARTIFACT_ID, JAR, version);
    }

    public static DefaultArtifact getAllureReportCommonsArtifact(String version) {
        return new DefaultArtifact(ALLURE_GROUP_ID, ALLURE_REPORT_COMMONS_ARTIFACT_ID, JAR, version);
    }

    public static DefaultArtifact getAllureReportFaceArtifact(String version) {
        return new DefaultArtifact(ALLURE_GROUP_ID, ALLURE_REPORT_FACE_ARTIFACT_ID, WAR, version);
    }

}
