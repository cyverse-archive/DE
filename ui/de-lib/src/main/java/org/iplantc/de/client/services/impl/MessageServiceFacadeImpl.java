package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.PermIdRequestUserServiceFacade;
import org.iplantc.de.client.services.callbacks.NotificationCallback;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.NotificationCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.Collections;
import java.util.List;

/**
 * Provides access to remote services to acquire messages and notifications.
 *
 * @author amuir
 *
 */
public class MessageServiceFacadeImpl implements MessageServiceFacade {

    private static final class CountsCB extends AsyncCallbackConverter<String, Counts> {
        private final NotificationAutoBeanFactory factory;

        public CountsCB(final AsyncCallback<Counts> callback, final NotificationAutoBeanFactory factory) {
            super(callback);
            this.factory = factory;
        }

        @Override
        protected Counts convertFrom(final String json) {
            return AutoBeanCodex.decode(factory, Counts.class, json).as();
        }
    }

    private final NotificationAutoBeanFactory notesFactory;
    private final DEProperties deProperties;
    private final DiscEnvApiService deServiceFacade;
    private final UserInfo userInfo;
    @Inject DiskResourceUtil diskResourceUtil;

    @Inject
    public MessageServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                    final DEProperties deProperties,
                                    final NotificationAutoBeanFactory notesFactory,
                                    final UserInfo userInfo) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.notesFactory = notesFactory;
        this.userInfo = userInfo;
    }

    @Override
    public <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback) {
        String address = deProperties.getMuleServiceBaseUrl();

        StringBuilder builder = new StringBuilder("notifications/messages?limit=" + limit + "&offset="
                                                      + offset);
        if (filter != null && !filter.isEmpty()) {
            builder.append("&filter=").append(URL.encodeQueryString(filter));
        }

        if (sortDir != null && !sortDir.isEmpty() && !sortDir.equalsIgnoreCase("NONE")) {
            builder.append("&sortDir=").append(URL.encodeQueryString(sortDir));
        }

        address = address + builder.toString();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getRecentMessages(AsyncCallback<NotificationList> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                             + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, new NotificationCallbackConverter(callback, notesFactory));
    }

    @Override
    public void markAsSeen(HasId id, AsyncCallback<String> callback) {
        markAsSeen(Collections.singletonList(id), callback);
    }

    @Override
    public void markAsSeen(final List<HasId> seenIds, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/seen"; //$NON-NLS-1$
        Splittable payload = StringQuoter.createSplittable();
        diskResourceUtil.createStringIdListSplittable(seenIds).assign(payload, "uuids");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload.getPayload());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteMessages(final JSONObject deleteIds, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/delete"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            deleteIds.toString());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getMessageCounts(final AsyncCallback<Counts> callback) {
        final String addr = deProperties.getMuleServiceBaseUrl()
                                + "notifications/count-messages?seen=false"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, addr);
        final AsyncCallback<String> convCB = new CountsCB(callback, notesFactory);
        deServiceFacade.getServiceData(wrapper, convCB);
    }

    @Override
    public void deleteAll(NotificationCategory category, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/delete-all"; //$NON-NLS-1$

        if (NotificationCategory.ALL != category) {
            address += "?filter=" + URL.encodeQueryString(category.toString().toLowerCase());
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void markAllNotificationsSeen(AsyncCallback<Void> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                             + "notifications/mark-all-seen"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            userInfo.getUsername());

        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getPermanentIdRequestStatusHistory(String id, AsyncCallback<String> callback) {
        String address = PermIdRequestUserServiceFacade.PERMID_REQUEST + "/" + id;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
