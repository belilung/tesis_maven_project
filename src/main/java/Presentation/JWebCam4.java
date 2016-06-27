package Presentation;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.media.protocol.vfw.VFWCapture;
import org.apache.log4j.Logger;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.util.Objects;
import java.util.Objects;
import java.util.TimerTask;
import java.util.Vector;

// import com.sun.media.vfw.VFWCapture;         // JMF 2.1.1c version


public class JWebCam4 extends JFrame
        implements WindowListener, ComponentListener {
    public static final Logger LOG = Logger.getLogger(JWebCam4.class);
    protected final static int MIN_WIDTH = 320;
    protected final static int MIN_HEIGHT = 240;
    protected static int shotCounter = 1;
    protected static int fileCounter = 1;

    protected JLabel statusBar = null;
    protected JPanel visualContainer = null;
    protected Component visualComponent = null;
    protected JToolBar toolbar = null;
    protected MyToolBarAction formatButton = null;
    protected MyToolBarAction captureButton = null;
    protected MyToolBarAction videoButton = null;

    protected Player player = null;
    protected CaptureDeviceInfo webCamDeviceInfo = null;
    protected MediaLocator ml = null;
    protected Dimension imageSize = null;
    protected FormatControl formatControl = null;

    protected VideoFormat currentFormat = null;
    protected Format[] videoFormats = null;
    protected MyVideoFormat[] myFormatList = null;

    static VideoFormat videoFormat;
    static AudioFormat audioFormat;
    static CaptureDeviceInfo videoDevice;
    static CaptureDeviceInfo audioDevice;

    protected boolean initialised = false;

    Toolkit toolkit;
    java.util.Timer timer = null;
    int seconds = 5;
    int EvtCnt = 0;
    String outFile = "images/tempa";
    int maxfileNum = 9;
    int writeFile = 0;
    /* --------------------------------------------------------------
     * Constructor
     * -------------------------------------------------------------- */

    public JWebCam4(String frameTitle) {
        super(frameTitle);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception cnfe) {
            LOG.info("Note : Cannot load look and feel settings");
        }

        setSize(320, 260); // default size...

        addWindowListener(this);
        addComponentListener(this);

        getContentPane().setLayout(new BorderLayout());

        visualContainer = new JPanel();
        visualContainer.setLayout(new BorderLayout());

        getContentPane().add(visualContainer, BorderLayout.CENTER);

        statusBar = new JLabel("");
        statusBar.setBorder(new EtchedBorder());
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        toolkit = Toolkit.getDefaultToolkit();
        timer = new java.util.Timer();
        timer.schedule(new RemindTask(), seconds * 1000, seconds * 1000);

    }

    class RemindTask extends TimerTask {
                      int EvtCnt = 0;
        public void run() {
            LOG.info("in");
            EvtCnt++;
            if (writeFile > 0) {
                toolkit.beep();
                // LOG.info("Time's up! " + EvtCnt);
                if (EvtCnt > 3) {
                    //timer.cancel(); //Terminate the timer thread, or
                    //System.exit(0);   // Stops the AWT thread
                    // (and everything else)
                    Image photo = grabFrameImage();
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
                        LOG.info("got Image photo ...file = " + str);
                        // MySnapshot snapshot = new MySnapshot ( photo, new Dimension ( imageSize ) );
                        writeJPEG(str, photo);
                    } else {
                        LOG.info("Error : Could not grab frame");
                    }
                } else {
                    LOG.info("Time's up! " + EvtCnt);
                }
            }
        }
    }

    /* --------------------------------------------------------------
     * Initialise
     *
     * @returns true if web cam is detected
     * -------------------------------------------------------------- */

    public boolean initialise()
            throws Exception {
        return (initialise(autoDetect()));
    }

    /* -------------------------------------------------------------------
     * Initialise
     *
     * @params _deviceInfo, specific web cam device if not autodetected
     * @returns true if web cam is detected
     * ------------------------------------------------------------------- */

    public boolean initialise(CaptureDeviceInfo _deviceInfo)
            throws Exception {
        LOG.info("Initialising...");
        statusBar.setText("Initialising...");
        webCamDeviceInfo = _deviceInfo;

        if (webCamDeviceInfo != null) {
            statusBar.setText("Connecting to : " + webCamDeviceInfo.getName());
            LOG.info("Connecting to : " + webCamDeviceInfo.getName());

            try {
                setUpToolBar();
                getContentPane().add(toolbar, BorderLayout.NORTH);

                ml = webCamDeviceInfo.getLocator();
                if (ml != null) {
                    player = Manager.createRealizedPlayer(ml);
                    if (player != null) {
                        player.start();
                        formatControl = (FormatControl) player.getControl("javax.media.control.FormatControl");
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
                                currentFormat = (VideoFormat) currFormat;
                                imageSize = currentFormat.getSize();
                                visualContainer.setPreferredSize(imageSize);
//                                setSize(imageSize.width, imageSize.height + statusBar.getHeight() + toolbar.getHeight());
                                statusBar.setText("Connected: " + webCamDeviceInfo.getName());
                                LOG.info("Connected : " + webCamDeviceInfo.getName());
                                LOG.info("Size : " + imageSize.width + "x" + imageSize.height);
                            } else {
                                System.err.println("Error : Cannot get current video format");
                            }

                            invalidate();
                            pack();
                            LOG.info("Done, returning TRUE ...");
                            return (true);
                        } else {
                            System.err.println("Error : Could not get visual component");
                            return (false);
                        }
                    } else {
                        System.err.println("Error : Cannot create player");
                        statusBar.setText("Cannot create player");
                        return (false);
                    }
                } else {
                    System.err.println("Error : No MediaLocator for " + webCamDeviceInfo.getName());
                    statusBar.setText("No Media Locator for : " + webCamDeviceInfo.getName());
                    return (false);
                }
            } catch (IOException ioEx) {
                statusBar.setText("NO connection to : " + webCamDeviceInfo.getName());
                return (false);
            } catch (NoPlayerException npex) {
                statusBar.setText("Cannot create player");
                return (false);
            } catch (CannotRealizeException nre) {
                statusBar.setText("Cannot realize player");
                return (false);
            }
        } else {
            statusBar.setText("No web cam information");
            return (false);
        }
    }

