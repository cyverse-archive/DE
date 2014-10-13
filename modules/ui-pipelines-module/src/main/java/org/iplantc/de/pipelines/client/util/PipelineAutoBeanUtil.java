package org.iplantc.de.pipelines.client.util;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDataObject;
import org.iplantc.de.client.models.apps.DataObject;
import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.client.models.pipelines.PipelineApp;
import org.iplantc.de.client.models.pipelines.PipelineAppData;
import org.iplantc.de.client.models.pipelines.PipelineAppMapping;
import org.iplantc.de.client.models.pipelines.PipelineAutoBeanFactory;
import org.iplantc.de.client.models.pipelines.ServicePipeline;
import org.iplantc.de.client.models.pipelines.ServicePipelineApp;
import org.iplantc.de.client.models.pipelines.ServicePipelineAutoBeanFactory;
import org.iplantc.de.client.models.pipelines.ServicePipelineMapping;
import org.iplantc.de.client.models.pipelines.ServicePipelineStep;
import org.iplantc.de.client.models.pipelines.ServicePipelineTask;
import org.iplantc.de.client.models.pipelines.ServiceSaveResponse;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A Utility class for Pipeline AutoBeans and converting them to/from the service JSON.
 *
 * @author psarando
 *
 */
public class PipelineAutoBeanUtil {

    private final PipelineAutoBeanFactory factory = GWT.create(PipelineAutoBeanFactory.class);
    private final ServicePipelineAutoBeanFactory serviceFactory = GWT
            .create(ServicePipelineAutoBeanFactory.class);

    private static final String AUTO_GEN_ID = "auto-gen"; //$NON-NLS-1$

    /**
     * @return A singleton instance of the PipelineAutoBeanFactory.
     */
    public PipelineAutoBeanFactory getPipelineFactory() {
        return factory;
    }

