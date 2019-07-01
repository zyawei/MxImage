package com.miaxis.image;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import org.zz.jni.mxImageTool;

import java.util.List;

/**
 * @author zhangyw
 * @date 2019-06-25 09:45
 * @email zyawei@live.com
 */
public class MxImageDrawer {

    /*******************************************************************************************
     功	能：	RGB图像数据转换为灰度图像数据
     参	数：	pRGBImage	- 输入	RGB图像数据
     iImgWidth	- 输入	图像宽度
     iImgHeight	- 输入	图像高度
     pGrayImage	- 输出	灰度图像数据
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public static void toGray(byte[] pRGBImage, int iImgWidth, int iImgHeight, byte[] pGrayImage) {
        mxImageTool.RGB2GRAY(pGrayImage, iImgWidth, iImgHeight, pGrayImage);
    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的Rect绘制矩形框
     参	数：	pRgbImgBuf  		- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iRect				- 输入	Rect[0]	=x;
     Rect[1]	=y;
     Rect[2]	=width;
     Rect[3]	=height;
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public static int drawRect(byte[] bytes, int width, int height, Rect rect) {
        int[] r = new int[]{rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top};
        return mxImageTool.DrawRect(bytes, width, height, r);
    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的点坐标绘制点
     参	数：	pRgbImgBuf  		- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iPointPos			- 输入	点坐标序列（x1,y1,x2,y2,...）
     iPointNum			- 输入  点个数
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public static int drawPoint(MxImage image, List<Point> points) {
        int[] iPointPos = new int[points.size() * 2];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            iPointPos[i * 2] = point.x;
            iPointPos[i * 2 + 1] = point.y;
        }
        return mxImageTool.DrawPoint(image.getData(), image.getWidth(), image.getHeight(), iPointPos, points.size());
    }

    /*******************************************************************************************
     功	能：	在输入的RGB图像上根据输入的点坐标绘制点序号
     参	数：	pRgbImgBuf  			- 输入	RGB图像缓冲区
     iImgWidth			- 输入	图像宽度
     iImgHeight			- 输入	图像高度
     iPointX				- 输入	指定位置的X坐标
     iPointY				- 输入  	指定位置的Y坐标
     szText				- 输入  	显示文字
     返	回：	1-成功，其他-失败
     *******************************************************************************************/
    public static int drawText(byte[] pRgbImgBuf, int iImgWidth, int iImgHeight,
                               int iPointX, int iPointY, String szText) {
        return mxImageTool.DrawText(pRgbImgBuf, iImgWidth, iImgHeight, iPointX, iPointY, szText);
    }


    public static Rect scaleRect(Rect rect, float scaleX, float scaleY) {
        return new Rect((int) (rect.left * scaleX), (int) (rect.top * scaleY), (int) (rect.right * scaleX), (int) (rect.bottom * scaleY));
    }

    public static RectF scaleRect(RectF rect, float scaleX, float scaleY) {
        return new RectF(rect.left * scaleX, rect.top * scaleY, rect.right * scaleX, rect.bottom * scaleY);
    }

}
