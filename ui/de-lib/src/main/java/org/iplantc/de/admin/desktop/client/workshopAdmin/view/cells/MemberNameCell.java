package org.iplantc.de.admin.desktop.client.workshopAdmin.view.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.client.models.groups.Member;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

/**
 * @author dennis
 */
public class MemberNameCell extends AbstractCell<Member> {

    public interface MemberNameCellAppearance {
        String CLICKABLE_ELEMENT_NAME = "memberName";

        void render(SafeHtmlBuilder builder, Member member);
    }

    private final WorkshopAdminView view;
    private final MemberNameCellAppearance appearance = GWT.create(MemberNameCellAppearance.class);

    public MemberNameCell(WorkshopAdminView view) {
        super(CLICK);
        this.view = view;
    }

    @Override
    public void render(Context context, Member value, SafeHtmlBuilder sb) {
        appearance.render(sb, value);
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               Member value,
                               NativeEvent event,
                               ValueUpdater<Member> valueUpdater) {
        if (isActionableEvent(value, event)) {
            view.memberSelected(value);
        }
    }

    private boolean isActionableEvent(Member value, NativeEvent event) {
        if (value == null) {
            return false;
        }

        Element eventTargetElement = Element.as(event.getEventTarget());
        String targetElementName = eventTargetElement.getAttribute("name");
        return targetElementName.equalsIgnoreCase(MemberNameCellAppearance.CLICKABLE_ELEMENT_NAME);
    }
}
