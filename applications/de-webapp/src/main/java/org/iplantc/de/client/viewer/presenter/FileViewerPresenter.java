package org.iplantc.de.client.viewer.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.client.viewer.callbacks.TreeUrlCallback;
import org.iplantc.de.client.viewer.commands.ViewCommand;
import org.iplantc.de.client.viewer.factory.MimeTypeViewerResolverFactory;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.views.windows.FileViewerWindow;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class FileViewerPresenter implements FileViewer.Presenter {

	// A presenter can handle more than one view of the same data at a time
	private List<FileViewer> viewers;

	private FileViewerWindow container;

	/**
	 * The file shown in the window.
	 */
	private File file;

	/**
	 * The manifest of file contents
	 */
	private final JSONObject manifest;

	private boolean treeViewer;

	private boolean genomeViewer;

	private final boolean editing;

	private boolean isDirty;

	public FileViewerPresenter(File file, JSONObject manifest, boolean editing) {
		this.manifest = manifest;
		viewers = new ArrayList<FileViewer>();
		this.file = file;
		this.editing = editing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.iplantc.de.commons.client.presenter.Presenter#go(com.google.gwt
	 * .user.client.ui.HasOneWidget )
	 */
	@Override
	public void go(HasOneWidget container) {
		this.container = (FileViewerWindow) container;
		composeView(manifest);
	}

	private boolean checkManifest(JSONObject obj) {
		if (obj == null) {
			return false;
		}
		String info_type = JsonUtil.getString(obj, "info-type");
		if (info_type == null || info_type.isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean isTreeTab(JSONObject obj) {
		if (checkManifest(obj)) {
			String infoType = JsonUtil.getString(obj, "info-type");
			return (infoType.equals(InfoType.NEXUS.toString())
					|| infoType.equals(InfoType.NEXML.toString())
					|| infoType.equals(InfoType.NEWICK.toString()) || infoType
						.equals(InfoType.PHYLOXML.toString()));
		}

		return false;

	}

	private boolean isGenomeVizTab(JSONObject obj) {
		if (checkManifest(obj)) {
			String info_type = JsonUtil.getString(obj, "info-type");
			return (info_type.equals(InfoType.FASTA.toString()));
		}

		return false;
	}

	@Override
	public void composeView(JSONObject manifest) {
		container.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
		String mimeType = JsonUtil.getString(manifest, "content-type");
		ViewCommand cmd = MimeTypeViewerResolverFactory
				.getViewerCommand(MimeType.fromTypeString(mimeType));
		String infoType = JsonUtil.getString(manifest, "info-type");
		List<? extends FileViewer> viewers_list = cmd.execute(file, infoType,
				editing);

		if (viewers_list != null && viewers_list.size() > 0) {
			viewers.addAll(viewers_list);
			for (FileViewer view : viewers) {
				view.setPresenter(this);
				container.getWidget().add(view.asWidget(), view.getViewName());
			}
			container.unmask();
		}

		treeViewer = isTreeTab(manifest);
		genomeViewer = isGenomeVizTab(manifest);

		if (treeViewer || genomeViewer) {
			cmd = MimeTypeViewerResolverFactory.getViewerCommand(MimeType
					.fromTypeString("viz"));
			List<? extends FileViewer> vizViewers = cmd.execute(file, infoType,
					editing);
			List<VizUrl> urls = getManifestVizUrls();
			if (urls != null && urls.size() > 0) {
				vizViewers.get(0).setData(urls);
			} else {
				if (treeViewer) {
					callTreeCreateService(vizViewers.get(0));
				}
                // else if (genomeViewer) {
                // final ConfirmMessageBox cmb = new ConfirmMessageBox(
                // org.iplantc.de.resources.client.messages.I18N.DISPLAY.visualization(),
                // org.iplantc.de.resources.client.messages.I18N.DISPLAY.cogePrompt());
                // cmb.addHideHandler(new HideHandler() {
                //
                // @Override
                // public void onHide(HideEvent event) {
                // if (cmb.getHideButton() == cmb
                // .getButtonById(PredefinedButton.YES.name())) {
                // loadInCoge(file);
                // }
                // // else do nothing
                //
                // }
                // });
                // cmb.show();
                // }
			}

			viewers.add(vizViewers.get(0));
			container.getWidget().add(vizViewers.get(0).asWidget(),
					vizViewers.get(0).getViewName());
		}

		if (viewers.size() == 0) {
			container.unmask();
			container.add(new HTML(org.iplantc.de.resources.client.messages.I18N.DISPLAY.fileOpenMsg()));
		}

	}

	/**
	 * Gets the tree-urls json array from the manifest.
	 * 
	 * @return A json array of at least one tree URL, or null otherwise.
	 */
	private List<VizUrl> getManifestVizUrls() {
		return TreeUrlCallback.getTreeUrls(manifest.toString());

	}

	/**
	 * Calls the tree URL service to fetch the URLs to display in the grid.
	 */
	public void callTreeCreateService(final FileViewer viewer) {
		container.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
        ServicesInjector.INSTANCE.getFileEditorServiceFacade().getTreeUrl(file.getId(), false,
				new TreeUrlCallback(file, container, viewer));
	}

	private void loadInCoge(File file) {
		container.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
		JSONObject obj = new JSONObject();
		JSONArray pathArr = new JSONArray();
		pathArr.set(0, new JSONString(file.getPath()));
		obj.put("paths", pathArr);
        ServicesInjector.INSTANCE.getFileEditorServiceFacade().viewGenomes(obj,
				new LoadGenomeInCoGeCallback(container));
	}

	@Override
	public void setVeiwDirtyState(boolean dirty) {
		this.isDirty = dirty;
		updateWindowTitle();
	}

	private void updateWindowTitle() {
		if (isDirty) {
			container
					.setTitle(container.getTitle()
							+ "<span style='color:red; vertical-align: super'> * </span>");
		} else {
			String temp = container.getTitle();
			if (temp.endsWith("*")) {
				temp = temp.substring(0, temp.length() - 1);
			}
			container.setTitle(temp);
		}
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void cleanUp() {
		if (viewers != null && viewers.size() > 0) {
			for (FileViewer view : viewers) {
				view.cleanUp();
			}
		}
		
		viewers = null;
		file = null;

	}

}
