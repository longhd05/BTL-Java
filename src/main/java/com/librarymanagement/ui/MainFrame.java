package com.librarymanagement.ui;

import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.service.AuthService;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;
import com.librarymanagement.ui.panel.BookFormPanel;
import com.librarymanagement.ui.panel.BookListPanel;
import com.librarymanagement.ui.panel.BorrowReturnPanel;
import com.librarymanagement.ui.panel.SearchPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class MainFrame extends JFrame {
    private static final String CARD_BOOK_LIST = "BOOK_LIST";
    private static final String CARD_BOOK_MANAGE = "BOOK_MANAGE";
    private static final String CARD_SEARCH = "SEARCH";
    private static final String CARD_BORROW_RETURN = "BORROW_RETURN";

    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;
    private final User currentUser;

    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final BookListPanel bookListPanel;
    private final SearchPanel searchPanel;
    private BookFormPanel bookFormPanel;
    private BorrowReturnPanel borrowReturnPanel;

    public MainFrame(AuthService authService, BookService bookService, LoanService loanService, User currentUser) {
        this.authService = authService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.currentUser = currentUser;

        setTitle("Ứng dụng quản lý thư viện");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Header đơn giản, luôn hiển thị tên và role hiện tại.
        JLabel headerLabel = new JLabel(currentUser.getDisplayName() + " - " + currentUser.getRole(), SwingConstants.LEFT);
        headerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        add(headerLabel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 8, 8));
        menuPanel.setPreferredSize(new Dimension(220, 0));

        bookListPanel = new BookListPanel(bookService);
        searchPanel = new SearchPanel(bookService);
        contentPanel.add(bookListPanel, CARD_BOOK_LIST);
        contentPanel.add(searchPanel, CARD_SEARCH);

        JButton danhSachButton = new JButton("Danh sách sách");
        danhSachButton.addActionListener(e -> showCard(CARD_BOOK_LIST));
        menuPanel.add(danhSachButton);

        if (currentUser.getRole() == Role.ADMIN) {
            bookFormPanel = new BookFormPanel(bookService, currentUser);
            contentPanel.add(bookFormPanel, CARD_BOOK_MANAGE);

            JButton quanLyButton = new JButton("Quản lý sách");
            quanLyButton.addActionListener(e -> showCard(CARD_BOOK_MANAGE));
            menuPanel.add(quanLyButton);
        }

        if (currentUser.getRole() == Role.USER) {
            borrowReturnPanel = new BorrowReturnPanel(loanService, bookService, currentUser);
            contentPanel.add(borrowReturnPanel, CARD_BORROW_RETURN);

            JButton borrowReturnButton = new JButton("Mượn / Trả sách");
            borrowReturnButton.addActionListener(e -> showCard(CARD_BORROW_RETURN));
            menuPanel.add(borrowReturnButton);
        }

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> showCard(CARD_SEARCH));
        menuPanel.add(searchButton);

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> logout());
        menuPanel.add(logoutButton);

        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        showCard(CARD_BOOK_LIST);
    }

    private void showCard(String cardName) {
        if (CARD_BOOK_LIST.equals(cardName)) {
            bookListPanel.refreshData();
        } else if (CARD_SEARCH.equals(cardName)) {
            searchPanel.search();
        } else if (CARD_BOOK_MANAGE.equals(cardName) && bookFormPanel != null) {
            bookFormPanel.refreshTable();
        } else if (CARD_BORROW_RETURN.equals(cardName) && borrowReturnPanel != null) {
            borrowReturnPanel.refreshData();
        }
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, cardName);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        authService.logout();
        LoginFrame loginFrame = new LoginFrame(authService, bookService, loanService);
        loginFrame.setVisible(true);
        dispose();
        JOptionPane.showMessageDialog(loginFrame, "Đã đăng xuất", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
