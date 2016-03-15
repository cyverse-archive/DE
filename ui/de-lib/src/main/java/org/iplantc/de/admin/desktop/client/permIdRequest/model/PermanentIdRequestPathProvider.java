package org.iplantc.de.admin.desktop.client.permIdRequest.model;

import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestViewAppearance;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author psarando
 */
public class PermanentIdRequestPathProvider implements ValueProvider<PermanentIdRequest, String> {

    private final PermanentIdRequestViewAppearance appearance;

    public PermanentIdRequestPathProvider(PermanentIdRequestViewAppearance appearance) {
        this.appearance = appearance;
    }

    @Override
    public String getValue(PermanentIdRequest request) {
        if (request == null || request.getFolder() == null) {
            return appearance.folderNotFound();
        }

        return request.getFolder().getPath();
    }

    @Override
    public void setValue(PermanentIdRequest request, String value) {
        if (request != null && request.getFolder() != null) {
            request.getFolder().setPath(value);
        }
    }

    @Override
    public String getPath() {
        return "Folder.path";
    }
}
