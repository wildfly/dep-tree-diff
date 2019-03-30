package org.wildfly.deptreediff.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class DepTreeParserTest {
   @Test
   public void testParserWorks() throws IOException {
      InputStream in = this.getClass().getResourceAsStream("dep-tree-sample.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      List<Dependency> dependencies = null;
      try {
         DepTreeParser parser = new DepTreeParser(reader);
         dependencies = parser.parse();
      } finally {
         reader.close();
      }
      Assert.assertNotNull(dependencies);
      Assert.assertEquals(26, dependencies.size());
      Assert.assertEquals("org.jboss.common:jboss-common-beans:jar:2.0.0.Final:compile", dependencies.get(0).getGavString());
      Assert.assertEquals("com.jcraft:jsch:jar:0.1.54:compile", dependencies.get(8).getGavString());
      Assert.assertEquals("org.infinispan:infinispan-client-hotrod:jar:9.4.8.Final:compile", dependencies.get(16).getGavString());
      Assert.assertEquals("org.jboss.spec.javax.rmi:jboss-rmi-api_1.0_spec:jar:1.0.6.Final:test", dependencies.get(25).getGavString());
   }
}
