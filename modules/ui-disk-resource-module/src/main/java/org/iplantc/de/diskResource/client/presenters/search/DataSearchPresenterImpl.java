package org.iplantc.de.diskResource.client.presenters.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.SearchView;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.events.search.DeleteSavedSearchClickedEvent;
import org.iplantc.de.diskResource.client.events.search.SaveDiskResourceQueryClickedEvent;
import org.iplantc.de.diskResource.client.events.search.SavedSearchDeletedEvent;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasName;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class DataSearchPresenterImpl implements SearchView.Presenter {

    final List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList();
    List<DiskResourceQueryTemplate> cleanCopyQueryTemplates = Lists.newArrayList();
    private final IplantAnnouncer announcer;
    private final SearchServiceFacade searchService;
    private HandlerManager handlerManager;
    private final Logger LOG = Logger.getLogger(DataSearchPresenterImpl.class.getName());

    @Inject
    DataSearchPresenterImpl(final SearchServiceFacade searchService,
                            final IplantAnnouncer announcer) {
        this.searchService = searchService;
        this.announcer = announcer;
    }

    @Override
    public HandlerRegistration addSavedSearchDeletedEventHandler(SavedSearchDeletedEvent.SavedSearchDeletedEventHandler handler) {
        return ensureHandlers().addHandler(SavedSearchDeletedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addUpdateSavedSearchesEventHandler(UpdateSavedSearchesEvent.UpdateSavedSearchesHandler handler) {
        return ensureHandlers().addHandler(UpdateSavedSearchesEvent.TYPE, handler);
    }

    @Override
    public void onDeleteSavedSearchClicked(DeleteSavedSearchClickedEvent event) {
        final DiskResourceQueryTemplate savedSearch = event.getSavedSearch();
        if (queryTemplates.remove(savedSearch)) {
            announcer.schedule(new SuccessAnnouncementConfig("Successfully deleted saved search: "
                                                                 + savedSearch.getName()));
            searchService.deleteQueryTemplates(Arrays.asList(savedSearch),
                                             new AsyncCallback<List<DiskResourceQueryTemplate>>() {

                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     announcer.schedule(new ErrorAnnouncementConfig("Unable to save filter."));
                                                 }

                                                 @Override
                                                 public void onSuccess(List<DiskResourceQueryTemplate> savedTemplates) {
                                                     if (queryTemplates.size() != savedTemplates.size()) {
                                                         LOG.fine("Failed to save query templates after delete of saved search");
                                                     }
                                                     fireEvent(new SavedSearchDeletedEvent(savedSearch));
                                                     fireEvent(new UpdateSavedSearchesEvent(null, savedTemplates));
                                                 }
                                             });
        } else {
            LOG.warning("Failed to remove saved search from presenter");
        }
    }

    /**
     * This handler is responsible for saving or updating the {@link DiskResourceQueryTemplate} contained
     * in the given {@link org.iplantc.de.diskResource.client.events.search.SaveDiskResourceQueryClickedEvent}.
     * <p/>
     * After the query has been successfully saved, a search with the given querytemplate will be
     * performed.
     */
    @Override
    public void onSaveDiskResourceQueryClicked(final SaveDiskResourceQueryClickedEvent event) {
        // Assume that once the filter is saved, a search should be performed.
        final DiskResourceQueryTemplate queryTemplate = event.getQueryTemplate();

        if (Strings.isNullOrEmpty(queryTemplate.getName())) {
            // Given query template has no name, ripple error back to view
            LOG.fine("TODO: User tried to save query with no name, cannot save. Ripple error back to view");
            return;
        } else {
            // Check for name uniqueness
            final Set<String> uniqueNames = getUniqueNames(getQueryTemplates());
            if (uniqueNames.size() == getQueryTemplates().size()) {
                // Sanity check: There were no dupes in the current list
                if (uniqueNames.contains(queryTemplate.getName())) {
                    /*
                     * The given query template is already in the list, remove it. The new one will be
                     * added to the list submitted to the service.
                     */
                    for (DiskResourceQueryTemplate hasId : ImmutableList.copyOf(getQueryTemplates())) {
                        String inListName = hasId.getName();
                        if (queryTemplate.getName().equalsIgnoreCase(inListName)) {
                            getQueryTemplates().remove(hasId);

                            break;
                        }
                    }
                }
            }
        }

        final ImmutableList<DiskResourceQueryTemplate> toBeSaved = ImmutableList.copyOf(Iterables.concat(queryTemplates, Collections.singletonList(queryTemplate)));
        // Call service to save template
        searchService.saveQueryTemplates(toBeSaved, new AsyncCallback<List<DiskResourceQueryTemplate>>() {

            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig("Unable to save filter."));
            }

            @Override
            public void onSuccess(List<DiskResourceQueryTemplate> savedTemplates) {
                // Clear list of saved query templates and re-add result.
                queryTemplates.clear();
                if (toBeSaved.size() != savedTemplates.size()) {
                    LOG.warning("Saved templates returned from search service facade is a different size than what we submitted.");
                }
                queryTemplates.addAll(savedTemplates);

                /*
                 * Determine if there has been a name change, if so, remove the original from the
                 * treestore.
                 */
                List<DiskResourceQueryTemplate> queriesToRemove = Lists.newArrayList();
                for (DiskResourceQueryTemplate qt : cleanCopyQueryTemplates) {
                    if (qt.getName().equals(event.getOriginalName())) {
                        queriesToRemove.add(qt);
                    }
                }
                // Create immutable copy of saved templates
                setCleanCopyQueryTemplates(searchService.createFrozenList(toBeSaved));

                List<DiskResourceQueryTemplate> toUpdate = Lists.newArrayList();
                // If it is an existing query, determine if it is dirty. If so, set dirty flag
                if (templateHasChanged(queryTemplate, cleanCopyQueryTemplates)) {
                    queryTemplate.setDirty(true);
                    // Replace existing object in current template list
                    for (DiskResourceQueryTemplate qt : getQueryTemplates()) {
                        if (qt.getName().equalsIgnoreCase(queryTemplate.getName())) {
                            toUpdate.add(queryTemplate);
                        } else {
                            toUpdate.add(qt);
                        }
                    }
                    getQueryTemplates().clear();
                    getQueryTemplates().addAll(toUpdate);
                } else {
                    toUpdate = Lists.newArrayList(getQueryTemplates());
                }

                // Performing a search has the effect of setting the given query as the current active query.
                fireEvent(new UpdateSavedSearchesEvent(toUpdate, queriesToRemove));
            }
        });

    }

    @Override
    public void onSavedSearchedRetrieved(SavedSearchesRetrievedEvent event) {
        loadSavedQueries(event.getSavedSearches());
    }

    boolean areTemplatesEqual(DiskResourceQueryTemplate lhs, DiskResourceQueryTemplate rhs) {
        final AutoBean<DiskResourceQueryTemplate> lhsAb = AutoBeanUtils.getAutoBean(lhs);
        final AutoBean<DiskResourceQueryTemplate> rhsAb = AutoBeanUtils.getAutoBean(rhs);

        return AutoBeanUtils.deepEquals(lhsAb, rhsAb);
    }

    HandlerManager createHandlerManager() {
        return new HandlerManager(this);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
    }

    void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    List<DiskResourceQueryTemplate> getQueryTemplates() {
        return queryTemplates;
    }

    Set<String> getUniqueNames(List<DiskResourceQueryTemplate> hasNames) {
        final HashSet<String> queryNameSet = Sets.newHashSet();
        for (HasName hasName : hasNames) {
            if (queryNameSet.contains(hasName.getName())) {
                // We have a dupe name!!
                LOG.warning("Duplicate QueryTemplate name found: " + hasName.getName());
            } else {
                queryNameSet.add(hasName.getName());
            }
        }

        return queryNameSet;
    }

    void loadSavedQueries(List<DiskResourceQueryTemplate> savedQueries) {
        setCleanCopyQueryTemplates(searchService.createFrozenList(savedQueries));

        List<DiskResourceQueryTemplate> queriesToRemove = Lists.newArrayList(queryTemplates);
        queryTemplates.clear();
        queryTemplates.addAll(savedQueries);

        // Update navigation window
        fireEvent(new UpdateSavedSearchesEvent(queryTemplates, queriesToRemove));
    }

    void setCleanCopyQueryTemplates(List<DiskResourceQueryTemplate> cleanCopyQueryTemplates) {
        this.cleanCopyQueryTemplates = cleanCopyQueryTemplates;
    }

    private boolean templateHasChanged(DiskResourceQueryTemplate template,
                                       List<DiskResourceQueryTemplate> controlList) {
        for (DiskResourceQueryTemplate qt : controlList) {
            if (qt.getName().equalsIgnoreCase(template.getName()) && !areTemplatesEqual(qt, template)) {
                // Given template has been changed
                return true;
            }
        }
        return false;
    }

}
