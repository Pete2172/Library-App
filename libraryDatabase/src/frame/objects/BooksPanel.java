package frame.objects;

import objects.Book;
import sqlconnection.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BooksPanel extends PanelTemplate {

    String[] genres = {"fantasy", "realistic fiction", "science fiction", "drama", "historical fiction", "horror", "poetry", "short story"};
    String[] columnNames = {"ID", "Title", "Author", "Genre", "Publisher", "Published", "Status"};

    private List<Book> books;

    private JList<String> list1, list2;
    private JButton button, find;
    private JTextField findIn;

    public BooksPanel(DatabaseConnector db){
        super(db);
        createLists();
        createTableData(this.columnNames);
        displayTableData("SELECT * FROM books");
        createLayout();
    }
    @Override
    void displayTableData(String query){
        books = db.listUsersQuery(query, "Book");
        if(!books.isEmpty()) {
            for (Book x : books) {
                model.addRow(new Object[]{x.getId(), x.getTitle(), x.getAuthor(), x.getGenre(), x.getPublisher(), x.getPublished(), x.getStatus()});
            }
        }
    }
    private void createLists(){
        DefaultListModel<String> model1 = new DefaultListModel<>();
        model1.addElement("all");
        for(int i = 0; i < genres.length; i++)
            model1.addElement(genres[i]);
        list1 = new JList<>(model1);
        list1.setSelectedIndex(0);
        list1.setAlignmentX(LEFT_ALIGNMENT);

        DefaultListModel<String> model2 = new DefaultListModel<>();
        model2.addElement("all");
        model2.addElement("free");
        model2.addElement("borrowed");
        list2 = new JList<>(model2);
        list2.setSelectedIndex(0);
        list2.setAlignmentX(LEFT_ALIGNMENT);



        button = new JButton("Ok");
        button.addActionListener(e -> executeQuery(columnNames, createQuery()));
    }
    private void createLayout(){
        this.setLayout(new BorderLayout(8, 6));
        this.setBackground(Color.YELLOW);

        JPanel listPanel = new JPanel();
        listPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JLabel lab2 = new JLabel("Show by genre:");
        lab2.setForeground(Color.BLUE);
        listPanel.add(lab2);
        listPanel.add(list1);
        listPanel.add(Box.createRigidArea(new Dimension(0, 7)));

        JLabel lab1 = new JLabel("Show by status:");
        lab1.setForeground(Color.BLUE);

        listPanel.add(lab1);
        listPanel.add(list2);
        listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        listPanel.add(button);
        this.add(listPanel, BorderLayout.WEST);
        scroll.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        this.add(scroll, BorderLayout.CENTER);

        list1.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        createUpperPanel();
    }
    private void createUpperPanel(){
        /** create Upper Panel **/
        JPanel upp_panel = new JPanel();
        upp_panel.setLayout(new GridLayout(0, 4));
        upp_panel.add(new JLabel("Find by a title or an author:"));
        upp_panel.add(findIn = new JTextField());
        upp_panel.add(Box.createGlue());
        find = new JButton("Find");
        find.addActionListener(e->{
            executeQuery(columnNames, "SELECT * FROM books WHERE author LIKE \'%" + findIn.getText() + "%\' OR title LIKE\'%" + findIn.getText() + "%\';");
        });
        upp_panel.add(find);
        upp_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        add(upp_panel, BorderLayout.NORTH);
    }

    @Override
    String createQuery(){
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM books");
        String option1 = list1.getSelectedValue();
        String option2 = list2.getSelectedValue();
        if("all".equals(option1) && "all".equals(option2))
            return query.toString();
        else{
            if(!"all".equals(option1) && "all".equals(option2)){
                query.append(" WHERE genre = \"" + option1 + "\"");
            }
            else if("all".equals(option1)){
                query.append(" WHERE status = \"" + option2 + "\"");
            }
            else{
                query.append(" WHERE status = \"" + option2 + "\" AND genre = \"" + option1 + "\"");
            }
        }
        return query.toString();
    }
}
