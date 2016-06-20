package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.models.notifications.payload.PayloadAppsList;
import org.iplantc.de.client.models.notifications.payload.PayloadData;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.desktop.client.presenter.util.NotificationUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class NotificationCallbackConverter extends AsyncCallbackConverter<String, NotificationList> {
    private final NotificationAutoBeanFactory notFactory;

    public NotificationCallbackConverter(AsyncCallback<NotificationList> callback,
                                         final NotificationAutoBeanFactory notFactory) {
        super(callback);
        this.notFactory = notFactory;
    }

    @Override
    protected NotificationList convertFrom(String object) {
        AutoBean<NotificationList> bean = AutoBeanCodex.decode(notFactory, NotificationList.class, object);
        NotificationList nList = bean.as();
        List<Notification> notifications = nList.getNotifications();
        for (Notification n : notifications) {
            NotificationMessage msg = NotificationUtil.getMessage(n, notFactory);
            n.setMessage(msg);
        }
      return nList;
    }
}
