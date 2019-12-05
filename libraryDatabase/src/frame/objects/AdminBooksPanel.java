package frame.objects;

import sqlconnection.DatabaseConnector;

import javax.swing.*;
import java.awt.*;

public class AdminBooksPanel extends BooksPanel {
    private JTextField title, author, publisher, published;
    private JComboBox<String> genre;
    private String[] label_names = {"Title:", "Author:", "Genre:", "Publisher:", "Published:"};

    public AdminBooksPanel(DatabaseConnector db){
        super(db);
        createAddBookPanel();

        table.getSelectionModel().addListSelectionListener(e -> { setTableItem(
                () -> {
                    title.setText((String) model.getValueAt(table.getSelectedRow(), 1));
                    author.setText((String) model.getValueAt(table.getSelectedRow(), 2));
                    genre.setSelectedItem((String) model.getValueAt(table.getSelectedRow(), 3));
                    publisher.setText((String) model.getValueAt(table.getSelectedRow(), 4));
                    published.setText((String) model.getValueAt(table.getSelectedRow(), 5));
                }
        );});
    }

    private void createAddBookPanel(){
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        panel.setLayout(new GridLayout(0, 6));

        for(int i = 0; i < 5; i++)
                panel.add(new JLabel(label_names[i]), i);
        JButton remove = new JButton("Remove selection");

     /** Create remove Button **/
        remove.addActionListener(e -> {
            if(!table.getSelectionModel().isSelectionEmpty()){
                int row = table.getSelectedRow();
                removeItem((Integer) model.getValueAt(row, 0), row);
            }
            else{
                JOptionPane.showMessageDialog(null, "No record is selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(remove);

     /** Create text fields **/
        title = new JTextField();
        author = new JTextField();
        publisher = new JTextField();
        published = new JTextField();
        genre = new JComboBox<>(genres);

        panel.add(title);
        panel.add(author);
        panel.add(genre);
        panel.add(publisher);
        panel.add(published);

    /** Create add button **/
        JButton add_button = new JButton("Add a book");
        add_button.addActionListener(e -> { addItem(); });
        panel.add(add_button);
        for(int i=0; i < 5; i++)
            panel.add(Box.createGlue());
        JButton update_button = new JButton("Update");
        update_button.addActionListener(e -> {
                if(!table.getSelectionModel().isSelectionEmpty()) {
                    int row = table.getSelectedRow();
                    final String[] args = {(String) genre.getSelectedItem(), (String) author.getText(), (String) title.getText(), publisher.getText(), published.getText(), String.valueOf(model.getValueAt(row, 0)) };
                    updateItem(columnNames, () -> { db.doQuery("UPDATE books SET genre=\"" + args[0] + "\", author=\"" + args[1] + "\", title=\"" + args[2] + "\", publisher=\"" +
                            args[3] + "\", published=\"" + args[4] + "\" WHERE id=\"" + args[5] + "\";"); });
                }
                else{
                    JOptionPane.showMessageDialog(null, "No record is selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });
        panel.add(update_button);
        add(panel, BorderLayout.SOUTH);
    }

    private synchronized void removeItem(int id, int row){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                db.deleteBookById(id);
                return true;
            }
            @Override
            protected void done(){
                model.removeRow(row);
                JOptionPane.showMessageDialog(null, "Selected item has been deleted successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    private synchronized void addItem(){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    db.insertBook((String) genre.getSelectedItem(), author.getText(), title.getText(), publisher.getText(), published.getText(), "free", "default");
                    title.setText("");
                    author.setText("");
                    publisher.setText("");
                    published.setText("");
                    JOptionPane.showMessageDialog(null, "A new book has been added to the database!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Something went wrong with adding the book: \n" + e.getMessage() , "Info", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            @Override
            protected void done(){
                executeQuery(columnNames, createQuery());
            }
        };
        worker.execute();
    }

}
