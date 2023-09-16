package com.petroandrushchak.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class FutBinService {

    public void storePlayersToFile(List<FutBinRawPlayer> futBinRawPlayers) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(FutBinRawPlayer.class).withHeader();
        File file = new File("src/main/resources/futbin/futBinPlayers.csv");

        try {
            FileWriter writer = new FileWriter(file, false);
            csvMapper.writer(schema).writeValues(writer).writeAll(futBinRawPlayers);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public List<FutBinRawPlayer> parsePlayersFromFile() {

        Reader reader = Files.newBufferedReader(new File("src/main/resources/futbin/futBinPlayers.csv").toPath());
        CsvMapper mapper = new CsvMapper();
        mapper.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.TRIM_SPACES);

        CsvSchema csvSchema = mapper
                .typedSchemaFor(FutBinRawPlayer.class)
                .withHeader()
                .withColumnSeparator(',')
                .withComments();

        try (MappingIterator<FutBinRawPlayer> iterator = mapper.readerWithTypedSchemaFor(FutBinRawPlayer.class)
                                                               .with(csvSchema)
                                                               .readValues(reader)) {
            return iterator.readAll();
        }
    }

    @SneakyThrows
    public List<FutBinNewRawPlayer> parsePlayersFromJsonFile() {
        JsonMapper jsonMapper = new JsonMapper();
        TypeFactory typeFactory = jsonMapper.getTypeFactory();

        List<FutBinNewRawPlayer> parsedPlayers = jsonMapper.readValue(Paths.get("/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/players_data.json").toFile(), typeFactory.constructCollectionType(List.class, FutBinNewRawPlayer.class));

        return parsedPlayers;

    }
}
