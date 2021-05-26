package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class GameDatabaseManager {
    private PlayerDao playerDao;
    private GameStateDao gameStateDao;

    public GameDatabaseManager() {
        setup();
    }

    public void setup() {
        try {
            DataSource dataSource = connect();
            playerDao = new PlayerDaoJdbc(dataSource);
            gameStateDao = new GameStateDaoJdbc(dataSource);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public PlayerModel savePlayer(Player player) {
        PlayerModel model = new PlayerModel(player);
        playerDao.add(model);
        return model;
    }

    public void saveGameState(String currentMap, String saveAs, PlayerModel player) {
        GameState model = new GameState(currentMap, saveAs, player);
        gameStateDao.add(model);
    }

    public List<GameState> getAll() {
        return gameStateDao.getAll();
    }

    public void updateGameState(String currentMap, String saveAs, PlayerModel player) {
        GameState model = new GameState(currentMap, saveAs, player);
        gameStateDao.update(model);
    }

    public DataSource connect() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String dbName = System.getenv("PSQL_DB_NAME");
        String user = System.getenv("PSQL_USER_NAME");
        String password = System.getenv("PSQL_PASSWORD");

        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        System.out.println("Trying to connect");
        dataSource.getConnection().close();
        System.out.println("Connection ok.");

        return dataSource;
    }
}
