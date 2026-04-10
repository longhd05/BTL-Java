package com.librarymanagement.ui.panel;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.User;
import com.librarymanagement.service.BookService;
import com.librarymanagement.util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class BookFormPanel extends JPanel {
    private final BookService bookService;
    private final User currentUser;

    private final JTextField idField = new JTextField();
    private final JTextField tenSachField = new JTextField();
    private final JTextField soLuongField = new JTextField();
    private final JTextField chuDeField = new JTextField();
    private final JTextField tacGiaField = new JTextField();

    private final DefaultTableModel tableModel;
    private final JTable table;

    public BookFormPanel(BookService bookService, User currentUser) {
        this.bookService = bookService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Quản lý sách (Admin)"));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 6, 6));
        formPanel.add(new JLabel("Mã sách:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Tên sách:"));
        formPanel.add(tenSachField);
        formPanel.add(new JLabel("Số lượng:"));
        formPanel.add(soLuongField);
        formPanel.add(new JLabel("Chủ đề:"));
        formPanel.add(chuDeField);
        formPanel.add(new JLabel("Tác giả:"));
        formPanel.add(tacGiaField);

        JButton addButton = new JButton("Thêm sách");
        addButton.addActionListener(e -> addBook());

        JButton updateButton = new JButton("Sửa sách");
        updateButton.addActionListener(e -> updateBook());

        JButton deleteButton = new JButton("Xóa sách");
        deleteButton.addActionListener(e -> deleteBook());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        formPanel.add(new JLabel("Tác vụ:"));
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Mã sách", "Tên sách", "Số lượng", "Chủ đề", "Tác giả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Book> books = bookService.getAllBooksSorted();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTenSach(),
                    book.getSoLuong(),
                    book.getChuDe(),
                    book.getTacGia()
            });
        }
    }

    private Book readBookFromForm() {
        String id = ValidationUtil.requireNotBlank(idField.getText(), "Mã sách");
        String tenSach = ValidationUtil.requireNotBlank(tenSachField.getText(), "Tên sách");
        int soLuong = ValidationUtil.parseSoLuong(soLuongField.getText());
        String chuDe = ValidationUtil.requireNotBlank(chuDeField.getText(), "Chủ đề");
        String tacGia = ValidationUtil.requireNotBlank(tacGiaField.getText(), "Tác giả");
        return new Book(id, tenSach, soLuong, chuDe, tacGia);
    }

    private void addBook() {
        try {
            Book book = readBookFromForm();
            bookService.addBook(currentUser, book);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Thêm sách thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        try {
            Book book = readBookFromForm();
            bookService.updateBook(currentUser, book.getId(), book);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Cập nhật sách thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        try {
            String id = ValidationUtil.requireNotBlank(idField.getText(), "Mã sách");
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            bookService.deleteBook(currentUser, id);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa sách thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        tenSachField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        soLuongField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        chuDeField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        tacGiaField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
    }

    private void clearForm() {
        idField.setText("");
        tenSachField.setText("");
        soLuongField.setText("");
        chuDeField.setText("");
        tacGiaField.setText("");
    }
}
