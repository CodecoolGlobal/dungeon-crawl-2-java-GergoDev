package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.Ghost;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.Skeleton;
import com.codecool.dungeoncrawl.model.GameState;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    public int x, y = 0;
    GameMap map = MapLoader.loadMap("/map.txt");
    Canvas canvas = new Canvas(
            25 * Tiles.TILE_WIDTH,
            21 * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label strengthLabel = new Label();
    Label inventoryLabel = new Label();
    private Button pickUpButton = new Button("Pick up item.");
    Stage stage;
    KeyCode lastKeyEvent;
    GameDatabaseManager db = new GameDatabaseManager();
    String currentMap = "/map.txt";

    //trying to make an alert
    public Alert wonGame = new Alert(Alert.AlertType.INFORMATION);
    public Alert lostGame = new Alert(Alert.AlertType.INFORMATION);

    public void setAlert(Alert alert, String string) {
        alert.setTitle("Frost Dungeon Crawl");
        alert.setHeaderText(string);
        alert.setX(685);
        alert.setY(350);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setMap(GameMap map) { this.map = map; }

    private void hideButton() {
        pickUpButton.setVisible(false);
    }

    private void showButton() { pickUpButton.setVisible(true); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        mainMenu(primaryStage);
    }

    public void preGameSettings(Stage primaryStage) throws FileNotFoundException {
        ImageView selectedImage = new ImageView();
        Image image1 = new Image(Main.class.getResourceAsStream("/fdc.png"));
        selectedImage.setImage(image1);
        HBox gameLogo = new HBox(selectedImage);
        Button startButton = new Button("Start the Game");
        Button backButton = new Button("Back to Menu");
        startButton.setId("allbtn");
        backButton.setId("allbtn");
        HBox buttons = new HBox(backButton, startButton);
        Text championNameLabel = new Text("Enter Your Name");
        championNameLabel.setId("text");
        TextField textField = new TextField();
        textField.setId("input");
        VBox settingsLayout = new VBox(championNameLabel, textField, buttons);
        settingsLayout.setAlignment(Pos.CENTER);
        startButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                map.getPlayer().setName(textField.getText());
                gameStart(primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        backButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                mainMenu(primaryStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        gameLogo.setAlignment(Pos.CENTER);
        HBox.setMargin(selectedImage, new Insets(50, 0, 0, 0));
        settingsLayout.setSpacing(25);
        BorderPane menuLayout = new BorderPane();
        menuLayout.setBackground(new Background(new BackgroundFill(Color.rgb(71, 45, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        menuLayout.setPrefWidth(1000);
        menuLayout.setPrefHeight(672);
        menuLayout.setTop(gameLogo);
        menuLayout.setCenter(settingsLayout);
        HBox.setMargin(backButton, new Insets(10, 10, 10, 10));
        buttons.setAlignment(Pos.CENTER);
        Scene scene = new Scene(menuLayout);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    public void saveScreen(Stage primaryStage, Scene gameScene) throws FileNotFoundException {
        ImageView selectedImage = new ImageView();
        Image image1 = new Image(Main.class.getResourceAsStream("/fdc.png"));
        selectedImage.setImage(image1);
        HBox gameLogo = new HBox(selectedImage);
        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");
        cancelButton.setId("allbtn");
        saveButton.setId("allbtn");
        HBox buttons = new HBox(cancelButton, saveButton);
        Text saveLabel = new Text("Save the Game");
        saveLabel.setId("text");
        TextField textField = new TextField();
        textField.setId("input");
        VBox settingsLayout = new VBox(saveLabel, textField, buttons);
        settingsLayout.setAlignment(Pos.CENTER);
        cancelButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                primaryStage.setScene(gameScene);
                primaryStage.setTitle("Dungeon Crawl");
                primaryStage.show();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        saveButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            boolean isNameExists = db.getAll().stream().anyMatch(gameState -> gameState.getSaveAs().equals(textField.getText()));
            if(isNameExists) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Would you like to overwrite the already existing state?");
                alert.showAndWait();
                if (alert.getResult().getText().equals("OK")) {
                    db.updateGameState(currentMap, textField.getText(), db.savePlayer(map.getPlayer()));
                    primaryStage.setScene(gameScene);
                    primaryStage.setTitle("Dungeon Crawl");
                    primaryStage.show();
                }
            } else {
                db.saveGameState(currentMap, textField.getText(), db.savePlayer(map.getPlayer()));
                primaryStage.setScene(gameScene);
                primaryStage.setTitle("Dungeon Crawl");
                primaryStage.show();
            }
        });
        gameLogo.setAlignment(Pos.CENTER);
        HBox.setMargin(selectedImage, new Insets(50, 0, 0, 0));
        settingsLayout.setSpacing(25);
        BorderPane menuLayout = new BorderPane();
        menuLayout.setBackground(new Background(new BackgroundFill(Color.rgb(71, 45, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        menuLayout.setPrefWidth(1000);
        menuLayout.setPrefHeight(672);
        menuLayout.setTop(gameLogo);
        menuLayout.setCenter(settingsLayout);
        HBox.setMargin(saveButton, new Insets(10, 10, 10, 10));
        buttons.setAlignment(Pos.CENTER);
        Scene scene = new Scene(menuLayout);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    public void mainMenu(Stage primaryStage) throws FileNotFoundException, RuntimeException {
        ImageView selectedImage = new ImageView();
        Image image1 = new Image(Main.class.getResourceAsStream("/fdc.png"));
        // Image image1 = new Image(new FileInputStream("C:\\Users\\haku9104\\Desktop\\fdc.png"));
        selectedImage.setImage(image1);
        HBox gameLogo = new HBox(selectedImage);
        Button startGameButton = new Button("New Adventure");
        Button loadGameButton = new Button("Load Game");
        Button exitGameButton = new Button("Exit Game");
        startGameButton.setId("allbtn");
        loadGameButton.setId("allbtn");
        exitGameButton.setId("allbtn");
        startGameButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                preGameSettings(primaryStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        loadGameButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                loadGameMenu(primaryStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        exitGameButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            System.exit(0);
        });

        VBox buttons = new VBox(startGameButton, loadGameButton, exitGameButton);
        gameLogo.setAlignment(Pos.CENTER);
        HBox.setMargin(selectedImage, new Insets(50, 0, 0, 0));
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        BorderPane menuLayout = new BorderPane();
        menuLayout.setCenter(buttons);
        menuLayout.setTop(gameLogo);
        menuLayout.setBackground(new Background(new BackgroundFill(Color.rgb(71, 45, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        menuLayout.setPrefWidth(1000);
        menuLayout.setPrefHeight(672);
        Scene scene = new Scene(menuLayout);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    public void loadGameMenu(Stage primaryStage) throws FileNotFoundException, RuntimeException {
        ImageView selectedImage = new ImageView();
        Image image1 = new Image(Main.class.getResourceAsStream("/fdc.png"));
        selectedImage.setImage(image1);
        HBox gameLogo = new HBox(selectedImage);
        Button backButton = new Button("Back");
        backButton.setId("allbtn");
        backButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            try {
                mainMenu(primaryStage);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        VBox buttons = new VBox(backButton);
        VBox.setMargin(backButton, new Insets(30));
        gameLogo.setAlignment(Pos.CENTER);
        HBox.setMargin(selectedImage, new Insets(50, 0, 0, 0));
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        BorderPane menuLayout = new BorderPane();
        menuLayout.setTop(gameLogo);

        GridPane savedGameList = new GridPane();
        savedGameList.getColumnConstraints().add(new ColumnConstraints(150));
        savedGameList.getColumnConstraints().add(new ColumnConstraints(150));
        savedGameList.getColumnConstraints().add(new ColumnConstraints(150));
        savedGameList.getColumnConstraints().add(new ColumnConstraints(150));

        Text nameHeader = new Text("Name");
        nameHeader.setId("savedGameList");
        GridPane.setHalignment(nameHeader, HPos.CENTER);
        savedGameList.add(nameHeader, 1, 1);

        Text dateHeader = new Text("Map");
        dateHeader.setId("savedGameList");
        GridPane.setHalignment(dateHeader, HPos.CENTER);
        savedGameList.add(dateHeader, 2, 1);

        Text playerHeader = new Text("Player");
        playerHeader.setId("savedGameList");
        GridPane.setHalignment(playerHeader, HPos.CENTER);
        savedGameList.add(playerHeader, 3, 1);

        Text loadHeader = new Text("Load");
        loadHeader.setId("savedGameList");
        GridPane.setHalignment(loadHeader, HPos.CENTER);
        savedGameList.add(loadHeader, 4, 1);

        List<GameState> dataFromSQL = db.getAll();

        for(int i=2; i<dataFromSQL.size()+2; i++) {
            Text column1 = new Text(dataFromSQL.get(i-2).getSaveAs());
            column1.setId("savedGameList");
            GridPane.setHalignment(column1, HPos.CENTER);
            savedGameList.add(column1, 1, i);

            Text column2 = new Text(dataFromSQL.get(i-2).getCurrentMap().equals("/map.txt") ? "First map" : "Second map");
            column2.setId("savedGameList");
            GridPane.setHalignment(column2, HPos.CENTER);
            savedGameList.add(column2, 2, i);

            Text column3 = new Text(dataFromSQL.get(i-2).getPlayer().getPlayerName());
            column3.setId("savedGameList");
            GridPane.setHalignment(column3, HPos.CENTER);
            savedGameList.add(column3, 3, i);

            Button loadButton = new Button("Load Game");
            int finalI = i;
            loadButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
                //TODO
                int x = dataFromSQL.get(finalI - 2).getPlayer().getX();
                int y = dataFromSQL.get(finalI - 2).getPlayer().getY();
                Player loadedPlayer = new Player(new Cell(map, x, y, CellType.EMPTY));
                loadedPlayer.setHealth(dataFromSQL.get(finalI - 2).getPlayer().getHp());
                loadedPlayer.setStrength(dataFromSQL.get(finalI - 2).getPlayer().getSt());
                map = MapLoader.loadMap(dataFromSQL.get(finalI - 2).getCurrentMap(), loadedPlayer);
                map.getPlayer().setName(dataFromSQL.get(finalI - 2).getPlayer().getPlayerName());
                try {
                    gameStart(primaryStage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                System.out.println("Clicked");
            });
            savedGameList.add(loadButton, 4, i);
        }

        savedGameList.setAlignment(Pos.CENTER);

        menuLayout.setCenter(savedGameList);
        menuLayout.setBottom(buttons);
        menuLayout.setBackground(new Background(new BackgroundFill(Color.rgb(71, 45, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        menuLayout.setPrefWidth(1000);
        menuLayout.setPrefHeight(672);
        Scene scene = new Scene(menuLayout);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    public void gameStart(Stage primaryStage) throws Exception{
        setAlert(wonGame, "You won the game! Congrats " + map.getPlayer().getName());
        setAlert(lostGame, "Game over, " + map.getPlayer().getName()+ " has died.");
        canvas.setFocusTraversable(false);
        pickUpButton.setFocusTraversable(false);
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));
        ui.add(new Label("Name: "), 0, 0);
        ui.add(new Label(map.getPlayer().getName()), 1, 0);
        ui.add(new Label("Health: "), 0, 1);
        ui.add(healthLabel, 1, 1);
        ui.add(new Label("Strength: "), 0, 2);
        ui.add(strengthLabel, 1, 2);
        ui.add(new Label("INVENTORY"), 0, 3);
        ui.add(inventoryLabel, 0, 4);
        ui.add(pickUpButton, 0, 20);
        hideButton();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh();
        scene.setOnKeyPressed(this::onKeyPressed);
        pickUpButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.getPlayer().itemPickUp();
            refresh();
        });
        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    private void monsterMove() {
        for (Cell[] cells: map.getCells()) {
            for (Cell cell: cells) {
                if (cell.getActor() instanceof Skeleton || cell.getActor() instanceof Ghost) {
                    if (cell.getActor().isCanMove()) {
                        Random r = new Random();
                        int direction = r.nextInt(2);
                        int upOrDown = r.nextInt(2);
                        if (direction == 0) {
                            y = 0;
                            x = (upOrDown == 0) ? -1 : 1;
                        } else {
                            x = 0;
                            y = (upOrDown == 0) ? -1 : 1;
                        }
                        cell.getActor().setCanMove(false);
                        cell.getActor().move(x, y);
                    }
                }
            }
        }
    }
    private void resetMonsterMove() {
        for (Cell[] cells: map.getCells()) {
            for (Cell cell: cells) {
                if (cell.getActor() instanceof Skeleton || cell.getActor() instanceof Ghost) {
                    cell.getActor().setCanMove(true);
                }
            }
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        monsterMove();
        resetMonsterMove();
        switch (keyEvent.getCode()) {
            case UP:
                map.getPlayer().move(0, -1);
                refresh();
                break;
            case DOWN:
                map.getPlayer().move(0, 1);
                refresh();
                break;
            case LEFT:
                map.getPlayer().move(-1, 0);
                refresh();
                break;
            case RIGHT:
                map.getPlayer().move(1,0);
                refresh();
                break;
            case S:
                if (lastKeyEvent == KeyCode.CONTROL) {
                    try {
                        saveScreen(stage, stage.getScene());
                    } catch (FileNotFoundException ex) {
                        System.out.println(ex);
                    }
                }
                break;
        }
        lastKeyEvent = keyEvent.getCode();
        if(map.getPlayer().isNextMapComing()) {
            String name = map.getPlayer().getName();
            setMap(MapLoader.loadMap("/map2.txt"));
            currentMap = "/map2.txt";
            map.getPlayer().setName(name);
        }
        if(!map.getPlayer().isAlive()) {
            lostGame.showAndWait();
            try {
                map = MapLoader.loadMap("/map2.txt");
                map = MapLoader.loadMap("/map.txt");
                currentMap = "/map.txt";
                mainMenu(stage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(map.getPlayer().isHasWon()) {
            wonGame.showAndWait();
            try {
                map = MapLoader.loadMap("/map2.txt");
                map = MapLoader.loadMap("/map.txt");
                currentMap = "/map.txt";
                mainMenu(stage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void refresh() {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        boolean isStandingOnItem = false;
        int playerX = map.getPlayer().getCell().getX();
        int playerY = map.getPlayer().getCell().getY();
        for (int x = playerX -100 ; x < playerX +100; x++) {
            for (int y = playerY -100 ; y < playerY +100; y++) {
                Cell cell;
                try {
                    cell = map.getCell(x+playerX-12, y+playerY-10);
                } catch(IndexOutOfBoundsException ex) {
                    Cell emptyCell = new Cell(map, x+playerX-12, y+playerY-10, CellType.EMPTY);
                    if (map.getPlayer().canSee(emptyCell).equals("hardly")) {
                        context.setGlobalAlpha(0.5);
                    } else if (map.getPlayer().canSee(emptyCell).equals("little")) {
                        context.setGlobalAlpha(0.7);
                    } else if (map.getPlayer().canSee(emptyCell).equals("yes")) {
                        context.setGlobalAlpha(1);
                    } else {
                        context.setGlobalAlpha(0.3);
                    }
                    Tiles.drawTile(context, emptyCell, x, y);
                    context.setGlobalAlpha(1);
                    continue;
                }
                if (cell.getType() == CellType.EMPTY && cell.getActor() == null) {
                    Cell cantSeeCell = new Cell(map, x, y, CellType.EMPTY);
                    if (map.getPlayer().canSee(cell).equals("hardly")) {
                        context.setGlobalAlpha(0.5);
                    } else if (map.getPlayer().canSee(cell).equals("little")) {
                        context.setGlobalAlpha(0.7);
                    } else if (map.getPlayer().canSee(cell).equals("yes")) {
                        context.setGlobalAlpha(1);
                    } else {
                        context.setGlobalAlpha(0.3);
                    }
                    Tiles.drawTile(context, cantSeeCell, x, y);
                    context.setGlobalAlpha(1);
                } else if (map.getPlayer().canSee(cell).equals("no") && !(cell.getActor() instanceof Player)){
                    context.setGlobalAlpha(0.3);
                    Cell cantSeeCell = new Cell(map, x, y, CellType.EMPTY);
                    Tiles.drawTile(context, cantSeeCell, x, y);
                    context.setGlobalAlpha(1);
                } else if (map.getPlayer().canSee(cell).equals("hardly") && !(cell.getActor() instanceof Player)){
                    context.setGlobalAlpha(0.5);
                    if (cell.getActor() != null) {
                        Tiles.drawTile(context, cell.getActor(), x, y);
                    } else {
                        Tiles.drawTile(context, cell, x, y);
                    }
                    context.setGlobalAlpha(1);
                } else if (map.getPlayer().canSee(cell).equals("little") && !(cell.getActor() instanceof Player)){
                    context.setGlobalAlpha(0.7);
                    Cell cantSeeCell = new Cell(map, x, y, CellType.EMPTY);
                    if (cell.getActor() != null) {
                        Tiles.drawTile(context, cell.getActor(), x, y);
                    } else {
                        Tiles.drawTile(context, cell, x, y);
                    }
                    context.setGlobalAlpha(1);
                } else {
                    if (cell.getActor() != null) {
                        Tiles.drawTile(context, cell.getActor(), x, y);
                        if (cell.getItem() != null && cell.getActor() instanceof Player) {
                            isStandingOnItem = true;
                        }
                    } else if (cell.getItem() != null) {
                        Tiles.drawTile(context, cell.getItem(), x, y);
                    } else {
                        Tiles.drawTile(context, cell, x, y);
                    }
                }

                }
            }

        if(isStandingOnItem) showButton();
        else hideButton();
        healthLabel.setText("" + map.getPlayer().getHealth());
        inventoryLabel.setText("" + map.getPlayer().inventoryToString());
        strengthLabel.setText("" + map.getPlayer().getStrength());
    }
}


