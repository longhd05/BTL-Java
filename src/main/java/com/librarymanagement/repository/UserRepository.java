package com.librarymanagement.repository;

import com.google.gson.reflect.TypeToken;
import com.librarymanagement.model.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class UserRepository {
    private static final Path USER_FILE = Paths.get("data", "users.json");
    private final JsonStorage storage;

    public UserRepository(JsonStorage storage) {
        this.storage = storage;
    }

    public List<User> findAll() {
        return storage.readList(USER_FILE, new TypeToken<List<User>>() {
        });
    }

    public void saveAll(List<User> users) {
        storage.writeList(USER_FILE, users);
    }

    public Path getUserFilePath() {
        return USER_FILE;
    }
}
