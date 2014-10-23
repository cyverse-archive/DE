/**
 *
 */
package org.iplantc.de.client.viewer.presenter;

import static org.iplantc.de.client.models.viewer.InfoType.*;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.views.AbstractFileViewer;
import org.iplantc.de.client.viewer.views.ExternalVisualizationURLViewerImpl;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ImageViewerImpl;
import org.iplantc.de.client.viewer.views.StructuredTextViewerImpl;
import org.iplantc.de.client.viewer.views.TextViewerImpl;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.util.WindowUtil;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class MimeTypeViewerResolverFactory {

    public final Map<MimeType, String> modeMap = new HashMap<>();
    Logger LOG = Logger.getLogger(MimeTypeViewerResolverFactory.class.getName());
    private final IplantAnnouncer announcer;
    private final FileEditorServiceFacade fileEditorService;

    @Inject
    public MimeTypeViewerResolverFactory(final IplantAnnouncer announcer,
                                         final FileEditorServiceFacade fileEditorService) {
        this.announcer = announcer;
        this.fileEditorService = fileEditorService;
        modeMap.put(MimeType.X_SH, "shell");
        modeMap.put(MimeType.X_PYTHON, "python");
        modeMap.put(MimeType.X_PERL, "perl");
        modeMap.put(MimeType.X_RSRC, "r");
        modeMap.put(MimeType.X_WEB_MARKDOWN, "markdown");
    }

    public List<? extends FileViewer> getViewerCommand(final File file,
                                        final String infoType,
                                        final boolean editing,
                                        final Folder parentFolder,
                                        final JSONObject manifest,
                                        final FileViewer.Presenter presenter,
                                        MimeType type) {

        LOG.fine("mime->" + type.toString());

        List<AbstractFileViewer> viewers = Lists.newArrayList();

        String filePath = null;
        if(file != null){
            filePath = file.getPath();
        }
        String textViewerMode = modeMap.get(type);
        switch (type) {

            case PNG:
            case JPEG:
            case GIF:
                if(editing) {
                    announcer.schedule(new ErrorAnnouncementConfig("Editing is not supported for this type of file."));
                }
                if((file != null) && !file.getId().isEmpty()){
                    String imageUrl = fileEditorService.getServletDownloadUrl(file.getPath());
                    LOG.fine("Image viewer url: " + imageUrl);
                    ImageViewerImpl imgViewer = new ImageViewerImpl(file, imageUrl);
                    viewers.add(imgViewer);
                }
                break;

            case PDF:
                if(editing) {
                    announcer.schedule(new ErrorAnnouncementConfig("Editing is not supported for this type of file."));
                }
                if(!Strings.isNullOrEmpty(filePath)){
                    String downloadUrl = fileEditorService.getServletDownloadUrl(filePath);
                    String url = downloadUrl + "&attachment=0";
                    LOG.fine("PDF viewer url: " + url);
                    WindowUtil.open(url);
                }
                break;

            case HTML:
            case XHTML_XML:
                if(editing) {
                    announcer.schedule(new ErrorAnnouncementConfig("Editing is not supported for this type of file."));
                }
                if(!Strings.isNullOrEmpty(filePath)){
                    String downloadUrl = fileEditorService.getServletDownloadUrl(filePath);
                    String url = downloadUrl + "&attachment=0";
                    LOG.fine(type.toString() + " viewer url: " + url);
                    WindowUtil.open(url);
                }
                break;

            case VIZ:
                ExternalVisualizationURLViewerImpl vizUrlViewer = new ExternalVisualizationURLViewerImpl(file, infoType);
                viewers.add(vizUrlViewer);
                break;

            case X_SH:
            case X_RSRC:
            case X_PYTHON:
            case X_PERL:
            case X_WEB_MARKDOWN:
                Preconditions.checkArgument(!Strings.isNullOrEmpty(textViewerMode),
                                            "Text viewer mode should not be empty or null.");
                LOG.fine("mode-->" + textViewerMode);
                TextViewerImpl textViewer = new TextViewerImpl(file, infoType, textViewerMode, editing, parentFolder, presenter);
                viewers.add(textViewer);
                break;

            case PLAIN:
            case PREVIEW:
            default:
                Integer columns = null;
                if(manifest.containsKey(FileViewer.COLUMNS_KEY)){
                    columns = JsonUtil.getNumber(manifest, FileViewer.COLUMNS_KEY).intValue();
                    LOG.fine("Columns are defined: " + columns);
                }
                if(CSV.toString().equals(infoType)
                    || TSV.toString().equals(infoType)
                    || VCF.toString().equals(infoType)
                    || GFF.toString().equals(infoType)){
                    StructuredTextViewerImpl structuredTextViewer = new StructuredTextViewerImpl(file, infoType, columns, parentFolder, presenter);
                    viewers.add(structuredTextViewer);
                }
                TextViewerImpl textViewer1 = new TextViewerImpl(file, infoType, null, editing, parentFolder, presenter);
                viewers.add(textViewer1);
                break;

        }

        return viewers;

    }
}
