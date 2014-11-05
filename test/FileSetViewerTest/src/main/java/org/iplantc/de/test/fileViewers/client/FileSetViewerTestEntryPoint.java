package org.iplantc.de.test.fileViewers.client;

import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.fileViewers.client.views.PathListViewer;
import org.iplantc.de.test.fileViewers.client.gin.FileSetViewerTestGinInjector;
import org.iplantc.de.test.fileViewers.client.json.JsonDataResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FileSetViewerTestEntryPoint implements EntryPoint {

    @Inject CommonModelAutoBeanFactory factory;
    @Inject JsonDataResources jsonData;

    FileSetViewerTestGinInjector injector = GWT.create(FileSetViewerTestGinInjector.class);

    public FileSetViewerTestEntryPoint(){}

    @Override
    public void onModuleLoad() {
        injector.injectEntryPoint(this);

        /**
         * First, we want to work on creating a file first.
         * What is the lifecycle for creating files, currently?
         *
         * TextViewerImpl:
         *  On construction, if the passed in file is null, the viewer is initialized with sample data
         * StructuredTextViewerImpl:
         *  On construction, the number of columns is passed in. If it is not null, then the viewer is
         *  initialized with sample data.
         *
         *
         */
        File testFile = null;
        boolean isEdititng = true;
        PathListViewer fileSetViewer = new PathListViewer(testFile, InfoType.PATH_LIST.toString(), isEdititng, null);
        String fileSetJson = jsonData.fileSetJson().getText();

        final HasPaths hasPaths = AutoBeanCodex.decode(factory, HasPaths.class, fileSetJson).as();
        RootPanel.get().add(fileSetViewer);
        fileSetViewer.setData(hasPaths.getPaths());
    }

}
