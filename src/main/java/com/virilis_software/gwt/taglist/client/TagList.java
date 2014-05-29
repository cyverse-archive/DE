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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.virilis_software.gwt.taglist.client.comp.TagListHandlers;
import com.virilis_software.gwt.taglist.client.comp.tag.TagView;
import com.virilis_software.gwt.taglist.client.comp.taglist.TagListView;
import com.virilis_software.gwt.taglist.client.resource.Resources;
import com.virilis_software.gwt.taglist.client.tag.Tag;

/**
 * 
 * @author cbopp
 *
 */
public class TagList<T extends Tag<?>> implements IsWidget, TagListHandlers {
    public interface TagCreationCodex<T extends Tag<?>> {
         T createTag( Tag<?> tag );
    }
    
    public enum InsertionPoint { BEFORE, AFTER };
    
    private final Resources resources;
    private final TagListView tagListView;
    private final List<TagItem> tagItems = new ArrayList<TagItem>();
    
    private boolean editable;
    private TagCreationCodex<T> tagCreationCodex;
    private Command onFocusCmd;
    private Command onBlurCmd;
    private Command onChangeCmd;
    
    
    /**
     * @return the editable
     */
    public boolean isEditable() {
        return this.editable;
    }
    
    /**
     * @param editable the editable to set
     */
    public void setEditable( boolean editable ) {
        this.editable = editable;
        
        this.tagListView.setEditable( editable );
        for( TagItem tagItem : this.tagItems )
            tagItem.getTagView().setEditable( true );
    }
    
    /**
     * @return the tagCreationCodex
     */
    public TagCreationCodex<T> getTagCreationCodex() {
        return this.tagCreationCodex;
    }

    /**
     * @param tagCreationCodex the tagCreationCodex to set
     */
    public void setTagCreationCodex( TagCreationCodex<T> tagCreationCodex ) {
        this.tagCreationCodex = tagCreationCodex;
    }
    
    /**
     * @return the onFocusCmd
     */
    public Command getOnFocusCmd() {
        return this.onFocusCmd;
    }

    /**
     * @param onFocusCmd the onFocusCmd to set
     */
    public void setOnFocusCmd( Command onFocusCmd ) {
        this.onFocusCmd = onFocusCmd;
    }

    /**
     * @return the onBlurCmd
     */
    public Command getOnBlurCmd() {
        return this.onBlurCmd;
    }
    
    /**
     * @param onBlurCmd the onBlurCmd to set
     */
    public void setOnBlurCmd( Command onBlurCmd ) {
        this.onBlurCmd = onBlurCmd;
    }
    
    /**
     * @return the onChangeCmd
     */
    public Command getOnChangeCmd() {
        return this.onChangeCmd;
    }

    /**
     * @param onChangeCmd the onChangeCmd to set
     */
    public void setOnChangeCmd( Command onChangeCmd ) {
        this.onChangeCmd = onChangeCmd;
    }

    /**
     * Creates a non editable TagList with default styles.
     * 
     * <p>Use {@link TagList#setEditable(boolean)} to enable/disable tag creation on an existing TagList.
     * You have to set a {@link TagCreationCodex} to successfully enable tag creation.
     */
    public TagList() {
        this( Resources.INSTANCE );
    }
    
    /**
     * Creates a non editable TagList with custom styles.
     */
    public TagList( Resources resources ) {
        super();
        
        this.resources = resources;
        this.resources.style().ensureInjected();

        this.tagListView = new TagListView( this.resources );
        this.tagListView.setUiHandlers( this );
    }

    public List<T> getTags() {
        List<T> tags = new ArrayList<T>();
        for( TagItem tagItem : this.tagItems )
            tags.add( tagItem.getTag() );
        return tags;
    }

