package io.cify.framework.recording


import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static io.humble.video.awt.MediaPictureConverterFactory.findDescriptor;

/**
 *  Class responsible for creating video from screenshots
 */

public class Recording {

    private static int imageWidth = 0;
    private static int imageHeight = 0;

    /**
     * Takes screenshot files from directory and converts them to media file
     *
     * @param screenshotsDir - list of images
     * @param mediaFileDuration - output media duration in seconds
     * @param outputMediaDirectory - output media file directory
     * @param outputMediaFile - output media file name
     *
     * */
    public static void screenshotsToVideo(String screenshotsDir, int mediaFileDuration, String outputMediaDirectory, String outputMediaFile){

        List<BufferedImage> bufferedImageList = getBufferedImageListFromDir(screenshotsDir);

        new File(outputMediaDirectory).mkdirs()

        imageListToMediaFile(bufferedImageList, outputMediaDirectory + outputMediaFile, null,null, mediaFileDuration);

    }

    /**
     * Takes all image files from directory and add them to list
     *
     * @param directory - directory with files
     * @return bufferedImageList - list of BufferedImage objects
     */
    private static List<BufferedImage> getBufferedImageListFromDir(String directory){
        List<BufferedImage> bufferedImageList = new ArrayList<BufferedImage>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equalsIgnoreCase(".ds_store")) {
                String filename = listOfFiles[i].getName();

                try {
                    bufferedImageList.add(ImageIO.read( new File(directory + "/" + filename)));
                } catch (Exception e){System.out.println(e.getMessage());}

            }
        }

        return bufferedImageList;
    }


    /**
     * Converts list of images to media file
     *
     * @param bufferedImageList - list of images
     * @param fileName - output media file name
     * @param formatName - output media format (optional)
     * @param codecName - output media codec (optional)
     * @param duration - output media duration
     *
     * */
    private static void imageListToMediaFile(List<BufferedImage> bufferedImageList, String fileName, String formatName,
                                             String codecName, int duration) throws AWTException, InterruptedException, IOException {

        /** Set expected dimensions basing on first image from list */
        BufferedImage firstScreenshotImage = bufferedImageList.get(0);
        imageWidth = firstScreenshotImage.getWidth();
        imageHeight = firstScreenshotImage.getHeight();
        if(imageWidth % 2 != 0 ) { imageWidth++;}
        if(imageHeight % 2 != 0) { imageHeight++;}


        /** Define video framerate and frame size  */
        final Rectangle screenbounds = new Rectangle(0,0, imageWidth, imageHeight);
        int ratio = 1;
        int imagesCount = bufferedImageList.size();
        if(duration!=0 && imagesCount!=0) { ratio = imagesCount/duration; }
        if(ratio < 1) {ratio =1;}
        final Rational framerate = Rational.make(1, ratio);

        /** Create muxer */
        final Muxer muxer = Muxer.make(fileName, null, formatName);

        /** Set codec */
        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        if (codecName != null) {
            codec = Codec.findEncodingCodecByName(codecName);
        } else {
            codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        }

        /** Create and set video encoder using YUV420P format and incoming image dimensions */
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        Encoder encoder = Encoder.make(codec);
        encoder.setWidth(screenbounds.width);
        encoder.setHeight(screenbounds.height);
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        /** Set encoder global header*/
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        /** Open the encoder. */
        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        /** Make MediaPicture object in YUV420P format */
        final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
        picture.setTimeBase(framerate);

        /**
         *  Loop through BufferedImage list,
         *  convert image, encode and write video
         */
        MediaPictureConverter converter = null;
        final MediaPacket packet = MediaPacket.make();
        int i = 0;
        for(BufferedImage bi: bufferedImageList) {
            final BufferedImage screen = convertToType(bi, BufferedImage.TYPE_3BYTE_BGR);

            /** Convert image to YUV420P format. */
            if (converter == null){
                String converterDescriptor = findDescriptor(screen);
                converter = MediaPictureConverterFactory.createConverter(converterDescriptor, picture.getFormat(),
                        imageWidth, imageHeight);
            }
            converter.toPicture(picture, screen, i);

            /** Encode */
            while (packet.isComplete()) {
                encoder.encode(packet, picture);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } ;

            i++;
        }

        /** Flush encoder */
        while (packet.isComplete()){
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        } ;

        muxer.close();
    }


    /**
     * Convert BufferedImage to specified type.
     *
     * @param sourceImage - the image to be converted
     * @param targetType - the desired BufferedImage type
     *
     * @return a BufferedImage of the specifed target type.
     */

    private static BufferedImage convertToType(BufferedImage sourceImage,
                                               int targetType)
    {
        BufferedImage image;
        if (sourceImage.getType() == targetType){
            image = sourceImage;
        }
        else
        {
            image = new BufferedImage(imageWidth,
                    imageHeight, targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
}