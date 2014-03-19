package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.DELETE;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.callbacks.NotificationCallback;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

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
    private final DEServiceFacade deServiceFacade;
    private final UserInfo userInfo;

    @Inject
    public MessageServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties, final NotificationAutoBeanFactory notesFactory, final UserInfo userInfo) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.notesFactory = notesFactory;
        this.userInfo = userInfo;
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getNotifications(int, int, java.lang.String, java.lang.String, C)
     */
    @Override
    public <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback) {
        String address = deProperties.getMuleServiceBaseUrl();

        StringBuilder builder = new StringBuilder("notifications/messages?limit=" + limit + "&offset="
                + offset);
        if (filter != null && !filter.isEmpty()) {
            builder.append("&filter=" + URL.encodeQueryString(filter));
        }

        if (sortDir != null && !sortDir.isEmpty() && !sortDir.equalsIgnoreCase("NONE")) {
            builder.append("&sortDir=" + URL.encodeQueryString(sortDir));
        }

        address = address + builder.toString();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getRecentMessages(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getRecentMessages(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#markAsSeen(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void markAsSeen(final JSONObject seenIds, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/seen"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                seenIds.toString());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#deleteMessages(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteMessages(final JSONObject deleteIds, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/delete"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                deleteIds.toString());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getRecentMessages(C)
     */
    @Override
    public <C extends NotificationCallback> void getRecentMessages(C callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);

    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#getMessageCounts(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getMessageCounts(final AsyncCallback<Counts> callback) {
        final String addr = deProperties.getMuleServiceBaseUrl()
                + "notifications/count-messages?seen=false"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, addr);
        final AsyncCallback<String> convCB = new CountsCB(callback, notesFactory);
        deServiceFacade.getServiceData(wrapper, convCB);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#deleteAll(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteAll(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "notifications/delete-all"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.MessageServiceFacade#acknowledgeAll(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void acknowledgeAll(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                + "notifications/mark-all-seen"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
 userInfo.getUsername());

        deServiceFacade.getServiceData(wrapper, callback);
    }
    
}
