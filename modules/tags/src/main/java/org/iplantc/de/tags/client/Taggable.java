package org.iplantc.de.tags.client;

import org.iplantc.de.client.models.tags.IplantTag;

public interface Taggable {

    void attachTag(IplantTag tag);

    void detachTag(IplantTag tag);

    void selectTag(IplantTag tag);

}
