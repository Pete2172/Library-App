package frame.objects;

import objects.Book;
import objects.Borrowing;
import objects.Reader;
import sqlconnection.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.Period;

public class BorrowingsPanel extends PanelTemplate {
    int id;
    private String[] columnNames = {"ID", "Reader's ID", "Book's ID", "Borrowing Date", "Date of Return", "Fine"};
    private List<Borrowing> borrowings;
    private JLabel reader_name, reader_address, reader_city, book_title, book_author;
    private JComboBox<String> combo;
    private JTextField id_reader, id_book;

    public BorrowingsPanel(DatabaseConnector db, int id){
        super(db);
        this.id = id;
        createLayout();
        table.getSelectionModel().addListSelectionListener(e-> {
            selectItem();
        });

    }

    @Override
    String createQuery() {
        StringBuilder str = new StringBuilder();
        str.append("SELECT * FROM borrowing");

        if(id != 0){
            str.append(" WHERE id_reader=\"" + id + "\"");
            if("history".equals(combo.getSelectedItem()))
                str.append(" AND return_date !=\"\"");
            else if("current".equals(combo.getSelectedItem()))
                str.append(" AND return_date = \"\"");
        }
        else{
            if("history".equals(combo.getSelectedItem()))
                str.append(" WHERE return_date !=\"\"");
            else if("current".equals(combo.getSelectedItem()))
                str.append(" WHERE return_date = \"\"");
        }
        return str.toString();
    }

    @Override
    void displayTableData(String query) {
        borrowings = db.listUsersQuery(query, "Borrowing");
        if(!borrowings.isEmpty()) {
            for (Borrowing x : borrowings) {
                try {
                    model.addRow(new Object[]{x.getId(), x.getId_reader(), x.getId_book(), x.getBorrow_date(), x.getReturn_date(), x.countFine()});
                }
                catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Something went wrong with listing borowings: \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void createLayout(){
        this.setLayout(new BorderLayout(8, 6));
        this.setBackground(Color.YELLOW);

        createLeftDataPanel();
        createBottomPanel();
        createTableData(columnNames);
        displayTableData(createQuery());
        scroll.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        this.add(scroll, BorderLayout.CENTER);
    }
    private void createLeftDataPanel(){
        JPanel left = new JPanel();
        left.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

        left.setLayout(new GridLayout(20, 0));
        left.add(new JLabel("Reader:"));
        left.add(reader_name = new JLabel());
        reader_name.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        left.add(reader_address = new JLabel());
        left.add(reader_city = new JLabel());
        reader_city.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        left.add(new JLabel("Book:"));
        left.add(book_title = new JLabel());
        book_title.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));
        left.add(book_author = new JLabel());
        left.add(Box.createGlue());
        left.add(new JLabel("Show:"));
        combo = new JComboBox<>();
        combo.addItem("all");
        combo.addItem("current");
        combo.addItem("history");
        combo.addActionListener(e -> {
            executeQuery(columnNames, createQuery());
        });
        left.add(combo);

        this.add(left, BorderLayout.WEST);
    }

    private synchronized void addItem(){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                if(db.insertBorrowing("", id_reader.getText(), id_book.getText())) {
                    id_reader.setText("");
                    id_book.setText("");
                    JOptionPane.showMessageDialog(null, "A new borrowing has been added to the database!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
                else{
                    JOptionPane.showMessageDialog(null, "Someone has already borrowed this book! \n", "Error", JOptionPane.ERROR_MESSAGE);
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

    private synchronized void selectItem(){
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Reader user = (Reader) db.listUsersQuery("SELECT * FROM readers WHERE id=\"" + model.getValueAt(table.getSelectedRow(), 1) + "\"", "Reader").get(0);
                reader_name.setText(user.getName() + " " + user.getSurname());
                reader_address.setText(user.getAddress());
                reader_city.setText(user.getCity());
                Book book = (Book) db.listUsersQuery("SELECT * FROM books WHERE id=\"" + model.getValueAt(table.getSelectedRow(), 2) + "\"", "Book").get(0);
                book_title.setText(book.getTitle());
                book_author.setText(book.getAuthor());
                final int _bookID = book.getId();
                final int _readerID = user.getId();

                setTableItem(()->{
                        id_book.setText(String.valueOf(_bookID));
                        id_reader.setText(String.valueOf(_readerID));
                });

                return true;
            }
        };
        worker.run();
    }

    private void createBottomPanel(){
        JPanel panel = new JPanel(new GridLayout(0, 3));
        panel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        panel.add(new JLabel("Book ID:"));
        panel.add(new JLabel("Reader ID:"));

        JButton return_button = new JButton("Return");
        return_button.addActionListener( e ->{
            updateItem(columnNames, () -> {
                    db.returnBook(Integer.parseInt(id_book.getText()), Integer.parseInt(id_reader.getText()));
            });
        });
        panel.add(return_button);

        id_reader = new JTextField();
        id_book = new JTextField();

        panel.add(id_book);
        panel.add(id_reader);

        JButton add_button = new JButton("Add");
        add_button.addActionListener( e-> {
            addItem();
        });
        panel.add(add_button);
        this.add(panel, BorderLayout.SOUTH);
    }

}
