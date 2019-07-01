#include <jni.h>
#include <string>
#include <malloc.h>
#include <stdio.h>
#include <opencv2/core/hal/interface.h>
#include <opencv2/imgproc/types_c.h>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

void mirrorBgr(unsigned char *string, jint width, jint height);

void cutYuv(unsigned char *tarYuv, unsigned char *srcYuv, int startW,
            int startH, int cutW, int cutH, int srcW, int srcH) {
    int i;
    int j = 0;
    int k = 0;
    //分配一段内存，用于存储裁剪后的Y分量
    unsigned char *tmpY = (unsigned char *) malloc(cutW * cutH);
    //分配一段内存，用于存储裁剪后的UV分量
    unsigned char *tmpUV = (unsigned char *) malloc(cutW * cutH / 2);
    for (i = startH; i < cutH + startH; i++) {
        // 逐行拷贝Y分量，共拷贝cutW*cutH
        memcpy(tmpY + j * cutW, srcYuv + startW + i * srcW, cutW);
        j++;
    }
    for (i = startH / 2; i < (cutH + startH) / 2; i++) {
        //逐行拷贝UV分量，共拷贝cutW*cutH/2
        memcpy(tmpUV + k * cutW, srcYuv + startW + srcW * srcH + i * srcW, cutW);
        k++;
    }
    //将拷贝好的Y，UV分量拷贝到目标内存中
    memcpy(tarYuv, tmpY, cutW * cutH);
    memcpy(tarYuv + cutW * cutH, tmpUV, cutW * cutH / 2);
    free(tmpY);
    free(tmpUV);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_cropYUV(JNIEnv *env, jclass type, jbyteArray bgrImage_, jint width,
                                       jint height, jint x, jint y, jint dw, jint dh) {
    jbyte *bgrImage = env->GetByteArrayElements(bgrImage_, NULL);

    jbyteArray yue = env->NewByteArray(dw * dh / 2 * 3);
    jbyte *yueBytes = env->GetByteArrayElements(yue, NULL);
    cutYuv((unsigned char *) (yueBytes), (unsigned char *) bgrImage, x, y, dw, dh, width, height);
    env->ReleaseByteArrayElements(bgrImage_, bgrImage, 0);
    return yue;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_rotateYuv90(JNIEnv *env, jclass type, jbyteArray data_, jint width,
                                           jint height) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    int dataLength = env->GetArrayLength(data_);

    jbyteArray yue = env->NewByteArray(dataLength);
    jbyte *yueBytes = env->GetByteArrayElements(yue, NULL);
    int wh = width * height;
    //旋转Y
    int k = 0;
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            yueBytes[k] = data[width * j + i];
            k++;
        }
    }

    for (int i = 0; i < width; i += 2) {
        for (int j = 0; j < height / 2; j++) {
            yueBytes[k] = data[wh + width * j + i];
            yueBytes[k + 1] = data[wh + width * j + i + 1];
            k += 2;
        }
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    return yue;
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_rotateYuv180(JNIEnv *env, jclass type, jbyteArray data_,
                                            jint width, jint height) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    int dataLength = env->GetArrayLength(data_);

    jbyteArray yue = env->NewByteArray(dataLength);
    jbyte *yueBytes = env->GetByteArrayElements(yue, NULL);

    int count = 0;
    for (int i = width * height - 1; i >= 0; i--) {
        yueBytes[count] = data[i];
        count++;
    }
    for (int i = width * height * 3 / 2 - 1; i >= width * height; i -= 2) {
        yueBytes[count++] = data[i - 1];
        yueBytes[count++] = data[i];
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    return yue;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_rotateYuv270(JNIEnv *env, jclass type, jbyteArray data_, jint width,
                                            jint height) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    int dataLength = env->GetArrayLength(data_);
    jbyteArray yue = env->NewByteArray(dataLength);
    jbyte *yueBytes = env->GetByteArrayElements(yue, NULL);

    int srcImageWidth = width;
    int srcImageHeight = height;
    int imageSize = width * height;
    // Rotate the Y luma
    int i = 0;
    for (int x = srcImageWidth - 1; x >= 0; x--) {
        for (int y = 0; y < srcImageHeight; y++) {
            yueBytes[i] = data[y * srcImageWidth + x];
            i++;
        }
    }
    i = imageSize;
    for (int x = srcImageWidth - 1; x > 0; x = x - 2) {
        for (int y = 0; y < srcImageHeight / 2; y++) {
            yueBytes[i] = data[imageSize + (y * srcImageWidth) + (x - 1)];
            i++;
            yueBytes[i] = data[imageSize + (y * srcImageWidth) + x];
            i++;
        }
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    return yue;
}


int convertByteToInt(jbyte data) {
    int heightBit = (data >> 4) & 0x0F;
    int lowBit = 0x0F & data;
    return heightBit * 16 + lowBit;
}


extern "C"
JNIEXPORT jintArray JNICALL
Java_com_miaxis_image_MxImages_BGR2Pixel(JNIEnv *env, jclass type, jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    int size = env->GetArrayLength(data_);

    int arg = (size % 3 == 0) ? 0 : 1;

    jintArray color = env->NewIntArray(size / 3 + arg);
    jint *colorBytes = env->GetIntArrayElements(color, NULL);
    int colorLen = env->GetArrayLength(color);

    int red, green, blue;
    if (arg == 0) {
        for (int i = 0; i < colorLen; ++i) {
            blue = convertByteToInt(data[i * 3]);
            green = convertByteToInt(data[i * 3 + 1]);
            red = convertByteToInt(data[i * 3 + 2]);
            // 获取RGB分量值通过按位或生成int的像素值
            colorBytes[i] = (red << 16) | (green << 8) | blue;
        }
    } else {
        for (int i = 0; i < colorLen - 1; ++i) {
            blue = convertByteToInt(data[i * 3]);
            green = convertByteToInt(data[i * 3 + 1]);
            red = convertByteToInt(data[i * 3 + 2]);
            colorBytes[i] = (red << 16) | (green << 8) | blue;
        }
        colorBytes[colorLen - 1] = 0xFF000000;
    }

    env->ReleaseByteArrayElements(data_, data, 0);
    return color;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_scaleBGR(JNIEnv *env, jclass type, jbyteArray data_,
                                        jint width, jint height,
                                        jint targetWidth, jint targetHeight,
                                        jint colorBits) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    int dataLen = env->GetArrayLength(data_);

    jbyteArray color = env->NewByteArray(dataLen / 4);
    jbyte *colorBytes = env->GetByteArrayElements(color, NULL);

    //参数有效性检查
    //ASSERT_EXP(pDest != NULL);
    //ASSERT_EXP((nDestBits == 32) || (nDestBits == 24));
    //ASSERT_EXP((targetWidth > 0) && (targetHeight > 0));

    //ASSERT_EXP(pSrc != NULL);
    //ASSERT_EXP((nSrcBits == 32) || (nSrcBits == 24));
    //ASSERT_EXP((width > 0) && (height > 0));

    //令dfAmplificationX和dfAmplificationY分别存储水平和垂直方向的放大率
    double dfAmplificationX = ((double) targetWidth) / width;
    double dfAmplificationY = ((double) targetHeight) / height;

    //计算单个源位图颜色和目的位图颜色所占字节数
    const int nSrcColorLen = colorBits / 8;
    const int nDestColorLen = colorBits / 8;

    //进行图片缩放计算
    for (int i = 0; i < targetHeight; i++) {
        //处理第i行
        for (int j = 0; j < targetWidth; j++) {
            //处理第i行中的j列
            //------------------------------------------------------
            //以下代码将计算nLine和nRow的值,并把目的矩阵中的(i, j)点
            //映射为源矩阵中的(nLine, nRow)点,其中,nLine的取值范围为
            //[0, height-1],nRow的取值范围为[0, width-1],
            double tmp = i / dfAmplificationY;
            int nLine = (int) tmp;

            if (tmp - nLine > 0.5)
                ++nLine;

            if (nLine >= height)
                --nLine;

            tmp = j / dfAmplificationX;
            int nRow = (int) tmp;

            if (tmp - nRow > 0.5)
                ++nRow;

            if (nRow >= width)
                --nRow;

            unsigned char *pSrcPos = (unsigned char *) data + (nLine * width + nRow) * nSrcColorLen;
            unsigned char *pDestPos =
                    (unsigned char *) colorBytes + (i * targetWidth + j) * nDestColorLen;

            //把pSrcPos位置的前三字节拷贝到pDestPos区域
            *pDestPos++ = *pSrcPos++;
            *pDestPos++ = *pSrcPos++;
            *pDestPos++ = *pSrcPos++;

            if (nDestColorLen == 4)
                *pDestPos = 0;
        }
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    return color;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_mirrorBGR(JNIEnv *env, jclass type, jbyteArray data_, jint width, jint height) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    mirrorBgr(reinterpret_cast<unsigned char *>(data), width, height);
    return data_;
}

void mirrorBgr(unsigned char *prgb, jint iWidth, jint iHeight) {
    if (prgb == NULL || iWidth <= 0 || iHeight <= 0)
        return;
    // 每行图像数据的字节数
    int iLBytes = (iWidth * 24 + 31) / 32 * 4;
    int itmp = iLBytes * iHeight;
    // 临时RGB图像指针
    unsigned char *prgbtmp = (unsigned char *) malloc(itmp);
    if (prgbtmp == NULL) {
        return;
    } else {
        memset(prgbtmp, 0, itmp); // itmp = iLBytes*iHeight;
    }
    int temp_start_row = 0;
    int iWidth_1 = iWidth - 1;
    unsigned char *pdst = NULL;
    unsigned char *psrc = NULL;
    // 每行
    for (int i = 0; i < iHeight; i++) {
        pdst = prgbtmp + temp_start_row;
        psrc = prgb + temp_start_row;
        // 每列
        for (int j = 0; j < iWidth; j++) {
            memcpy((pdst + 3 * (iWidth_1 - j)), (psrc + 3 * j), 3);
        }
        temp_start_row += iLBytes;
    }
    memcpy(prgb, prgbtmp, itmp);
    free(prgbtmp);
}

bool YV12ToBGR24_OpenCV(unsigned char *pYUV, unsigned char *pBGR24, int width, int height) {
    if (width < 1 || height < 1 || pYUV == NULL || pBGR24 == NULL)
        return false;
    Mat dst(height, width, CV_8UC3, pBGR24);
    Mat src(height + height / 2, width, CV_8UC1, pYUV);
    cvtColor(src, dst, CV_YUV2BGR_NV21);
    return true;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_miaxis_image_MxImages_convertYUV2BGR(JNIEnv *env, jclass type, jbyteArray bgrImage_, jint width, jint height) {
    jbyte *bgrImage = env->GetByteArrayElements(bgrImage_, NULL);
    jbyteArray bgr = env->NewByteArray(width * height * 3);
    jbyte *bgrBytes = env->GetByteArrayElements(bgr, NULL);
    YV12ToBGR24_OpenCV((unsigned char *) bgrImage, (unsigned char *) bgrBytes, width, height);
    env->ReleaseByteArrayElements(bgrImage_, bgrImage, 0);
    return bgr;
}

