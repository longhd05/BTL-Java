package com.librarymanagement.util;

import com.librarymanagement.model.Book;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống");
        }
        String cleaned = value.trim();
        if (cleaned.length() > 200) {
            throw new IllegalArgumentException(fieldName + " quá dài");
        }
        return cleaned;
    }

    public static int parseSoLuong(String rawSoLuong) {
        try {
            int soLuong = Integer.parseInt(rawSoLuong.trim());
            if (soLuong < 0) {
                throw new IllegalArgumentException("Số lượng không được âm");
            }
            return soLuong;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng phải là số nguyên hợp lệ");
        }
    }

    public static void validateBook(Book book) {
        requireNotBlank(book.getId(), "Mã sách");
        requireNotBlank(book.getTenSach(), "Tên sách");
        requireNotBlank(book.getChuDe(), "Chủ đề");
        requireNotBlank(book.getTacGia(), "Tác giả");
        if (book.getSoLuong() < 0) {
            throw new IllegalArgumentException("Số lượng không được âm");
        }
    }

    public static void validateUsernamePassword(String username, String password) {
        requireNotBlank(username, "Tên đăng nhập");
        requireNotBlank(password, "Mật khẩu");
    }
}
