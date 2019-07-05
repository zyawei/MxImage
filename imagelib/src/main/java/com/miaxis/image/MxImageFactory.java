package com.miaxis.image;

import org.zz.jni.mxImageTool;

/**
 * @author zhangyw
 * @date 2019-06-25 09:27
 * @email zyawei@live.com
 */
public class MxImageFactory {

    /**
     * 加载图像文件
     *
     * @param path 图像路径
     * @return {@link MxImage}
     * @see #decodeFile(String, int)
     */
    public static MxImage decodeFile(String path) {
        return decodeFile(path, MxImage.CHANNEL_RGB);
    }

    /**
     * 功	能：	图像文件加载到内存
     *
     * @param path    - 输入	图像路径
     * @param channel - 输入  图像通道数，1-加载为灰度图像，3-加载为RGB图像
     * @return {@link MxImage}
     */
    public static MxImage decodeFile(String path, @MxImage.Channel int channel) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        int[] outImageWidth = new int[1];
        int[] outImageHeight = new int[1];
        int sizeResult = mxImageTool.ImageLoad(path, channel, null, outImageWidth, outImageHeight);
        if (sizeResult != 1) {
            return null;
        }
        byte[] outImageData = new byte[outImageWidth[0] * outImageHeight[0] * 3];
        int loadResult = mxImageTool.ImageLoad(path, channel, outImageData, outImageWidth, outImageHeight);
        if (loadResult != 1) {
            return null;
        }
        return new MxImage(outImageData, outImageWidth[0], outImageHeight[0], MxImage.FORMAT_BGR, channel);
    }

    public static MxImage decodeBytes(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        byte[] pRGB24Buf = new byte[bytes.length * 3];
        int[] iWidth = new int[1];
        int[] iHeight = new int[1];
        int result = mxImageTool.ImageDecode(bytes, bytes.length, pRGB24Buf, iWidth, iHeight);
        return result == 0 ? new MxImage(pRGB24Buf, iWidth[0], iHeight[0]) : null;
    }

}
