/**
 *
 */
package org.iplantc.de.fileViewers.client.presenter;

import static org.iplantc.de.client.models.viewer.InfoType.BED;
import static org.iplantc.de.client.models.viewer.InfoType.BOWTIE;
import static org.iplantc.de.client.models.viewer.InfoType.CSV;
import static org.iplantc.de.client.models.viewer.InfoType.GFF;
import static org.iplantc.de.client.models.viewer.InfoType.GTF;
import static org.iplantc.de.client.models.viewer.InfoType.HT_ANALYSIS_PATH_LIST;
import static org.iplantc.de.client.models.viewer.InfoType.TSV;
import static org.iplantc.de.client.models.viewer.InfoType.VCF;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.views.AbstractFileViewer;
import org.iplantc.de.fileViewers.client.views.ExternalVisualizationURLViewerImpl;
import org.iplantc.de.fileViewers.client.views.ImageViewerImpl;
import org.iplantc.de.fileViewers.client.views.PathListViewer;
import org.iplantc.de.fileViewers.client.views.StructuredTextViewer;
import org.iplantc.de.fileViewers.client.views.TextViewerImpl;

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
    @Inject IplantAnnouncer announcer;
    @Inject FileEditorServiceFacade fileEditorService;
    @Inject JsonUtil jsonUtil;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject DiskResourceServiceFacade diskResourceServiceFacade;

    @Inject
    public MimeTypeViewerResolverFactory() {
        modeMap.put(MimeType.X_SH, "shell");
        modeMap.put(MimeType.X_PYTHON, "python");
        modeMap.put(MimeType.X_PERL, "perl");
        modeMap.put(MimeType.X_RSRC, "r");
        modeMap.put(MimeType.X_WEB_MARKDOWN, "markdown");
    }

    public List<? extends FileViewer> getViewerCommand(final File file,
                                        final String infoType,
                                        final boolean editing,
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
                ExternalVisualizationURLViewerImpl vizUrlViewer = new ExternalVisualizationURLViewerImpl(file, infoType, fileEditorService, diskResourceServiceFacade);
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
                TextViewerImpl textViewer = new TextViewerImpl(file,
                                                               infoType,
                                                               textViewerMode,
                                                               editing,
                                                               presenter);
                viewers.add(textViewer);
                break;

            case PLAIN:
            case PREVIEW:
            default:
                Integer columns = null;
                if(manifest.containsKey(FileViewer.COLUMNS_KEY)){
                    columns = jsonUtil.getNumber(manifest, FileViewer.COLUMNS_KEY).intValue();
                    LOG.fine("Columns are defined: " + columns);
                }
                if(CSV.toString().equals(infoType)
                    || TSV.toString().equals(infoType)
                    || VCF.toString().equals(infoType)
                    || GFF.toString().equals(infoType)
                    || GTF.toString().equals(infoType)
                    || BED.toString().equals(infoType)
                    || BOWTIE.toString().equals(infoType)){
                    StructuredTextViewer structuredTextViewer = new StructuredTextViewer(file,
                                                                                         infoType,
                                                                                         editing,
                                                                                         columns,
                                                                                         presenter);
                    viewers.add(structuredTextViewer);
                } else if(HT_ANALYSIS_PATH_LIST.toString().equals(infoType)){
                    PathListViewer pathListViewer = new PathListViewer(file,
                                                                     infoType,
                                                                     editing,
                                                                       presenter,
                                                                       diskResourceUtil);
                    viewers.add(pathListViewer);
                }
                TextViewerImpl textViewer1 = new TextViewerImpl(file,
                                                                infoType,
                                                                null,
                                                                editing,
                                                                presenter);
                viewers.add(textViewer1);
                break;

        }

        return viewers;

    }
}
