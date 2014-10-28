package org.iplantc.de.test.fileViewers.client;

import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.fileViewers.client.views.FileSetViewer;
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

    @Inject
    CommonModelAutoBeanFactory factory;
    @Inject
    JsonDataResources jsonData;
    @Inject
    FileSetViewer fileSetViewer;

    FileSetViewerTestGinInjector injector = GWT.create(FileSetViewerTestGinInjector.class);

    public FileSetViewerTestEntryPoint(){}

    @Override
    public void onModuleLoad() {
        injector.injectEntryPoint(this);


        String fileSetJson = jsonData.fileSetJson().getText();

        final HasPaths hasPaths = AutoBeanCodex.decode(factory, HasPaths.class, fileSetJson).as();

        RootPanel.get().add(fileSetViewer);
        fileSetViewer.setData(hasPaths.getPaths());
//        String version = GXT.getVersion().getRelease();
//        TextButton textButton = new TextButton("Verify GXT Works: Version=" + version);
//        RootPanel.get().add(textButton);
//        textButton.addSelectHandler(new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                MessageBox messageBox = new MessageBox("HasPaths = " + hasPaths.getPaths().toString());
//          MessageBox messageBox = new MessageBox("GXT Works.");
//                messageBox.show();
//            }
//        });
    }

}
