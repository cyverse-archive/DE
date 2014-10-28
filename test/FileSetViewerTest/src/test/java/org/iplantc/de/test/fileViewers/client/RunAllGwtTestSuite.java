package org.iplantc.de.test.fileViewers.client;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RunAllGwtTestSuite extends GWTTestSuite {
  
  public static Test suite() {
    TestSuite suite = new TestSuite("Application Tests");
    suite.addTestSuite(CompileGwtTest.class);
    
    return suite;
  }
  
}
