package ru.yandex.qatools.allure.report.utils;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;

import java.io.File;
import java.util.Arrays;

/**
 * This is object factory for default aether objects
 *
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 29.05.14
 */
public class AetherObjectFactory {

    //TODO windows?
    public static File DEFAULT_LOCAL_REPOSITORY = new File(System.getProperty("user.home") + "/.m2/repository");

    public static final String MAVEN_CENTRAL_URL = "http://repo1.maven.org/maven2/";

    public static final String DEFAULT_TYPE = "default";

    private AetherObjectFactory() {
    }

    public static DependencyResolver newResolver() {
        RepositorySystem system = newRepositorySystem();
        RepositorySystemSession session = newSession(system, DEFAULT_LOCAL_REPOSITORY);

        return new DependencyResolver(system, session, Arrays.asList(newRemoteRepository(MAVEN_CENTRAL_URL)));
    }

    public static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        return locator.getService(RepositorySystem.class);
    }

    public static RemoteRepository newRemoteRepository(String repoUrl) {
        return newRemoteRepository(repoUrl, null, DEFAULT_TYPE);
    }

    public static RemoteRepository newRemoteRepository(String repoUrl, String repoName, String repoType) {
        return new RemoteRepository.Builder(repoName, repoType, repoUrl).build();
    }

    public static RepositorySystemSession newSession(RepositorySystem system, File localRepoDir) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(localRepoDir.getAbsolutePath());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }

}
