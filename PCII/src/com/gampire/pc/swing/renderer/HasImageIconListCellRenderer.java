package com.gampire.pc.swing.renderer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.gampire.pc.swing.lookandfeel.PCIITheme;

public class HasImageIconListCellRenderer extends JLabel implements ListCellRenderer {

    public HasImageIconListCellRenderer() {
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setVerticalTextPosition(TOP);
        setHorizontalTextPosition(CENTER);
        setFont(PCIITheme.FONT);
    }

    /*
     * This method finds the image and text corresponding to the selected value
     * and returns the label, set up to display the text and image.
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        // Set the icon and text. If icon was null, say so.
        ImageIcon icon = ((HasImageIcon) value).getImageIcon();
        setIcon(icon);
        if (icon != null) {
            setText(icon.getDescription());
        } else {
            setText("icon missing");
        }
        return this;
    }

}
