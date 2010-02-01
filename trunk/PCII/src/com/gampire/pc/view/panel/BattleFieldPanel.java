package com.gampire.pc.view.panel;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gampire.pc.control.UnitTableMouseListener;
import com.gampire.pc.model.BattleField;
import com.gampire.pc.model.Camp;
import com.gampire.pc.model.Unit;
import com.gampire.pc.swing.cursor.CursorUtil;
import com.gampire.pc.util.image.ImageUtil;
import com.gampire.pc.view.table.UnitTable;
import com.gampire.pc.view.table.UnitTableModel;

public class BattleFieldPanel extends JPanel implements ListSelectionListener {

    private BattleField battleField;

    protected final UnitTableModel unitTableModel1;

    protected final UnitTableModel unitTableModel2;

    private final UnitTable unitTable1;

    private final UnitTable unitTable2;

    private final JScrollPane scrollPane1;

    private final JScrollPane scrollPane2;

    private final CampControlPanel campControl1;

    private final CampControlPanel campControl2;

    private final LogPanel logPanel = new LogPanel();

    private BattleFieldControlPanel battleFieldControlPanel;

    //previous images were moved to etc/resources
    //private final ImageIcon background = new ImageIcon(ImageUtil.getScaledImage("battleField.jpg"));
    //private final ImageIcon background = new ImageIcon(ImageUtil.getScaledImage("longHorn.bmp"));
    private final static BufferedImage background = ImageUtil.getScaledImage("germanLandscape.jpg");
      
    private static BufferedImage scaledBackground; 
    
    public BattleFieldPanel(BattleField battleField) {
        super(new GridBagLayout());

        this.battleField = battleField;

        unitTableModel1 = battleField.getUnitTableModel1();
        unitTableModel2 = battleField.getUnitTableModel2();

        unitTable1 = new UnitTable(unitTableModel1, true);
        unitTable2 = new UnitTable(unitTableModel2, false);

        // Create the battleFieldControlPanel
        battleFieldControlPanel = new BattleFieldControlPanel(battleField);

        // add a UnitTableMouseListener to both tables
        unitTable1.addMouseListener(new UnitTableMouseListener(battleField.getCamp1(), battleFieldControlPanel));
        unitTable2.addMouseListener(new UnitTableMouseListener(battleField.getCamp2(), battleFieldControlPanel));

        // add this as listSelectionListener of the table models
        unitTable1.getSelectionModel().addListSelectionListener(this);
        unitTable2.getSelectionModel().addListSelectionListener(this);

        // Create the campControlPanels
        campControl1 = new CampControlPanel(unitTable1);
        campControl2 = new CampControlPanel(unitTable2);

        // Create the scroll pane and add the table to it through the Panel
        scrollPane1 = new JScrollPane(unitTable1);
        scrollPane2 = new JScrollPane(unitTable2);
        
        // vertical scrollbar on the left side for left table
        scrollPane1.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

        // scrollpanes and their viewports not opaque
        scrollPane1.setOpaque(false);
        scrollPane2.setOpaque(false);
        scrollPane1.getViewport().setOpaque(false);
        scrollPane2.getViewport().setOpaque(false);

        // add the components
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(battleFieldControlPanel, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 5, 0);
        add(campControl1, c);
        c.gridx = 1;
        add(campControl2, c);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 10, 0, 5);
        add(scrollPane1, c);
        c.gridx = 1;
        c.insets = new Insets(0, 5, 0, 10);
        add(scrollPane2, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(10, 10, 10, 10);
        add(logPanel, c);
    }

    @Override
	public void paintComponent(Graphics g) {
        // make sure the same part of the image is seen as when us camp was at
        // right side
        //g.drawImage(background.getImage(), getWidth() - background.getImage().getWidth(null), 0, null);
    	scaledBackground=ImageUtil.computeScaledImage(background,getWidth(),getHeight());
        g.drawImage(scaledBackground, 0, 0, null);
    }
    
