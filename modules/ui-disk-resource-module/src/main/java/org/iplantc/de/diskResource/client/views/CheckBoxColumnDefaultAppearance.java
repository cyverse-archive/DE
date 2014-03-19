/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.views.DiskResourceSelectionModel.CheckBoxColumnAppearance;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridDataTableStyles;

public class CheckBoxColumnDefaultAppearance implements CheckBoxColumnAppearance {

  public interface CheckBoxColumnStyle extends CssResource, ColumnHeaderStyles, GridDataTableStyles {
    String checkColumn();

    String checkColumnSelected();

    String checkColumnDisabled();

    String headerChecked();
  }

  public interface CheckBoxColumnResources extends ClientBundle {
    @Source("CheckBoxColumn.css")
    CheckBoxColumnStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource specialColumn();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource specialColumnSelected();
  }

  private final CheckBoxColumnResources resources;
  private final CheckBoxColumnStyle style;

  public CheckBoxColumnDefaultAppearance() {
    this(GWT.<CheckBoxColumnResources> create(CheckBoxColumnResources.class));
  }

  public CheckBoxColumnDefaultAppearance(CheckBoxColumnResources resources) {
    this.resources = resources;
    this.style = this.resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public void renderCheckBox(Context context, DiskResource value, SafeHtmlBuilder sb) {
    sb.appendHtmlConstant("<div class='x-grid-row-checker'>&#160;</div>");
  }

  @Override
  public void onHeaderChecked(XElement header, boolean checked) {
    header.setClassName(style.headerChecked(), checked);
  }

  @Override
  public boolean isHeaderChecked(XElement header) {
    return header.getParentElement().<XElement>cast().hasClassName(style.headerChecked());
  }

}
