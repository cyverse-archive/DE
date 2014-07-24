package org.iplantc.de.client.notifications.util;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestHistory;
import org.iplantc.de.client.notifications.events.DeleteNotificationsUpdateEvent;
import org.iplantc.de.client.notifications.views.dialogs.ToolRequestHistoryDialog;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.AnalysisWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * helps with notifications for the user.
 *
 *
 * @author lenards, sriram
 *
 */
public class NotificationHelper {
    private static NotificationHelper instance = null;

    private int total;

    private final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private final AnalysesAutoBeanFactory analysesFactory = GWT.create(AnalysesAutoBeanFactory.class);
    private final NotificationAutoBeanFactory notificationFactory = GWT
            .create(NotificationAutoBeanFactory.class);

    private NotificationHelper() {
    }


    /** View a notification */
    public void view(NotificationMessage msg) {
        if (msg == null) {
            return;
        }

        // did we get a category?
        NotificationCategory category = msg.getCategory();
        if (category == null) {
            return;
        }

        // did we get a context to execute?
        String context = msg.getContext();
        if (context == null) {
            return;
        }

        switch (category) {
            case DATA:
                // execute data context
                File file = AutoBeanCodex.decode(drFactory, File.class, context).as();
                ArrayList<HasId> selectedResources = Lists.newArrayList();
                selectedResources.add(file);

                DiskResourceWindowConfig dataWindowConfig = ConfigFactory
                        .diskResourceWindowConfig(false);
                HasPath folder = DiskResourceUtil.getFolderPathFromFile(file);
                dataWindowConfig.setSelectedFolder(folder);
                dataWindowConfig.setSelectedDiskResources(selectedResources);
                EventBus.getInstance().fireEvent(new WindowShowRequestEvent(dataWindowConfig, true));

                break;

            case ANALYSIS:
                AutoBean<Analysis> hAb = AutoBeanCodex.decode(analysesFactory, Analysis.class, context);

                AnalysisWindowConfig analysisWindowConfig = ConfigFactory.analysisWindowConfig();
                analysisWindowConfig.setSelectedAnalyses(Lists.newArrayList(hAb.as()));
                EventBus.getInstance().fireEvent(new WindowShowRequestEvent(analysisWindowConfig, true));

                break;

            case TOOLREQUEST:
                PayloadToolRequest toolRequest = AutoBeanCodex.decode(notificationFactory,
                        PayloadToolRequest.class, context).as();

                List<ToolRequestHistory> history = toolRequest.getHistory();

                Logger logger = Logger.getLogger("NameOfYourLogger");
                logger.log(Level.SEVERE, "history size==>" + history.size());

                ToolRequestHistoryDialog dlg = new ToolRequestHistoryDialog(toolRequest.getName(),
                        history);
                dlg.show();

                break;

            default:
                break;
        }
    }

    /**
     * Return the shared, singleton instance of the manager.
     *
     * @return a singleton reference to the notification manager.
     */
    public static NotificationHelper getInstance() {
        if (instance == null) {
            instance = new NotificationHelper();
        }

        return instance;
    }

    private void doDelete(final List<NotificationMessage> notifications, final JSONObject json,
            final Command callback) {
        if (json != null) {
            ServicesInjector.INSTANCE.getMessageServiceFacade().deleteMessages(json, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.notificationDeletFail(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    if (callback != null) {
                        callback.execute();
                        DeleteNotificationsUpdateEvent event = new DeleteNotificationsUpdateEvent(
                                notifications);
                        EventBus.getInstance().fireEvent(event);
                    }
                }
            });
        }
    }

    /**
     * Mark notifications as seen
     *
     */
    public void markAsSeen(List<NotificationMessage> list) {
        if (list != null && list.size() > 0) {
            JSONArray arr = buildSeenServiceRequestBody(list);

            if (arr.size() > 0) {
                JSONObject obj = new JSONObject();
                obj.put("uuids", arr);
                /*ServicesInjector.INSTANCE.getMessageServiceFacade().markAsSeen(obj, new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        JSONObject obj = JsonUtil.getObject(result);
                        int new_count = Integer.parseInt(JsonUtil.getString(obj, "count"));
                        // fire update of the new unseen count;
                        NotificationCountUpdateEvent event = new NotificationCountUpdateEvent(new_count);
                        EventBus.getInstance().fireEvent(event);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(caught);
                    }
                });*/
            }
        }
    }

    private JSONArray buildSeenServiceRequestBody(List<NotificationMessage> list) {
        JSONArray arr = new JSONArray();
        int i = 0;

        for (NotificationMessage n : list) {
            if (!n.isSeen()) {
                arr.set(i++, new JSONString(n.getId()));
                n.setSeen(true);
            }
        }
        return arr;
    }

    /**
     * Delete a list of notifications.
     *
     * @param notifications notifications to be deleted.
     */
    public void delete(final List<NotificationMessage> notifications, Command callback) {
        // do we have any notifications to delete?
        if (notifications != null && !notifications.isEmpty()) {
            JSONObject obj = new JSONObject();
            JSONArray arr = new JSONArray();
            int i = 0;
            for (NotificationMessage n : notifications) {
                arr.set(i++, new JSONString(n.getId()));
            }
            obj.put("uuids", arr);

            doDelete(notifications, obj, callback);
        }
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }
}
