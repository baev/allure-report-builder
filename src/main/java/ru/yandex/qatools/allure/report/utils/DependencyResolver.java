package ru.yandex.qatools.allure.report.utils;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.*;

/**
 * eroshenkoam
 * 5/28/14
 */
public class DependencyResolver {

    private List<RemoteRepository> remoteRepositories;
    private File localRepository;

    public DependencyResolver(File localRepository) {
        this.remoteRepositories = new ArrayList<RemoteRepository>();
        this.localRepository = localRepository;
    }

    public void setLocalRepository(File file) {
        this.localRepository = file;
    }

    public void addRemoteRepository(String url) {
        this.remoteRepositories.add(newRemoteRepository(url));
    }

    public synchronized DependencyResult resolve(AbstractArtifact artifact) throws IOException,
            DependencyCollectionException, DependencyResolutionException {

        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem, localRepository);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, JavaScopes.RUNTIME));

        for (RemoteRepository remoteRepository : remoteRepositories) {
            collectRequest.addRepository(remoteRepository);
        }

        DependencyNode node = repositorySystem.collectDependencies(session, collectRequest).getRoot();

        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setRoot(node);

        return repositorySystem.resolveDependencies(session, dependencyRequest);
    }

}