package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.models.notifications.payload.PayloadData;
import org.iplantc.de.client.util.CommonModelUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class NotificationCallbackConverter extends AsyncCallbackConverter<String, List<Notification>> {
    private final NotificationAutoBeanFactory notFactory;

    public NotificationCallbackConverter(AsyncCallback<List<Notification>> callback,
                                         final NotificationAutoBeanFactory notFactory) {
        super(callback);
        this.notFactory = notFactory;
    }

    @Override
    protected List<Notification> convertFrom(String object) {
        AutoBean<NotificationList> bean = AutoBeanCodex.decode(notFactory, NotificationList.class, object);
        List<Notification> notifications = bean.as().getNotifications();
        for (Notification n : notifications) {
            NotificationMessage msg = n.getMessage();
            msg.setSeen(n.isSeen());
            msg.setCategory(NotificationCategory.fromTypeString(n.getCategory()));
            Splittable payload = n.getNotificationPayload();

            if (payload == null) {
                continue;
            }

            switch (msg.getCategory()) {
                case ALL:
                    GWT.log("ALL Analysis category");
                    break;

                case ANALYSIS:
                    PayloadAnalysis analysisPayload = AutoBeanCodex.decode(notFactory,
                            PayloadAnalysis.class, payload).as();
                    String analysisAction = analysisPayload.getAction();

                    if ("job_status_change".equals(analysisAction)) {
                        msg.setContext(payload.getPayload());
                    } else {
                        GWT.log("Unhandled Analysis action type!!");
                    }
                    break;

                case DATA:
                    PayloadData dataPayload = AutoBeanCodex.decode(notFactory,
                            PayloadData.class, payload).as();
                    String dataAction = dataPayload.getAction();

                    if ("file_uploaded".equals(dataAction)) {
                        AutoBean<File> fileAb = AutoBeanUtils.getAutoBean(dataPayload.getData());
                        msg.setContext(AutoBeanCodex.encode(fileAb).getPayload());
                    } else if ("share".equals(dataAction) || "unshare".equals(dataAction)) {
                        List<String> paths = dataPayload.getPaths();
                        if (paths != null && !paths.isEmpty()) {
                            String path = paths.get(0);
                            Splittable file = CommonModelUtils.getInstance().createHasPathSplittableFromString(path);
                            msg.setContext(file.getPayload());
                        }
                    }
                    break;

                case NEW:
                    GWT.log("NEW  category");
                    break;

                case SYSTEM:
                    GWT.log("SYSTEM  category");
                    break;

                case PERMANENTIDREQUEST:
                    GWT.log("Permanent Id request category");
                    msg.setContext(payload.getPayload());
                    break;
                case TOOLREQUEST:
                    GWT.log("TOOLREQUEST  category");
                    msg.setContext(payload.getPayload());
                    break;

                default:
                    break;
            }
        }

        return notifications;
    }
}
