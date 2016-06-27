import Presentation.JWebCam4;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Андрей on 21.06.2016.
 */

public class Main extends Application{
    public static void main(String[] args) {
        launch(args);
//            Media pick = new Media("C:\\slowmotion.mp4").toUri(); //throws here
//            MediaPlayer player = new MediaPlayer(pick);
//            player.play();
////            JFileChooser fileChooser = new JFileChooser();
////
////            // show open file dialog
////            int result = fileChooser.showOpenDialog( null );
////
////            if ( result == JFileChooser.APPROVE_OPTION ) // user chose a file
////               	{
////                  	URL mediaURL = null;
////
////                  	try
////                  	{
////                         	// get the file as URL
////                         	mediaURL = fileChooser.getSelectedFile().toURI().toURL();
////                       } // end try
////                  	catch ( MalformedURLException malformedURLException )
////                  	{
////                         	System.err.println( "Could not create URL for the file" );
////                      	} // end catch
////
////                  	if ( mediaURL != null ) // only display if there is a valid URL
////                      	{
////                         	JFrame mediaTest = new JFrame( "Media Tester" );
////                         	mediaTest.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
////
////                            VideoPlayer mediaPanel = new VideoPlayer( mediaURL );
////                         	mediaTest.add( mediaPanel );
////
////                         	mediaTest.setSize( 300, 300 );
////                         	mediaTest.setVisible( true );
////                      	} // end inner if
////               	} // end outer if
//            //VideoPlayer vp = new VideoPlayer(new File("C:\\Users\\Андрей\\IdeaProjects\\tesis_work\\slowmotion.mp4").toURI().toURL());


    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            JWebCam4 myWebCam = new JWebCam4("title");
            myWebCam.setVisible(true);
//            JWebCam4 vp = new JWebCam4();
//            vp.start(primaryStage);
            if (!myWebCam.initialise()) {
                System.out.println("Web Cam not detected / initialised");
            } else {
                System.out.println("Web Cam detected, and initialised ...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
