package com.codecool.dungeoncrawl.model;

import com.codecool.dungeoncrawl.logic.actors.Player;

public class PlayerModel extends BaseModel {
    private String playerName;
    private int st;
    private int hp;
    private String iv;
    private int x;
    private int y;

    public PlayerModel(String playerName, int strength, int hp, String inventory, int x, int y) {
        this.playerName = playerName;
        this.st = strength;
        this.iv = inventory;
        this.hp = hp;
        this.x = x;
        this.y = y;
    }

    public PlayerModel(Player player) {
        this.playerName = player.getName();
        this.x = player.getX();
        this.y = player.getY();
        this.st = player.getStrength();
        this.hp = player.getHealth();
        this.iv = player.inventoryToString();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSt() { return st; }

    public String getIv() { return iv; }
}
