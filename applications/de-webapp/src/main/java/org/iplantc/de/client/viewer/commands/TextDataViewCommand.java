package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.StructuredTextViewerImpl;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

import com.google.common.base.Strings;
import com.google.gwt.json.client.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram, jstroot
 */
public class TextDataViewCommand implements ViewCommand {

    private Folder parentFolder;
    private final String mode;

    public TextDataViewCommand(String mode) {
        this.mode = mode;
    }

    @Override
    public List<FileViewer> execute(final File file,
                                    String infoType,
                                    boolean editing,
                                    Folder parentFolder,
                                    JSONObject manifest) {
        this.parentFolder = parentFolder;
        Integer columns = null;
        if(manifest.containsKey(FileViewer.COLUMNS_KEY)){
            columns = JsonUtil.getNumber(manifest, FileViewer.COLUMNS_KEY).intValue();
        }
        return getViewerByInfoType(file, infoType, columns, editing);
    }

    private List<FileViewer> getViewerByInfoType(final File file,
                                                 String infoType,
                                                 Integer columns,
                                                 boolean editing) {
        List<FileViewer> viewers = new ArrayList<>();
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals(InfoType.CSV.toString()) || infoType.equals(InfoType.TSV.toString())
                    || infoType.equals(InfoType.VCF.toString())
                    || infoType.equals(InfoType.GFF.toString())) {
                viewers.add(new StructuredTextViewerImpl(file, infoType, columns, parentFolder));

            }
        }
        viewers.add(new TextViewerImpl(file, infoType, mode, editing, parentFolder));
        return viewers;
    }
}
