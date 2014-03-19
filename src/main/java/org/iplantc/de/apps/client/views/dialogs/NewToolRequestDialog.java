/**
 *
 */
package org.iplantc.de.apps.client.views.dialogs;

import org.iplantc.de.apps.client.presenter.NewToolRequestFormPresenterImpl;
import org.iplantc.de.apps.client.views.NewToolRequestFormView;
import org.iplantc.de.apps.client.views.NewToolRequestFormView.Presenter;
import org.iplantc.de.apps.client.views.NewToolRequestFormViewImpl;
import org.iplantc.de.client.models.toolRequests.Architecture;
import org.iplantc.de.client.models.toolRequests.YesNoMaybe;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Command;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

import java.util.Arrays;

/**
 * @author sriram
 *
 */
public class NewToolRequestDialog extends IPlantDialog {

    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String DONT_KNOW = "Don't know";
    private static final String ARCH32 = "32-bit Generic";
    private static final String ARCH64 = "64-bit Generic";
    private static final String OTHERS = "Others";

    // TODO this should be part of a widget factory for the NewToolRequestFormView.
    private static ComboBox<Architecture> makeArchitectureChooser() {
        final LabelProvider<Architecture> labeler = new LabelProvider<Architecture>() {
            @Override
            public String getLabel(final Architecture item) {
                switch (item) {
                    case GENERIC_32:
                        return ARCH32;
                    case GENERIC_64:
                        return ARCH64;
                    case VM_OR_INTERPRETED:
                        return OTHERS;
                    case UNKNOWN:
                    default:
                        return DONT_KNOW;
                }
            }
        };
        final SimpleComboBox<Architecture> chooser = new SimpleComboBox<Architecture>(labeler);
        chooser.add(Arrays.asList(Architecture.GENERIC_32, Architecture.GENERIC_64, Architecture.VM_OR_INTERPRETED, Architecture.UNKNOWN));
        chooser.setValue(Architecture.GENERIC_64);
        return chooser;
    }

    // TODO this should be part of a widget factory for the NewToolRequestFormView.
    private static ComboBox<YesNoMaybe> makeMultithreadChooser() {
        final LabelProvider<YesNoMaybe> labeler = new LabelProvider<YesNoMaybe>() {
            @Override
            public String getLabel(final YesNoMaybe item) {
                switch (item) {
                    case YES:
                        return YES;
                    case NO:
                        return NO;
                    default:
                        return DONT_KNOW;
                }
            }
        };
        final SimpleComboBox<YesNoMaybe> chooser = new SimpleComboBox<YesNoMaybe>(labeler);
        chooser.add(Arrays.asList(YesNoMaybe.YES, YesNoMaybe.NO, YesNoMaybe.MAYBE));
        chooser.setValue(YesNoMaybe.MAYBE);
        return chooser;
    }

    public NewToolRequestDialog() {
        setHeadingText(I18N.DISPLAY.requestNewTool());
        setPixelSize(480, 400);
        this.setResizable(false);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setHideOnButtonClick(false);
        setOkButtonText(I18N.DISPLAY.submit());
        final ComboBox<Architecture> archChooser = makeArchitectureChooser();
        final ComboBox<YesNoMaybe> multithreadChooser = makeMultithreadChooser();
        final NewToolRequestFormView<Architecture, YesNoMaybe> view = new NewToolRequestFormViewImpl<Architecture, YesNoMaybe>(archChooser, multithreadChooser);
        final Presenter p = new NewToolRequestFormPresenterImpl(view, new Command() {

            @Override
            public void execute() {
                hide();

            }
        });
        p.go(this);
        
        addOkButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                p.onSubmitBtnClick();
            }
        });
        
        addCancelButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                p.onCancelBtnClick();
            }
        });

    }
    


}
