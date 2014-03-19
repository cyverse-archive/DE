package org.iplantc.de.client.viewer.views;

public interface EditingSupport {

    void save();

    void setDirty(Boolean dirty);

    boolean isDirty();
}
