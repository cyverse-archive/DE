package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author psarando
 */
public class FolderPathProvider implements ValueProvider<PermanentIdRequest, String> {

    private final PermanentIdRequestView.PermanentIdRequestViewAppearance appearance;

    public FolderPathProvider(PermanentIdRequestView.PermanentIdRequestViewAppearance appearance) {
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
