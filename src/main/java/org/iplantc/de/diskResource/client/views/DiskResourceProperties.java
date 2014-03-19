package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface DiskResourceProperties extends PropertyAccess<DiskResource> {

    ValueProvider<DiskResource, String> id();

    @Path("path")
    ValueProvider<DiskResource, String> path();

    @Path("name")
    LabelProvider<DiskResource> nameLabel();

    ValueProvider<DiskResource, String> name();

    ValueProvider<DiskResource, Date> dateCreated();

    ValueProvider<DiskResource, Date> lastModified();
    
    @Path("dateCreated")
    ValueProvider<DiskResource, Date> dateSubmitted();

}
