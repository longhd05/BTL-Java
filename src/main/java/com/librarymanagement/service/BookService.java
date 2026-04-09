package com.librarymanagement.service;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.repository.BookRepository;
import com.librarymanagement.util.ValidationUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookService {
    private final BookRepository bookRepository;
    private final Comparator<Book> byTenSach;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        Collator collator = Collator.getInstance(new Locale("vi", "VN"));
        this.byTenSach = (b1, b2) -> collator.compare(b1.getTenSach(), b2.getTenSach());
    }

    /**
     * Nghiệp vụ lấy toàn bộ sách để hiển thị.
     * Input: không có.
     * Output: danh sách sách đã sort theo tên A-Z.
     * Validate: dữ liệu đọc từ repository được chuẩn hóa, không trả null.
     * Ảnh hưởng dữ liệu: không thay đổi file JSON, chỉ đọc dữ liệu.
     * Lý do: đảm bảo mọi màn hình luôn hiển thị cùng một thứ tự chuẩn.
     */
    public List<Book> getAllBooksSorted() {
        List<Book> books = new ArrayList<>(bookRepository.findAll());
        books.sort(byTenSach);
        return books;
    }

    /**
     * Nghiệp vụ nhóm sách theo chủ đề để render bằng JTabbedPane.
     * Input: danh sách sách đã/hoặc chưa sort.
     * Output: map theo thứ tự tên chủ đề, mỗi tab chứa danh sách sách theo tên A-Z.
     * Validate: xử lý an toàn với danh sách rỗng.
     * Ảnh hưởng dữ liệu: không ghi file JSON.
     * Lý do: giúp UI đơn giản, chỉ cần duyệt map để tạo tab.
     */
    public Map<String, List<Book>> groupBooksByTopic(List<Book> books) {
        return books.stream()
                .sorted(Comparator.comparing(Book::getChuDe).thenComparing(byTenSach))
                .collect(Collectors.groupingBy(
                        Book::getChuDe,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.sort(byTenSach);
                            return list;
                        })
                ));
    }

    /**
     * Nghiệp vụ tìm kiếm sách theo tên, tác giả, chủ đề.
     * Input: keyword từ người dùng.
     * Output: danh sách phù hợp đã sort theo tên A-Z.
     * Validate: keyword được trim, nếu rỗng thì trả toàn bộ danh sách.
     * Ảnh hưởng dữ liệu: không ghi file JSON.
     * Lý do: cho phép admin/user tìm nhanh nhưng vẫn giữ chuẩn hiển thị.
     */
    public List<Book> searchBooks(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Book> allBooks = getAllBooksSorted();
        if (key.isBlank()) {
            return allBooks;
        }
        return allBooks.stream()
                .filter(book -> containsIgnoreCase(book.getTenSach(), key)
                        || containsIgnoreCase(book.getTacGia(), key)
                        || containsIgnoreCase(book.getChuDe(), key))
                .sorted(byTenSach)
                .toList();
    }

    /**
     * Nghiệp vụ thêm sách mới (chỉ ADMIN).
     * Input: user đang thao tác và dữ liệu Book từ form.
     * Output: Book đã chuẩn hóa sau khi thêm thành công.
     * Validate: kiểm tra quyền admin, validate từng field, kiểm tra ID trùng.
     * Ảnh hưởng dữ liệu: thêm phần tử mới vào danh sách và ghi lại books.json.
     * Lý do: chặn leo thang đặc quyền ngay tại service thay vì dựa vào UI.
     */
    public Book addBook(User actor, Book book) {
        requireAdmin(actor);
        ValidationUtil.validateBook(book);

        List<Book> books = new ArrayList<>(bookRepository.findAll());
        boolean idExists = books.stream().anyMatch(b -> b.getId().equalsIgnoreCase(book.getId().trim()));
        if (idExists) {
            throw new IllegalArgumentException("Mã sách đã tồn tại");
        }

        Book saved = normalizeBook(book);
        books.add(saved);
        bookRepository.saveAll(books);
        return saved;
    }

    /**
     * Nghiệp vụ cập nhật thông tin sách (chỉ ADMIN).
     * Input: user đang thao tác, id sách cần sửa, dữ liệu cập nhật.
     * Output: Book sau cập nhật.
     * Validate: kiểm tra quyền admin, validate dữ liệu mới, kiểm tra sách tồn tại.
     * Ảnh hưởng dữ liệu: cập nhật đúng bản ghi theo id rồi ghi lại books.json.
     * Lý do: đảm bảo thao tác sửa không làm phát sinh bản ghi sai hoặc thiếu dữ liệu.
     */
    public Book updateBook(User actor, String id, Book updatedBook) {
        requireAdmin(actor);
        ValidationUtil.validateBook(updatedBook);
        String cleanedId = ValidationUtil.requireNotBlank(id, "Mã sách");

        List<Book> books = new ArrayList<>(bookRepository.findAll());
        Optional<Book> existingOpt = books.stream()
                .filter(b -> b.getId().equalsIgnoreCase(cleanedId))
                .findFirst();
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy sách cần sửa");
        }

        Book existing = existingOpt.get();
        Book normalized = normalizeBook(updatedBook);

        if (!existing.getId().equalsIgnoreCase(normalized.getId())) {
            throw new IllegalArgumentException("Không được thay đổi mã sách");
        }

        existing.setTenSach(normalized.getTenSach());
        existing.setSoLuong(normalized.getSoLuong());
        existing.setChuDe(normalized.getChuDe());
        existing.setTacGia(normalized.getTacGia());

        bookRepository.saveAll(books);
        return existing;
    }

    /**
     * Nghiệp vụ xóa sách (chỉ ADMIN).
     * Input: user đang thao tác và id sách cần xóa.
     * Output: không có.
     * Validate: kiểm tra quyền admin, kiểm tra id hợp lệ, kiểm tra sách tồn tại.
     * Ảnh hưởng dữ liệu: loại bỏ sách khỏi danh sách và ghi lại books.json.
     * Lý do: tập trung kiểm soát xóa dữ liệu tại service để tránh xóa nhầm hoặc vượt quyền.
     */
    public void deleteBook(User actor, String id) {
        requireAdmin(actor);
        String cleanedId = ValidationUtil.requireNotBlank(id, "Mã sách");

        List<Book> books = new ArrayList<>(bookRepository.findAll());
        boolean removed = books.removeIf(book -> book.getId().equalsIgnoreCase(cleanedId));
        if (!removed) {
            throw new IllegalArgumentException("Không tìm thấy sách để xóa");
        }

        bookRepository.saveAll(books);
    }

    public Optional<Book> findById(String id) {
        String cleanedId = ValidationUtil.requireNotBlank(id, "Mã sách");
        return bookRepository.findAll().stream()
                .filter(book -> book.getId().equalsIgnoreCase(cleanedId))
                .findFirst();
    }

    /**
     * Nghiệp vụ cập nhật tồn kho khi mượn/trả.
     * Input: id sách và delta số lượng (+1 khi trả, -1 khi mượn).
     * Output: Book sau cập nhật.
     * Validate: kiểm tra sách tồn tại, không cho phép tồn kho âm.
     * Ảnh hưởng dữ liệu: thay đổi soLuong và ghi lại books.json.
     * Lý do: tái sử dụng cho LoanService và giữ nhất quán dữ liệu tồn kho.
     */
    public Book changeQuantity(String id, int delta) {
        String cleanedId = ValidationUtil.requireNotBlank(id, "Mã sách");
        List<Book> books = new ArrayList<>(bookRepository.findAll());

        Optional<Book> targetOpt = books.stream()
                .filter(book -> book.getId().equalsIgnoreCase(cleanedId))
                .findFirst();
        if (targetOpt.isEmpty()) {
            throw new IllegalArgumentException("Sách không tồn tại");
        }

        Book target = targetOpt.get();
        int newQuantity = target.getSoLuong() + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Số lượng sách không đủ để mượn");
        }
        target.setSoLuong(newQuantity);

        bookRepository.saveAll(books);
        return target;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Book normalizeBook(Book rawBook) {
        return new Book(
                ValidationUtil.requireNotBlank(rawBook.getId(), "Mã sách"),
                ValidationUtil.requireNotBlank(rawBook.getTenSach(), "Tên sách"),
                rawBook.getSoLuong(),
                ValidationUtil.requireNotBlank(rawBook.getChuDe(), "Chủ đề"),
                ValidationUtil.requireNotBlank(rawBook.getTacGia(), "Tác giả")
        );
    }

    private void requireAdmin(User actor) {
        if (actor == null || actor.getRole() != Role.ADMIN) {
            throw new SecurityException("Bạn không có quyền thực hiện chức năng này");
        }
    }
}
