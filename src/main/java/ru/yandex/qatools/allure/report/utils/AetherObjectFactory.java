package ru.yandex.qatools.allure.report.utils;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;

import java.io.File;
import java.io.IOException;

/**
 * This is object factory for default aether objects
 * eroshenkoam
 * 5/28/14
 */
public class AetherObjectFactory {

    public static File DEFAULT_LOCAL_REPOSITORY = new File(System.getProperty("user.home") + "/.m2/repository");

    public static final String MAVEN_CENTRAL_URL = "http://repo1.maven.org/maven2/";

    public static final String DEFAULT_TYPE = "default";

    private AetherObjectFactory() {
    }

    /**
     * This method create dependency resolver with default local repository (at ~/.m2/repository) and
     * default remote repository (http://repo1.maven.org/maven2/)
     *
     * @return default dependency resolver
     */
    public static DependencyResolver newDependencyResolver() {
        DependencyResolver dependencyResolver = new DependencyResolver(DEFAULT_LOCAL_REPOSITORY);
        dependencyResolver.addRemoteRepository(MAVEN_CENTRAL_URL);
        return dependencyResolver;
    }

    public static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public static RemoteRepository newRemoteRepository(String repoUrl) {
        return newRemoteRepository(repoUrl, null, DEFAULT_TYPE);
    }

    public static RemoteRepository newRemoteRepository(String repoUrl, String repoName, String repoType) {
        return new RemoteRepository.Builder(repoName, repoType, repoUrl).build();
    }

    public static RepositorySystemSession newSession(RepositorySystem system, File localRepoDir) throws IOException {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(localRepoDir.getAbsolutePath());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }

}
