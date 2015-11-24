package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.dialogs.IplantInfoBox;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnsemblUtil {

    public interface EnsemblUtilAppearance {

        String indexFileMissing();

        String indexFileMissingError();
    }

    private final IsMaskable container;
    private final File file;
    private final String infoType;
    private final EnsemblUtilAppearance appearance;
    private final DiskResourceUtil diskResourceUtil;

    public EnsemblUtil(final File file,
                       final String infoType,
                       final IsMaskable container) {
        this(file, infoType, container, GWT.<EnsemblUtilAppearance> create(EnsemblUtilAppearance.class));
    }

    EnsemblUtil(final File file,
                final String infoType,
                final IsMaskable container,
                final EnsemblUtilAppearance appearance) {
        this.file = file;
        this.container = container;
        this.infoType = infoType;
        this.appearance = appearance;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
    }

    public void sendToEnsembl(final DiskResourceServiceFacade diskResourceServiceFacade) {
        List<HasPath> list = new ArrayList<>();
        final HasPaths diskResourcePaths = diskResourceServiceFacade.getDiskResourceFactory()
                                                                    .pathsList()
                                                                    .as();
        final String path = file.getPath();
        String filename = diskResourceUtil.parseNameFromPath(path);
        String parent = diskResourceUtil.parseParent(path);
        String indexFile = null;
        String indexFilePath = null;
        if (infoType.equals(InfoType.BAM.toString())) {
            indexFile = filename + ".bai";
        } else if (infoType.equals(InfoType.VCF.toString())) {
            indexFile = filename + ".tbi";
        } else if (infoType.equals(InfoType.GFF.toString()) || infoType.equals(InfoType.BED.toString())) {
            indexFile = null;
        }

        list.add(file);

        if (indexFile != null) {
            indexFilePath = parent + "/" + indexFile;
            list.add(CommonModelUtils.getInstance().createHasPathFromString((indexFilePath)));

        }

        if (!Strings.isNullOrEmpty(indexFilePath)) {
            diskResourcePaths.setPaths(Arrays.asList(path, indexFilePath));
        } else {
            diskResourcePaths.setPaths(Arrays.asList(path));
        }

        diskResourceServiceFacade.getStat(diskResourceUtil.asStringPathTypeMap(list, TYPE.FILE),
                                          new AsyncCallback<FastMap<DiskResource>>() {

                                              @Override
                                              public void onFailure(Throwable caught) {
                                                  IplantInfoBox info = new IplantInfoBox(appearance.indexFileMissing(),
                                                                                         appearance.indexFileMissingError());
                                                  info.show();
                                                  if (container != null) {
                                                      container.unmask();
                                                  }
                                              }

                                              @Override
                                              public void onSuccess(FastMap<DiskResource> result) {

                                                  diskResourceServiceFacade.shareWithAnonymous(diskResourcePaths,
                                                                                               new ShareAnonymousCallback(file,
                                                                                                                          container));
                                              }
                                          });
    }

}
