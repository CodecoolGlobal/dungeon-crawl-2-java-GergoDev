package com.codecool.dungeoncrawl.serialize;

import com.codecool.dungeoncrawl.model.GameState;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SerializationHandler {
    public static void serializeObj(GameState state) {
        String serializedMovie = new Gson().toJson(state);
        FileWriter file = null;
        try {
            file = new FileWriter(state.getSaveAs());
            file.write(serializedMovie);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState deSerializeObj(String fileName) {
        GameState fromJSON = null;
        File fileRead = new File(fileName);
        Scanner reader = null;
        try {
            reader = new Scanner(fileRead);
            fromJSON = new Gson().fromJson(reader.nextLine(), GameState.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fromJSON;
    }
}
