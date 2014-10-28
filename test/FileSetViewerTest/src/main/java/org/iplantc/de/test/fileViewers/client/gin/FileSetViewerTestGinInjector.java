package org.iplantc.de.test.fileViewers.client.gin;

import org.iplantc.de.test.fileViewers.client.FileSetViewerTestEntryPoint;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author jstroot
 */
@GinModules(FileSetViewerTestGinModule.class)
public interface FileSetViewerTestGinInjector extends Ginjector {

    public void injectEntryPoint(FileSetViewerTestEntryPoint entryPoint);
}
