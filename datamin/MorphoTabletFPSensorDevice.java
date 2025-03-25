package com.igenesys.datamin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

//import com.igenesys.BiometricActivity;
import com.igenesys.datamin.AuthBfdCap;
//import com.datamini.morphointegrationsample.AuthBfdCap;
import com.igenesys.utils.AppLog;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
import com.morpho.morphosmart.sdk.MorphoUser;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;



public class MorphoTabletFPSensorDevice implements Observer {


    private Activity actObj;

    private static final Logger logger = Logger
            .getLogger(MorphoTabletFPSensorDevice.class.getName());

    protected static final USBManager ProcessInfo = null;

//	protected static final int MORPHO_FAR_5 = 0;

    private static final int MORPHO_STANDARD_MATCHING_STRATEGY = 0;

    protected static final CompressionAlgorithm MORPHO_NO_COMPRESS = null;

    public static String userID,Firstname,LastName;

    int compressRate=0;
    boolean exportMinutiae=true;
    int fingerNumber=0x02;
    public String userid;
    public String fname;
    public String lname;

    static final int  MORPHO_FAR_5 = 5;


    ImageView im;

    AuthBfdCap objClass;

    public Bitmap bm = null;

    /**
     * The Morpho device.
     */
    static MorphoDevice morphoDevice = new MorphoDevice();

    static MorphoUser morphoenroll = new MorphoUser();

    static MorphoUser fingerdata = new MorphoUser();

    /**
     * The Morpho database.
     */
    static MorphoDatabase morphoDatabase = new MorphoDatabase();

    /**
     * The timeout.
     */
    private int timeout = 30;

    /**
     * The acquisition threshold.
     */
    private int acquisitionThreshold = 0;



    /**
     * The advanced security levels required.
     */
    private int advancedSecurityLevelsRequired = 0;

    /**
     * The template type.
     */
    private TemplateType templateType = TemplateType.MORPHO_PK_ISO_FMR;
    //private TemplateType templateType = TemplateType.MORPHO_PK_MAT;

    /**
     * The finger template format.
     */
    //private TemplateType fingerTemplateFormat = TemplateType.MORPHO_PK_COMP;
    //private TemplateType fingerTemplateFormat = TemplateType.MORPHO_PK_MAT;

    /**
     * The template finger VP type.
     */
    private TemplateFVPType templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;

    /**
     * The max size template.
     */
    private int maxSizeTemplate = 512;

    /**
     * The enroll type.
     */
    private EnrollmentType enrollType = EnrollmentType.ONE_ACQUISITIONS;

    /**
     * The latent detection.
     */
    private LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;

    /**
     * The coder choice.
     */
    private Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;



    /**
     * The detect mode choice.
     */
    private int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue()
            | DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();

    /**
     * The callback command.
     */
    private int callbackCmd = CallbackMask.MORPHO_CALLBACK_IMAGE_CMD.getValue()
            ^ CallbackMask.MORPHO_CALLBACK_COMMAND_CMD.getValue()
            ^ CallbackMask.MORPHO_CALLBACK_CODEQUALITY.getValue()
            ^ CallbackMask.MORPHO_CALLBACK_DETECTQUALITY.getValue();;

    /**
     * The last image.
     */
    public byte[] lastImage = null;

    /**
     * The last image width.
     */
    private int lastImageWidth = 0;
    private Template t;

    /**
     * The last image height.
     */
    public int lastImageHeight = 0;

    public byte[] templateBuffer = null;

    /**
     * last callback msg
     */

    private String callbackMsg = "";

    protected ResultMatching resultMatching = new ResultMatching();

    /**
     * Instantiates a new MorphoTablet fingerprint sensor device.
     *
     * @since 2.0
     */

    public MorphoTabletFPSensorDevice(AuthBfdCap obj) {

        objClass = obj;

    }

    public void setViewToUpdate(ImageView imageView) {

        im = imageView;

    }



    public int open(Activity arg0) {
        // LOGGER.info(">>> MorphoTabletFPSensorDevice open");
        // USBManager.registerReceiver(arg0);
        String sensorName;
        actObj = arg0;
        int ret;
        USBManager.getInstance().initialize(arg0,"com.morpho.morphosample.USB_ACTION");
        Integer nbUsbDevice = new  Integer(0);
        ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);
        sensorName = morphoDevice.getUsbDeviceName(0);
        morphoDevice.closeDevice();

