package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PATCH;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.DataSourceList;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.models.apps.integration.FileInfoTypeList;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.models.apps.integration.ReferenceGenome;
import org.iplantc.de.client.models.apps.integration.ReferenceGenomeList;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppTemplateServicesImpl implements AppTemplateServices, AppMetadataServiceFacade {

    private final String APPS = "org.iplantc.services.apps";
    private final String ARG_PREVIEW = "org.iplantc.services.apps.argPreview";
    private final String DATA_SOURCES = "org.iplantc.apps.elements.dataSources";
    private final String FILE_INFO_TYPES = "org.iplantc.services.apps.elements.infoTypes";
    private final String REFERENCE_GENOMES = "org.iplantc.services.referenceGenomes";

    private final AppTemplateAutoBeanFactory factory;
    private static final Queue<AsyncCallback<List<DataSource>>> dataSourceQueue = Lists.newLinkedList();
    private static final Queue<AsyncCallback<List<FileInfoType>>> fileInfoTypeQueue = Lists.newLinkedList();
    private static final Queue<AsyncCallback<List<ReferenceGenome>>> refGenQueue = Lists.newLinkedList();
    private final List<DataSource> dataSourceList = Lists.newArrayList();
    private final DeployedComponentServices dcServices;
    private final List<FileInfoType> fileInfoTypeList = Lists.newArrayList();

    private final List<ReferenceGenome> refGenList = Lists.newArrayList();
    private final DiscEnvApiService deServiceFacade;
    private final DEProperties deProperties;

    Logger LOG = Logger.getLogger("Template service");

    @Inject
    public AppTemplateServicesImpl(final DiscEnvApiService deServiceFacade,
                                   final DEProperties deProperties,
                                   final DeployedComponentServices dcServices,
                                   final AppTemplateAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.dcServices = dcServices;
        this.factory = factory;
    }

    @Override
    public void cmdLinePreview(AppTemplate at, AsyncCallback<String> callback) {
        String address = ARG_PREVIEW;
        AppTemplate cleaned = doCmdLinePreviewCleanup(at);
        // SS: Service wont accept string values for dates
        cleaned.setEditedDate(null);
        Splittable split = appTemplateToSplittable(cleaned);
        String payload = split.getPayload();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppTemplate(HasId appId, AsyncCallback<AppTemplate> callback) {
        String address = APPS + "/" + appId.getId();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(factory, dcServices, callback));
    }

    @Override
    public AppTemplateAutoBeanFactory getAppTemplateFactory() {
        return factory;
    }

    @Override
    public void getAppTemplateForEdit(HasId appId, AsyncCallback<AppTemplate> callback) {
        String address = APPS + "/" + appId.getId() + "/ui";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(factory, dcServices, callback));
    }

    @Override
    public void getAppTemplatePreview(AppTemplate at, AsyncCallback<AppTemplate> callback) {
        String address = deProperties.getUnproctedMuleServiceBaseUrl() + "preview-template";
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(factory, dcServices, callback));
    }

    @Override
    public void getDataSources(AsyncCallback<List<DataSource>> callback) {
        if (!dataSourceList.isEmpty()) {
            callback.onSuccess(dataSourceList);
        } else {
            enqueueDataSourceCallback(callback);
        }
    }

    @Override
    public void getFileInfoTypes(AsyncCallback<List<FileInfoType>> callback) {
        if (!fileInfoTypeList.isEmpty()) {
            callback.onSuccess(fileInfoTypeList);
        } else {
            enqueueFileInfoTypeCallback(callback);
        }
    }

    @Override
    public void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback) {
        if (!refGenList.isEmpty()) {
            callback.onSuccess(refGenList);
        } else {
            enqueueRefGenomeCallback(callback);
        }
    }

    @Override
    public void launchAnalysis(AppTemplate at, JobExecution je, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "workspaces/" + je.getWorkspaceId() + "/newexperiment";  //$NON-NLS-2$
        Splittable assembledPayload = doAssembleLaunchAnalysisPayload(at, je);
        GWT.log("LaunchAnalysis Json:\n" + JsonUtil.prettyPrint(assembledPayload));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, assembledPayload.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void rerunAnalysis(HasId analysisId, AsyncCallback<AppTemplate> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "app-rerun-info/" + analysisId.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(factory, dcServices, callback));
    }

    @Override
    public void saveAndPublishAppTemplate(AppTemplate at, AsyncCallback<String> callback) {
        String address = APPS + "/" + at.getId();
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void createAppTemplate(AppTemplate at, AsyncCallback<String> callback) {
        String address = APPS;
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);

    }

    @Override
    public void updateAppLabels(AppTemplate at, AsyncCallback<String> callback) {
        String address = APPS +  "/" + at.getId();
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);

    }

    private Splittable appTemplateToSplittable(AppTemplate at) {
        AutoBean<AppTemplate> autoBean = AutoBeanUtils.getAutoBean(at);
        Splittable ret = AutoBeanCodex.encode(autoBean);
        LOG.log(Level.SEVERE, "template from bean-->" + ret.getPayload() + "");
        if (at.getTools() != null && at.getTools().size() > 0) {
            Splittable tools = StringQuoter.createIndexed();
            for (Tool t : at.getTools()) {
                AutoBean<Tool> toolBean = AutoBeanUtils.getAutoBean(t);
                Splittable sp = AutoBeanCodex.encode(toolBean);
                sp.assign(tools, tools.size());
            }
            tools.assign(ret, "tools");

        }
        // JDS Convert Argument.getValue() which contain any selected/checked *Selection types to only
        // contain their value.
        for (ArgumentGroup ag : at.getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                if (arg.getType().equals(ArgumentType.TreeSelection)) {
                    if ((arg.getSelectionItems() != null) && (arg.getSelectionItems().size() == 1)) {
                        SelectionItemGroup sig = AppTemplateUtils.selectionItemToSelectionItemGroup(arg.getSelectionItems().get(0));
                        Splittable split = AppTemplateUtils.getSelectedTreeItemsAsSplittable(sig);
                        arg.setValue(split);
                    }
                }
            }
        }
        return ret;
    }

    private Splittable doAssembleLaunchAnalysisPayload(AppTemplate at, JobExecution je) {
        Splittable assembledPayload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(je));
        Splittable configSplit = StringQuoter.createSplittable();
        for (ArgumentGroup ag : at.getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                Splittable value = arg.getValue();
                if ((value == null) && !arg.getType().equals(ArgumentType.TreeSelection)) {
                    continue;
                }
                if (AppTemplateUtils.isSimpleSelectionArgumentType(arg.getType())) {
                    value.assign(configSplit, arg.getId());
                } else if (AppTemplateUtils.isDiskResourceArgumentType(arg.getType()) && !arg.getType().equals(ArgumentType.MultiFileSelector)) {
                    value.get("path").assign(configSplit, arg.getId());
                } else if (arg.getType().equals(ArgumentType.MultiFileSelector) && value.isIndexed()) {
                    value.assign(configSplit, arg.getId());
                } else if (arg.getType().equals(ArgumentType.TreeSelection) && (arg.getSelectionItems() != null) && (arg.getSelectionItems().size() == 1)) {
                    SelectionItemGroup sig = AppTemplateUtils.selectionItemToSelectionItemGroup(arg.getSelectionItems().get(0));
                    Splittable sigSplit = AppTemplateUtils.getSelectedTreeItemsAsSplittable(sig);
                    sigSplit.assign(configSplit, arg.getId());
                } else {
                    value.assign(configSplit, arg.getId());
                }

            }
        }
        configSplit.assign(assembledPayload, "config");
        return assembledPayload;
    }

    private AppTemplate doCmdLinePreviewCleanup(AppTemplate templateToClean) {
        AppTemplate copy = AppTemplateUtils.copyAppTemplate(templateToClean);
        // JDS Transform any Argument's value which contains a full SelectionItem obj to the
        // SelectionItem's value
        for (ArgumentGroup ag : copy.getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                if (AppTemplateUtils.isSimpleSelectionArgumentType(arg.getType())) {

                    if ((arg.getValue() != null) && arg.getValue().isKeyed() && !arg.getValue().isUndefined("value")) {
                        arg.setValue(arg.getValue().get("value"));
                    } else {
                        arg.setValue(null);
                    }
                } else if (arg.getType().equals(ArgumentType.TreeSelection)) {
                    if ((arg.getSelectionItems() != null) && (arg.getSelectionItems().size() == 1)) {
                        SelectionItemGroup sig = AppTemplateUtils.selectionItemToSelectionItemGroup(arg.getSelectionItems().get(0));
                        List<SelectionItem> siList = AppTemplateUtils.getSelectedTreeItems(sig);
                        String retVal = "";
                        for (SelectionItem si : siList) {
                            if (si.getValue() != null) {
                                retVal += si.getValue() + " ";
                            }
                        }
                        arg.setValue(StringQuoter.create(retVal.trim()));
                    }
                } else if (arg.getType().equals(ArgumentType.EnvironmentVariable)) {
                    // Exclude environment variables from the command line
                    arg.setValue(null);
                    arg.setName("");
                } else if (AppTemplateUtils.isDiskResourceOutputType(arg.getType())) {
                    if (arg.getFileParameters().isImplicit()) {
                        arg.setValue(null);
                        arg.setName("");
                    }
                }
            }
        }

        return copy;
    }

    private void enqueueDataSourceCallback(final AsyncCallback<List<DataSource>> callback) {
        if (dataSourceQueue.isEmpty()) {
            String address = DATA_SOURCES;
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
            deServiceFacade.getServiceData(wrapper, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(String result) {
                    DataSourceList dsList = AutoBeanCodex.decode(factory, DataSourceList.class, result).as();
                    dataSourceList.clear();
                    dataSourceList.addAll(dsList.getDataSources());

                    while (!dataSourceQueue.isEmpty()) {
                        dataSourceQueue.remove().onSuccess(dataSourceList);
                    }
                }
            });

        }
        dataSourceQueue.add(callback);
    }

    private void enqueueFileInfoTypeCallback(final AsyncCallback<List<FileInfoType>> callback) {
        if (fileInfoTypeQueue.isEmpty()) {
            String address = FILE_INFO_TYPES;
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

            deServiceFacade.getServiceData(wrapper, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(String result) {
                    FileInfoTypeList fitListWrapper = AutoBeanCodex.decode(factory, FileInfoTypeList.class, result).as();

                    fileInfoTypeList.clear();
                    fileInfoTypeList.addAll(fitListWrapper.getFileInfoTypes());

                    while (!fileInfoTypeQueue.isEmpty()) {
                        fileInfoTypeQueue.remove().onSuccess(fileInfoTypeList);
                    }
                }
            });
        }
        fileInfoTypeQueue.add(callback);

    }

    private void enqueueRefGenomeCallback(final AsyncCallback<List<ReferenceGenome>> callback) {
        if (refGenQueue.isEmpty()) {
            String address = REFERENCE_GENOMES;
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
            deServiceFacade.getServiceData(wrapper, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(String result) {
                    ReferenceGenomeList rgList = AutoBeanCodex.decode(factory, ReferenceGenomeList.class, result).as();
                    refGenList.clear();
                    refGenList.addAll(rgList.getReferenceGenomes());

                    while (!refGenQueue.isEmpty()) {
                        refGenQueue.remove().onSuccess(refGenList);
                    }
                }
            });

        }
        refGenQueue.add(callback);
    }

}
