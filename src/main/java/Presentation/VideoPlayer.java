package Presentation;

/**
 * Created by Андрей on 21.06.2016.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayer extends Application {
    String message = "";

    public void setTexts(String s){
        this.message = s;
    }

    private HBox addToolBar() {
        HBox toolBar = new HBox();
        toolBar.setPadding(new Insets(20));
        toolBar.setAlignment(Pos.CENTER);
        toolBar.alignmentProperty().isBound();
        toolBar.setSpacing(5);
        toolBar.setStyle("-fx-background-color: Black");

        return toolBar;
    }

    public VideoPlayer(String test) {
        this.message = test;
    }

    public void start(Stage primaryStage) {

//The location of your file
        Media media = new Media(new File("C:\\Users\\Андрей\\IdeaProjects\\tesis_work\\testcam.mp4").toURI().toString());

        System.out.println(this.message);

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        MediaView mediaView = new MediaView(mediaPlayer);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(mediaView);
        borderPane.setBottom(addToolBar());

        borderPane.setStyle("-fx-background-color: Black");

        Scene scene = new Scene(borderPane, 600, 600);
        scene.setFill(Color.BLACK);

        DropShadow dropshadow = new DropShadow();
        dropshadow.setOffsetY(5.0);
        dropshadow.setOffsetX(5.0);
        dropshadow.setColor(Color.WHITE);

        mediaView.setEffect(dropshadow);

        primaryStage.setTitle("Media Player!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
} // end class MediaPanel