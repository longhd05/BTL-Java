package com.librarymanagement.service;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.LoanRecord;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.repository.LoanRepository;
import com.librarymanagement.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanService {
    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    /**
     * Nghiệp vụ mượn sách cho USER.
     * Input: user đang đăng nhập và id sách cần mượn.
     * Output: phiếu mượn mới được tạo.
     * Validate: kiểm tra role USER, kiểm tra id hợp lệ, kiểm tra sách tồn tại và soLuong > 0.
     * Ảnh hưởng dữ liệu: giảm tồn kho sách đi 1 và thêm LoanRecord mới vào loans.json.
     * Lý do: khóa toàn bộ logic mượn ở service để tránh lỗi hoặc gian lận từ UI.
     */
    public LoanRecord borrowBook(User actor, String bookId) {
        requireUser(actor);
        String cleanedBookId = ValidationUtil.requireNotBlank(bookId, "Mã sách");

        Optional<Book> bookOpt = bookService.findById(cleanedBookId);
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Sách không tồn tại");
        }

        Book book = bookOpt.get();
        if (book.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Sách đã hết, không thể mượn");
        }

        bookService.changeQuantity(cleanedBookId, -1);

        LoanRecord record = new LoanRecord(
                actor.getUsername(),
                book.getId(),
                book.getTenSach(),
                LocalDate.now().toString(),
                false
        );

        List<LoanRecord> loans = new ArrayList<>(loanRepository.findAll());
        loans.add(record);
        loanRepository.saveAll(loans);
        return record;
    }

    /**
     * Nghiệp vụ trả sách cho USER.
     * Input: user đang đăng nhập và mã sách cần trả.
     * Output: LoanRecord được cập nhật trạng thái returned=true.
     * Validate: kiểm tra role USER, kiểm tra phiếu mượn chưa trả và đúng username.
     * Ảnh hưởng dữ liệu: tăng tồn kho sách lên 1 và cập nhật trạng thái phiếu mượn trong loans.json.
     * Lý do: tránh trả sai dữ liệu hoặc trả sách không thuộc người dùng hiện tại.
     */
    public LoanRecord returnBook(User actor, String bookId) {
        requireUser(actor);
        String cleanedBookId = ValidationUtil.requireNotBlank(bookId, "Mã sách");

        List<LoanRecord> loans = new ArrayList<>(loanRepository.findAll());
        Optional<LoanRecord> loanOpt = loans.stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getUsername().equals(actor.getUsername()))
                .filter(loan -> loan.getBookId().equalsIgnoreCase(cleanedBookId))
                .findFirst();

        if (loanOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy phiếu mượn hợp lệ để trả sách");
        }

        LoanRecord record = loanOpt.get();
        bookService.changeQuantity(cleanedBookId, +1);
        record.setReturned(true);
        loanRepository.saveAll(loans);
        return record;
    }

    /**
     * Nghiệp vụ lấy danh sách phiếu mượn của user hiện tại.
     * Input: user đang đăng nhập.
     * Output: danh sách LoanRecord của đúng username.
     * Validate: kiểm tra role USER, không trả dữ liệu của user khác.
     * Ảnh hưởng dữ liệu: chỉ đọc loans.json.
     * Lý do: giới hạn phạm vi dữ liệu, ngăn người dùng xem mượn/trả của người khác.
     */
    public List<LoanRecord> getLoansForUser(User actor) {
        requireUser(actor);
        return loanRepository.findAll().stream()
                .filter(loan -> loan.getUsername().equals(actor.getUsername()))
                .toList();
    }

    private void requireUser(User actor) {
        if (actor == null || actor.getRole() != Role.USER) {
            throw new SecurityException("Bạn không có quyền thực hiện chức năng này");
        }
    }
}
