package com.librarymanagement.util;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.repository.BookRepository;
import com.librarymanagement.repository.LoanRepository;
import com.librarymanagement.repository.UserRepository;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class SeedDataUtil {
    private static final String[] TOPICS = {
            "Khoa học", "Lập trình", "Toán học", "Lịch sử", "Văn học",
            "Kinh tế", "Thiết kế", "Kỹ năng", "Ngoại ngữ", "Tâm lý"
    };

    private SeedDataUtil() {
    }

    public static void initializeData(BookRepository bookRepository, UserRepository userRepository, LoanRepository loanRepository) {
        seedBooksIfMissing(bookRepository);
        seedUsersIfMissing(userRepository);
        seedLoansIfMissing(loanRepository);
    }

    private static void seedBooksIfMissing(BookRepository bookRepository) {
        if (Files.exists(bookRepository.getBookFilePath()) && !bookRepository.findAll().isEmpty()) {
            return;
        }
        List<Book> books = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            String id = String.format("B%03d", i);
            String topic = TOPICS[(i - 1) % TOPICS.length];
            String tenSach = "Sách " + topic + " " + i;
            String tacGia = "Tác giả " + ((i % 12) + 1);
            int soLuong = (i % 7) + 1;
            books.add(new Book(id, tenSach, soLuong, topic, tacGia));
        }
        bookRepository.saveAll(books);
    }

    private static void seedUsersIfMissing(UserRepository userRepository) {
        if (Files.exists(userRepository.getUserFilePath()) && !userRepository.findAll().isEmpty()) {
            return;
        }
        List<User> users = new ArrayList<>();

        String adminSalt = PasswordUtil.generateSalt();
        users.add(new User(
                "admin",
                PasswordUtil.hashPassword("admin123", adminSalt),
                Role.ADMIN,
                "Quản trị viên",
                adminSalt
        ));

        String userSalt = PasswordUtil.generateSalt();
        users.add(new User(
                "user",
                PasswordUtil.hashPassword("user123", userSalt),
                Role.USER,
                "Người dùng",
                userSalt
        ));

        userRepository.saveAll(users);
    }

    private static void seedLoansIfMissing(LoanRepository loanRepository) {
        if (Files.exists(loanRepository.getLoanFilePath()) && !loanRepository.findAll().isEmpty()) {
            return;
        }
        loanRepository.saveAll(new ArrayList<>());
    }
}
