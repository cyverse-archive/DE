package org.iplantc.de.collaborators.client.views.cells;

import org.iplantc.de.client.models.collaborators.Collaborator;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public final class NameCell extends AbstractCell<Collaborator> {
    @Override
    public void render(Context context,
                       Collaborator value,
                       SafeHtmlBuilder sb) {
        StringBuilder builder = new StringBuilder();
        if (value.getFirstName() != null && !value.getFirstName().isEmpty()) {
            builder.append(value.getFirstName());
            if (value.getLastName() != null && !value.getLastName().isEmpty()) {
                builder.append(" " + value.getLastName());
            }
            sb.appendEscaped(builder.toString());
        } else {
            sb.appendEscaped(value.getUserName());
        }

    }
}
