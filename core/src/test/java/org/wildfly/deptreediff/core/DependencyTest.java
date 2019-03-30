package org.wildfly.deptreediff.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DependencyTest {
    @Test
    public void testGAVPackaging() {
        Dependency dep = Dependency.parseDependency("com.acme.org:foo:jar:1.0");
        checkDependency(dep, "com.acme.org", "foo", "jar", "1.0", null, null);
    }

    @Test
    public void testGAVPackagingScope() {
        Dependency dep = Dependency.parseDependency("com.acme.org:foo:jar:1.0:compile");
        checkDependency(dep, "com.acme.org", "foo", "jar", "1.0", "compile", null);
    }

    @Test
    public void testGAVPackagingScopeClassifier() {
        Dependency dep = Dependency.parseDependency("com.acme.org:foo:jar:linux:1.0:compile");
        checkDependency(dep, "com.acme.org", "foo", "jar", "1.0", "compile", "linux");
    }


    void checkDependency(Dependency dep, String groupId, String artifactId, String packaging,
                         String version, String scope, String classifier) {
        Assert.assertEquals(groupId, dep.getGroupId());
        Assert.assertEquals(artifactId, dep.getArtifactId());
        Assert.assertEquals(packaging, dep.getPackaging());
        Assert.assertEquals(version, dep.getVersion().getVersion());
        Assert.assertEquals(scope, dep.getScope());
        Assert.assertEquals(classifier, dep.getClassifier());
    }
}
