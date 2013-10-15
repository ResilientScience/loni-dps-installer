package pipelineserverinstaller.gui.models;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Petros Petrosyan
 */
public class ToolsTableModel extends AbstractTableModel {
    public ToolsTableModel() {
        super();

        tableData = new ArrayList<ArrayList<Object>>();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == COLUMN_SELECTED)
            return true;
        else
            return false;
    }

    @Override
    public Class getColumnClass(int col) {
        if ( col == COLUMN_SELECTED )
            return Boolean.class;
        else
            return String.class;
    }

    public int getRowCount() {
        return tableData.size();
    }

    public int getColumnCount() {
        return COLUMN_COUNT - 3;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case COLUMN_SELECTED:
                return "Selected";
            case COLUMN_NAME:
                return "Name";
            case COLUMN_VERSION:
                return "Version";
            case COLUMN_SIZE:
                return "Size";
            case COLUMN_INST_TYPE:
                return "Installation type";
            case COLUMN_DESCRIPTION:
                return "Not Visible";
            case COLUMN_CONFIGFIELD_NAME:
                return "Not Visible";
            case COLUMN_CONFIGFIELD_VERSION:
                return "Not Visible";
            default:
                throw new ArrayIndexOutOfBoundsException("No such column exists in the table: " + col);
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableData.get(rowIndex).get(columnIndex);
    }

    public void addRow(Object[] rowData) {
        if (rowData == null)
            throw new NullPointerException("Can't add a null row");
        if (rowData.length != COLUMN_COUNT)
            throw new IllegalArgumentException("Row length must be " + COLUMN_COUNT + ". Array of length " + rowData.length + " was given.");

        ArrayList<Object> row = new ArrayList<Object>(COLUMN_COUNT);
        for (int i = 0; i<rowData.length; i++)
            row.add(rowData[i]);
        tableData.add(row);

        int newRow = tableData.size()-1;
        fireTableRowsInserted(newRow, newRow);
    }

     public void insertRow(int row, Object[] data) {
        if (data.length != COLUMN_COUNT)
            throw new ArrayIndexOutOfBoundsException("Incorrect number of elements ("+data.length+") in array.");
        ArrayList<Object> dataList = new ArrayList<Object>();

        for (int i=0; i<data.length; i++) {
            dataList.add(data[i]);
        }
        tableData.add(row, dataList);

        fireTableRowsInserted(row, row);
    }

    public void removeRow(int row) {
        tableData.remove(row);

        fireTableRowsDeleted(row, row);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tableData.get(rowIndex).set(columnIndex, aValue);

        fireTableCellUpdated(rowIndex, columnIndex);
    }

    

    public final static int COLUMN_SELECTED             = 0;
    public final static int COLUMN_NAME                 = 1;
    public final static int COLUMN_VERSION              = 2;
    public final static int COLUMN_SIZE                 = 3;
    public final static int COLUMN_INST_TYPE            = 4;
    public final static int COLUMN_DESCRIPTION          = 5;
    public final static int COLUMN_CONFIGFIELD_NAME     = 6;
    public final static int COLUMN_CONFIGFIELD_VERSION  = 7;

    public final int COLUMN_COUNT              = 8;

    
    public final static String INST_TYPE_AUTO   = "Full Auto";
    public final static String INST_TYPE_MANUAL = "Manual Download, Auto Install";



    private ArrayList<ArrayList<Object>> tableData;

    // just to make the compiler stop complaining
    static final long serialVersionUID = 1L;
}
