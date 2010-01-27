package com.gampire.pc.view.table;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import com.gampire.pc.model.Alliance;
import com.gampire.pc.model.Unit;
import com.gampire.pc.model.UnitType;
import com.gampire.pc.swing.lookandfeel.PCIITheme;
import com.gampire.pc.swing.model.FreezeableListSelectionModel;
import com.gampire.pc.util.image.ImageUtil;
import com.gampire.pc.view.table.renderer.UnitDetailsTableCellRenderer;
import com.gampire.pc.view.table.renderer.UnitPictureTableCellRenderer;
import com.gampire.pc.view.table.renderer.UnitTypeListCellRenderer;

public class UnitTable extends JTable {

	final private static int ROW_HEIGHT = 175;

	final private static int DETAILS_COLUMN_WIDTH = 180;

	final private static int PICTURE_COLUMN_WIDTH = 320;

	private boolean isEditable = true;

	private boolean isSelectable = true;

	private FreezeableListSelectionModel selectionModel = new FreezeableListSelectionModel();

	private UnitDetailsTableCellRenderer detailsRenderer = new UnitDetailsTableCellRenderer();

	private UnitPictureTableCellRenderer pictureRenderer = new UnitPictureTableCellRenderer();

	private final boolean isLeft;

	public UnitTable(UnitTableModel unitTableModel, boolean isLeft) {
		super(unitTableModel);

		this.isLeft = isLeft;

		// selection
		setSelectionModel(selectionModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// dimensions
		setRowHeight(ROW_HEIGHT);
		setPreferredScrollableViewportSize(new Dimension(DETAILS_COLUMN_WIDTH
				+ PICTURE_COLUMN_WIDTH, ROW_HEIGHT * 3));
		getColumnModel().getColumn(0).setPreferredWidth(DETAILS_COLUMN_WIDTH);
		getColumnModel().getColumn(1).setPreferredWidth(PICTURE_COLUMN_WIDTH);

		// not opaque
		setOpaque(false);

		// no header
		setTableHeader(null);

		// font
		setFont(PCIITheme.FONT);

		// interCellSpacing and grid
		setIntercellSpacing(new Dimension(0, 2));
		setShowGrid(false);

		Alliance alliance = unitTableModel.getCamp().getAlliance();

		// renderer
		TableColumn unitColumn = getColumnModel().getColumn(0);
		unitColumn.setCellRenderer(detailsRenderer);

		TableColumn unitTypeColumn = getColumnModel().getColumn(1);
		unitTypeColumn.setCellRenderer(pictureRenderer);

		// cursor
		updateCursor();

		// editor
		JComboBox unitTypeComboBox = new JComboBox(UnitType
				.getAllForAliance(alliance));
		unitTypeComboBox.setFont(getFont());
		unitTypeComboBox.setForeground(PCIITheme.LIGHT_GRAY);
		unitTypeComboBox.setBackground(PCIITheme.DARK_GRAY);

		unitTypeComboBox.setRenderer(new UnitTypeListCellRenderer());
		unitTypeComboBox.setMaximumRowCount(3);
		unitTypeColumn.setCellEditor(new DefaultCellEditor(unitTypeComboBox));

		// invert the columns for the right table
		if (!isLeft) {
			moveColumn(1, 0);
		}

	}

	public Unit getSelectedUnit() {
		// both columns contain units (so don't need to care about the inversion)
		if (getRowCount() == 1) {
			return (Unit) getValueAt(0, 0);
		}
		int iSelectedRow = getSelectedRow();
		if (iSelectedRow > -1 && iSelectedRow < getRowCount()) {
			return (Unit) getValueAt(iSelectedRow, 0);
		} else {
			return null;
		}
	}

	public Point getUnitPosition(int iUnit) {
		JViewport viewport = (JViewport) getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle cellRect = getCellRect(iUnit, 0, true);

		// The location of the view relative to the table
		Rectangle viewRect = viewport.getViewRect();

		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0).
		cellRect.setLocation(cellRect.x - viewRect.x, cellRect.y - viewRect.y);
		Point position = new Point();
		if (isLeft) {
			position.x = columnModel.getColumn(0).getWidth()
					+ columnModel.getColumn(1).getWidth() / 2;
		} else {
			position.x = columnModel.getColumn(0).getWidth() / 2;
		}
		position.y += cellRect.y + UnitTable.ROW_HEIGHT / 2;
		return position;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		// stop cell editing, commit changes
		// (otherwise editing is still possible in an opened editor)
		if (!isEditable && isEditing()) {
			getCellEditor().stopCellEditing();
		}
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
		selectionModel.setFreezed(!isSelectable);
		updateCursor();
	}

