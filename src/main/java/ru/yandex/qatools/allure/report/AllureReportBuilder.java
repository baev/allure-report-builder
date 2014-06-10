package ru.yandex.qatools.allure.report;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyResult;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.qatools.allure.report.AllureArtifacts.getReportCommonsArtifact;
import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newResolver;
import static ru.yandex.qatools.allure.report.utils.JarUtils.unpackJar;
import static ru.yandex.qatools.allure.report.utils.RegexJarEntryFilter.filterByRegex;

/**
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 29.05.14
 *         <p/>
 *         Uses this class you can generate allure report any version.
 */
public class AllureReportBuilder {

    private static final String ALLURE_REPORT_GENERATOR_CLASS = "ru.yandex.qatools.allure.data.AllureReportGenerator";

    private static final String ALLURE_REPORT_FACE_FILE_REGEX = "^((?!(META\\-INF|WEB-INF)).)*$";

    private static final String METHOD_NAME = "generate";

    private final String version;

    private File outputDirectory;

    private DependencyResolver resolver;

    /**
     * Create {@link AllureReportBuilder} to generate Allure report with given version to specified directory.
     * Uses to resolve allure dependencies specified {@link ru.yandex.qatools.allure.report.utils.DependencyResolver}
     *
     * @param version         a allure report version
     * @param outputDirectory a directory to generate report
     * @throws AllureReportBuilderException if specified <code>outputDirectory</code> doesn't exists and can't be created
     */
    public AllureReportBuilder(String version, File outputDirectory, DependencyResolver resolver)
            throws AllureReportBuilderException {
        checkDirectory(outputDirectory);

        this.version = version;
        this.outputDirectory = outputDirectory;
        this.resolver = resolver;
    }

    /**
     * Create {@link AllureReportBuilder} to generate Allure report with given version to specified directory.
     *
     * @param version         a allure report version
     * @param outputDirectory a directory to generate report
     * @throws AllureReportBuilderException can't create default dependency resolver
     *                                      {@see ru.yandex.qatools.allure.report.utils.AetherObjectFactory#newResolver()}
     * @throws AllureReportBuilderException if specified <code>outputDirectory</code> doesn't exists and can't be created
     */
    public AllureReportBuilder(String version, File outputDirectory) throws AllureReportBuilderException {
        this(version, outputDirectory, newResolver());
    }

    /**
     * Process test results in given directories. Generates report data to {@link #outputDirectory}.
     *
     * @param inputDirectories a directories with test results
     * @throws AllureReportBuilderException if one of given directories doesn't exists and can't be created
     * @throws AllureReportBuilderException if can't resolve {@link AllureArtifacts#getReportCommonsArtifact(String)}
     *                                      using {@link #resolver}
     * @throws AllureReportBuilderException if resolved DependencyResult contains artifact with invalid path.
     * @throws AllureReportBuilderException if can't find class {@link #ALLURE_REPORT_GENERATOR_CLASS} in classpath
     * @throws AllureReportBuilderException if can't create instance of {@link #ALLURE_REPORT_GENERATOR_CLASS}
     * @throws AllureReportBuilderException if can't find {@link #METHOD_NAME} method in class
     *                                      {@link #ALLURE_REPORT_GENERATOR_CLASS}
     * @throws AllureReportBuilderException if can't invoke method {@link #METHOD_NAME}
     */
    public void processResults(File... inputDirectories) throws AllureReportBuilderException {
        try {
            checkDirectories(inputDirectories);

            DefaultArtifact artifact = getReportCommonsArtifact(version);
            DependencyResult dependencyResult = resolver.resolve(artifact);
            URLClassLoader urlClassLoader = createClassLoader(dependencyResult);

            Class<?> clazz = urlClassLoader.loadClass(ALLURE_REPORT_GENERATOR_CLASS);
            Object generator = clazz.getConstructor(File[].class).newInstance(new Object[]{inputDirectories});
            clazz.getMethod(METHOD_NAME, File.class).invoke(generator, outputDirectory);
        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }
    }

    /**
     * Create a {@link URLClassLoader} for specified {@link DependencyResult}.
     *
     * @param dependencyResult a DependencyResult which contains extra classpath for class loader.
     * @return created URLClassLoader
     * @throws MalformedURLException if DependencyResult contains artifact with invalid path.
     */
    private URLClassLoader createClassLoader(DependencyResult dependencyResult) throws MalformedURLException {
        List<URL> urls = new ArrayList<>();
        for (ArtifactResult artRes : dependencyResult.getArtifactResults()) {
            urls.add(artRes.getArtifact().getFile().toURI().toURL());
        }

        return new URLClassLoader(
                urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader()
        );
    }

    /**
     * Unpack Allure report face to {@link #outputDirectory}
     *
     * @throws AllureReportBuilderException if can't resolve
     *                                      {@link ru.yandex.qatools.allure.report.AllureArtifacts#getReportFaceArtifact(String)}
     * @throws AllureReportBuilderException if some problems with
     *                                      {@link ru.yandex.qatools.allure.report.utils.JarUtils#unpackJar(java.io.File,
     *                                      java.io.File, ru.yandex.qatools.allure.report.utils.JarEntryFilter)}
     */
    public void unpackFace() throws AllureReportBuilderException {
        try {
            DefaultArtifact artifact = AllureArtifacts.getReportFaceArtifact(version);
            DependencyResult dependencyResult = resolver.resolve(artifact);
            File allureReportFace = dependencyResult.getArtifactResults().get(0).getArtifact().getFile();
            unpackJar(allureReportFace, outputDirectory, filterByRegex(ALLURE_REPORT_FACE_FILE_REGEX));
        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }

    }

    /**
     * Check given directories. For each directory try to create directory if doesn't exist.
     *
     * @param dirs a array of directories to check
     * @throws AllureReportBuilderException if can't create directory
     * @see #checkDirectory(java.io.File)
     */
    private void checkDirectories(File... dirs) throws AllureReportBuilderException {
        for (File dir : dirs) {
            checkDirectory(dir);
        }
    }

    /**
     * Check given directory. Try to create directory if doesn't exist.
     *
     * @param dir a directory to check
     * @throws AllureReportBuilderException if can't create directory
     */
    private void checkDirectory(File dir) throws AllureReportBuilderException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new AllureReportBuilderException("Report directory doesn't exists and can't be created.");
        }
    }
}
