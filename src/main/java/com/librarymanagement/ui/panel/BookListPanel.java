package com.librarymanagement.ui.panel;

import com.librarymanagement.model.Book;
import com.librarymanagement.service.BookService;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

public class BookListPanel extends JPanel {
    private final BookService bookService;
    private final JTabbedPane tabbedPane;

    public BookListPanel(BookService bookService) {
        this.bookService = bookService;
        this.tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Danh sách sách theo chủ đề"));
        add(tabbedPane, BorderLayout.CENTER);
        refreshData();
    }

    public void refreshData() {
        tabbedPane.removeAll();
        List<Book> books = bookService.getAllBooksSorted();
        Map<String, List<Book>> grouped = bookService.groupBooksByTopic(books);

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
            tabbedPane.addTab(topic, new JScrollPane(table));
        });
    }
}
