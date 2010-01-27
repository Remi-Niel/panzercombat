package com.gampire.pc.view.table;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.gampire.pc.model.Camp;
import com.gampire.pc.model.Unit;
import com.gampire.pc.model.UnitType;

public class UnitTableModel extends AbstractTableModel {
    private final String[] columnNames = { "Unit", "Type" };

    private Camp camp;

    public UnitTableModel(Camp camp) {
        super();
        this.camp = camp;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return camp.getUnits().size();
    }

    @Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        Vector<Unit> units = camp.getUnits();
        return units.get(row);
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column would
     * contain text ("true"/"false"), rather than a check box.
     */
    @Override
	public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
	public boolean isCellEditable(int row, int col) {
        if (col == 1) {
			return true;
		}
        return false;
    }

    @Override
	public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            camp.changeUnitType(row, (UnitType) value);
        }

        // when the name of a unit changes, other unit name might be affected
        // when the unitType changes, its name its recomputed, so we need
        // to recompute the whole table instead of doing only
        // fireTableCellUpdated(row, col);
        int numRow = getRowCount();
        int numCol = getColumnCount();
        for (int iRow = 0; iRow < numRow; iRow++) {
            for (int iCol = 0; iCol < numCol; iCol++) {
                fireTableCellUpdated(iRow, iCol);
            }
        }
    }

    public void addUnit() {
        camp.addUnit();
        // notify the table that the table model has changed
        fireTableDataChanged();
    }

    public void removeUnit(Unit unit) {
        camp.removeUnit(unit);
        // notify the table that the table model has changed
        fireTableDataChanged();
    }

    public int removeDestroyedUnits() {
        int numRemoved = camp.removeDestroyedUnits();
        // notify the table that the table model has changed
        fireTableDataChanged();
        return numRemoved;
    }

    public Camp getCamp() {
        return camp;
    }
}
