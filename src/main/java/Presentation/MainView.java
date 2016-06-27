package Presentation;

import com.sun.media.protocol.vfw.VFWCapture;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.media.*;
import javax.media.control.FormatControl;
import javax.media.format.VideoFormat;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.IOException;
import java.util.TimerTask;
import java.util.Vector;
//import java.awt.*;
//import java.awt.*;
//import java.awt.*;
//import java.awt.event.*;

public class MainView extends Application {
    private final JPanel visualContainer;
//    protected boolean initialised = false;
    VBox statusBar = null;

    java.util.Timer timer = null;
    int seconds = 5;
    int EvtCnt = 0;
    String outFile = "images/tempa";
    int maxfileNum = 9;
    int writeFile = 0;
    private Player player;
    private Format[] videoFormats;
    protected Component visualComponent = null;
    /* --------------------------------------------------------------
     * Constructor
     * -------------------------------------------------------------- */

    public MainView() {
//        super(frameTitle);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception cnfe) {
            System.out.println("Note : Cannot load look and feel settings");
        }

//        setSize(320, 260); // default size...

//        addWindowListener(this);
//        addComponentListener(this);

//        getContentPane().setLayout(new BorderLayout());

        visualContainer = new JPanel();
//        visualContainer.setLayout(new BorderLayout());

//        getContentPane().add(visualContainer, BorderLayout.CENTER);

