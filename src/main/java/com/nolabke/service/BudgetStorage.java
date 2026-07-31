package com.nolabke.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nolabke.model.BudgetEntry;
import com.nolabke.utils.AppDirectories;
import com.nolabke.utils.AppLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class BudgetStorage {


    private static final TypeReference<List<BudgetEntry>> TYPE =
            new TypeReference<>() {};

    private static final String FILE_PREFIX = "budget_";
    private static final String EXTENSION = ".json";


    private final ObjectMapper mapper;

    public BudgetStorage() {

        mapper = new ObjectMapper();

        mapper.registerModule(
                new JavaTimeModule()
        );

        mapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );
    }


    public List<BudgetEntry> load(YearMonth month) {

        Path file = getMonthPath(month);

        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        try {

            return mapper.readValue(
                    file.toFile(),
                    TYPE
            );

        } catch (IOException e) {

            AppLogger.error(
                    "Cannot load budget from: " + file,
                    e
            );
            return new ArrayList<>();
        }
    }

    private Path getMonthPath(YearMonth month) {

        Path dir = AppDirectories.getDataDirectory();

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            AppLogger.error(
                    "Cannot create data directory",
                    e
            );
        }

        return dir.resolve(FILE_PREFIX + month + EXTENSION);
    }


    public void save(YearMonth month,
                     List<BudgetEntry> entries) {

        Path file = getMonthPath(month);

        try {

            mapper.writeValue(
                    file.toFile(),
                    entries
            );

        } catch (IOException e) {

            AppLogger.error(
                    "Cannot save budget",
                    e
            );
        }
    }
}