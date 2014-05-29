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
package com.virilis_software.gwt.taglist.client.samples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.virilis_software.gwt.taglist.client.TagList;
import com.virilis_software.gwt.taglist.client.tag.StringTag;

/**
 * 
 * @author cbopp
 *
 */
public class EditView extends Composite {
    interface Binder extends UiBinder<Widget, EditView> {}
    private static Binder uiBinder = GWT.create( Binder.class );

    interface MyStyle extends CssResource {
        String focus();
    }
    @UiField MyStyle style;
    
    @UiField SimplePanel tagPanel;
    private TagList<StringTag> tagList;
    
    
    /**
     * @return the tagList
     */
    public TagList<StringTag> getTagList() {
        return this.tagList;
    }
    
    
    public EditView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        
        this.tagList = new TagList<StringTag>();
        this.tagList.setEditable( true );
        this.tagList.setTagCreationCodex( StringTag.tagCreationCodex );
        this.tagList.setOnFocusCmd( new Command() {
            @Override
            public void execute() {
                tagPanel.addStyleName( style.focus() );
            }
        } );
        this.tagList.setOnBlurCmd( new Command() {
            @Override
            public void execute() {
                tagPanel.removeStyleName( style.focus() );
            }
        } );
        this.tagPanel.add( tagList );
    }
}
