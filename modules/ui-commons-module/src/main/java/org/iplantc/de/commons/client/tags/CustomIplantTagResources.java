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
package org.iplantc.de.commons.client.tags;

import com.google.gwt.core.client.GWT;

import com.virilis_software.gwt.taglist.client.resource.Resources;

/**
 * 
 * @author cbopp
 *
 */
public interface CustomIplantTagResources extends Resources {
    public static final CustomIplantTagResources INSTANCE = GWT.create( CustomIplantTagResources.class );

    public interface CustomStyle extends Style {
        //Widget style
        @Override
        String tagList();
        @Override
        String tagListEditable();

        //Tag style
        @Override
        String tag();
        @Override
        String tagEditable();
    	@Override
        String tagCaption();
    	@Override
        String tagCaptionEditable();
    	@Override
        String tagDelete();

        @Override
        String tagEdit();
    	
    	@Override
        String inputListItem();
    	@Override
        String inputField();
    	
    	//DnD
    	@Override
        String previewLeft();
        @Override
        String previewRight();

        @Override
        String tagOptions();
    }
    
    @Override
    @Source("style.css")
    CustomStyle style();
}
