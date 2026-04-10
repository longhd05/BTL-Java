package com.librarymanagement.repository;

import com.google.gson.reflect.TypeToken;
import com.librarymanagement.model.Book;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BookRepository {
    private static final Path BOOK_FILE = Paths.get("data", "books.json");
    private final JsonStorage storage;

    public BookRepository(JsonStorage storage) {
        this.storage = storage;
    }

    public List<Book> findAll() {
        return storage.readList(BOOK_FILE, new TypeToken<List<Book>>() {
        });
    }

    public void saveAll(List<Book> books) {
        storage.writeList(BOOK_FILE, books);
    }

    public Path getBookFilePath() {
        return BOOK_FILE;
    }
}