//
    /* -------------------------------------------------------------------
     * Dynamically create menu items
     *
     * @returns the device info object if found, null otherwise
     * ------------------------------------------------------------------- */

    public void setFormat(VideoFormat selectedFormat) {
        if (formatControl != null) {
            player.stop();

            imageSize = selectedFormat.getSize();
            formatControl.setFormat(selectedFormat);

            player.start();

            statusBar.setText("Format : " + selectedFormat);

            currentFormat = selectedFormat;
            visualContainer.setPreferredSize(currentFormat.getSize());

            setSize(imageSize.width, imageSize.height + statusBar.getHeight() + toolbar.getHeight());
        } else {
            LOG.info("Visual component not an instance of FormatControl");
            statusBar.setText("Visual component cannot change format");
        }
    }


    public VideoFormat getFormat() {
        return (currentFormat);
    }


    protected void setUpToolBar() {
        toolbar = new JToolBar();

        // Note : If you supply the 16 x 16 bitmaps then you can replace
        // the commented line in the MyToolBarAction constructor

        formatButton = new MyToolBarAction("Resolution", "BtnFormat.jpg");
        captureButton = new MyToolBarAction("Capture", "BtnCapture.jpg");
        videoButton = new MyToolBarAction("Video", "BtnCapture.jpg");

        toolbar.add(formatButton);
        toolbar.add(captureButton);
        toolbar.add(videoButton);

        getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    protected void toolbarHandler(MyToolBarAction actionBtn) {
        if (actionBtn == formatButton) {
            Object selected = JOptionPane.showInputDialog(this,
                    "Select Video format",
                    "Capture format selection",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,        //  Icon icon,
                    myFormatList, // videoFormats,
                    currentFormat);
            if (selected != null) {
                setFormat(((MyVideoFormat) selected).format);
            }
        } else if (actionBtn == captureButton) {
            Image photo = grabFrameImage();
            if (photo != null) {
                int i = fileCounter++;
                String str = "tempb";
                if (i < 10) {
                    str = str + "000" + i;
                } else if (i < 100) {
                    str = str + "00" + i;
                }
                if (fileCounter > 9) {
                    fileCounter = 1;
                }
                str += ".jpg";
                LOG.info("got Image photo ...file = " + str);
//                    new Timer(3000, (ActionListener) this).start();
                MySnapshot snapshot = new MySnapshot(photo, new Dimension(imageSize));

            } else {
                System.err.println("Error : Could not grab frame");
            }
        } else if((actionBtn == videoButton)) {
            LOG.info("grab video");

            try {
                playerClose();

                Vector deviceList = CaptureDeviceManager.getDeviceList(new YUVFormat()); //get all media devices

                CaptureDeviceInfo device = webCamDeviceInfo; //in this computer the only capture device is in=built webcam stays at 0th position
                Format[] formats = device.getFormats(); //get all formats

                MediaLocator mlv = device.getLocator();

                Player pl = Manager.createRealizedPlayer(mlv);

                pl.start();

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
                Thread.sleep(2000);
                dataSink.start();
                processor.start();

                Thread.sleep(2000);

                dataSink.close();
                processor.stop();
                processor.close();

                LOG.info("proccessor and datasink stop");

                pl.stop();

                LOG.info(pl);
                pl.deallocate();
                pl = null;

                initialise();

//                new Thread() {
//                    @Override
//                    public void run() {
//                        javafx.application.Application.launch(VideoPlayer.class);
//                    }
//                }.start();
//
//                VideoPlayer startUpTest = VideoPlayer.waitForStartUpTest();
//                new Thread() {
//                    @Override
//                    public void run() {
//                        javafx.application.Application.launch(ShootingVideo.class);
//                    }
//                }.start();


//                Application.launch(VideoPlayer.class);
//                startUpTest.printSomething();
                String exec = "C:\\ffmpeg-20160619-5f5a97d-win64-static\\bin\\ffmpeg -i C:\\Users\\Андрей\\IdeaProjects\\tesis\\testcam.mp4 -r 25 -f image2 images%05d.png ";
                Process process = Runtime.getRuntime().exec(exec);

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line = "";

                while((line = br.readLine()) != null){
                    LOG.info("Line " + line);
                }

            } catch (Exception ex) {
                LOG.info(ex);

            }
        }
    }

    /* -------------------------------------------------------------------
     * autoDetects the first web camera in the system
     * searches for video for windows ( vfw ) capture devices
     *
     * @returns the device info object if found, null otherwise
     * ------------------------------------------------------------------- */

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


    /* -------------------------------------------------------------------
     * deviceInfo
     *
     * @note outputs text information
     * ------------------------------------------------------------------- */

    public void deviceInfo() {
        if (webCamDeviceInfo != null) {
            Format[] formats = webCamDeviceInfo.getFormats();

            if ((formats != null) && (formats.length > 0)) {
                LOG.info("deviceInfo: Video Format count " + formats.length);
            } else {
                LOG.info("deviceInfo: NO Video Format count " + formats.length);
            }

            for (int i = 0; i < formats.length; i++) {
                Format aFormat = formats[i];
                if (aFormat instanceof VideoFormat) {
                    Dimension dim = ((VideoFormat) aFormat).getSize();
                    LOG.info("Video Format " + i + " : " + formats[i].getEncoding() + ", " + dim.width + " x " + dim.height);
                }
            }
        } else {
            LOG.info("Error : No web cam detected");
        }
    }

    /* -------------------------------------------------------------------
     * grabs a frame's buffer from the web cam / device
     *
     * @returns A frames buffer
     * ------------------------------------------------------------------- */

    public Buffer grabFrameBuffer() {
        if (player != null) {
            FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
            if (fgc != null) {
                return (fgc.grabFrame());
            } else {
                System.err.println("Error : FrameGrabbingControl is null");
                return (null);
            }
        } else {
            System.err.println("Error : Player is null");
            return (null);
        }
    }


    /* -------------------------------------------------------------------
     * grabs a frame's buffer, as an image, from the web cam / device
     *
     * @returns A frames buffer as an image
     * ------------------------------------------------------------------- */

    public Image grabFrameImage() {
        Buffer buffer = grabFrameBuffer();
        if (buffer != null) {
            // Convert it to an image
            BufferToImage btoi = new BufferToImage((VideoFormat) buffer.getFormat());
            if (btoi != null) {
                Image image = btoi.createImage(buffer);
                if (image != null) {
                    return (image);
                } else {
                    System.err.println("Error : BufferToImage cannot convert buffer");
                    return (null);
                }
            } else {
                System.err.println("Error : cannot create BufferToImage instance");
                return (null);
            }
        } else {
            LOG.info("Error : Buffer grabbed is null");
            return (null);
        }
    }


    /* -------------------------------------------------------------------
     * Closes and cleans up the player
     *
     * ------------------------------------------------------------------- */

    public void playerClose() {
        if (player != null) {
            LOG.info("playerClose(): closing ...");
            player.close();
            player.deallocate();
            player = null;
        }
        if (timer != null) {
            timer.cancel(); //Terminate the timer thread, or
        }
    }

    public void windowClosing(WindowEvent e) {
        LOG.info("windowClosing(e): close player ...");
        playerClose();
        System.exit(1);
    }

    public void componentResized(ComponentEvent e) {
        Dimension dim = getSize();
        boolean mustResize = false;

        if (dim.width < MIN_WIDTH) {
            dim.width = MIN_WIDTH;
            mustResize = true;
        }

        if (dim.height < MIN_HEIGHT) {
            dim.height = MIN_HEIGHT;
            mustResize = true;
        }

        if (mustResize)
            setSize(dim);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }


    protected void finalize() throws Throwable {
        playerClose();
        super.finalize();
    }

    class MyToolBarAction extends AbstractAction {
        public MyToolBarAction(String name, String imagefile) {
            // Note : Use this version, if you supply your own toolbar icons
            // super ( name, new ImageIcon ( imagefile ) );

            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            toolbarHandler(this);
        }
    }

    ;


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

    ;

    // window, to hold the SNAPSHOT, sort of PREVIEW
    // When the window is CLOSED, a Save File dialog is openned
    // and the JPEG is written to the user's accepted, file name
    class MySnapshot extends JFrame {
        protected Image photo = null;
        protected int shotNumber;

        public MySnapshot(Image grabbedFrame, Dimension imageSize) {
            super();

            shotNumber = shotCounter++;
            setTitle("Photo" + shotNumber);

            photo = grabbedFrame;

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            int imageHeight = photo.getWidth(this);
            int imageWidth = photo.getHeight(this);

            setSize(imageSize.width, imageSize.height);
            long nanoTime = (System.nanoTime());
            String str = Objects.toString(nanoTime, null);
            // if ( saveJPEG ( filename ) )
            if (writeJPEG(str, photo)) {
                dispose();
            }

            dispose();

            setVisible(true);
        }


        public void paint(Graphics g) {
            g.drawImage(photo, 0, 0, getWidth(), getHeight(), this);
        }


        /* -------------------------------------------------------------------
         * Saves an image as a JPEG
         *
         * @params the image to save
         * @params the filename to save the image as
                 * java FileOutputStream -
                 *
         * ---------------r---------------------------------------------------- */

    }   // of MySnapshot

    public boolean writeJPEG(String filename, Image photo) {
        boolean saved = false;
        BufferedImage bi = new BufferedImage(photo.getWidth(null),
                photo.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(photo, null, null);
        FileOutputStream out = null;

        try {
            LOG.info(filename);
            filename += ".jpg";
            out = new FileOutputStream(filename);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            param.setQuality(1.0f, false);   // 100% high quality setting, no compression
            encoder.setJPEGEncodeParam(param);
            encoder.encode(bi);
            out.close();
            saved = true;
            LOG.info("Done saving JPEG : " + filename);
        } catch (Exception ex) {
            LOG.info("Error saving JPEG : " + ex.getMessage());
        }

        return (saved);
    }
}

// eof
