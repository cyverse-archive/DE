package org.iplantc.de.theme.base.client.fileViewers;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/15/15.
 * @author jstroot
 */
public interface FileViewerErrorStrings extends Messages {

    @Key("cogeError")
    String cogeError();

    @Key("indexFileMissingError")
    String indexFileMissingError();

    @Key("unableToRetrieveTreeUrls")
    String unableToRetrieveTreeUrls(String fileName);
}
