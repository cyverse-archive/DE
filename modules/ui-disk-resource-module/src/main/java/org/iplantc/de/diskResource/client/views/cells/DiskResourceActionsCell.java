package org.iplantc.de.diskResource.client.views.cells;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.dataLink.DataLinkList;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.cells.DiskResourceNameCell.CALLER_TAG;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Arrays;
import java.util.List;

public class DiskResourceActionsCell extends AbstractCell<DiskResource> {

	final CALLER_TAG tag;
	private final DiskResourceView view;

	interface MyCss extends CssResource {
		@ClassName("actions_icon")
		String actionIcon();
	}

	interface Resource extends ClientBundle {
		@Source("DiskResourceActionsCell.css")
		MyCss css();
	}

	/**
	 * The HTML templates used to render the cell.
	 */
	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<img name=\"{0}\" title=\"{1}\" class=\"{2}\" src=\"{3}\"></img>")
		SafeHtml imgCell(String name, String toolTip, String className,
				SafeUri imgSrc);
	}

	private static Templates templates = GWT.create(Templates.class);
	private static final Resource resources = GWT.create(Resource.class);

	public DiskResourceActionsCell(DiskResourceView caller, CALLER_TAG tag) {
		super(CLICK, MOUSEOVER, MOUSEOUT);

		this.tag = tag;
		this.view = caller;
		resources.css().ensureInjected();
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			DiskResource value, SafeHtmlBuilder sb) {

		if (!value.isFilter() && !DiskResourceUtil.inTrash(value)) {
			if (value instanceof Folder) {
				sb.append(templates.imgCell(I18N.DISPLAY.share() + " " + I18N.DISPLAY.path(),
						I18N.DISPLAY.share() + " " +I18N.DISPLAY.path(), resources.css().actionIcon(),
						IplantResources.RESOURCES.dataLink().getSafeUri()));
			} else {
				if (value.getPermissions().isOwner()) {
					sb.append(templates.imgCell(I18N.DISPLAY.share() + " " + I18N.DISPLAY.viaPublicLink(),
							I18N.DISPLAY.share() + " " + I18N.DISPLAY.viaPublicLink(), resources.css()
									.actionIcon(), IplantResources.RESOURCES
									.linkAdd().getSafeUri()));
				}
			}
			if (value.getPermissions().isOwner()) {
				sb.append(templates.imgCell(I18N.DISPLAY.share(),
						I18N.DISPLAY.share() + " " + I18N.DISPLAY.viaDiscoveryEnvironment(), resources.css()
								.actionIcon(), IplantResources.RESOURCES
								.share().getSafeUri()));
			}

			sb.append(templates.imgCell(I18N.DISPLAY.metadata(), I18N.DISPLAY.metadata(), resources.css()
					.actionIcon(), IplantResources.RESOURCES.metadata()
					.getSafeUri()));
		}
	}

	@Override
	public void onBrowserEvent(Cell.Context context, Element parent,
			DiskResource value, NativeEvent event,
			ValueUpdater<DiskResource> valueUpdater) {
		if (value == null) {
			return;
		}

		Element eventTarget = Element.as(event.getEventTarget());
		if (eventTarget.getNodeName().equalsIgnoreCase("img")
				&& parent.isOrHasChild(eventTarget)) {
			switch (Event.as(event).getTypeInt()) {
			case Event.ONCLICK:
				doOnClick(parent, eventTarget, value);
				break;
			default:
				break;
			}
		}
	}

	private void doOnClick(final Element parent, Element eventTarget,
			DiskResource value) {
		String action = eventTarget.getAttribute("name");
		if (action.equalsIgnoreCase(I18N.DISPLAY.share() + " " + I18N.DISPLAY.path())) {
			buildFolderLink(value);
		} else if (action.equalsIgnoreCase(I18N.DISPLAY.share() + " " + I18N.DISPLAY.viaPublicLink())) {
			buildQuickSharePopup(value);
		} else if (action.equalsIgnoreCase(I18N.DISPLAY.share())) {
			view.getPresenter().doShare();
		} else if (action.equalsIgnoreCase(I18N.DISPLAY.metadata())) {
			view.getPresenter().doMetadata();
		}

	}

	private void buildFolderLink(final DiskResource value) {
		showShareLink(GWT.getHostPageBaseURL() + "?type=data&folder="
				+ value.getId());
	}

	private void buildQuickSharePopup(final DiskResource value) {
		final DiskResourceServiceFacade drService = ServicesInjector.INSTANCE
				.getDiskResourceServiceFacade();
		final DataLinkFactory dlFactory = GWT.create(DataLinkFactory.class);
		drService.createDataLinks(Arrays.asList(value.getPath()),
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(
								dlFactory, DataLinkList.class, result);
						List<DataLink> dlList = tickets.as().getTickets();
						showShareLink(dlList.get(0).getDownloadUrl());
					}

					@Override
					public void onFailure(Throwable caught) {
						ErrorHandler.post(I18N.ERROR.createDataLinksError(),
								caught);
					}
				});
	}

	private void showShareLink(String linkId) {
		// Open dialog window with text selected.
		IPlantDialog dlg = new IPlantDialog();
		dlg.setHeadingText(I18N.DISPLAY.copy());
		dlg.setHideOnButtonClick(true);
		dlg.setResizable(false);
		dlg.setSize("535", "130");
		TextField textBox = new TextField();
		textBox.setWidth(500);
		textBox.setReadOnly(true);
		textBox.setValue(linkId);
		VerticalLayoutContainer container = new VerticalLayoutContainer();
		dlg.setWidget(container);
		container.add(textBox);
		container.add(new Label(I18N.DISPLAY.copyPasteInstructions()));
		dlg.setFocusWidget(textBox);
		dlg.show();
		textBox.selectAll();
	}

}
