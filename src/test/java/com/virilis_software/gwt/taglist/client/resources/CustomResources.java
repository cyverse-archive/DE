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
package com.virilis_software.gwt.taglist.client.resources;

import com.google.gwt.core.client.GWT;
import com.virilis_software.gwt.taglist.client.resource.Resources;

/**
 * 
 * @author cbopp
 *
 */
public interface CustomResources extends Resources {
    public static final Resources INSTANCE = GWT.create( CustomResources.class );
    
    public interface CustomStyle extends Style {
        //Widget style
        String tagList();
        String tagListEditable();

        //Tag style
        String tag();
        String tagEditable();
        String tagCaption();
        String tagCaptionEditable();
        String tagDelete();
        
        String inputListItem();
        String inputField();
        
        //DnD
        String previewLeft();
        String previewRight();
    }
    
    @Source( cssRoot + "/customStyle.css" )
    CustomStyle style();
}
