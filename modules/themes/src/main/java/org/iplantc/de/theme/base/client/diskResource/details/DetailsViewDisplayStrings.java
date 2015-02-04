package org.iplantc.de.theme.base.client.diskResource.details;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 2/3/15.
 * @author jstroot
 */
public interface DetailsViewDisplayStrings extends Messages{
    @Key("createdDate")
    String createdDate();

    @Key("ensembl")
    String ensembl();

    @Key("files")
    String files();

    @Key("folders")
    String folders();

    @Key("noDetails")
    String noDetails();

    @Key("noSharing")
    String noSharing();

    @Key("selectInfoType")
    String selectInfoType();

    @Key("sendTo")
    String sendTo();

    @Key("share")
    String share();

    @Key("sharingDisabled")
    String sharingDisabled();

    @Key("treeViewer")
    String treeViewer();

    @Key("unsupportedCogeInfoType")
    String unsupportedCogeInfoType();

    @Key("unsupportedEnsemblInfoType")
    String unsupportedEnsemblInfoType();

    @Key("unsupportedTreeInfoType")
    String unsupportedTreeInfoType();
}
