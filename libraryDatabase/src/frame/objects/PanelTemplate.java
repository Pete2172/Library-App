package frame.objects;

import sqlconnection.DatabaseConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public abstract class PanelTemplate extends JPanel {
    JTable table;
    DefaultTableModel model;
    JScrollPane scroll;
    DatabaseConnector db;


    public PanelTemplate(DatabaseConnector db){
        this.db = db;
    }

     void createTableData(String[] columnNames){
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.setColumnIdentifiers(columnNames);
        table = new JTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }
    abstract String createQuery();
    abstract void displayTableData(String query);

    protected synchronized void executeQuery(String[] columnNames, String query){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    model = new DefaultTableModel(){
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    model.setColumnIdentifiers(columnNames);
                    table.setModel(model);
                    displayTableData(query);
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Something went wrong after listing table! \n"
                            + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return true;
            }
        };
        worker.run();
    }

    protected synchronized void updateItem(String[] columnNames, Runnable f){
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() {
            f.run();
            return true;
        }
        @Override
        protected void done(){
            executeQuery(columnNames, createQuery());
            JOptionPane.showMessageDialog(null, "The table has been updated!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    };
    worker.execute();
}

    protected synchronized void setTableItem(Runnable f){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                f.run();
                return true;
            }
        };
        worker.execute();
    }

}
