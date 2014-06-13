package org.iplantc.de.commons.client.tags;

import org.iplantc.de.client.models.tags.IplantTag;

public interface Taggable {

    void attachTag(IplantTag tag);

    void detachTag(IplantTag tag);

}
