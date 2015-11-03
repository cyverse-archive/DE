/**
 * Contains classes related to the {@link org.iplantc.de.apps.client.AppsView}.
 *
 * The primary view is the {@link org.iplantc.de.apps.client.AppsView}. All other views are
 * primarily support views.
 *
 * Each view interface is implemented in the {@code views} package, and their corresponding
 * presenters in the {@code presenters} package.
 *
 * This entire module makes heavy use of Ginjection. All Gin related classes may be found in the
 * {@code gin} package. In particular, assisted injection is used to a large degree.
 *
 * The {@link org.iplantc.de.apps.client.events.AppUpdatedEvent} and
 * {@link org.iplantc.de.apps.client.events.AppFavoritedEvent} are each fired through the global
 * event bus. This was done to reduce coupling of the Grid and Category related presenters,
 * since app updates can affect category counts or visual state representation of an app in the
 * listing (e.g. when an app is favorited through the
 * {@link org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog}).
 *
 * Currently, the {@link org.iplantc.de.apps.client.AppDetailsView.Presenter} must be
 * instantiated through the {@link org.iplantc.de.apps.client.AppCategoriesView.Presenter}
 * in order for app group hierarchies to be properly displayed in the
 * {@link org.iplantc.de.apps.client.AppDetailsView}.
 *
 *
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
package org.iplantc.de.apps.client;