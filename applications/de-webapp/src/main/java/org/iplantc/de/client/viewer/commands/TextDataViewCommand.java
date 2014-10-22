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
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class TextDataViewCommand implements ViewCommand {

    Logger LOG = Logger.getLogger(TextDataViewCommand.class.getName());
    private final String mode;
    private Folder parentFolder;

    public TextDataViewCommand(String mode) {
        this.mode = mode;
    }

    @Override
    public List<FileViewer> execute(final File file,
                                    final String infoType,
                                    final boolean editing,
                                    final Folder parentFolder,
                                    final JSONObject manifest,
                                    final FileViewer.Presenter presenter) {
        this.parentFolder = parentFolder;
        Integer columns = null;
        if (manifest.containsKey(FileViewer.COLUMNS_KEY)) {
            columns = JsonUtil.getNumber(manifest, FileViewer.COLUMNS_KEY).intValue();
            LOG.info("Columns are defined: " + columns);
        }
        return getViewerByInfoType(file, infoType, columns, editing, presenter);
    }

    private List<FileViewer> getViewerByInfoType(final File file,
                                                 final String infoType,
                                                 final Integer columns,
                                                 final boolean editing,
                                                 final FileViewer.Presenter presenter) {
        List<FileViewer> viewers = new ArrayList<>();
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals(InfoType.CSV.toString())
                    || infoType.equals(InfoType.TSV.toString())
                    || infoType.equals(InfoType.VCF.toString())
                    || infoType.equals(InfoType.GFF.toString())) {
                viewers.add(new StructuredTextViewerImpl(file, infoType, columns, parentFolder, presenter));

            }
        }
        viewers.add(new TextViewerImpl(file, infoType, mode, editing, parentFolder, presenter));
        return viewers;
    }
}
