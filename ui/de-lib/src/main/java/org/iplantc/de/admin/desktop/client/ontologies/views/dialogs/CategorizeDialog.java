package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeHierarchiesToAppEvent;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
public class CategorizeDialog extends IPlantDialog implements CategorizeHierarchiesToAppEvent.HasCategorizeHierarchiesToAppEventHandlers {

    private OntologiesView.OntologiesViewAppearance appearance;
    private AppCategorizeView categorizeView;
    private List<Avu> selectedAvus;
    private Map<String, List<OntologyHierarchy>> iriToHierarchyMap;
    private App targetApp;

    public CategorizeDialog(OntologiesView.OntologiesViewAppearance appearance,
                            final App targetApp,
                            final AppCategorizeView categorizeView,
                            List<OntologyHierarchy> hierarchyRoots,
                            Map<String, List<OntologyHierarchy>> iriToHierarchyMap,
                            List<Avu> selectedAvus) {
        super(true);

        this.appearance = appearance;
        this.categorizeView = categorizeView;
        this.selectedAvus = selectedAvus;
        this.iriToHierarchyMap = iriToHierarchyMap;
        this.targetApp = targetApp;

        setHideOnButtonClick(false);
        setResizable(true);
        setUpButtons();
        setHeadingText(appearance.categorizeApp(targetApp));

        setOnEsc(false);

        categorizeView.setHierarchies(hierarchyRoots);

        markTaggedHierarchies();

        VerticalLayoutContainer con = new VerticalLayoutContainer();
        con.add(categorizeView);
        add(con);

        show();

    }

    private void setUpButtons() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        getOkButton().setText(appearance.categorize());
        addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                fireEvent(new CategorizeHierarchiesToAppEvent(targetApp, categorizeView.getSelectedCategories()));
                hide();
            }
        });
        addCancelButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
    }

    void markTaggedHierarchies() {
        List<OntologyHierarchy> selectedHierarchies = Lists.newArrayList();
        for (Avu avu: selectedAvus) {
            List<OntologyHierarchy> hierarchies = iriToHierarchyMap.get(avu.getValue());
            if (hierarchies != null) {
                selectedHierarchies.addAll(hierarchies);
            }
        }
        categorizeView.setSelectedHierarchies(selectedHierarchies);
    }

    @Override
    public HandlerRegistration addCategorizeHierarchiesToAppEventHandler(CategorizeHierarchiesToAppEvent.CategorizeHierarchiesToAppEventHandler handler) {
        return addHandler(handler, CategorizeHierarchiesToAppEvent.TYPE);
    }
}
