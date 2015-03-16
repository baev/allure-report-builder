package ru.yandex.qatools.allure.report;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static ru.yandex.qatools.allure.report.AllureReportBuilderTest.NotEmptyArrayMatcher.notEmpty;

/**
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 29.05.14
 */
@RunWith(Parameterized.class)
public class AllureReportBuilderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private URL allureResults;

    private String version;

    private AllureReportBuilder builder;

    private File reportDirectory;

    public AllureReportBuilderTest(String version) {
        this.version = version;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{"1.3.9"},
                new Object[]{"1.3.7"},
                new Object[]{"1.4.0.RC3"},
                new Object[]{"1.4.0.RC4"},
                new Object[]{AllureReportBuilder.DEFAULT_ALLURE_REPORT_VERSION}
        );
    }

    @Before
    public void setUpClassLoader() throws Exception {
        allureResults = getClass().getClassLoader().getResource("allure-results");
    }

    @Before
    public void setUpBuilder() throws Exception {
        reportDirectory = folder.newFolder();
        builder = new AllureReportBuilder(version, reportDirectory);
    }

    @Test
    public void processResultsTest() throws Exception {
        builder.processResults(new File(allureResults.toURI()));
        assertThat(reportDirectory.list(), notEmpty());
    }

    @Test
    public void unpackFaceTest() throws Exception {
        builder.unpackFace();
        assertThat(reportDirectory.list(), notEmpty());
    }

    @Test
    public void addExtensionInvalid() {
        builder.addExtension(null);
        assertThat(builder.getExtensions(), hasSize(0));

        builder.addExtension("");
        assertThat(builder.getExtensions(), hasSize(0));
    }

    @Test
    public void addSingleExtension() {
        builder.addExtension("group:artifact:version");
        assertThat(builder.getExtensions(), hasSize(1));
    }

    @Test
    public void addMultipleExtensions() {
        builder.addExtensions("group1:artifact1:version1", "group2:artifact2:jar:version2", "group3:artifact3:jar:jdk5:version3");
        assertThat(builder.getExtensions(), hasSize(3));
    }

    public static class NotEmptyArrayMatcher extends TypeSafeMatcher<String[]> {

        @Override
        protected boolean matchesSafely(String[] item) {
            return item.length > 0;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("should be not empty");
        }

        public static NotEmptyArrayMatcher notEmpty() {
            return new NotEmptyArrayMatcher();
        }
    }
}
