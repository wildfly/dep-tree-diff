package org.wildfly.deptreediff.core;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DependencyVersionTest {

    @Test
    public void testSameVersion() {
        DependencyVersion versionA = DependencyVersion.parseVersion("1.0.0.Final-blah");
        DependencyVersion versionB = DependencyVersion.parseVersion("1.0.0.Final-blah");
        Assert.assertEquals(4, versionA.getVersionParts().size());

        Assert.assertEquals(versionA, versionB);
        Assert.assertTrue(versionA.equalsMajorVersion(versionB));
    }

    @Test
    public void testStrangeVersion() {
        DependencyVersion version = DependencyVersion.parseVersion("StrangeVersion");
        Assert.assertEquals(Arrays.asList(new String[]{"StrangeVersion"}), version.getVersionParts());
    }

    @Test
    public void testSameMajor() {
        DependencyVersion versionA = DependencyVersion.parseVersion("1.0.0.Final-blah");
        DependencyVersion versionB = DependencyVersion.parseVersion("1.1");

        Assert.assertFalse(versionA.equals(versionB));
        Assert.assertTrue(versionA.equalsMajorVersion(versionB));
    }

    @Test
    public void testDifferentMajor() {
        DependencyVersion versionA = DependencyVersion.parseVersion("1.0.0.Final-blah");
        DependencyVersion versionB = DependencyVersion.parseVersion("2.0.0.Final-blah");

        Assert.assertFalse(versionA.equals(versionB));
        Assert.assertFalse(versionA.equalsMajorVersion(versionB));
    }
}
