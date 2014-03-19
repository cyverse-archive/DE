package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.apps.client.presenter.proxy.PublicAppGroupProxy;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppRefLink;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.shared.services.ConfluenceServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.ArrayList;
import java.util.List;

public class SubmitAppForPublicPresenter implements SubmitAppForPublicUseView.Presenter {

	private final SubmitAppForPublicUseView view;
    private AsyncCallback<String> callback;
    private final AppUserServiceFacade appService;
    private final PublicAppGroupProxy appGroupProxy;

	@Inject
    public SubmitAppForPublicPresenter(SubmitAppForPublicUseView view, AppUserServiceFacade appService, PublicAppGroupProxy appGroupProxy) {
		this.view = view;
        this.appService = appService;
        this.appGroupProxy = appGroupProxy;
	}

	@Override
	public void go(HasOneWidget container) {
		container.setWidget(view);
		// Fetch AppGroups
		appGroupProxy.load(null, new AsyncCallback<List<AppGroup>>() {
			@Override
			public void onSuccess(List<AppGroup> result) {
				addAppGroup(null, result);
				view.expandAppGroups();
				// remove workspace node from store
				view.getTreeStore().remove(
						view.getTreeStore().findModelWithKey(
								DEProperties.getInstance()
										.getDefaultBetaCategoryId()));
			}

			private void addAppGroup(AppGroup parent, List<AppGroup> children) {
				if ((children == null) || children.isEmpty()) {
					return;
				}
				if (parent == null) {
					view.getTreeStore().add(children);
				} else {
					view.getTreeStore().add(parent, children);
				}

				for (AppGroup ag : children) {
					addAppGroup(ag, ag.getGroups());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				ErrorHandler.post(I18N.ERROR.publishFailureDefaultMessage(),caught);
			}
		});
	}

	@Override
	public void onSubmit() {
		if (view.validate()) {
			createDocumentationPage(view.toJson());
		} else {
			AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.warning(),
					I18N.DISPLAY.publicSubmitTip());
			amb.show();
		}
	}

    private void getAppDetails() {
        appService.getAppDetails(view.getSelectedApp().getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.publishFailureDefaultMessage(), caught);
            }

            @Override
            public void onSuccess(String result) {
                JSONObject obj = JsonUtil.getObject(result);
                JSONArray arr = JsonUtil.getArray(obj, "references");
                if (arr != null && arr.size() > 0) {
                    view.loadReferences(parseRefLinks(arr));
                }
            }
        });
    }

	private void createDocumentationPage(final JSONObject obj) {
	    final AutoProgressMessageBox pmb = new AutoProgressMessageBox(I18N.DISPLAY.submitForPublicUse(), I18N.DISPLAY.submitRequest());
	    pmb.setProgressText(I18N.DISPLAY.submitting());
	    pmb.setClosable(false);
	    pmb.getProgressBar().setInterval(100);
	    pmb.auto();
	    pmb.show();
		ConfluenceServiceFacade.getInstance().createDocumentationPage(
				JsonUtil.getString(obj, "name"),
				JsonUtil.getString(obj, "desc"), new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
					    pmb.hide();
						ErrorHandler.post(I18N.ERROR.cantCreateConfluencePage(JsonUtil.getString(obj, "name")), caught);
						
						//SS:uncomment this for testing purposes only...
						//onSuccess("http://test.com/url");
					}

					@Override
					public void onSuccess(final String url) {
					    obj.put("wiki_url", new JSONString(url));
					    appService.publishToWorld(obj, new AsyncCallback<String>() {
					        @Override
					        public void onSuccess(String result) {
					            pmb.hide();
					            EventBus.getInstance().fireEvent(new AppPublishedEvent(view.getSelectedApp()));
					            callback.onSuccess(url);
					        }

					        @Override
					        public void onFailure(Throwable caught) {
					            pmb.hide();
					            callback.onFailure(caught);
					        }
					    });
					}
				});
	}

	private List<AppRefLink> parseRefLinks(JSONArray arr) {
		AppAutoBeanFactory factory = GWT
				.create(AppAutoBeanFactory.class);
		List<AppRefLink> linksList = new ArrayList<AppRefLink>();
		for (int i = 0; i < arr.size(); i++) {
			AutoBean<AppRefLink> bean = AutoBeanCodex
					.decode(factory, AppRefLink.class, "{}");
			AppRefLink link = bean.as();
			String stringValue = arr.get(i).isString()
					.stringValue();
			link.setId(stringValue);
			link.setRefLink(stringValue);
			linksList.add(link);
		}

		return linksList;
	}

    @Override
    public void go(HasOneWidget container, App selectedApp, AsyncCallback<String> callback) {
        view.setSelectedApp(selectedApp);
        this.callback = callback;
        getAppDetails();
        go(container);
    }

}
