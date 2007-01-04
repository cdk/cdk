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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *  A table model wrapper which listens to model changes and fowards them
 *  to other listeners.
 *
 * @cdk.module applications
 * @cdk.require swing
 *
 *  @author Bradley A. Smith <bradley@baysmith.com>
 */
public class ListeningTableModel extends AbstractTableModel
    implements TableModelListener {

	private static final long serialVersionUID = 5913883776580719650L;

/**
   *  Creates a listening table model for the given table model. This table
   *  model will listen for table change events from the given table model.
   */
  public ListeningTableModel(TableModel model) {
    this.model = model;
    model.addTableModelListener(this);
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public Object getValueAt(int aRow, int aColumn) {
    return model.getValueAt(aRow, aColumn);
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public void setValueAt(Object aValue, int aRow, int aColumn) {
    model.setValueAt(aValue, aRow, aColumn);
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public int getRowCount() {
    return model.getRowCount();
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public int getColumnCount() {
    return model.getColumnCount();
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public String getColumnName(int aColumn) {
    return model.getColumnName(aColumn);
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public Class getColumnClass(int aColumn) {
    return model.getColumnClass(aColumn);
  }

  /**
   *  Forwards messages to the AbstractTableModel
   */
  public boolean isCellEditable(int row, int column) {
    return model.isCellEditable(row, column);
  }

  /**
   *  Forwards events to listeners of this model.
   */
  public void tableChanged(TableModelEvent event) {
    fireTableChanged(event);
  }

  /**
   *  The actual table model.
   */
  protected TableModel model;
}

