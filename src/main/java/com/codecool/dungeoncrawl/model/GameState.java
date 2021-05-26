package com.codecool.dungeoncrawl.model;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class GameState extends BaseModel {
    private String saveAs;
    private String currentMap;
    private PlayerModel player;

    public GameState(String currentMap, String saveAs, PlayerModel player) {
        this.currentMap = currentMap;
        this.saveAs = saveAs;
        this.player = player;
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public String getSaveAs() {
        return saveAs;
    }
}