    /**
     * Clones the data contained in the given App into a PipelineApp, calling the App service to fetch
     * the input and output data objects for inclusion in the PipelineApp result.
     *
     * @param app Must be eligible for pipelines, otherwise the callback's onFailure method is called.
     * @param callback Receives the PipelineApp result on success, cloned from the data contained in the
     *            given app plus the data objects returned from the App service.
     */
    public void appToPipelineApp(final App app, final AsyncCallback<PipelineApp> callback) {
        if (app == null) {
            callback.onFailure(new NullPointerException());
            return;
        }

        if (!app.getPipelineEligibility().isValid()) {
            callback.onFailure(new Exception(app.getPipelineEligibility().getReason()));
            return;
        }

        ServicesInjector.INSTANCE.getAppUserServiceFacade().getDataObjectsForApp(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                AutoBean<App> appBean = AutoBeanUtils.getAutoBean(app);

                // Clone the App AutoBean so we don't modify the original.
                Splittable appJson = AutoBeanCodex.encode(appBean);
                appBean = AutoBeanCodex.decode(appBean.getFactory(), App.class, appJson.getPayload());

                Splittable json = StringQuoter.split(result);
                AutoBeanCodex.decodeInto(json, appBean);

                callback.onSuccess(appBeanToPipelineApp(appBean));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.dataObjectsRetrieveError(), caught);
                callback.onFailure(caught);
            }
        });
    }

    /**
     * Converts an App AutoBean into a PipelineApp.
     *
     * @param appBean
     * @return A PipelineApp cloned from the data contained in the given appBean.
     */
    private PipelineApp appBeanToPipelineApp(AutoBean<App> appBean) {
        if (appBean == null) {
            return null;
        }

        App app = appBean.as();

        PipelineApp ret = AutoBeanCodex.decode(factory, PipelineApp.class,
                AutoBeanCodex.encode(appBean).getPayload()).as();

        ret.setTaskId(app.getId());

        ret.setInputs(appDataObjectsToPipelineAppData(app.getInputs()));
        ret.setOutputs(appDataObjectsToPipelineAppData(app.getOutputs()));

        return ret;
    }

    private List<PipelineAppData> appDataObjectsToPipelineAppData(List<AppDataObject> appDataObjs) {
        List<PipelineAppData> ret = new ArrayList<PipelineAppData>();
        if (appDataObjs != null) {
            for (AppDataObject appDataObj : appDataObjs) {
                PipelineAppData appData = dataObjectToPipelineAppData(appDataObj.getDataObject());

                if (appData != null) {
                    ret.add(appData);
                }
            }
        }

        return ret;
    }

    private PipelineAppData dataObjectToPipelineAppData(DataObject dataObject) {
        AutoBean<DataObject> dataBean = AutoBeanUtils.getAutoBean(dataObject);
        if (dataBean == null) {
            return null;
        }

        return AutoBeanCodex.decode(factory, PipelineAppData.class,
                AutoBeanCodex.encode(dataBean).getPayload()).as();
    }

    /**
     * Get the JSON of the given pipeline required for publishing.
     *
     * @return JSON string required for publishing the given pipeline.
     */
    public String getPublishJson(Pipeline pipeline) {
        if (pipeline == null) {
            return null;
        }

        List<PipelineApp> steps = pipeline.getApps();

        if (steps == null) {
            return null;
        }

        String id = pipeline.getId();
        if (Strings.isNullOrEmpty(id)) {
            id = AUTO_GEN_ID;
        }

        ServicePipelineApp pipelineApp = serviceFactory.servicePipelineAnalysis().as();
        pipelineApp.setId(id);
        pipelineApp.setAppName(pipeline.getName());
        pipelineApp.setDescription(pipeline.getDescription());

        List<ServicePipelineStep> publishSteps = new ArrayList<ServicePipelineStep>();
        List<ServicePipelineMapping> publishMappings = new ArrayList<ServicePipelineMapping>();

        for (PipelineApp app : pipeline.getApps()) {
            // Convert the Pipeline step to a service step.
            ServicePipelineStep step = getServiceStep(app);

            if (step != null) {
                publishSteps.add(step);

                // The first step should not have any input mappings.
                if (app.getStep() > 0) {
                    List<PipelineAppMapping> appMappings = app.getMappings();

                    if (appMappings != null) {
                        // Convert the Pipeline output->input mappings to service input->output mappings.
                        int targetStepId = app.getStep();

                        for (PipelineAppMapping mapping : appMappings) {
                            ServicePipelineMapping publishMapping = getServiceMapping(targetStepId,
                                    mapping);

                            if (publishMapping != null) {
                                publishMappings.add(publishMapping);
                            }
                        }
                    }
                }
            }
        }

        pipelineApp.setSteps(publishSteps);
        pipelineApp.setMappings(publishMappings);

        AutoBean<ServicePipeline> servicePipeline = serviceFactory.servicePipeline();
        servicePipeline.as().setApps(Collections.singletonList(pipelineApp));

        return AutoBeanCodex.encode(servicePipeline).getPayload();
    }

    /**
     * Gets a ServicePipelineStep representing the given PipelineApp step.
     *
     * @return The PipelineApp as a workflow ServicePipelineStep.
     */
    private ServicePipelineStep getServiceStep(PipelineApp app) {
        ServicePipelineStep step = serviceFactory.servicePipelineStep().as();
        step.setTaskId(app.getTaskId());
        step.setAppType(app.getAppType());
        step.setName(app.getName());
        step.setDescription(app.getName());
        return step;
    }

    /**
     * Gets the given App's workflow step name, based on its position in the workflow and its ID.
     *
     * @param app
     * @return the PipelineApp's step name.
     */
    // public String getStepName(PipelineApp app) {
    //        return app == null ? "" : getStepName(app.getStep(), app.getTaskId()); //$NON-NLS-1$
    // }

    /**
     * Gets a workflow step name, based on the given workflow step position and App ID.
     *
     * @param step A position in the workflow.
     * @param id An App ID.
     * @return A workflow step name.
     */
    public String getStepName(int step, String id) {
        return Format.substitute("step_{0}_{1}", step, id); //$NON-NLS-1$
    }

    /**
     * Formats the output->input mappings for the given source PipelineAppMapping to the targetStepName,
     * as a ServicePipelineMapping for the Import Workflow service.
     *
     * @return A ServicePipelineMapping of input->output mappings.
     */
    private ServicePipelineMapping getServiceMapping(int targetStepId,
            PipelineAppMapping sourceStepMapping) {
        if (sourceStepMapping != null) {
            Map<String, String> stepMap = sourceStepMapping.getMap();
            if (stepMap != null) {
                // Build the service input->output mapping.
                Map<String, String> map = new FastMap<String>();

                for (String inputId : stepMap.keySet()) {
                    String outputId = stepMap.get(inputId);

                    if (!Strings.isNullOrEmpty(outputId)) {
                        map.put(inputId, outputId);
                    }
                }

                // Ensure at least one input->output is set for sourceStepName in the service mapping.
                if (!map.keySet().isEmpty()) {
                    // Return the mappings from sourceStepName to targetStepName.
                    ServicePipelineMapping mapping = serviceFactory.servicePipelineMapping().as();

                    // check bug for javascript 0
                    mapping.setSourceStep(sourceStepMapping.getStep());
                    mapping.setTargetStep(targetStepId);
                    mapping.setMap(map);

                    return mapping;
                }
            }
        }

        // No mappings were found in the given sourceStepMapping.
        return null;
    }

    /**
     * Parses the Pipeline's ID from the save service call response.
     * 
     * @param response The JSON response from the Pipeline save service call.
     * @return The ID of the saved Pipeline.
     */
    public String parseServiceSaveResponseId(String response) {
        AutoBean<ServiceSaveResponse> responseBean = AutoBeanCodex.decode(serviceFactory,
                ServiceSaveResponse.class, response);

        if (responseBean != null) {
            ServiceSaveResponse saveResponse = responseBean.as();

            List<String> ids = saveResponse.getWorkflowIds();
            if (ids != null && !ids.isEmpty()) {
                return ids.get(0);
            }
        }

        return null;
    }

    /**
     * Converts a workflow JSON object into a Pipeline object.
     *
     * @param serviceJson A JSON object representing a workflow.
     * @return A Pipeline object representing the workflow.
     */
    public Pipeline serviceJsonToPipeline(Splittable serviceJson) {
        AutoBean<ServicePipeline> serviceBean = AutoBeanCodex.decode(serviceFactory,
                ServicePipeline.class, serviceJson);

        if (serviceBean != null) {
            ServicePipeline servicePipeline = serviceBean.as();

            List<ServicePipelineApp> analyses = servicePipeline.getApps();
            if (analyses != null && !analyses.isEmpty()) {
                return serviceAnalysisToPipeline(analyses.get(0), servicePipeline.getTasks());
            }
        }

        return null;
    }

    private Pipeline serviceAnalysisToPipeline(ServicePipelineApp app, List<ServicePipelineTask> tasks) {
        Pipeline ret = factory.pipeline().as();

        // Set high-level details.
        ret.setId(app.getId());
        ret.setName(app.getAppName());
        ret.setDescription(app.getDescription());
        ret.setApps(new ArrayList<PipelineApp>());

        // Create quick lookups for each template and PipelineApp created.
        FastMap<ServicePipelineTask> taskLookUp = new FastMap<ServicePipelineTask>();
        FastMap<PipelineApp> stepLookup = new FastMap<PipelineApp>();

        if (tasks != null) {
            for (ServicePipelineTask task : tasks) {
                taskLookUp.put(task.getId(), task);
            }
        }

        // Convert Steps to PipelineApps.
        List<ServicePipelineStep> steps = app.getSteps();
        if (steps != null) {
            int stepPosition = 0;
            for (ServicePipelineStep step : steps) {
                PipelineApp pipelineApp = serviceStepToPipelineApp(step, stepPosition, taskLookUp);

                ret.getApps().add(pipelineApp);
                stepLookup.put(stepPosition + "", pipelineApp);
                stepPosition++;
            }
        }

        // Convert output to input mappings.
        List<ServicePipelineMapping> mappings = app.getMappings();
        if (mappings != null) {
            for (ServicePipelineMapping svcMapping : mappings) {
                convertServicePipelineMapping(svcMapping, stepLookup);
            }
        }

        return ret;
    }

    private PipelineApp serviceStepToPipelineApp(ServicePipelineStep step, int stepPosition,
            FastMap<ServicePipelineTask> templateLookup) {
        PipelineApp ret = AutoBeanCodex.decode(factory, PipelineApp.class,
                AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(step)).getPayload()).as();

        ret.setStep(stepPosition);

        ServicePipelineTask template = templateLookup.get(ret.getTaskId());
        if (template != null) {
            ret.setName(template.getName());
            ret.setDescription(template.getDescription());
            ret.setInputs(templateDataObjectsToPipelineAppData(template.getInputs()));
            ret.setOutputs(templateDataObjectsToPipelineAppData(template.getOutputs()));
        }

        return ret;
    }

    private List<PipelineAppData> templateDataObjectsToPipelineAppData(List<DataObject> appDataObjs) {
        List<PipelineAppData> ret = new ArrayList<PipelineAppData>();
        if (appDataObjs != null) {
            for (DataObject dataObj : appDataObjs) {
                PipelineAppData appData = dataObjectToPipelineAppData(dataObj);

                if (appData != null) {
                    ret.add(appData);
                }
            }
        }

        return ret;
    }

    private void convertServicePipelineMapping(ServicePipelineMapping svcMapping,
            FastMap<PipelineApp> stepLookup) {
        Map<String, String> map = svcMapping.getMap();

        if (map != null) {
            PipelineApp targetStep = stepLookup.get(svcMapping.getTargetStep() + "");
            PipelineApp sourceStep = stepLookup.get(svcMapping.getSourceStep() + "");

            for (String inputId : map.keySet()) {
                String outputId = map.get(inputId);
                setInputOutputMapping(targetStep, inputId, sourceStep, outputId);
            }
        }
    }

    /**
     * Sets a mapping for targetStep's Input DataObject, with the given targetInputId, to sourceStep's
     * Output DataObject with the given sourceOutputId. A null sourceOutputId will clear the mapping for
     * the given targetInputId.
     *
     * @param targetStep
     * @param targetInputId
     * @param sourceStep
     * @param sourceOutputId
     */
    public void setInputOutputMapping(PipelineApp targetStep, String targetInputId,
            PipelineApp sourceStep, String sourceOutputId) {
        if (targetStep == null || sourceStep == null) {
            return;
        }


        // Find the output->input mappings for sourceStepName.
        FastMap<PipelineAppMapping> mapInputsOutputs = getTargetMappings(targetStep);
        PipelineAppMapping targetAppMapping = mapInputsOutputs.get(sourceStep.getStep());

        if (targetAppMapping == null) {
            // There are no output mappings from this sourceStepName yet.
            if (Strings.isNullOrEmpty(sourceOutputId)) {
                // nothing to do in order to clear this mapping.
                return;
            }

            // Create a new output->input mapping for sourceStepName.
            targetAppMapping = factory.appMapping().as();
            targetAppMapping.setStep(sourceStep.getStep());
            targetAppMapping.setId(sourceStep.getTaskId());
            targetAppMapping.setMap(new FastMap<String>());

            mapInputsOutputs.put(sourceStep.getStep() + "", targetAppMapping);
        }

        // TODO validate targetInputId belongs to one of this App's Inputs?
        Map<String, String> map = targetAppMapping.getMap();
        if (Strings.isNullOrEmpty(sourceOutputId)) {
            // clear the mapping for this Input ID.
            map.put(targetInputId, null);
        } else {
            // Map sourceOutputId to this App's given targetInputId.
            map.put(targetInputId, sourceOutputId);
        }

        targetStep.setMappings(new ArrayList<PipelineAppMapping>(mapInputsOutputs.values()));
    }

    private FastMap<PipelineAppMapping> getTargetMappings(PipelineApp targetStep) {
        AutoBean<PipelineApp> targetBean = AutoBeanUtils.getAutoBean(targetStep);
        FastMap<PipelineAppMapping> mapInputsOutputs = targetBean.getTag("stepMappings"); //$NON-NLS-1$

        if (mapInputsOutputs == null) {
            mapInputsOutputs = new FastMap<PipelineAppMapping>();
            targetBean.setTag("stepMappings", mapInputsOutputs); //$NON-NLS-1$

            List<PipelineAppMapping> appMappings = targetStep.getMappings();
            if (appMappings != null) {
                for (PipelineAppMapping mapping : appMappings) {
                    mapInputsOutputs.put(mapping.getStep() + "", mapping);
                }
            }
        }

        return mapInputsOutputs;
    }

    /**
     * Removes all output to input mappings to the given PipelineApp step.
     *
     * @param targetStep
     */
    public void resetAppMappings(PipelineApp targetStep) {
        AutoBean<PipelineApp> targetBean = AutoBeanUtils.getAutoBean(targetStep);
        targetBean.setTag("stepMappings", null); //$NON-NLS-1$

        targetStep.setMappings(null);
    }
}