        JLabel statusBar = new JLabel("");
        statusBar.setBorder(new EtchedBorder());
//        getContentPane().add(statusBar, BorderLayout.SOUTH);

//        toolkit = Toolkit.getDefaultToolkit();
        timer = new java.util.Timer();
//        timer.schedule(new Presentation.MainView.RemindTask(), seconds * 1000, seconds * 1000);

    }

    int fileCounter = 0;

    class RemindTask extends TimerTask {
        int EvtCnt = 0;
        public void run() {
            System.out.println("in");
            EvtCnt++;
            if (writeFile > 0) {
//                toolkit.beep();
                // System.out.println("Time's up! " + EvtCnt);
                if (EvtCnt > 3) {
                    //timer.cancel(); //Terminate the timer thread, or
                    //System.exit(0);   // Stops the AWT thread
                    // (and everything else)
                    java.awt.Image photo = null;
                    if (photo != null) {
                        int i = fileCounter++;
                        String str = outFile; // commence filename, like "temp";
                        if (i < 10) {
                            str += "000" + i;
                        } else if (i < 100) {
                            str += "00" + i;
                        } else if (i < 1000) {
                            str += "0" + i;
                        } else {
                            str += "" + i;
                        }
                        if (fileCounter > maxfileNum) {
                            fileCounter = 1;
                        }
                        str += ".jpg";
                        System.out.println("got Image photo ...file = " + str);
                        // MySnapshot snapshot = new MySnapshot ( photo, new Dimension ( imageSize ) );
//                        writeJPEG(str, photo);
                    } else {
                        System.out.println("Error : Could not grab frame");
                    }
                } else {
                    System.out.println("Time's up! " + EvtCnt);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../style.fxml"));
        primaryStage.setTitle("Automation camera snapshot/video");

        timer = new java.util.Timer();
        timer.schedule(new RemindTask(), seconds * 1000, seconds * 1000);

        final Menu menuFile = new Menu("Файл");
        final Menu menuVideo = new Menu("Видео");
        final Menu menuPhoto = new Menu("Фото");

        MenuItem open = new MenuItem("Открыть");
        menuFile.getItems().add(open);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile,
                menuVideo,
                menuPhoto);

        final Group rootGroup = new Group();
        final Scene scene = new Scene(rootGroup, 800, 400, Color.WHEAT);

        rootGroup.getChildren().add(menuBar);

//        statusBar = new VBox();
//        statusBar.setStyle("-fx-background-color: gainsboro;" +
//                "-webkit-fx-position: absolute;" +
//                "-moz-fx-position: absolute;" +
//                "-ms-fx-position: absolute;" +
//                "-o-fx-position: absolute;" +
//                "-fx-position: absolute;" +
//                "-fx-top: 1000px;" +
//                "");
//
//        final Text statusText = new Text("Playing Game");
//        statusBar.getChildren().add(statusText);

//        final BorderPane gameLayout = new BorderPane();
//        gameLayout.setCenter(null);
//        gameLayout.setTop(null);
//        gameLayout.setBottom(statusBar);
//
//        rootGroup.getChildren().add(gameLayout);

        //initialise(autoDetect());

        if (!initialise()) {
            System.out.println("Web Cam not detected / initialised");
        } else {
            System.out.println("Web Cam detected, and initialised ...");
        }

        primaryStage.setScene(scene);
//        primaryStage.getScene().getStylesheets().add("../style.css");
        primaryStage.show();

        open.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.out.print("click");
            }
        });
    }

    protected MyVideoFormat[] myFormatList = null;
    protected Dimension imageSize = null;
    protected boolean initialised = false;

    public boolean initialise()
            throws Exception {
        return (initialise(autoDetect()));
    }

    public boolean initialise(CaptureDeviceInfo _deviceInfo)
            throws Exception {
        System.out.println("Initialising...");
//        statusBar.setText("Initialising...");
        CaptureDeviceInfo webCamDeviceInfo = _deviceInfo;

        if (webCamDeviceInfo != null) {
//            statusBar.setText("Connecting to : " + webCamDeviceInfo.getName());
            System.out.println("Connecting to : " + webCamDeviceInfo.getName());

            try {
//                setUpToolBar();
//                getContentPane().add(toolbar, BorderLayout.NORTH);
                System.out.println("2Connecting to : " + webCamDeviceInfo.getName());
                MediaLocator ml = webCamDeviceInfo.getLocator();
                if (ml != null) {
                    System.out.println("3Connecting to : " + webCamDeviceInfo.getName());
                    player = (javax.media.Player) Manager.createRealizedPlayer(ml);
                    player.start();
                    System.out.println(player);
                    if (player != null) {
                        System.out.println("4Connecting to : " + webCamDeviceInfo.getName());
                        player.start();
                        FormatControl formatControl = (FormatControl) player.getControl("javax.media.control.FormatControl");
                        videoFormats = webCamDeviceInfo.getFormats();

                        visualComponent = player.getVisualComponent();
                        if (visualComponent != null) {
                            visualContainer.add(visualComponent, BorderLayout.CENTER);

                            myFormatList = new MyVideoFormat[videoFormats.length];
                            for (int i = 0; i < videoFormats.length; i++) {
                                myFormatList[i] = new MyVideoFormat((VideoFormat) videoFormats[i]);
                            }

                            Format currFormat = formatControl.getFormat();
                            if (currFormat instanceof VideoFormat) {
                                VideoFormat currentFormat = (VideoFormat) currFormat;
                                imageSize = currentFormat.getSize();
                                visualContainer.setPreferredSize(imageSize);
//                                setSize(imageSize.width, imageSize.height + statusBar.getHeight() + toolbar.getHeight());
//                                statusBar.setText("Connected: " + webCamDeviceInfo.getName());
                                System.out.println("Connected : " + webCamDeviceInfo.getName());
                                System.out.println("Size : " + imageSize.width + "x" + imageSize.height);
                            } else {
                                System.err.println("Error : Cannot get current video format");
                            }

//                            invalidate();
//                            pack();
                            System.out.println("Done, returning TRUE ...");
                            return (true);
                        } else {
                            System.err.println("Error : Could not get visual component");
                            return (false);
                        }
                    } else {
                        System.err.println("Error : Cannot create player");
//                        statusBar.setText("Cannot create player");
                        return (false);
                    }
                } else {
                    System.err.println("Error : No MediaLocator for " + webCamDeviceInfo.getName());
//                    statusBar.setText("No Media Locator for : " + webCamDeviceInfo.getName());
                    return (false);
                }
            } catch (IOException ioEx) {
                System.err.println("NO connection to : " + webCamDeviceInfo.getName());
//                statusBar.setText();
                return (false);
            } catch (NoPlayerException npex) {
                System.err.println("Cannot create player");
//                statusBar.setText();
                return (false);
            } catch (CannotRealizeException nre) {
                System.err.println("Cannot realize player");
//                statusBar.setText();
                return (false);
            }
        } else {
            System.err.println("No web cam information");
//            statusBar.setText();
            return (false);
        }
    }

    class MyVideoFormat {
        public VideoFormat format;

        public MyVideoFormat(VideoFormat _format) {
            format = _format;
        }

        public String toString() {
            Dimension dim = format.getSize();
            return (format.getEncoding() + " [ " + dim.width + " x " + dim.height + " ]");
        }
    }

    public CaptureDeviceInfo autoDetect() {
        Vector list = CaptureDeviceManager.getDeviceList(null);
        CaptureDeviceInfo devInfo = null;

        if (list != null) {
            String name;
            int i;
            // for debug only
            for ( i=0; i < list.size(); i++ ) {
                devInfo = (CaptureDeviceInfo)list.elementAt ( i );
                name = devInfo.getName();
                  System.out.println ("Device:"+(i + 1)+":"+name);
            }

            for (i = 0; i < list.size(); i++) {
                devInfo = (CaptureDeviceInfo) list.elementAt(i);
                name = devInfo.getName();

                if (name.startsWith("vfw:")) {
                    break;
                }
            }

            if (devInfo != null && devInfo.getName().startsWith("vfw:")) {
                return (devInfo);
            } else {
                for (i = 0; i < 10; i++) {
                    try {
                        name = VFWCapture.capGetDriverDescriptionName(i);
                        if (name != null && name.length() > 1) {
                            devInfo = com.sun.media.protocol.vfw.VFWSourceStream.autoDetect(i);
                            if (devInfo != null) {
                                return (devInfo);
                            }
                        }
                    } catch (Exception ioEx) {
                        // ignore errors detecting device
                        System.out.println("AutoDetect failed : " + ioEx.getMessage());
                    }
                }
                return (null);
            }
        } else {
            return (null);
        }
    }
}