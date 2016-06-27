package Presentation;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Андрей on 21.06.2016.
 */
public class ShootingVideo extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        VideoPlayer vp = new VideoPlayer("some");
        vp.start(primaryStage);
    }

}
