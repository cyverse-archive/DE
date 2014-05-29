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
package com.virilis_software.gwt.taglist.client.tag;

import com.google.common.base.Objects;

/**
 * 
 * @author cbopp
 *
 */
public abstract class Tag<T> {
    private String caption;
    private T value;
    

    /**
     * @return the caption
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption( String caption ) {
        this.caption = caption;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( T value ) {
        this.value = value;
    }
    

    public Tag() {}

    public Tag( String caption, T value ) {
        this.caption = caption;
        this.value = value;
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if( obj == this ) return true;
        if( obj == null ) return false;
        if( obj.getClass() != getClass() ) return false;
        
        Tag<?> other = (Tag<?>) obj;
        return Objects.equal( this.caption, other.caption );
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode( this.caption );
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper( Tag.class )
                .add( "tag", this.caption )
                .add( "value", this.value )
                .toString();
    }
}
