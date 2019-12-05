package frame.objects;

import objects.Reader;
import sqlconnection.DatabaseConnector;

import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * Class creates panel, which displays all readers in database
 */
public class ReaderPanel extends PanelTemplate {

        String[] columnNames = {"ID", "Name", "Surname", "Address", "City"};
        List<Reader> readers;
        JTextField name, surname, address, city;

        public ReaderPanel(DatabaseConnector db){
            super(db);
            createLayout();

        }

    @Override
    String createQuery() {
        return "SELECT * FROM readers";
    }

    @Override
    void displayTableData(String query) {
        readers = db.listUsersQuery(query, "Reader");
        for(Reader x : readers)
            if(!readers.isEmpty())
                model.addRow(new Object[] {x.getId(), x.getName(), x.getSurname(), x.getAddress(), x.getCity()});
    }
    private void createLayout(){
            this.setLayout(new BorderLayout(8, 6));
            createTableData(columnNames);
            displayTableData("SELECT * FROM readers");
            this.setBackground(Color.YELLOW);
            scroll.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

            createPanelBelow();
            createUpperPanel();

            table.getSelectionModel().addListSelectionListener(e -> { setTableItem(
                () -> {
                    name.setText((String) model.getValueAt(table.getSelectedRow(), 1));
                    surname.setText((String) model.getValueAt(table.getSelectedRow(), 2));
                    address.setText((String) model.getValueAt(table.getSelectedRow(), 3));
                    city.setText((String) model.getValueAt(table.getSelectedRow(), 4));
                }
             );});

            this.add(scroll, BorderLayout.CENTER);
    }
    private void createPanelBelow(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 5));
        panel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        String[] labels = {"Name:", "Surname", "Address:", "City:"};
        JButton update_button = new JButton("Update");
        JButton add_button = new JButton("Add");
        name = new JTextField();
        surname = new JTextField();
        address = new JTextField();
        city = new JTextField();

        for(int i=0; i < 4; i++)
            panel.add(new JLabel(labels[i]));

        update_button.addActionListener(e->{
            if(!table.getSelectionModel().isSelectionEmpty()) {
                int row = table.getSelectedRow();
                final String[] args = {name.getText(), surname.getText(), address.getText(), city.getText(), String.valueOf(model.getValueAt(table.getSelectedRow(), 0)) };
                updateItem(columnNames, () -> {
                        db.doQuery("UPDATE readers SET name=\"" + args[0] + "\", surname=\"" + args[1] + "\", address=\"" + args[2] + "\", city=\"" +
                                args[3] + "\" WHERE id=\"" + args[4] + "\";");
                 });
            }
            else{
                JOptionPane.showMessageDialog(null, "No record is selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add_button.addActionListener(e->{
            addItem();
        });

        panel.add(update_button);

        panel.add(name);
        panel.add(surname);
        panel.add(address);
        panel.add(city);



        panel.add(add_button);

        this.add(panel, BorderLayout.SOUTH);
    }
    private void createUpperPanel(){
        JTextField findIn = new JTextField();
        JButton find = new JButton("Find");
        JButton reset = new JButton("Reset");

        JPanel upp_panel = new JPanel();
        upp_panel.setLayout(new GridLayout(0, 3));
        upp_panel.add(new JLabel("Find by a name, surname or address:"));
        upp_panel.add(Box.createGlue());

        find.addActionListener(e->{
            executeQuery(columnNames, "SELECT * FROM readers WHERE name LIKE \'%" + findIn.getText() + "%\' OR surname LIKE\'%" + findIn.getText() + "%\' OR address LIKE\'%" +
                      findIn.getText() + "%\'" + ";");
        });
        reset.addActionListener(e->{
            executeQuery(columnNames, "SELECT * FROM readers;");
        });

        upp_panel.add(reset);
        upp_panel.add(findIn);
        upp_panel.add(Box.createGlue());
        upp_panel.add(find);

        upp_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        this.add(upp_panel, BorderLayout.NORTH);
    }
    private synchronized void addItem(){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                    db.insertReader(name.getText(), surname.getText(), "", city.getText(), address.getText());
                    name.setText("");
                    surname.setText("");
                    city.setText("");
                    address.setText("");
                    JOptionPane.showMessageDialog(null, "A new reader has been added to the database!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return true;
            }
            @Override
            protected void done(){
                executeQuery(columnNames, createQuery());
            }
        };
        worker.execute();
    }
}
