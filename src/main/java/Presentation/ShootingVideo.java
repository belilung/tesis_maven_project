package Presentation;

import com.sun.media.protocol.vfw.VFWCapture;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.media.*;
import javax.media.control.FormatControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Андрей on 21.06.2016.
 */
public class ShootingVideo extends JFrame{

    static VideoFormat videoFormat;
    static AudioFormat audioFormat;
    static CaptureDeviceInfo videoDevice;
    public static final Logger LOG = Logger.getLogger(JWebCam4.class);
    public CaptureDeviceInfo webCamDeviceInfo = autoDetect();
    public JLabel statusBar = null;
    protected JPanel visualContainer = null;
    Player player = null;
    protected FormatControl formatControl = null;

    protected Component visualComponent = null;

//    public void setFormat(VideoFormat selectedFormat) {
//        if (formatControl != null) {
//            player.stop();
//
//            imageSize = selectedFormat.getSize();
//            formatControl.setFormat(selectedFormat);
//
//            player.start();
//
//            statusBar.setText("Format : " + selectedFormat);
//
//            currentFormat = selectedFormat;
//            visualContainer.setPreferredSize(currentFormat.getSize());
//
//            setSize(imageSize.width, imageSize.height + statusBar.getHeight() + toolbar.getHeight());
//        } else {
//            LOG.info("Visual component not an instance of FormatControl");
//            statusBar.setText("Visual component cannot change format");
//        }
//    }


    public ShootingVideo(JLabel statusBar) throws CannotRealizeException, IOException, NoPlayerException, NoDataSourceException, IncompatibleSourceException, NoDataSinkException, InterruptedException {
        setSize(320, 260); // default size...

        LOG.info("Shooting VIDEO");
        this.statusBar = statusBar;

        Vector deviceList = CaptureDeviceManager.getDeviceList(new YUVFormat()); //get all media devices

        CaptureDeviceInfo device = webCamDeviceInfo; //in this computer the only capture device is in=built webcam stays at 0th position
        Format[] formats = device.getFormats(); //get all formats

        MediaLocator mlv = device.getLocator();

        player = Manager.createRealizedPlayer(mlv);

        visualContainer = new JPanel();
        visualContainer.setLayout(new BorderLayout());

        getContentPane().add(visualContainer, BorderLayout.CENTER);
        player.start();
        formatControl = (FormatControl) player.getControl("javax.media.control.FormatControl");
//        videoFormats = webCamDeviceInfo.getFormats();
        visualComponent = player.getVisualComponent();
        visualContainer.add(visualComponent, BorderLayout.CENTER);
        visualContainer.setPreferredSize(new Dimension(320, 480));

        for (int x = 0; x < formats.length; x++) {
            if (formats[x] != null && formats[x] instanceof VideoFormat) {
                videoFormat = (VideoFormat) formats[x]; //take the video format
                videoDevice = device;
            }
            if (formats[x] != null && formats[x] instanceof AudioFormat) {
                audioFormat = (AudioFormat) formats[x]; //take the audio format
                //audioDevice = device;
            }
        }
        //create data sources
        DataSource videoDataSource = Manager.createDataSource(device.getLocator());

        deviceList = CaptureDeviceManager.getDeviceList(new AudioFormat(null)); //get all media devices
        device = (CaptureDeviceInfo) deviceList.firstElement();

        DataSource audioDataSource=Manager.createDataSource(device.getLocator());

        DataSource[] dArray=new DataSource[2];
        dArray[0]=videoDataSource;
        dArray[1]=audioDataSource;

        DataSource mixedDataSource = Manager.createMergingDataSource(dArray);

        //format for output

        Format[] outputFormats=new Format[2];
        outputFormats[0]=new VideoFormat(VideoFormat.YUV);
        outputFormats[1]=new AudioFormat(AudioFormat.LINEAR);
        //output type
        FileTypeDescriptor outputType=new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO);

        //settingup Processor
        ProcessorModel processorModel=new ProcessorModel(mixedDataSource, outputFormats, outputType);
        Processor processor=Manager.createRealizedProcessor(processorModel);

        //settingup sink
        DataSource outputDataSource=processor.getDataOutput();
        MediaLocator destination=new MediaLocator("file:.\\testcam.mp4");
        DataSink dataSink=Manager.createDataSink(outputDataSource, destination);
        dataSink.open();

        //start sink + processor
//        Thread.sleep(2000);
        dataSink.start();
        processor.start();

        Thread.sleep(2000);

        dataSink.close();
        processor.stop();
        processor.close();

        LOG.info("proccessor and datasink stop");

        player.stop();
        player.deallocate();
        player = null;

//        initialise();
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
                LOG.info ("Device:"+(i + 1)+":"+name);
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
                        statusBar.setText("AutoDetect failed : " + ioEx.getMessage());
                    }
                }
                return (null);
            }
        } else {
            return (null);
        }
    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
////        VideoPlayer vp = new VideoPlayer("some");
////        vp.start(primaryStage);
//    }

}
