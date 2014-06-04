package com.virilis_software.gwt.taglist.client;

import com.virilis_software.gwt.taglist.client.tag.Tag;

public interface TagCreationCodex<T extends Tag<?>> {
     T createTag( Tag<?> tag );
}