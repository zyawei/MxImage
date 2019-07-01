package com.miaxis.image;

import android.graphics.*;
import org.zz.jni.mxImageTool;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * MxImage Utils
 *
 * @date: 2018/11/13 15:23
 * @author: zhang.yw
 */
public class MxImages {

    static {
        System.loadLibrary("mx-image");
    }

    public static byte[] covertToJpeg(MxImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        switch (image.getFormat()) {
            case MxImage.FORMAT_BGR:
                byte[] buffer = new byte[image.getData().length * 2];
                int[] len = new int[1];
                int i = mxImageTool.ImageEncode(image.getData(), image.getWidth(), image.getHeight(), ".jpg", buffer, len);
                return i == 0 ? Arrays.copyOf(buffer, len[0]) : new byte[0];
            case MxImage.FORMAT_YUV:
                YuvImage yuvImage = new YuvImage(image.getData(), ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
                ByteArrayOutputStream os = new ByteArrayOutputStream(image.getData().length);
                yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, os);
                return os.toByteArray();
            default:
                throw new IllegalArgumentException("UnKnow format " + image.getFormat() + ", Only Support format BGR or YUV! ");
        }
    }

    public static Bitmap covertToBitmap(MxImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        switch (image.getFormat()) {
            case MxImage.FORMAT_BGR:
                /*return RGBBitmaps.rgb2Bitmap(image.getData(), image.getWidth(), image.getHeight());*/
                int[] colors = MxImages.BGR2Pixel(image.getData());
                return Bitmap.createBitmap(colors, 0, image.getWidth(), image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
            case MxImage.FORMAT_YUV:
                YuvImage yuvImage = new YuvImage(image.getData(), ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
                ByteArrayOutputStream os = new ByteArrayOutputStream(image.getData().length);
                yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, os);
                byte[] buffer = os.toByteArray();
                return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            default:
                throw new IllegalArgumentException("UnKnow format " + image.getFormat() + ", Only Support format BGR or YUV! ");
        }
    }


    public static MxImage yuv2BGR(MxImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MxImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format YUV ! ");
        }
        long startTime = System.nanoTime();
        byte[] bytes = convertYUV2BGR(image.getData(), image.getWidth(), image.getHeight());
        return new MxImage(bytes, image.getWidth(), image.getHeight(), MxImage.FORMAT_BGR, image.getChannel());
    }

    public static MxImage mirror(MxImage image) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MxImage.FORMAT_BGR) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Now Only Support format BGR ! ");
        }
        byte[] data = MxImages.mirrorBGR(image.getData(), image.getWidth(), image.getHeight());
        return new MxImage(data, image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
    }

    public static MxImage crop(MxImage image, Rect rect) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (rect == null) {
            throw new NullPointerException("rect  = null !");
        }
        if (image.getFormat() != MxImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format YUV ! ");
        }
        if (rect.left < 0 || rect.top < 0 || rect.right > image.getWidth() || rect.bottom > image.getHeight()) {
            throw new IllegalArgumentException("rect not in image bound !");
        }
        byte[] bytes = MxImages.cropYUV(image.getData(), image.getWidth(), image.getHeight(), rect.left, rect.top, rect.width(), rect.height());

        return new MxImage(bytes, rect.width(), rect.height(), image.getFormat(), image.getChannel());
    }


    public static MxImage scale(MxImage image, float scale) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (scale >= 1) {
            return image;
        }
        if (image.getFormat() != MxImage.FORMAT_BGR) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Only Support format BGR ! ");
        }
        int targetWidth = (int) (image.getWidth() * scale);
        int targetHeight = (int) (image.getHeight() * scale);
        byte[] bytes = MxImages.scaleBGR(image.getData(), image.getWidth(), image.getHeight(), targetWidth, targetHeight, 24);
        return new MxImage(bytes, targetWidth, targetHeight, image.getFormat(), image.getChannel());
    }


    public static MxImage rotate(MxImage image, int degree) {
        if (image == null) {
            throw new NullPointerException("image = null !");
        }
        if (image.getFormat() != MxImage.FORMAT_YUV) {
            throw new IllegalArgumentException("UnSupport format " + image.getFormatName() + ", Now Only Support format YUV ! ");
        }
        switch (degree) {
            case 0:
                return new MxImage(image.getData(), image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
            case 90: {
                byte[] data = MxImages.rotateYuv90(image.getData(), image.getWidth(), image.getHeight());
                return new MxImage(data, image.getHeight(), image.getWidth(), image.getFormat(), image.getChannel());
            }
            case 180: {
                byte[] data = MxImages.rotateYuv180(image.getData(), image.getWidth(), image.getHeight());
                return new MxImage(data, image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
            }
            case 270: {
                byte[] data = MxImages.rotateYuv270(image.getData(), image.getWidth(), image.getHeight());
                return new MxImage(data, image.getHeight(), image.getWidth(), image.getFormat(), image.getChannel());
            }
            default:
                throw new IllegalArgumentException("UnSupport degree " + degree);
        }

    }

    public static String getFormatName(int format) {
        switch (format) {
            case MxImage.FORMAT_BGR:
                return "BGR";
            case MxImage.FORMAT_YUV:
                return "YUV";
            default:
                return "UnKnow";
        }
    }

    public static String getChannelName(int channel) {
        switch (channel) {
            case MxImage.CHANNEL_RGB:
                return "RGB";
            case MxImage.CHANNEL_GRAY:
                return "GRAY";
            default:
                return "UnKnow";
        }
    }

    private static native byte[] rotateYuv90(byte[] data, int width, int height);

    private static native byte[] rotateYuv180(byte[] data, int width, int height);

    private static native byte[] rotateYuv270(byte[] data, int width, int height);

    private static native byte[] cropYUV(byte[] bgrImage, int width, int height, int x, int y, int dw, int dh);

    private static native byte[] convertYUV2BGR(byte[] bgrImage, int width, int height);

    private static native byte[] scaleBGR(byte[] data, int width, int height, int targetWidth, int targetHeight, int colorBits);

    private static native int[] BGR2Pixel(byte[] data);

    private static native byte[] mirrorBGR(byte[] data, int width, int height);
}
