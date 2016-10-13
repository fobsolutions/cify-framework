package io.cify.framework.recording;


import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.humble.video.awt.MediaPictureConverterFactory.findDescriptor;

/**
 * Class responsible for creating video from images
 */

public class Recording {

    private static final String MARKER = "RECORDING: ";
    private static int imageWidth = 0;
    private static int imageHeight = 0;

    /**
     * Takes images from directory and converts them to media file
     *
     * @param imagesDir            - images directory
     * @param mediaFileDuration    - output media duration in seconds
     * @param outputMediaDirectory - output media file directory
     * @param outputMediaFile      - output media file name
     */
    public static void imagesToMedia(String imagesDir, int mediaFileDuration, String outputMediaDirectory, String outputMediaFile) {

        System.out.println(MARKER + " - images to media started.");
        System.out.println(MARKER + " - parameters: incoming images directory " + imagesDir);
        System.out.println(MARKER + " - parameters: output directory " + outputMediaDirectory);
        System.out.println(MARKER + " - parameters: output media file " + outputMediaFile);
        System.out.println(MARKER + " - parameters: duration " + mediaFileDuration);

        if (!outputMediaDirectory.endsWith("/")) {
            outputMediaDirectory = outputMediaDirectory + "/";
        }

        try {
            List<BufferedImage> bufferedImageList = getBufferedImageListFromDir(imagesDir);
            imageListToMediaFile(bufferedImageList, outputMediaDirectory + outputMediaFile, null, null, mediaFileDuration);
        } catch (Exception e) {
            System.out.println(MARKER + " - failed to create media file from images cause: " + e.getMessage());
        }

        System.out.println(MARKER + " - images to media finished.");
    }

    /**
     * Takes all image files from directory and add them to list
     *
     * @param directory - directory with images
     * @return bufferedImageList - list of BufferedImage objects
     */
    private static List<BufferedImage> getBufferedImageListFromDir(String directory) throws IOException {
        System.out.println(MARKER + " - get BufferedImage list from directory "+ directory);

        List<BufferedImage> bufferedImageList = new ArrayList<>();
        File[] listOfFiles = new File(directory).listFiles();

        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                String filename = listOfFile.getName();

                if (!directory.endsWith("/")) {
                    directory = directory + "/";
                }
                bufferedImageList.add(ImageIO.read(new File(directory + filename)));
            }
        }
        return bufferedImageList;
    }


    /**
     * Converts list of images to media file
     *
     * @param bufferedImageList - list of images
     * @param fileName          - output media file name
     * @param formatName        - output media format (optional)
     * @param codecName         - output media codec (optional)
     * @param duration          - output media duration
     */
    private static void imageListToMediaFile(List<BufferedImage> bufferedImageList, String fileName, String formatName,
                                             String codecName, int duration) throws AWTException, InterruptedException, IOException {

        System.out.println(MARKER + " - image list to media file");

        /* Set expected dimensions basing on first image from list */
        BufferedImage firstScreenshotImage = bufferedImageList.get(0);
        imageWidth = firstScreenshotImage.getWidth();
        imageHeight = firstScreenshotImage.getHeight();
        if (imageWidth % 2 != 0) {
            imageWidth++;
        }
        if (imageHeight % 2 != 0) {
            imageHeight++;
        }

        /* Define video frame rate and frame size  */
        final Rectangle screenbounds = new Rectangle(0, 0, imageWidth, imageHeight);
        int ratio = 1;
        int imagesCount = bufferedImageList.size();
        if (duration != 0 && imagesCount != 0) {
            ratio = imagesCount / duration;
        }
        if (ratio < 1) {
            ratio = 1;
        }
        final Rational frameRate = Rational.make(1, ratio);

        /* Create muxer */
        final Muxer muxer = Muxer.make(fileName, null, formatName);

        /* Set codec */
        final MuxerFormat format = muxer.getFormat();
        final Codec codec;
        if (codecName != null) {
            codec = Codec.findEncodingCodecByName(codecName);
        } else {
            codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
        }

        /* Create and set video encoder using YUV420P format and incoming image dimensions */
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;
        Encoder encoder = Encoder.make(codec);
        encoder.setWidth(screenbounds.width);
        encoder.setHeight(screenbounds.height);
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(frameRate);

        /* Set encoder global header*/
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }

        /* Open the encoder. */
        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        /* Make MediaPicture object in YUV420P format */
        final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelFormat);
        picture.setTimeBase(frameRate);

        /*
         *  Loop through BufferedImage list,
         *  convert image, encode and write video
         */
        MediaPictureConverter converter = null;
        final MediaPacket packet = MediaPacket.make();
        int i = 0;
        for (BufferedImage bi : bufferedImageList) {
            final BufferedImage screen = convertToType(bi, BufferedImage.TYPE_3BYTE_BGR);

            /* Convert image to YUV420P format. */
            if (converter == null) {
                String converterDescriptor = findDescriptor(screen);
                converter = MediaPictureConverterFactory.createConverter(converterDescriptor, picture.getFormat(),
                        imageWidth, imageHeight);
            }
            converter.toPicture(picture, screen, i);

            /* Encode */
            do {
                encoder.encode(packet, picture);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());

            i++;
        }

        /* Flush encoder */
        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());

        muxer.close();
    }


    /**
     * Convert BufferedImage to specified type.
     *
     * @param sourceImage - the image to be converted
     * @param targetType  - the desired BufferedImage type
     * @return a BufferedImage of the specified target type.
     */

    private static BufferedImage convertToType(BufferedImage sourceImage,
                                               int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(imageWidth,
                    imageHeight, targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }
}