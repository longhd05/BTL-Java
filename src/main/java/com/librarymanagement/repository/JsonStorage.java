package com.librarymanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public <T> List<T> readList(Path path, TypeToken<List<T>> token) {
        try {
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }
            Type type = token.getType();
            List<T> data = gson.fromJson(json, type);
            return data == null ? new ArrayList<>() : data;
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc file JSON: " + path, e);
        }
    }

    public <T> void writeList(Path path, List<T> data) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String json = gson.toJson(data);
            Files.writeString(path, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể ghi file JSON: " + path, e);
        }
    }
}
