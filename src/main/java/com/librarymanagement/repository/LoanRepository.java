package com.librarymanagement.repository;

import com.google.gson.reflect.TypeToken;
import com.librarymanagement.model.LoanRecord;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LoanRepository {
    private static final Path LOAN_FILE = Paths.get("data", "loans.json");
    private final JsonStorage storage;

    public LoanRepository(JsonStorage storage) {
        this.storage = storage;
    }

    public List<LoanRecord> findAll() {
        return storage.readList(LOAN_FILE, new TypeToken<List<LoanRecord>>() {
        });
    }

    public void saveAll(List<LoanRecord> loans) {
        storage.writeList(LOAN_FILE, loans);
    }

    public Path getLoanFilePath() {
        return LOAN_FILE;
    }
}
