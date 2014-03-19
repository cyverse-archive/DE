package org.iplantc.de.apps.integration.client.gin;

import org.iplantc.de.apps.integration.client.presenter.AppsEditorPresenterImpl;
import org.iplantc.de.apps.integration.client.view.AppEditorToolbar;
import org.iplantc.de.apps.integration.client.view.AppEditorToolbarImpl;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.integration.client.view.AppsEditorViewImpl;
import org.iplantc.de.apps.integration.client.view.ArgumentGroupEditorAppEditorImpl;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ArgumentGroupPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.DecimalInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.DecimalSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.EnvVarPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FileInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FileOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FlagArgumentPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FolderInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FolderOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.InfoPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.IntegerInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.IntegerSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiFileInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiFileOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiLineTextInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceAnnotationPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceGenomePropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceSequencePropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TextInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TextSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TreeSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.widgets.ArgumentValidatorEditor;
import org.iplantc.de.apps.widgets.client.presenter.AppLaunchPresenterImpl;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView;
import org.iplantc.de.apps.widgets.client.view.AppLaunchViewImpl;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.LaunchAnalysisView;
import org.iplantc.de.apps.widgets.client.view.editors.AppTemplateFormImpl;
import org.iplantc.de.apps.widgets.client.view.editors.ArgumentEditorFactoryImpl;
import org.iplantc.de.apps.widgets.client.view.editors.LaunchAnalysisViewImpl;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationErrorMessages;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author jstroot
 * 
 */
public class AppEditorGinModule extends AbstractGinModule {

    @Provides
    @Singleton
    public AppTemplateWizardAppearance createAppTemplateWizardAppearance() {
        return AppTemplateWizardAppearance.INSTANCE;
    }

    @Provides
    @Singleton
    public EventBus createEventBus() {
        return EventBus.getInstance();
    }

    @Provides
    @Singleton
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides
    @Singleton
    public UserSettings createUserSettings() {
        return UserSettings.getInstance();
    }

    @Provides
    public AppMetadataServiceFacade createAppMetadataService() {
        return ServicesInjector.INSTANCE.getAppMetadataService();
    }

    @Provides
    public AppTemplateServices createAppTemplateServices() {
        return ServicesInjector.INSTANCE.getAppTemplateServices();
    }

    @Provides
    public IplantAnnouncer createAnnouncer() {
        return IplantAnnouncer.getInstance();
    }

    @Override
    protected void configure() {
        bind(AppsEditorView.class).to(AppsEditorViewImpl.class);
        bind(AppEditorToolbar.class).to(AppEditorToolbarImpl.class);
        bind(AppsEditorView.Presenter.class).to(AppsEditorPresenterImpl.class);

        // Bind the appearance for the ArgumentGroupEditors
        bind(AppIntegrationErrorMessages.class).to(IplantErrorStrings.class);

        bind(AppTemplateForm.class).to(AppTemplateFormImpl.class);
        bind(AppLaunchView.class).to(AppLaunchViewImpl.class);
        bind(AppLaunchView.Presenter.class).to(AppLaunchPresenterImpl.class);
        bind(LaunchAnalysisView.class).to(LaunchAnalysisViewImpl.class);

        bind(AppTemplateForm.ArgumentGroupEditor.class).to(ArgumentGroupEditorAppEditorImpl.class);
        bind(AppTemplateForm.ArgumentEditorFactory.class).to(ArgumentEditorFactoryImpl.class);

        // Bind all the property editors
        bind(FileInputPropertyEditor.class);
        bind(FolderInputPropertyEditor.class);
        bind(MultiFileInputPropertyEditor.class);
        bind(TextInputPropertyEditor.class);
        bind(EnvVarPropertyEditor.class);
        bind(MultiLineTextInputPropertyEditor.class);
        bind(DecimalInputPropertyEditor.class);
        bind(IntegerInputPropertyEditor.class);
        bind(FlagArgumentPropertyEditor.class);
        bind(TextSelectionPropertyEditor.class);
        bind(IntegerSelectionPropertyEditor.class);
        bind(DecimalSelectionPropertyEditor.class);
        bind(TreeSelectionPropertyEditor.class);
        bind(InfoPropertyEditor.class);
        bind(FileOutputPropertyEditor.class);
        bind(FolderOutputPropertyEditor.class);
        bind(MultiFileOutputPropertyEditor.class);
        bind(ReferenceAnnotationPropertyEditor.class);
        bind(ReferenceGenomePropertyEditor.class);
        bind(ReferenceSequencePropertyEditor.class);
        bind(ArgumentGroupPropertyEditor.class);
        bind(ArgumentValidatorEditor.class);
    }
}
