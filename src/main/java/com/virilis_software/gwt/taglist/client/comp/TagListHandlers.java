/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.virilis_software.gwt.taglist.client.comp;

import com.virilis_software.gwt.taglist.client.TagList.InsertionPoint;
import com.virilis_software.gwt.taglist.client.comp.tag.TagView;
import com.virilis_software.gwt.taglist.client.tag.Tag;

/**
 * 
 * @author cbopp
 *
 */
public interface TagListHandlers {
    void onAddTag( Tag<?> tag );
    void onRemoveTag( TagView tagView );
    void onRelocateTag( TagView tagViewToRelocate, TagView tagViewRelocationRef, InsertionPoint insertionPoint );
    void onFocus();
    void onBlur();
}
