package com.librarymanagement;

import com.librarymanagement.repository.BookRepository;
import com.librarymanagement.repository.JsonStorage;
import com.librarymanagement.repository.LoanRepository;
import com.librarymanagement.repository.UserRepository;
import com.librarymanagement.service.AuthService;
import com.librarymanagement.service.BookService;
import com.librarymanagement.service.LoanService;
import com.librarymanagement.ui.LoginFrame;
import com.librarymanagement.util.SeedDataUtil;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        JsonStorage storage = new JsonStorage();
        BookRepository bookRepository = new BookRepository(storage);
        UserRepository userRepository = new UserRepository(storage);
        LoanRepository loanRepository = new LoanRepository(storage);

        SeedDataUtil.initializeData(bookRepository, userRepository, loanRepository);

        AuthService authService = new AuthService(userRepository);
        BookService bookService = new BookService(bookRepository);
        LoanService loanService = new LoanService(loanRepository, bookService);

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(authService, bookService, loanService);
            frame.setVisible(true);
        });
    }
}
