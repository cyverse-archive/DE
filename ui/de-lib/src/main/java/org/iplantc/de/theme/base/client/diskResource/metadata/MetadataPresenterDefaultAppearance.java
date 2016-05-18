package org.iplantc.de.theme.base.client.diskResource.metadata;

import org.iplantc.de.diskResource.client.MetadataView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;

public class MetadataPresenterDefaultAppearance implements MetadataView.Presenter.Appearance {

	private final MetadataDisplayStrings displayStrings = GWT.<MetadataDisplayStrings> create(MetadataDisplayStrings.class);
	
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
	
}