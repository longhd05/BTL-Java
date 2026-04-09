package com.librarymanagement.service;

import com.librarymanagement.model.User;
import com.librarymanagement.repository.UserRepository;
import com.librarymanagement.util.PasswordUtil;
import com.librarymanagement.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Nghiệp vụ đăng nhập.
     * Input: username và password từ form đăng nhập.
     * Output: trả về User đã xác thực thành công.
     * Validate: kiểm tra rỗng, kiểm tra user tồn tại, kiểm tra hash mật khẩu với salt.
     * Ảnh hưởng dữ liệu: chỉ cập nhật trạng thái phiên hiện tại (currentUser), không ghi file JSON.
     * Lý do: tách xử lý xác thực ra service để UI không tự kiểm tra quyền hoặc mật khẩu.
     */
    public User login(String username, String password) {
        ValidationUtil.validateUsernamePassword(username, password);
        List<User> users = userRepository.findAll();
        Optional<User> userOpt = users.stream()
                .filter(u -> u.getUsername().equals(username.trim()))
                .findFirst();

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Sai tên đăng nhập hoặc mật khẩu");
        }

        User user = userOpt.get();
        boolean matched = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash());
        if (!matched) {
            throw new IllegalArgumentException("Sai tên đăng nhập hoặc mật khẩu");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
