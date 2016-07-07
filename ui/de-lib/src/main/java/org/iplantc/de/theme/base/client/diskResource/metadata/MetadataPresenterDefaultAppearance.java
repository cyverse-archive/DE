package org.iplantc.de.theme.base.client.diskResource.metadata;

import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class MetadataPresenterDefaultAppearance implements MetadataView.Presenter.Appearance {

	private final MetadataDisplayStrings displayStrings = GWT.<MetadataDisplayStrings> create(MetadataDisplayStrings.class);

	private final IplantResources iplantResources = GWT.create(IplantResources.class);
	
	@Override
	public String templateListingError() {
		return displayStrings.templateListingError();
	}

	@Override
	public String loadMetadataError() {
		return displayStrings.loadMetadataError();
	}

	@Override
	public String saveMetadataError() {
		return displayStrings.saveMetadataError();
	}

	@Override
	public String templateinfoError() {
		return displayStrings.templateinfoError();
	}

	@Override
	public String selectTemplate() {
		return displayStrings.selectTemplate();
	}

	@Override
	public String templates() {
		return displayStrings.templates();
	}

	@Override
	public String error() {
		return displayStrings.error();
	}

	@Override
	public String incomplete() {
		return displayStrings.incomplete();
	}

	@Override
	public ImageResource info() {
		return iplantResources.info();
	}

}