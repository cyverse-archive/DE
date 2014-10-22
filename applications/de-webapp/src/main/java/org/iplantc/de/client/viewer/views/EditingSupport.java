package org.iplantc.de.client.viewer.views;

public interface EditingSupport {

    boolean isDirty();

    void setDirty(Boolean dirty);

    void save();
}
