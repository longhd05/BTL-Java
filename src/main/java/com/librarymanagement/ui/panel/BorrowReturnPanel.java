package com.librarymanagement.ui.panel;

import com.librarymanagement.model.LoanRecord;
import com.librarymanagement.model.User;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;
import com.librarymanagement.util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class BorrowReturnPanel extends JPanel {
    private final LoanService loanService;
    private final BookListPanel bookListPanel;
    private final User currentUser;

    private final JTextField bookIdField;
    private final DefaultTableModel loanTableModel;

    public BorrowReturnPanel(LoanService loanService, BookService bookService, User currentUser) {
        this.loanService = loanService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Mượn / Trả sách"));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(new JLabel("Mã sách:"));
        bookIdField = new JTextField(12);
        actionPanel.add(bookIdField);

        JButton borrowButton = new JButton("Mượn sách");
        borrowButton.addActionListener(e -> borrowBook());
        actionPanel.add(borrowButton);

        JButton returnButton = new JButton("Trả sách");
        returnButton.addActionListener(e -> returnBook());
        actionPanel.add(returnButton);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> refreshData());
        actionPanel.add(refreshButton);

        add(actionPanel, BorderLayout.NORTH);

        bookListPanel = new BookListPanel(bookService);
        add(bookListPanel, BorderLayout.CENTER);

        loanTableModel = new DefaultTableModel(new Object[]{"Mã sách", "Tên sách", "Ngày mượn", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable loanTable = new JTable(loanTableModel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Phiếu mượn của tôi"));
        bottomPanel.add(new JScrollPane(loanTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        bookListPanel.refreshData();
        loanTableModel.setRowCount(0);
        List<LoanRecord> loans = loanService.getLoansForUser(currentUser);
        for (LoanRecord loan : loans) {
            loanTableModel.addRow(new Object[]{
                    loan.getBookId(),
                    loan.getBookName(),
                    loan.getBorrowDate(),
                    loan.isReturned() ? "Đã trả" : "Đang mượn"
            });
        }
    }

    private void borrowBook() {
        try {
            String id = ValidationUtil.requireNotBlank(bookIdField.getText(), "Mã sách");
            loanService.borrowBook(currentUser, id);
            refreshData();
            JOptionPane.showMessageDialog(this, "Mượn sách thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        try {
            String id = ValidationUtil.requireNotBlank(bookIdField.getText(), "Mã sách");
            loanService.returnBook(currentUser, id);
            refreshData();
            JOptionPane.showMessageDialog(this, "Trả sách thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
