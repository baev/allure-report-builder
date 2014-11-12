package ru.yandex.qatools.allure.report;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.artifact.DefaultArtifact;
import ru.yandex.qatools.clay.Aether;
import ru.yandex.qatools.clay.AetherResult;

import java.io.File;
import java.net.URLClassLoader;

import static ru.yandex.qatools.allure.report.AllureArtifacts.getReportDataArtifact;
import static ru.yandex.qatools.allure.report.internal.RegexJarEntryFilter.filterByRegex;
import static ru.yandex.qatools.clay.Aether.MAVEN_CENTRAL_URL;
import static ru.yandex.qatools.clay.Aether.aether;
import static ru.yandex.qatools.clay.maven.settings.FluentProfileBuilder.newProfile;
import static ru.yandex.qatools.clay.maven.settings.FluentRepositoryBuilder.newRepository;
import static ru.yandex.qatools.clay.maven.settings.FluentSettingsBuilder.loadSettings;
import static ru.yandex.qatools.clay.utils.archive.ArchiveUtil.unpackJar;

/**
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 29.05.14
 *         <p/>
 *         Uses this class you can generate allure report any version.
 */
public class AllureReportBuilder {

    public static final String ALLURE_REPORT_GENERATOR_CLASS = "ru.yandex.qatools.allure.data.AllureReportGenerator";

    public static final String ALLURE_REPORT_FACE_FILE_REGEX = "^((?!(META\\-INF|WEB-INF)).)*$";

    public static final String METHOD_NAME = "generate";

    private String version;

    private File outputDirectory;

    private ClassLoader classLoader;

    private Aether aether;

    public AllureReportBuilder(String version, File outputDirectory, Aether aether)
            throws AllureReportBuilderException {
        checkDirectory(outputDirectory);

        this.outputDirectory = outputDirectory;
        this.aether = aether;
        this.version = version;

    }

    public AllureReportBuilder(String version, File outputDirectory) throws AllureReportBuilderException {
        this(version, outputDirectory, aether(mavenSettings()));
    }

    public static Settings mavenSettings() throws AllureReportBuilderException {
        try {
            return loadSettings()
                    .withActiveProfile(
                            newProfile()
                                    .withId("profile")
                                    .withRepository(newRepository().withUrl(MAVEN_CENTRAL_URL))
                    ).build();
        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }
    }

    /**
     * Set class loader for resolved dependencies
     *
     * @param classLoader class loader
     */
    @SuppressWarnings("unused")
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Return class loader for resolved dependencies
     *
     * @return class loader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Set allure version for report generation
     *
     * @param version allure version in maven format
     */
    @SuppressWarnings("unused")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get allure version for report generation
     *
     * @return allure version
     */
    @SuppressWarnings("unused")
    public String getVersion() {
        return this.version;
    }

    /**
     * Process test results in given directories. Generates report data to {@link #outputDirectory}.
     *
     * @param inputDirectories a directories with test results
     * @throws AllureReportBuilderException if one of given directories doesn't exists and can't be created
     *                                      AllureReportBuilderException if can't resolve {@link AllureArtifacts#getReportDataArtifact(String)} (String)}
     *                                      using {@link #aether}
     *                                      AllureReportBuilderException if resolved DependencyResult contains artifact with invalid path.
     *                                      AllureReportBuilderException if can't find class {@link #ALLURE_REPORT_GENERATOR_CLASS} in classpath
     *                                      AllureReportBuilderException if can't create instance of {@link #ALLURE_REPORT_GENERATOR_CLASS}
     *                                      AllureReportBuilderException if can't find {@link #METHOD_NAME} method in class
     *                                      {@link #ALLURE_REPORT_GENERATOR_CLASS}
     *                                      AllureReportBuilderException if can't invoke method {@link #METHOD_NAME}
     */
    public void processResults(File... inputDirectories) throws AllureReportBuilderException {
        try {
            checkDirectories(inputDirectories);

            DefaultArtifact artifact = getReportDataArtifact(version);
            URLClassLoader urlClassLoader = aether.resolve(artifact).getAsClassLoader(getClassLoader());

            Class<?> clazz = urlClassLoader.loadClass(ALLURE_REPORT_GENERATOR_CLASS);
            Object generator = clazz.getConstructor(File[].class).newInstance(new Object[]{inputDirectories});
            clazz.getMethod(METHOD_NAME, File.class).invoke(generator, outputDirectory);
        } catch (Exception e) {
            throw new AllureReportBuilderException(e);
        }
    }

    /**
     * Unpack report face to {@link #outputDirectory}.
     *
     * @throws AllureReportBuilderException if can't resolve report face artifact
     *                                      if can't unpack report war to {@link #outputDirectory}
     * @see ru.yandex.qatools.clay.utils.archive.ArchiveUtil#unpackJar(java.io.File, java.io.File,
     * ru.yandex.qatools.clay.utils.archive.JarEntryFilter)
     */
    public void unpackFace() throws AllureReportBuilderException {
        try {
            DefaultArtifact artifact = AllureArtifacts.getReportFaceArtifact(version);
            AetherResult aetherResult = aether.resolve(artifact);
            File allureReportFace = aetherResult.get().get(0).getArtifact().getFile();
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