    public void scrollToUnit(Unit unit, Window parentWindow) {
        Camp camp = unit.getCamp();

        UnitTable unitTable = camp.isLeft() ? unitTable1 : unitTable2;

        int iUnit = camp.getIndex(unit);

        unitTable.scrollToUnit(iUnit);

        if (parentWindow != null) {
            // position cursor
            JScrollPane scrollPane = camp.isLeft() ? scrollPane1 : scrollPane2;
            Point relativePosition = scrollPane.getLocation();
            Point cellLocationInTable = unitTable.getUnitPosition(iUnit);
            relativePosition.x += cellLocationInTable.x;
            relativePosition.y += cellLocationInTable.y;
            CursorUtil.positionCursor(parentWindow, relativePosition);
        }
    }

    public void setSelectable(Camp camp, boolean isSelectable) {
        UnitTable unitTable = camp.isLeft() ? unitTable1 : unitTable2;
        unitTable.setSelectable(isSelectable);
    }

    public BattleField getBattleField() {
        return battleField;
    }

    public Unit getSelectedUnit(Camp camp) {
        UnitTable table = camp.isLeft() ? unitTable1 : unitTable2;
        int iUnitColumn = camp.isLeft() ? 0 : 1;
        if (table.getRowCount() == 1) {
            return (Unit) table.getValueAt(0, iUnitColumn);
        }
        int iSelectedRow = table.getSelectedRow();
        if (iSelectedRow > -1 && iSelectedRow < table.getRowCount()) {
            return (Unit) table.getValueAt(iSelectedRow, iUnitColumn);
        } else {
            return null;
        }
    }

    public Unit selectMostProbableTarget(Unit unitSelectedForAction) {
        Camp campSelectedForAction = unitSelectedForAction.getCamp();
        UnitTable targetUnitTable = campSelectedForAction.isLeft() ? unitTable2 : unitTable1;
        int iUnitColumn = campSelectedForAction.isLeft() ? 1 : 0;
        // if there is only one enemy unit left, select it
        if (targetUnitTable.getRowCount() == 1) {
            targetUnitTable.setRowSelectionInterval(0, 0);
            return (Unit) targetUnitTable.getValueAt(0, iUnitColumn);
        } else {
        	// take closest enemy unit as most probable target
            Unit unitSelectedAsTarget = unitSelectedForAction.getClosestEnemyUnit();
            if (unitSelectedAsTarget != null) {
                for (int iRow = 0; iRow < targetUnitTable.getRowCount(); iRow++) {
                    if (targetUnitTable.getValueAt(iRow, iUnitColumn) == unitSelectedAsTarget) {
                        targetUnitTable.setRowSelectionInterval(iRow, iRow);
                        break;
                    }
                }
            }
            return unitSelectedAsTarget;
        }
    }

    public void fireTableDataChanged(Camp camp) {
        final boolean campToUpdateIsLeft = camp.isLeft();
        // do UI related stuff on the AWT thread
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    if (campToUpdateIsLeft) {
                        unitTableModel1.fireTableDataChanged();
                    } else {
                        unitTableModel2.fireTableDataChanged();
                    }
                }
            });
        } catch (InterruptedException e) {
        	//nothing to do
        } catch (InvocationTargetException e) {
        	//nothing to do
        }
    }

    public void setEditable(boolean isEditable) {
        unitTable1.setEditable(isEditable);
        unitTable2.setEditable(isEditable);
        campControl1.setActivated(isEditable);
        campControl2.setActivated(isEditable);
    }

    // what to do when a new selection is made on one of the tables
    public void valueChanged(ListSelectionEvent e) {
        battleFieldControlPanel.updateButtons();
    }

    public void inform(String message) {
        logPanel.inform(message);
    }

	public static BufferedImage getScaledBackground() {
		return scaledBackground;
	}

}
