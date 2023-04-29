package models;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

	private JCheckBox checkBox = this;

    public CheckBoxRenderer() {
      setHorizontalAlignment(JLabel.CENTER);
      

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
    	
	    boolean val = false;

	    try {
            val = ((Boolean) value).booleanValue();
        } catch (Exception e) {
        	
        }
        checkBox.setSelected(val);
    
    
        return checkBox;
    }
}