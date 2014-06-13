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
package org.iplantc.de.commons.client.tags.presenter;

import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.commons.client.tags.views.TagView;


/**
 * 
 * @author cbopp
 *
 */
public interface TagListHandlers {

    public enum InsertionPoint {
        BEFORE, AFTER
    };

    void onCreateTag(IplantTag tag);

    void onAddTag(IplantTag tag);
    void onRemoveTag( TagView tagView );

    void onEditTag(TagView tagView);
    void onRelocateTag( TagView tagViewToRelocate, TagView tagViewRelocationRef, InsertionPoint insertionPoint );
    void onFocus();
    void onBlur();

}
