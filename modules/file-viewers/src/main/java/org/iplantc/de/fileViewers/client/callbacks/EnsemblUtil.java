package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.gin.ServicesInjector;
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
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnsemblUtil {

    private final IsMaskable container;
    private final File file;
    private final String infoType;
    private final DiskResourceUtil diskResourceUtil;

    public EnsemblUtil(File file, String infoType, IsMaskable container) {
        this.file = file;
        this.container = container;
        this.infoType = infoType;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
    }

    public void sendToEnsembl() {
        final DiskResourceServiceFacade diskResourceServiceFacade = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
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
        } else if (infoType.equals(InfoType.GFF.toString())) {
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
                                                  IplantInfoBox info = new IplantInfoBox(I18N.DISPLAY.indexFileMissing(),
                                                                                         I18N.ERROR.indexFileMissing());
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
