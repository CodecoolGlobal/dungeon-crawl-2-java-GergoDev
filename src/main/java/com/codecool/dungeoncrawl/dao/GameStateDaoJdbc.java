package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;


public class GameStateDaoJdbc implements GameStateDao {

    private DataSource dataSource;

    public GameStateDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(GameState state) {
        try (Connection conn = this.dataSource.getConnection()) {
            String query = "INSERT INTO game_state(current_map, saved_at, player_id, saved_as) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, state.getCurrentMap());
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setInt(3, state.getPlayer().getId());
            ps.setString(4, state.getSaveAs());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void update(GameState state) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT player_id FROM game_state WHERE saved_as = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, state.getSaveAs());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int playerId = rs.getInt(1);
            String query2 = "UPDATE game_state SET current_map = ?, saved_at = ? WHERE saved_as = ?";
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setString(1, state.getCurrentMap());
            ps2.setDate(2, new Date(System.currentTimeMillis()));
            ps2.setString(3, state.getSaveAs());
            ps2.executeUpdate();

            String query3 = "UPDATE player SET st = ?, hp = ?, iv = ?, x = ?, y = ? WHERE id = ?";
            PreparedStatement ps3 = conn.prepareStatement(query3);
            ps3.setInt(1, state.getPlayer().getSt());
            ps3.setInt(2, state.getPlayer().getHp());
            ps3.setString(3, state.getPlayer().getIv());
            ps3.setInt(4, state.getPlayer().getX());
            ps3.setInt(5, state.getPlayer().getY());
            ps3.setInt(6, playerId);
            ps3.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public GameState get(int id) {
        return null;
    }

    @Override
    public List<GameState> getAll() {
        List<GameState> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT player_name, st, hp, iv, x, y, current_map, saved_as " +
                           "FROM game_state JOIN player ON player_id = player.id";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while(rs.next()) {
                PlayerModel playerToAdd = new PlayerModel(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6));
                GameState gameStateToAdd = new GameState(
                        rs.getString(7),
                        rs.getString(8),
                        playerToAdd);
                result.add(gameStateToAdd);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return result;
    }
}
