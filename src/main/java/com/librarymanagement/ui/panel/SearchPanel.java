package com.librarymanagement.ui.panel;

import com.librarymanagement.model.Book;
import com.librarymanagement.service.BookService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

public class SearchPanel extends JPanel {
    private final BookService bookService;
    private final JTextField keywordField;
    private final JTabbedPane resultTabs;

    public SearchPanel(BookService bookService) {
        this.bookService = bookService;
        this.keywordField = new JTextField(25);
        this.resultTabs = new JTabbedPane();

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Tìm kiếm sách"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Từ khóa (tên sách/tác giả/chủ đề):"));
        searchBar.add(keywordField);

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> search());
        searchBar.add(searchButton);

        JButton resetButton = new JButton("Làm mới");
        resetButton.addActionListener(e -> {
            keywordField.setText("");
            search();
        });
        searchBar.add(resetButton);

        add(searchBar, BorderLayout.NORTH);
        add(resultTabs, BorderLayout.CENTER);
        search();
    }

    public void search() {
        try {
            List<Book> books = bookService.searchBooks(keywordField.getText());
            Map<String, List<Book>> grouped = bookService.groupBooksByTopic(books);
            resultTabs.removeAll();

            if (grouped.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách phù hợp", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            grouped.forEach((topic, topicBooks) -> {
                DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã sách", "Tên sách", "Tác giả", "Số lượng"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                for (Book book : topicBooks) {
                    model.addRow(new Object[]{book.getId(), book.getTenSach(), book.getTacGia(), book.getSoLuong()});
                }

                JTable table = new JTable(model);
                resultTabs.addTab(topic, new JScrollPane(table));
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
