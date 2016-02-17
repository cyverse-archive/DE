package org.iplantc.de.notifications.client.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.callbacks.NotificationCallback;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.notifications.client.events.DeleteNotificationsUpdateEvent;
import org.iplantc.de.notifications.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.notifications.client.views.NotificationToolbarView;
import org.iplantc.de.notifications.client.views.NotificationToolbarViewImpl;
import org.iplantc.de.notifications.client.views.NotificationView;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A presenter for notification window
 *
 * @author sriram
 */
public class NotificationPresenterImpl implements NotificationView.Presenter, NotificationToolbarView.Presenter {

    private final class NotificationServiceCallback extends NotificationCallback {
        private final AsyncCallback<PagingLoadResult<NotificationMessage>> callback;
        private final PagingLoadConfig loadConfig;

        private NotificationServiceCallback(PagingLoadConfig loadConfig,
                                            AsyncCallback<PagingLoadResult<NotificationMessage>> callback) {
            this.loadConfig = loadConfig;
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(String result) {
            super.onSuccess(result);
            Splittable splitResult = StringQuoter.split(result);
            int total = 0;

            if (splitResult.get("total") != null) {
                total = Integer.parseInt(splitResult.get("total").asString());
            }

            List<NotificationMessage> messages = Lists.newArrayList();
            for (Notification n : this.getNotifications()) {
                messages.add(n.getMessage());
            }

            callbackResult = new PagingLoadResultBean<>(messages, total,
                                                        loadConfig.getOffset());
            callback.onSuccess(callbackResult);

            List<HasId> hasIds = Lists.newArrayList();
            for (NotificationMessage nm : messages) {
                hasIds.add(nm);
            }
            messageServiceFacade.markAsSeen(hasIds, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }

                @Override
                public void onSuccess(String result1) {
                    JSONObject obj = jsonUtil.getObject(result1);
                    int new_count = Integer.parseInt(jsonUtil.getString(obj, "count"));
                    // fire update of the new unseen count;
                    eventBus.fireEvent(new NotificationCountUpdateEvent(new_count));
                }
            });
        }
    }

    private final IplantDisplayStrings displayStrings;

    private final IplantErrorStrings errorStrings;

    private final EventBus eventBus;
    private final MessageServiceFacade messageServiceFacade;
    private final NotificationToolbarView toolbar;
    private final NotificationView view;
    private PagingLoadResult<NotificationMessage> callbackResult;
    private NotificationCategory currentCategory;
    private final JsonUtil jsonUtil;

    public NotificationPresenterImpl(final NotificationView view) {
        this.view = view;
        this.errorStrings = I18N.ERROR;
        this.displayStrings = I18N.DISPLAY;
        this.messageServiceFacade = ServicesInjector.INSTANCE.getMessageServiceFacade();
        this.eventBus = EventBus.getInstance();
        currentCategory = NotificationCategory.ALL;
        toolbar = new NotificationToolbarViewImpl();
        this.jsonUtil = JsonUtil.getInstance();
        toolbar.setPresenter(this);
        view.setNorthWidget(toolbar);
        this.view.setPresenter(this);
        setRefreshButton(view.getRefreshButton());
        // set default cat
    }

    @Override
    public FilterPagingLoadConfig buildDefaultLoadConfig() {
        FilterPagingLoadConfig config = new FilterPagingLoadConfigBean();
        config.setLimit(10);

        SortInfo info = new SortInfoBean("timestamp", SortDir.DESC);
        List<SortInfo> sortInfo = new ArrayList<>();
        sortInfo.add(info);
        config.setSortInfo(sortInfo);

        FilterConfig filterBean = new FilterConfigBean();
        if (!NotificationCategory.ALL.equals(currentCategory)) {
            filterBean.setField(currentCategory.toString());
        }

        List<FilterConfig> filters = new ArrayList<>();
        filters.add(filterBean);
        config.setFilters(filters);

        return config;
    }

    @Override
    public void filterBy(NotificationCategory category) {
        currentCategory = category;
        toolbar.setCurrentCategory(category);
        FilterPagingLoadConfig config = view.getCurrentLoadConfig();
        FilterConfig filterBean = new FilterConfigBean();
        if (!NotificationCategory.ALL.equals(currentCategory)) {
            filterBean.setField(currentCategory.toString());
        }

        List<FilterConfig> filters = new ArrayList<>();
        filters.add(filterBean);
        config.setFilters(filters);

        view.loadNotifications(config);

    }

    @Override
    public NotificationCategory getCurrentCategory() {
        return currentCategory;
    }

    @Override
    public void onGridRefresh() {
        if (view.getListStore().size() > 0) {
            toolbar.setDeleteAllButtonEnabled(true);
        } else {
            toolbar.setDeleteAllButtonEnabled(false);
        }
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
        view.setLoader(initProxyLoader());
    }

    @Override
    public void onDeleteAllClicked() {
        view.mask();
        messageServiceFacade.deleteAll(currentCategory.toString(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(String result) {
                view.unmask();
                view.loadNotifications(view.getCurrentLoadConfig());
                DeleteNotificationsUpdateEvent event = new DeleteNotificationsUpdateEvent(null);
                eventBus.fireEvent(event);
            }
        });

    }

    @Override
    public void onDeleteClicked() {
        final List<NotificationMessage> notifications = view.getSelectedItems();
        final Command callback = new Command() {
            @Override
            public void execute() {
                view.loadNotifications(view.getCurrentLoadConfig());
            }
        };
        // do we have any notifications to delete?
        if (notifications != null && !notifications.isEmpty()) {
            JSONObject obj = new JSONObject();
            JSONArray arr = new JSONArray();
            int i = 0;
            for (NotificationMessage n : notifications) {
                arr.set(i++, new JSONString(n.getId()));
            }
            obj.put("uuids", arr);

            messageServiceFacade.deleteMessages(obj, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(errorStrings.notificationDeletFail(), caught);
                }

                @Override
                public void onSuccess(String result) {
                    callback.execute();
                    DeleteNotificationsUpdateEvent event = new DeleteNotificationsUpdateEvent(notifications);
                    eventBus.fireEvent(event);
                }
            });
        }

    }

    @Override
    public void onFilterSelection(NotificationCategory cat) {
        filterBy(cat);
    }

    @Override
    public void onNotificationSelection(List<NotificationMessage> items) {
        if (items == null || items.size() == 0) {
            toolbar.setDeleteButtonEnabled(false);
        } else {
            toolbar.setDeleteButtonEnabled(true);
        }
    }

    @Override
    public void setRefreshButton(TextButton refreshBtn) {
        if (refreshBtn != null) {
            refreshBtn.setText(displayStrings.refresh());
            toolbar.setRefreshButton(refreshBtn);
        }
    }

    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>> initProxyLoader() {

        RpcProxy<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>> proxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>>() {
            @Override
            public void load(final FilterPagingLoadConfig loadConfig,
                             final AsyncCallback<PagingLoadResult<NotificationMessage>> callback) {
                // for 'NEW' filter always set offset to 0
                List<FilterConfig> fc_list = loadConfig.getFilters();
                if (fc_list != null) {
                    String cat = (fc_list.get(0).getField() != null ? fc_list.get(0).getField()
                                                                             .toLowerCase() : null);
                    if ((!Strings.isNullOrEmpty(cat))) {
                        if (cat.equalsIgnoreCase(NotificationCategory.NEW.toString())) {
                            loadConfig.setOffset(0);
                        }
                    }
                }
                messageServiceFacade.getNotifications(loadConfig.getLimit(),
                                                      loadConfig.getOffset(),
                                                      (loadConfig.getFilters().get(0).getField()) == null ? ""
                                                          : loadConfig.getFilters().get(0).getField().toLowerCase(),
                                                      loadConfig.getSortInfo().get(0).getSortDir().toString(),
                                                      new NotificationServiceCallback(loadConfig, callback));
            }

        };

        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<NotificationMessage>> loader = new PagingLoader<>(proxy);
        loader.setRemoteSort(true);
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, NotificationMessage, PagingLoadResult<NotificationMessage>>(view.getListStore()));
        loader.useLoadConfig(buildDefaultLoadConfig());
        return loader;
    }
}
