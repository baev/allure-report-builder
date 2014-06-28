package ru.yandex.qatools.allure.report.utils;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.internal.impl.*;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.spi.log.NullLoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static DependencyResolver newDependencyResolver() {
        return newDependencyResolver(DEFAULT_LOCAL_REPOSITORY, MAVEN_CENTRAL_URL);
    }

    public static DependencyResolver newDependencyResolver(File localRepository, String... repoUrls) {
        RepositorySystem system = newRepositorySystem();
        RepositorySystemSession session = newSession(system, localRepository);
        return new DependencyResolver(system, session, newRemoteRepositories(repoUrls));
    }

    public static RepositorySystem newRepositorySystem() {
        DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new ManualWagonProvider());

        locator.setService(LoggerFactory.class, NullLoggerFactory.class);
        locator.setService(VersionResolver.class, DefaultVersionResolver.class);
        locator.setService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
        locator.setService(ArtifactResolver.class, DefaultArtifactResolver.class);
        locator.setService(MetadataResolver.class, DefaultMetadataResolver.class);
        locator.setService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        locator.setService(DependencyCollector.class, DefaultDependencyCollector.class);
        locator.setService(Installer.class, DefaultInstaller.class);
        locator.setService(Deployer.class, DefaultDeployer.class);
        locator.setService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
        locator.setService(SyncContextFactory.class, DefaultSyncContextFactory.class);

        repositorySystem.initService(locator);
        return repositorySystem;
    }

    public static RemoteRepository newRemoteRepository(String repoUrl) {
        return newRemoteRepository(repoUrl, null, DEFAULT_TYPE);
    }

    public static List<RemoteRepository> newRemoteRepositories(String... reporUrls) {
        List<RemoteRepository> remoteRepositories = new ArrayList<>();
        for (String repoUrl : reporUrls) {
            remoteRepositories.add(newRemoteRepository(repoUrl));
        }
        return remoteRepositories;
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
