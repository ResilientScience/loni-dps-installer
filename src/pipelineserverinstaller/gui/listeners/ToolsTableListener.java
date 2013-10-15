package pipelineserverinstaller.gui.listeners;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.table.TableModel;
import pipelineserverinstaller.gui.panels.BioinformaticsToolsConfigurationPanel;
import pipelineserverinstaller.gui.panels.NIToolsConfigurationPanel;


/**
 *
 * @author Petros Petrosyan
 */
public class ToolsTableListener implements MouseInputListener, ListSelectionListener {

    private ToolsTableListener(JTable paramTable,
                                TableModel tableModel) {
        this.paramTable = paramTable;
        this.tableModel = tableModel;
        clickedRow = -1;
    }
    /** Creates a new instance of ModuleParamTableListener */
    public ToolsTableListener(JTable paramTable,
                                TableModel tableModel,
                                JPanel panel) {

        this(paramTable,tableModel);
        this.panel = panel;

        if ( !(panel instanceof NIToolsConfigurationPanel) && !(panel instanceof BioinformaticsToolsConfigurationPanel))
            System.err.println("Warning: Invalid type of panel has been given to ToolsTableListener constructor. Class =" + panel.getClass().toString());
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (panel instanceof NIToolsConfigurationPanel) {
                NIToolsConfigurationPanel tp = (NIToolsConfigurationPanel) panel;
                tp.tableRowChanged();
            }
            if (panel instanceof BioinformaticsToolsConfigurationPanel) {
                BioinformaticsToolsConfigurationPanel tp = (BioinformaticsToolsConfigurationPanel) panel;
                tp.tableRowChanged();
            }

        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        clickedRow = paramTable.rowAtPoint(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
        paramTable.setCursor(Cursor.getDefaultCursor());
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        
    }

    public void mouseMoved(MouseEvent e) {
    }

    private JTable paramTable;
    private TableModel tableModel;
    private JPanel panel;
    private JDialog dialog;
    private int clickedRow;

}