    public boolean addTag( T tag ) {
        for( TagItem tagItem : this.tagItems )
            if( tagItem.getTag().equals( tag ) )
                return false;

        TagView tagView = new TagView( this.resources, tag );
        tagView.setUiHandlers( this );
        tagView.setEditable( this.editable );

        this.tagListView.getTagsPanel().add( tagView );
        this.tagItems.add( new TagItem( tag, tagView ) );
        
        return true;
    }
    
    public boolean addTags( List<T> tags ) {
        boolean hasChanged = false;
        
        for( T tag : tags ) {
            hasChanged |= this.addTag( tag );
        }
        
        return hasChanged;
    }
    
    public boolean removeTag( T tag ) {
        for( Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext(); ) {
            TagItem tagItem = tagItemIt.next();
            if( tagItem.getTag().equals( tag ) ) {
                this.tagListView.getTagsPanel().remove( tagItem.getTagView() );
                tagItemIt.remove();
                return true;
            }
        }
        
        return false;
    }
    
    public void clear() {
        for( Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext(); ) {
            TagItem tagItem = tagItemIt.next();
            this.tagListView.getTagsPanel().remove( tagItem.getTagView() );
            tagItemIt.remove();
        }
    }
    
    @Override
    public Widget asWidget() {
        return tagListView;
    }

    @Override
    public void onAddTag( Tag<?> tag ) {
        if( this.tagCreationCodex == null )
            throw new RuntimeException( "Found no TagCreationCodex."
                    + " You have to specify a TagCreationCodex in order to convert base tags to your tag type" );
        this.addTag( this.tagCreationCodex.createTag( tag ) );
        
        if( this.onChangeCmd != null )
            this.onChangeCmd.execute();
    }

    @Override
    public void onRemoveTag( TagView tagView ) {
        for( Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext(); ) {
            TagItem tagItem = tagItemIt.next();
            if( tagItem.getTagView().equals( tagView ) ) {
                this.tagListView.getTagsPanel().remove( tagItem.getTagView() );
                tagItemIt.remove();
            }
        }
        
        if( this.onChangeCmd != null )
            this.onChangeCmd.execute();
    }

    @Override
    public void onRelocateTag( TagView tagViewToRelocate, TagView tagViewRelocationRef, InsertionPoint insertionPoint ) {
        //Prevent modification between two similar TagLists
        if( !this.containsTagView( tagViewToRelocate ) || !this.containsTagView( tagViewRelocationRef ) )
            return;
        
        int fromIndex = -1;
        int toIndex = -1;
        
        for( int i = 0; i < this.tagItems.size(); i++ )
            if( this.tagItems.get( i ).getTagView().equals( tagViewToRelocate ) )
                fromIndex = i;
            else if( this.tagItems.get( i ).getTagView().equals( tagViewRelocationRef ) )
                if( InsertionPoint.BEFORE == insertionPoint )
                    toIndex = i;
                else
                    toIndex = i + 1;
        
        //Reorder tagItem list
        this.tagItems.add( toIndex, this.tagItems.get( fromIndex ) );
        this.tagItems.remove( fromIndex > toIndex ? fromIndex + 1 : fromIndex );
        
        //Reset TagListView
        for( TagItem tagItem : this.tagItems ) {
            this.tagListView.getTagsPanel().remove( tagItem.getTagView() );
            this.tagListView.getTagsPanel().add( tagItem.getTagView() );
        }
    }

    private boolean containsTagView( TagView tagView ) {
        for( TagItem tagItem : this.tagItems )
            if( tagItem.getTagView().equals( tagView ) )
                return true;
        return false;
    }

    @Override
    public void onFocus() {
        if( this.onFocusCmd != null )
            this.onFocusCmd.execute();
    }

    @Override
    public void onBlur() {
        if( this.onBlurCmd != null )
            this.onBlurCmd.execute();
    }
    
    public class TagItem {
        private T tag;
        private TagView tagView;

        public TagItem( T tag, TagView tagView ) {
            this.tag = tag;
            this.tagView = tagView;
        }

        public TagView getTagView() {
            return this.tagView;
        }

        public T getTag() {
            return this.tag;
        }
    }
}