        return ret = morphoDevice.openUsbDevice(sensorName, 0);

    }



    public boolean isDeviceConnected()
    {

        int result=0;
        result = open(actObj);
        if(result==0)
            return true;
        else
            return false;
    }


    public String getSerialNumber()
    {
        return morphoDevice.getUsbDeviceName(0);
    }


    public String getDeviceMake()
    {
        if(morphoDevice.getUsbDeviceName(0) !=null && morphoDevice.getUsbDeviceName(0).trim().equals(""))
            return "-1";
        else
            return "Morpho";
    }


    public String getDeviceModel()
    {
        if(morphoDevice.getUsbDeviceName(0) !=null && morphoDevice.getUsbDeviceName(0).trim().equals(""))
            return "-1";
        else
            return "MSO 1300 E";
    }



    /**
     * Start capture.
     *
     * @version 2.0
     * @throws Exception
     *             the exception
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#startCapture()
     * @since 2.0
     */


    public void startCapture(Activity activity) throws Exception {

        final Observer oThis = this;
        this.open(actObj);
        final Template template = null;


        new Thread() {
            public void run() {
                // Capture the image
                TemplateList templateList = new TemplateList();
                templateList.setActivateFullImageRetrieving(true);

                final int ret = morphoDevice.capture(timeout,
                        acquisitionThreshold, advancedSecurityLevelsRequired,
                        1, templateType, templateFVPType, maxSizeTemplate,
                        enrollType, latentDetection, coderChoice,
                        detectModeChoice, templateList, callbackCmd, oThis);

                try {
                    if ((ret == 0)
                            && (templateType != TemplateType.MORPHO_NO_PK_FP)) {

                        int nb = templateList.getNbTemplate();

                        for (int j = 0; j < nb; j++) {
                            MorphoImage morphoImage = null;
                            try {

                                t = templateList.getTemplate(j);
                                templateBuffer = t.getData();
                                morphoImage = templateList.getImage(j);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                int dataWidth = lastImageWidth;
                                int dataHeight = lastImageHeight;
                                byte[] datafi = (lastImage);
                                if (morphoImage != null) {

                                    datafi = morphoImage.getImage();
                                    dataWidth = morphoImage.getMorphoImageHeader().getNbColumn();
                                    dataHeight = morphoImage.getMorphoImageHeader().getNbRow();
                                }

                                lastImage = datafi;
                                lastImageWidth = dataWidth;
                                lastImageHeight = dataHeight;

                                updateView("Finger Captured Successfully", ret);


                            } catch (Exception e) {
                                updateView("Error in Capturing Finger", ret);
                                e.printStackTrace();
                               // AppLog.logData(activity,"Exception startCapture:"+e.getMessage());
                            }
                        }

                    } else if (ret == ErrorCodes.MORPHOERR_TIMEOUT) {
                        updateView("Capture Timed Out", ret);
                    } else {
                        updateView("Error in Capturing Finger", ret);
                    }

                } catch (Exception e) {

                }
            }
        }.start();
    }


    //@Override
    public Bitmap convertRAWtoBitmap(byte[] rawImage) {

        int retError = 0;
        byte[] Src = rawImage; // Comes from somewhere...
        if (retError == 0) {
            byte[] Bits = new byte[Src.length * 4]; // That's where the RGBA
            // array
            // goes.
            int i;
            for (i = 0; i < Src.length; i++) {
                Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) Src[i];

                // Invert the source bits
                Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
            }

            // Now put these nice RGBA pixels into a Bitmap object

            bm = Bitmap.createBitmap(lastImageWidth, lastImageHeight, Bitmap.Config.ARGB_8888);
            bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

        } else {
            objClass.updateImageView(null, null, "Error", true, retError);
        }

        return bm;
    }


    public int verifyMatch(Template tmp1,Template tmp2) {
        System.out.println("CBMTEST in verify match");
        // in Fact nothing, we'll use the Matcher module for that
        TemplateList listSearch, listRef;
        Template tmpl1, tmpl2;
        Integer matchingScore = new Integer(0);

        listSearch = new TemplateList();
        listRef = new TemplateList();

        listSearch.putTemplate(tmp1);
        listRef.putTemplate(tmp2);

        int err = morphoDevice.verifyMatch(5, listSearch, listRef,matchingScore);
        System.out.println("CBMTEST Found error: "+err);

        return err;
    }



    /**
     * Cancel live acquisition.
     *
     * @version 2.0
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#cancelLiveAcquisition()
     * @since 2.0
     */
    // @Override
    public void cancelLiveAcquisition() {
        try {
            // LOGGER.info(">>> cancelLiveAcquisition");
            morphoDevice.cancelLiveAcquisition();
            // LOGGER.info("<<< cancelLiveAcquisition");
        } catch (Exception e) {
            // LOGGER.error("cancelLiveAcquisition error", e);

        }
    }

    /**
     * Release.
     *
     * @version 2.0
     * @throws DeviceException
     *             the device exception
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.Device#release()
     * @since 2.0
     */
    // @Override
    public void release() {
        morphoDevice.closeDevice();
    }

    public void updateLiveView(byte[] liveImage, String msg, int imageWidth,
                               int imageHeight) {

        byte[] Src = liveImage; // Comes from somewhere...
        if (liveImage != null) {
            byte[] Bits = new byte[Src.length * 4]; // That's where the RGBA
            // array
            // goes.
            int i;
            for (i = 0; i < Src.length; i++) {
                Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = ((byte) ~Src[i]);

                // Invert the source bits
                Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
            }

            // Now put these nice RGBA pixels into a Bitmap object

            bm = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
            bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
            if (objClass != null && im != null){
                objClass.updateImageView(im, bm, msg, false, 0);
                logger.log(Level.SEVERE, "Inside 3");
            }

        } else {
            logger.log(Level.SEVERE, "Inside 4");
            objClass.updateImageView(null, null, msg, false, 0);
            //objClass.setQlyFinger(quality);
        }

    }


    public int verify(final TemplateList listSearch) {
        final ResultMatching matchingScore = new ResultMatching();
        return morphoDevice.verify(30,5, coderChoice,detectModeChoice,0, listSearch,callbackCmd,MorphoTabletFPSensorDevice.this, matchingScore);

        // in Fact nothing, we'll use the Matcher module for that
//
//        final int result;
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//            }
//        }).start();

    }

    // @Override
    public void updateView(String msg, int retError) {

        byte[] Src = lastImage; // Comes from somewhere...
        if (retError == 0) {
            byte[] Bits = new byte[Src.length * 4]; // That's where the RGBA
            // array
            // goes.
            int i;
            for (i = 0; i < Src.length; i++) {
                Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) Src[i];

                // Invert the source bits
                Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
            }

            // Now put these nice RGBA pixels into a Bitmap object

            bm = Bitmap.createBitmap(lastImageWidth, lastImageHeight,
                    Bitmap.Config.ARGB_8888);
            bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
            if (bm != null) {
                logger.log(Level.SEVERE, "Inside 1");
                objClass.updateImageView(im, bm, msg, true, retError);

                //objClass.setQlyFinger(quality);
            }
        } else {
            logger.log(Level.SEVERE, "Inside 2");
            objClass.updateImageView(null, null, msg, true, retError);
        }

    }

    /**
     * Gets the image from data.
     *
     * @version 2.0
     * @param numColumns
     *            the num columns
     * @param numRows
     *            the num rows
     * @param greyscaleImageData
     *            the grey scale image data
     * @return the image from data
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#getImageFromData(int,
     *      int, byte[])
     * @since 2.0
     * @post $result != null
     */
    // @Override
    public Bitmap getImageFromData(int numColumns, int numRows,
                                   byte[] greyscaleImageData) {
        // message is a low resolution image, display it.
        return BitmapFactory.decodeByteArray(greyscaleImageData, 0,
                greyscaleImageData.length);
    }

    /**
     * Get live image from the device.
     *
     * @version 2.0
     * @param numColumns
     *            height of the image
     * @param numRows
     *            width of the image
     * @param bwImageData
     *            image data to display
     * @return Bitmap converted image
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#getPreviewFromData(int,
     *      int, byte[])
     * @since 2.0
     * @post $result != null
     */
    // @Override
    public Bitmap getPreviewFromData(int numColumns, int numRows,
                                     byte[] bwImageData) {
        return getImageFromData(numColumns, numRows, bwImageData);
    }

    /**
     * Retrieve image.
     *
     * @version 2.0
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#retrieveImage()
     * @since 2.0
     */
    // /@Override
    public void retrieveImage() {
        // TODO Complete
    }

    /**
     * Verify fingerprint.
     *
     * @version 2.0
     * @param arg0
     *            the arg0
     * @param arg1
     *            the arg1
     * @see com.morpho.mims.fmk.platform.device.deviceinterface.FingerPrintDevice#verifyFingerprint(byte[],
     *      int)
     * @since 2.0
     */
    // @Override
    public void verifyFingerprint(byte[] arg0, int arg1) {
        // TODO : in Fact nothing, we'll use the Matcher module for that

    }

    /**
     * The raw header size.
     */
    private int RAW_HEADER_SIZE = 12;

    public int quality;

    /**
     * Update.
     *
     * @version 2.0
     * @param observable
     *            the observable
     * @param arg
     *            the arg
     * @see Observer#update(Observable, Object)
     * @since 2.0
     */
    public void update(Observable observable, Object arg) {
        try {
            // convert the object to a callback back message.
            CallbackMessage message = (CallbackMessage) arg;
            int type = message.getMessageType();
            switch (type) {
                // --------------------
                // MESSAGES
                // --------------------
                case 1:
                    // FingerPrintMessage fingerPrintMessage =
                    // FingerPrintMessage.UNKNOWN_MESSAGE;
                    // message is a command.
                    Integer command = (Integer) message.getMessage();

                    // Analyze the command.
                    switch (command) {
                        case 0:
                            /** < The terminal waits for the user's finger. */
                            // fingerPrintMessage =
                            // FingerPrintMessage.PLACE_FINGER_FOR_ACQUISITION;
                            callbackMsg = "Place Finger For Acquisition";
                            break;
                        case 1:
                            /** < The user must move his/her finger up. */
                            // fingerPrintMessage = FingerPrintMessage.MOVE_UP;
                            callbackMsg = "Move Up";
                            break;
                        case 2:
                            /** < The user must move his/her finger down. */
                            // fingerPrintMessage = FingerPrintMessage.MOVE_DOWN;
                            callbackMsg = "Move Down";
                            break;
                        case 3:
                            /** < The user must move his/her finger to the left. */
                            // fingerPrintMessage = FingerPrintMessage.MOVE_LEFT;
                            callbackMsg = "Move Left";
                            break;
                        case 4:
                            /** < The user must move his/her finger to the right. */
                            // fingerPrintMessage = FingerPrintMessage.MOVE_RIGHT;
                            callbackMsg = "Move Right";
                            break;
                        case 5:
                            /**
                             * < The user must press his/her finger harder for the
                             * device to acquire a larger fingerprint image.
                             */
                            // fingerPrintMessage = FingerPrintMessage.PRESS_HARDER;
                            callbackMsg = "Press Harder";
                            break;
                        case 6:
                            /**
                             * < The system has detected a latent fingerprint in the
                             * input fingerprint. Please change finger position.
                             */
                            // fingerPrintMessage =
                            // FingerPrintMessage.REMOVE_YOUR_FINGER;
                            callbackMsg = "Remove Finger";
                            break;
                        case 7:
                            /** < User must remove his finger. */
                            // fingerPrintMessage =
                            // FingerPrintMessage.REMOVE_YOUR_FINGER;
                            callbackMsg = "Remove Finger";
                            break;
                        case 8:
                            /** < The finger acquisition was correctly completed. */
                            // fingerPrintMessage =
                            // FingerPrintMessage.ACQUISITION_COMPLETE;
                            callbackMsg = "Finger Capture Complete";
                            break;
                    }
                    logger.log(Level.SEVERE, "Command is " + command);
                    updateLiveView(null, callbackMsg, 0, 0);

                    break;

                // --------------------
                // IMAGES
                // --------------------
                case 2:
                    // message is a low resolution image, display it.
                    byte[] image = (byte[]) message.getMessage();
                    // quality = (Integer) message.getMessage();
                    byte[] imageRAW = new byte[image.length - RAW_HEADER_SIZE];
                    MorphoImage morphoImage = MorphoImage
                            .getMorphoImageFromLive(image);
                    if (morphoImage != null) {
                        int imageRowNumber = morphoImage.getMorphoImageHeader()
                                .getNbRow();
                        int imageColumnNumber = morphoImage.getMorphoImageHeader()
                                .getNbColumn();
                        System.arraycopy(image, RAW_HEADER_SIZE, imageRAW, 0,
                                image.length - RAW_HEADER_SIZE);
                        // Thumb picture is in reverse video : reverse it
                        // for (int reverseColor = 0; reverseColor <
                        // imageRAW.length; reverseColor++) {
                        // imageRAW[reverseColor] = (byte) (255 -
                        // imageRAW[reverseColor]);
                        // }
                        // ImageManager im = ImageManager.getInstance();
                        // Convert RAW8 to BMP
                        // byte[] bmpBuffer = im.raw8To(2 imageRAW,
                        // imageColumnNumber, imageRowNumber);
                        updateLiveView(imageRAW, callbackMsg, imageColumnNumber,
                                imageRowNumber);

                        lastImage = imageRAW;
                        lastImageWidth = imageColumnNumber;
                        lastImageHeight = imageRowNumber;
                    }

                    break;
                // --------------------
                // QUALITY
                // --------------------
                case 3:
                    quality = (Integer) message.getMessage();
                    Log.v("", "quality " + quality);

                    break;
            }
        } catch (Exception e) {
        }
    }
}

