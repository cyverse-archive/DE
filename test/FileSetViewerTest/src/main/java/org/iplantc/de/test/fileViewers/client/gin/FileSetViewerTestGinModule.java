package org.iplantc.de.test.fileViewers.client.gin;

import org.iplantc.de.fileViewers.client.views.FileSetViewer;
import org.iplantc.de.test.fileViewers.client.appearance.FileSetViewerAppearanceTestImpl;

import com.google.gwt.inject.client.AbstractGinModule;

public class FileSetViewerTestGinModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(FileSetViewer.class);
        bind(FileSetViewer.FileSetEditorAppearance.class).to(FileSetViewerAppearanceTestImpl.class);
    }
}
