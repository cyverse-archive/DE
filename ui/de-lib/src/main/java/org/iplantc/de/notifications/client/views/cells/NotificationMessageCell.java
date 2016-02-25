package org.iplantc.de.notifications.client.views.cells;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadApps;
import org.iplantc.de.client.models.notifications.payload.PayloadAppsList;
import org.iplantc.de.client.models.notifications.payload.PayloadRequest;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.window.configs.AnalysisWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.notifications.client.events.WindowShowRequestEvent;
import org.iplantc.de.notifications.client.views.dialogs.RequestHistoryDialog;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A cell to render notification messages in a Grid
 * 
 * @author sriram
 * 
 */
public class NotificationMessageCell extends AbstractCell<NotificationMessage> {

    public interface NotificationMessageCellAppearance {
        void render(Cell.Context context, NotificationMessage value, SafeHtmlBuilder sb);
    }

    private final NotificationMessageCellAppearance appearance =
            GWT.create(NotificationMessageCellAppearance.class);

    private final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private final AnalysesAutoBeanFactory analysesFactory = GWT.create(AnalysesAutoBeanFactory.class);
    private final NotificationAutoBeanFactory notificationFactory = GWT.create(NotificationAutoBeanFactory.class);
    private final DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

    public NotificationMessageCell() {
        super("click"); //$NON-NLS-1$
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, NotificationMessage value,
            NativeEvent event, ValueUpdater<NotificationMessage> valueUpdater) {
        if (value == null) {
            return;
        }

        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        if ("click".equals(event.getType())) { //$NON-NLS-1$
            if (value.getContext() != null
                && value.getCategory() != null) {

                NotificationCategory category = value.getCategory();
                String context1 = value.getContext();

                switch (category) {

                    case APPS:
                        final AppsWindowConfig appsConfig = ConfigFactory.appsWindowConfig();
                        final PayloadAppsList pal = AutoBeanCodex.decode(notificationFactory,
                                                                         PayloadAppsList.class,
                                                                         context1).as();
                        if (pal != null && pal.getApps() != null && pal.getApps().size() > 0) {
                            PayloadApps payload = pal.getApps().get(0);
                            final String appCategoryId = payload.getCategoryId();
                            final String appId = payload.getId();
                            appsConfig.setSelectedAppCategory(CommonModelUtils.getInstance()
                                                                              .createHasIdFromString(
                                                                                      appCategoryId));
                            appsConfig.setSelectedApp(CommonModelUtils.getInstance()
                                                                      .createHasIdFromString(appId));
                            EventBus.getInstance()
                                    .fireEvent(new WindowShowRequestEvent(appsConfig, true));
                        }

                        break;
                    case DATA:
                        // execute data context
                        File file = AutoBeanCodex.decode(drFactory, File.class, context1).as();
                        ArrayList<HasId> selectedResources = Lists.newArrayList();
                        selectedResources.add(file);

                        DiskResourceWindowConfig dataWindowConfig =
                                ConfigFactory.diskResourceWindowConfig(false);
                        HasPath folder = diskResourceUtil.getFolderPathFromFile(file);
                        dataWindowConfig.setSelectedFolder(folder);
                        dataWindowConfig.setSelectedDiskResources(selectedResources);
                        EventBus.getInstance()
                                .fireEvent(new WindowShowRequestEvent(dataWindowConfig, true));

                        break;

                    case ANALYSIS:
                        AutoBean<Analysis> hAb =
                                AutoBeanCodex.decode(analysesFactory, Analysis.class, context1);

                        AnalysisWindowConfig analysisWindowConfig = ConfigFactory.analysisWindowConfig();
                        analysisWindowConfig.setSelectedAnalyses(Lists.newArrayList(hAb.as()));
                        EventBus.getInstance()
                                .fireEvent(new WindowShowRequestEvent(analysisWindowConfig, true));

                        break;
                    case PERMANENTIDREQUEST:
                        // fall through to ToolRequest logic
                    case TOOLREQUEST:
                        PayloadRequest toolRequest =
                                AutoBeanCodex.decode(notificationFactory, PayloadRequest.class, context1)
                                             .as();

                        List<org.iplantc.de.client.models.requestStatus.RequestHistory> history =
                                toolRequest.getHistory();

                        RequestHistoryDialog dlg =
                                new RequestHistoryDialog(toolRequest.getName(), history);
                        dlg.show();

                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void render(Context context, NotificationMessage value, SafeHtmlBuilder sb) {
        appearance.render(context, value, sb);
    }

}
