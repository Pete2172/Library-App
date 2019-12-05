package frame.objects;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LoginPanel extends JPanel{
    JPanel panel;
    JLabel lcard, lpasswd, info, title;
    JTextField card;
    JPasswordField passwd;
    JButton signIn;

    public LoginPanel(){
        panel = new JPanel(new GridLayout(3, 1));
        lcard = new JLabel("Card number:", SwingConstants.CENTER);
        lpasswd = new JLabel("Password:", SwingConstants.CENTER);
        title = new JLabel("Login here:");
        info = new JLabel();

        card = new JTextField();
        passwd = new JPasswordField();

        signIn = new JButton("Sign in");

        panel.add(lcard);
        panel.add(card);
        panel.add(lpasswd);
        panel.add(passwd);
        panel.add(info);
        panel.add(signIn);
        DefaultTableModel model = new DefaultTableModel();


        add(panel, BorderLayout.CENTER);


    }

}
