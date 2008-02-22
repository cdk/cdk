/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) Project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.swing;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *  A sorted table model. The objects in the model must implement the Comparable
 *  interface.
 *
 * @cdk.module applications
 * @cdk.svnrev  $Revision$
 * @cdk.require swing
 *
 * @author Bradley A. Smith <bradley@baysmith.com>
 */
public class SortedTableModel extends ListeningTableModel {


    private static final long serialVersionUID = -8517584055155609668L;
    
    Integer[] indexes;
  List sortingColumns = new ArrayList();
  boolean ascending = true;

  public SortedTableModel(TableModel model) {
    super(model);
    initializeIndexes();
  }

  public void initializeIndexes() {

    indexes = new Integer[model.getRowCount()];
    for (int row = 0; row < indexes.length; ++row) {
      indexes[row] = new Integer(row);
    }
  }

  public void tableChanged(TableModelEvent e) {

    initializeIndexes();
    super.tableChanged(e);
  }

  void sort() {
    Arrays.sort(indexes,
                new Comparator() {

                  public int compare(Object r1, Object r2) {

                    int row1 = ((Integer) r1).intValue();
                    int row2 = ((Integer) r2).intValue();
                    int result = 0;
                    int level = 0;
                    while ((result == 0) && (level < sortingColumns.size())) {
                      int column =
                        ((Integer) sortingColumns.get(level)).intValue();
                      Object o1 = model.getValueAt(row1, column);
                      Object o2 = model.getValueAt(row2, column);
                      if ((o1 == null) && (o2 == null)) {
                        // Null objects are equal.
                        return 0;
                      } else if (o1 == null) {
                        // Null is less than anything.
                        return -1;
                      } else if (o2 == null) {
                        // Anything is greather than null.
                        return 1;
                      }
                      result = ((Comparable) o1).compareTo(o2);
                      ++level;
                    }
                    if (!ascending) {
                      result = -result;
                    }
                    return result;
                  }
                });
  }


  /**
   *  Returns the object at the given indices.
   */
  public Object getValueAt(int aRow, int aColumn) {
    return model.getValueAt(indexes[aRow].intValue(), aColumn);
  }

  public int getSortedIndex(int aRow) {
      return indexes[aRow].intValue();
  }
  
  /**
   *  Sets the object at the given indices.
   */
  public void setValueAt(Object aValue, int aRow, int aColumn) {
    model.setValueAt(aValue, indexes[aRow].intValue(), aColumn);
  }

  public int getSortedColumn() {
    if (sortingColumns.size() > 0) {
      return ((Integer) sortingColumns.get(0)).intValue();
    }
    return -1;
  }

  public boolean isAscending() {
    return ascending;
  }

  public void sortByColumn(int column) {
    sortByColumn(column, true);
  }

  public void sortByColumn(int column, boolean ascending) {

    Integer oldSortColumn = null;
    if (sortingColumns.size() > 0) {
      oldSortColumn = (Integer) sortingColumns.get(0);
    }
    Boolean oldSortDirection = new Boolean(this.ascending);
    this.ascending = ascending;
    Integer newSortColumn = new Integer(column);
    sortingColumns.add(0, newSortColumn);

    // Limit size of sorting columns so that it doesn't grow forever.
    while (sortingColumns.size() > model.getColumnCount()) {
      sortingColumns.remove(sortingColumns.size() - 1);
    }
    sort();
    super.tableChanged(new TableModelEvent(this));
    changeSupport.firePropertyChange("sortColumn", oldSortColumn,
        newSortColumn);
    Boolean newSortDirection = new Boolean(ascending);
    changeSupport.firePropertyChange("sortDirection", oldSortDirection,
        newSortDirection);
  }

  /**
   *  Adds a mouse listener to the given Table for sorting a column
   *  when the heading is clicked.  When the sorted column is clicked,
   *  the sort order alternates between ascending and descending.
   */
  public void addMouseListenerToHeaderInTable(final JTable table) {

    table.setColumnSelectionAllowed(false);
    MouseAdapter listMouseListener = new MouseAdapter() {

      boolean order = true;

      public void mouseClicked(MouseEvent e) {

        TableColumnModel columnModel = table.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        int column = table.convertColumnIndexToModel(viewColumn);
        if ((e.getClickCount() == 1) && (column != -1)) {
          if (getSortedColumn() == column) {
            order = !order;
          }
          sortByColumn(column, order);
        }
      }
    };
    table.getTableHeader().addMouseListener(listMouseListener);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  private PropertyChangeSupport changeSupport =
    new PropertyChangeSupport(this);

}
