package rocnikova_prace_minesweeper;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Main.uvod(primaryStage);

    }

    public static void uvod(Stage primaryStage) {
        MenuItem m1 = new MenuItem("      10 X 10      ");
        MenuItem m2 = new MenuItem("      15 X 15      ");
        MenuItem m3 = new MenuItem("      20 X 20      ");

        MenuButton menuButton = new MenuButton("HRÁT", null, m1, m2, m3);
        menuButton.setPrefSize(100, 50);

        final ToggleButton obtiznost = new ToggleButton("VÍCE MIN");
        obtiznost.setPrefSize(100, 50);

        GridPane g = new GridPane();
        g.setAlignment(Pos.CENTER);
        g.setHgap(10);
        g.add(obtiznost, 0, 0, 1, 1);
        g.add(menuButton, 1, 0, 1, 1);
        Image img = new Image("rocnikova_prace_minesweeper/background.png");
        BackgroundImage backImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background back = new Background(backImg);
        g.setBackground(back);

        Scene s = new Scene(g);
        GridPane grid = new GridPane();
        GridPane mines = new GridPane();
        grid.add(mines, 0, 1, 1, 1);
        Scene u = new Scene(grid);

        m1.setOnAction((ActionEvent event) -> {
            Main.pole(10, u, mines, grid, obtiznost.isSelected(), primaryStage);
            primaryStage.setScene(u);
        });
        m2.setOnAction((ActionEvent event) -> {
            Main.pole(15, u, mines, grid, obtiznost.isSelected(), primaryStage);
            primaryStage.setScene(u);
        });
        m3.setOnAction((ActionEvent event) -> {
            Main.pole(20, u, mines, grid, obtiznost.isSelected(), primaryStage);
            primaryStage.setScene(u);
        });

        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void pole(int strana, Scene u, GridPane mines, GridPane grid, boolean viceMin, Stage primaryStage) {

        ButtonInfo[][] buttonInfo = new ButtonInfo[strana][strana];
        for (int i = 0; i < strana; i++) {
            for (int o = 0; o < strana; o++) {
                buttonInfo[i][o] = new ButtonInfo();
            }
        }

        int pocetMin = strana + strana / 2;

        if (viceMin == true) {
            pocetMin += pocetMin / 2;
        }

        for (int i = 0; i < pocetMin; i++) {
            int x = ThreadLocalRandom.current().nextInt(0, strana);
            int y = ThreadLocalRandom.current().nextInt(0, strana);
            buttonInfo[x][y].nastavBombu();  // Může nastat že se jedno pole vybere dvakrát a vygeneruje se méně min.
        }

        for (int i = 0; i < strana; i++) {
            for (int o = 0; o < strana; o++) {
                if (buttonInfo[i][o].jeBomba == true) {
                    for (int p = i - 1; p <= i + 1; p++) {
                        for (int l = o - 1; l <= o + 1; l++) {
                            if (p < strana && l < strana && p >= 0 && l >= 0) {
                                buttonInfo[p][l].zvecCislo();
                            }
                        }
                    }
                }
            }
        }

        u.setCursor(Cursor.HAND);
        for (int i = 0; i < strana; i++) {
            ColumnConstraints column = new ColumnConstraints(30);
            mines.getColumnConstraints().add(column);
            RowConstraints row = new RowConstraints(30);
            mines.getRowConstraints().add(row);
        }
        Button[][] poleTlacitek = new Button[strana][strana];
        mines.gridLinesVisibleProperty().set(true);
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(5);
        grid.setHgap(5);

        Label cas = new Label("a");
        cas.setFont(new Font("Noto Serif", 20));
        grid.add(cas, 0, 0, 1, 1);
        GridPane.setHalignment(cas, HPos.CENTER);

        long startTime = System.currentTimeMillis();
        Timer casovac = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long duration = System.currentTimeMillis() - startTime;
                String s = Long.toString(duration / 1000);
                Platform.runLater(() -> cas.setText(s + " s"));
            }
        };
        casovac.schedule(task, 0, 1000);

        for (int i = 0; i < strana; i++) {
            for (int j = 0; j < strana; j++) {
                Button b = new Button();
                b.setPrefSize(30, 30);
                mines.add(b, i, j, 1, 1);

                poleTlacitek[i][j] = b;

                b.setOnMouseClicked(event -> {
                    int col = GridPane.getColumnIndex(b);
                    int row = GridPane.getRowIndex(b);

                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (buttonInfo[col][row].vlajka == false) {
                            if (buttonInfo[col][row].jeBomba == true) {
                                buttonInfo[col][row].prvniBomba = true;
                            }
                            Main.odkryj(col, row, mines, grid, buttonInfo, strana, poleTlacitek, primaryStage, casovac);

                            if (buttonInfo[col][row].cislo == 0) {
                                Main.prazdne(col, row, strana, poleTlacitek, buttonInfo, mines, grid, primaryStage, casovac);
                            }
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {

                        if (buttonInfo[col][row].vlajka == true) {
                            buttonInfo[col][row].vlajka = false;
                            b.setGraphic(null);

                        } else {
                            Image img = new Image("rocnikova_prace_minesweeper/flag.png");
                            ImageView view = new ImageView(img);
                            view.setFitHeight(20);
                            view.setFitWidth(15);
                            b.setGraphic(view);
                            buttonInfo[col][row].vlajka = true;

                        }
                    }
                });
            }

        }
    }

    public static void prazdne(int col, int row, int strana, Button[][] poleTlacitek, ButtonInfo[][] buttonInfo, GridPane mines, GridPane grid, Stage primaryStage, Timer casovac) {

        for (int p = col - 1; p <= col + 1; p++) {
            for (int l = row - 1; l <= row + 1; l++) {
                if (p < strana && l < strana && p >= 0 && l >= 0 && buttonInfo[p][l].otocene != true) {
                    Main.odkryj(p, l, mines, grid, buttonInfo, strana, poleTlacitek, primaryStage, casovac);
                    if (buttonInfo[p][l].cislo == 0) {
                        Main.prazdne(p, l, strana, poleTlacitek, buttonInfo, mines, grid, primaryStage, casovac);
                    }
                }
            }
        }
    }

    public static void odkryj(int col, int row, GridPane mines, GridPane grid, ButtonInfo[][] buttonInfo, int strana, Button[][] poleTlacitek, Stage primaryStage, Timer casovac) {

        if (buttonInfo[col][row].otocene == true) {
            return;
        }
        buttonInfo[col][row].otocene = true;
        boolean konec = true;
        boolean prohra = false;
        for (int i = 0; i < strana; i++) {
            for (int o = 0; o < strana; o++) {
                if (buttonInfo[i][o].jeBomba == true) {
                    if (buttonInfo[i][o].prvniBomba) {
                        prohra = true;
                    }
                } else {
                    if (buttonInfo[i][o].otocene == false && buttonInfo[i][o].jeBomba == false) {
                        konec = false;
                    }
                }
            }
        }
        if (konec == true && prohra == false) {
            casovac.cancel();
            Button zpet = new Button("ZPĚT");
            zpet.setPrefSize(100, 50);
            zpet.setOnAction((event) -> {
                Main.uvod(primaryStage);
            });
            grid.add(zpet, 1, 1);
            Label win = new Label("VYHRÁL JSI");
            win.setFont(new Font("Arial", 40));
            grid.add(win, 1, 0);
            for (int i = 0; i < strana; i++) {
                for (int o = 0; o < strana; o++) {
                    if (buttonInfo[i][o].otocene == false) {
                        poleTlacitek[i][o].setDisable(true);
                    }
                }
            }
        }
        buttonInfo[col][row].otocene = true;
        mines.getChildren().remove(poleTlacitek[col][row]);
        if (buttonInfo[col][row].jeBomba == false) {
            Label cislo = new Label(String.valueOf(buttonInfo[col][row].cislo));
            cislo.setFont(new Font("Noto Serif", 20));
            if (buttonInfo[col][row].cislo != 0) {
                mines.add(cislo, col, row, 1, 1);
                GridPane.setHalignment(cislo, HPos.CENTER);
            }

            switch (buttonInfo[col][row].cislo) {
                case 1:
                    cislo.setTextFill(Color.web("#0000ff"));
                    break;
                case 2:
                    cislo.setTextFill(Color.web("#008000"));
                    break;
                case 3:
                    cislo.setTextFill(Color.web("#ff0000"));
                    break;
                case 4:
                    cislo.setTextFill(Color.web("#000080"));
                    break;
                case 5:
                    cislo.setTextFill(Color.web("#b22222"));
                    break;
                case 6:
                    cislo.setTextFill(Color.web("#20b2aa"));
                    break;
                case 7:
                    cislo.setTextFill(Color.web("#b22222"));
                    break;
                case 8:
                    cislo.setTextFill(Color.web("#a9a9a9"));
                    break;
            }
        } else {

            Label bomb = new Label();
            mines.add(bomb, col, row, 1, 1);
            if (buttonInfo[col][row].prvniBomba == false) {
                Image img = new Image("rocnikova_prace_minesweeper/mina.png");
                ImageView view = new ImageView(img);
                bomb.setGraphic(view);
                GridPane.setHalignment(bomb, HPos.CENTER);
            } else {
                Image img = new Image("rocnikova_prace_minesweeper/boooom.gif");
                ImageView view = new ImageView(img);
                bomb.setGraphic(view);
                GridPane.setHalignment(bomb, HPos.CENTER);
                for (int i = 0; i < strana; i++) {
                    for (int o = 0; o < strana; o++) {
                        Main.odkryj(i, o, mines, grid, buttonInfo, strana, poleTlacitek, primaryStage, casovac);
                    }
                }
                casovac.cancel();
                Button zpet = new Button("ZPĚT");
                zpet.setPrefSize(100, 50);
                zpet.setOnAction((event) -> {
                    Main.uvod(primaryStage);
                });
                grid.add(zpet, 1, 1);
                Label lose = new Label("PROHRÁL JSI");
                lose.setFont(new Font("Arial", 40));
                grid.add(lose, 1, 0);
            }

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
