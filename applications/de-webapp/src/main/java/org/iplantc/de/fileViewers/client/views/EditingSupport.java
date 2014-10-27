package org.iplantc.de.fileViewers.client.views;

public interface EditingSupport {

    boolean isDirty();

    void setDirty(Boolean dirty);

    void save();
}
