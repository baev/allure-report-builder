package ru.yandex.qatools.allure.report;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResult;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newDependencyResolver;
import static ru.yandex.qatools.allure.report.utils.JarUtils.unpackJar;
import static ru.yandex.qatools.allure.report.utils.RegexJarEntryFilter.filterByRegex;

/**
 * eroshenkoam
 * 5/28/14
 */
public class AllureReportBuilder {

    public static String ALLURE_REPORT_FACE_FILE_REGEX = "^((?!(META\\-INF|WEB-INF)).)*$";

    private final DefaultArtifact allureReportCommonsArtifact;

    private final DefaultArtifact allureReportFaceArtifact;

    public AllureReportBuilder(String version) {
        this.allureReportCommonsArtifact = AllureArtifacts.getAllureReportCommonsArtifact(version);
        this.allureReportFaceArtifact = AllureArtifacts.getAllureReportFaceArtifact(version);
    }

    public void processTestsResults(File reportDirectory, File[] resultDirectories)
            throws AllureReportBuilderException {

        try {
            DependencyResolver resolver = newDependencyResolver();
            DependencyResult dependencyResult = resolver.resolve(allureReportCommonsArtifact);

            List<URL> artifactUrls = new ArrayList<URL>();
            for (ArtifactResult artRes : dependencyResult.getArtifactResults()) {
                artifactUrls.add(artRes.getArtifact().getFile().toURI().toURL());
            }

            final URLClassLoader urlClassLoader = new URLClassLoader(artifactUrls.toArray(new URL[artifactUrls.size()]),
                    ClassLoader.getSystemClassLoader());

            Class<?> clazz = urlClassLoader.loadClass("ru.yandex.qatools.allure.data.AllureReportGenerator");
            Object generator = clazz.getConstructor(File[].class).newInstance(new Object[]{resultDirectories});
            clazz.getMethod("generate", File.class).invoke(generator, new Object[]{reportDirectory});

        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }
    }

    public void unpackReportFace(File reportDirectory) throws AllureReportBuilderException {
        try {
            DependencyResolver resolver = newDependencyResolver();
            DependencyResult dependencyResult = resolver.resolve(allureReportFaceArtifact);
            File allureReportFace = dependencyResult.getArtifactResults().get(0).getArtifact().getFile();
            unpackJar(allureReportFace, reportDirectory, filterByRegex(ALLURE_REPORT_FACE_FILE_REGEX));
        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }
    }

}
