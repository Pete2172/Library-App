package frame.objects;

import sqlconnection.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainWindow extends JFrame {

    private DatabaseConnector db;

    public MainWindow(){
        super();
        db = new DatabaseConnector();
        addTabbedPanels();
        setResizable(true);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                    db.closeConnection();
                    System.exit(0);
            }
        });
    }
    private void addTabbedPanels(){
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", new AdminBooksPanel(db));
        tabs.add("Borrowings", new BorrowingsPanel(db, 0));
        tabs.add("Readers", new ReaderPanel(db));
        add(tabs, BorderLayout.NORTH);
        pack();
    }

}