	public void scrollToUnit(int iUnit) {
		JViewport viewport = (JViewport) getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle cellRect = getCellRect(iUnit, 0, true);

		// The location of the view relative to the table
		Rectangle viewRect = viewport.getViewRect();

		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0).
		cellRect.setLocation(cellRect.x - viewRect.x, cellRect.y - viewRect.y);

		// Calculate location of rect if it were at the center of view
		int centerX = (viewRect.width - cellRect.width) / 2;
		int centerY = (viewRect.height - cellRect.height) / 2;

		// Fake the location of the cell so that scrollRectToVisible
		// will move the cell to the center
		if (cellRect.x < centerX) {
			centerX = -centerX;
		}
		if (cellRect.y < centerY) {
			centerY = -centerY;
		}

		cellRect.translate(centerX, centerY);
		viewport.scrollRectToVisible(cellRect);
	}

	private void updateCursor() {
		Cursor cursor;
		// when a UnitTable is not editable, it means the game is running
		if (!isEditable) {
			// when a UnitTable is not selectable, its camp is selected for
			// action
			if (isSelectable) {
				ImageIcon cursorIcon = new ImageIcon(ImageUtil
						.getScaledImage("target.gif"));
				cursor = Toolkit.getDefaultToolkit().createCustomCursor(
						cursorIcon.getImage(), new Point(10, 10), "target");
			} else {
				ImageIcon cursorIcon = new ImageIcon(ImageUtil
						.getScaledImage("action.gif"));
				cursor = Toolkit.getDefaultToolkit().createCustomCursor(
						cursorIcon.getImage(), new Point(10, 10), "action");
			}
		} else {
			cursor = Cursor.getDefaultCursor();
		}
		setCursor(cursor);
	}

	// tooltips that depend on the row of the table
	@Override
	public String getToolTipText(MouseEvent me) {
		// priority to the tooltip returned by the renderer
		String toolTip = super.getToolTipText(me);
		if (toolTip != null)
			return toolTip;
		Point p = me.getPoint();
		int column = columnAtPoint(p);
		int row = rowAtPoint(p);
		Unit unit = (Unit) getValueAt(row, column);
		// when a UnitTable is not editable, it means the game is running
		if (!isEditable) {
			// when a table is not selectable, its camp is selected for action
			if (!isSelectable) {
				if (unit.isSelectedForAction()) {
					return unit.getName() + " is selected for action";
				} else {
					// be carefull for null pointer exception on
					// unitSelectedForAction: the game might be in transit mode
					// (still running, but no unit selected for action anymore)
					String message = unit.getName() + " waits for action";
					Unit unitSelectedForAction = unit.getCamp()
							.getUnitSelectedForAction();
					if (unitSelectedForAction != null) {
						message += " waits for action from "
								+ unitSelectedForAction.getName();
					}
					return message;
				}
			} else {
				return "click to select " + unit.getName() + " as target";
			}
		} else {
			// compute tooltip for unit detail column
			if ((isLeft && column == 1) || (!isLeft && column == 0)) {
				return unit.getUnitType().getName() + " (click to change)";
			}
		}
		return null;
	}
}
