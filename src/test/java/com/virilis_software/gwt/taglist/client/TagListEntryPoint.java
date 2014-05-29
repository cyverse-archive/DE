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
package com.virilis_software.gwt.taglist.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.virilis_software.gwt.taglist.client.TagList;
import com.virilis_software.gwt.taglist.client.resource.Resources;
import com.virilis_software.gwt.taglist.client.resources.CustomResources;
import com.virilis_software.gwt.taglist.client.tag.StringTag;

/**
 * 
 * @author cbopp
 *
 */
public class TagListEntryPoint implements EntryPoint {
    @Override
    public void onModuleLoad() {
        VerticalPanel vp = new VerticalPanel();
        
        vp.add( this.createSample( "Small non editable box", "border: 1px solid black; width: 100px; font-family: arial, sans-serif; font-size: 10px;", null, false ) );
        vp.add( this.createSample( "Small editable box with a dashed border", "border: 1px dashed black; width: 100px; font-family: arial, sans-serif; font-size: 10px;", null, true ) );
        vp.add( this.createSample( "Big non editable box with a dashed border", "border: 1px dashed black; width: 200px; font-family: arial, sans-serif; font-size: 20px;", null, false ) );
        vp.add( this.createSample( "Big editable box", "border: 1px solid black; width: 200px; font-family: arial, sans-serif; font-size: 20px;", null, true ) );
        
        vp.add( this.createSample( "Non editable box with custom style", "border: 1px solid black; width: 100px; font-family: arial, sans-serif; font-size: 10px;", CustomResources.INSTANCE, false ) );
        vp.add( this.createSample( "Editable box with custom style", "border: 1px dashed black; width: 100px; font-family: arial, sans-serif; font-size: 10px;", CustomResources.INSTANCE, true ) );
        
        RootPanel.get().add( vp );
    }
    

    private Widget createSample( String description, String containerStyle, Resources resources, boolean editable ) {
        HorizontalPanel hp = new HorizontalPanel();
        hp.getElement().setAttribute( "style", "margin-bottom: 20px;" );
        
        //Description
        HTMLPanel descPanel = new HTMLPanel( description );
        descPanel.getElement().setAttribute( "style", "padding-right: 10px;" );
        hp.add( descPanel );
        
        //TagList
        SimplePanel boundaryBox = new SimplePanel();
        boundaryBox.setWidget( createTagList( resources, editable, this.createOnFocusCmd( boundaryBox, containerStyle ), this.createOnBlurCmd( boundaryBox, containerStyle ) ) );
        boundaryBox.getElement().setAttribute( "style", containerStyle );
        hp.add( boundaryBox );
        return hp;
    }

    private Command createOnFocusCmd( final SimplePanel boundaryBox, final String defaultStyle ) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute( "style", defaultStyle + " outline: -webkit-focus-ring-color auto 5px;" );
            }
        };
    }

    private Command createOnBlurCmd( final SimplePanel boundaryBox, final String defaultStyle ) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute( "style", defaultStyle );
            }
        };
    }

    private TagList<StringTag> createTagList( Resources resources, boolean editable, Command onFocusCmd, Command onBlurCmd ) {
        List<StringTag> items = new ArrayList<StringTag>();
        items.add( new StringTag( "Tag 1", "Tag 1" ) );
        items.add( new StringTag( "Tag 2", "Tag 2" ) );
        items.add( new StringTag( "Bigger Tag", "Bigger Tag" ) );
        items.add( new StringTag( "This Tag is even bigger", "Bigger Tag" ) );

        TagList<StringTag> tagList;
        if( resources == null )
            tagList = new TagList<StringTag>();
        else
            tagList = new TagList<StringTag>( resources );
        tagList.setEditable( editable );
        tagList.setTagCreationCodex( StringTag.tagCreationCodex );
        tagList.setOnFocusCmd( onFocusCmd );
        tagList.setOnBlurCmd( onBlurCmd );
        tagList.addTags( items );

        return tagList;
    }
}
