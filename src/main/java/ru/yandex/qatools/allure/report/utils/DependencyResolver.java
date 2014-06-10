package ru.yandex.qatools.allure.report.utils;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.util.List;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 10.06.14
 */
public class DependencyResolver {

    private RepositorySystem system;

    private RepositorySystemSession session;

    private List<RemoteRepository> remotes;

    public DependencyResolver(RepositorySystem system, RepositorySystemSession session, List<RemoteRepository> remotes) {
        this.system = system;
        this.session = session;
        this.remotes = remotes;
    }

    public DependencyResult resolve(DefaultArtifact artifact) throws Exception {
        CollectRequest collectRequest = new CollectRequest(
                new Dependency(artifact, JavaScopes.RUNTIME),
                remotes
        );

        DependencyNode node = system.collectDependencies(session, collectRequest).getRoot();
        DependencyRequest request = new DependencyRequest(node, null);
        return system.resolveDependencies(session, request);
    }

}
