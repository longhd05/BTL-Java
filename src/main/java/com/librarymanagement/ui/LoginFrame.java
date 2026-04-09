package com.librarymanagement.ui;

import com.librarymanagement.model.User;
import com.librarymanagement.service.AuthService;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;

    public LoginFrame(AuthService authService, BookService bookService, LoanService loanService) {
        this.authService = authService;
        this.bookService = bookService;
        this.loanService = loanService;

        setTitle("Đăng nhập - Quản lý thư viện");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));

        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User user = authService.login(username, password);

                MainFrame mainFrame = new MainFrame(authService, bookService, loanService, user);
                mainFrame.setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);
    }
}
