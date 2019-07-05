package com.miaxis.image;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * 图片数据
 * 解析文件中的数据建议使用此类，数据被弱引用
 */
public class MxFileImage extends MxImage {
    private String path;
    private WeakReference<byte[]> weekData;

    public MxFileImage(String path, MxImage source) {
        setChannel(source.getChannel());
        setFormat(source.getFormat());
        setWidth(source.getWidth());
        setHeight(source.getHeight());
        this.weekData = new WeakReference<>(source.getData());
        this.path = path;
    }

    protected MxFileImage(Parcel in) {
        super(in);
        path = in.readString();
        weekData = new WeakReference<>(in.createByteArray());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(path);
        dest.writeByteArray(weekData.get());
    }

    public static final Parcelable.Creator<MxFileImage> CREATOR = new Parcelable.Creator<MxFileImage>() {
        @Override
        public MxFileImage createFromParcel(Parcel in) {
            return new MxFileImage(in);
        }

        @Override
        public MxFileImage[] newArray(int size) {
            return new MxFileImage[size];
        }
    };

    public String getImagePath() {
        return path;
    }

    @Override
    public byte[] getData() {
        byte[] data = weekData.get();
        if (data == null) {
            data = getBytesFromFile();
            weekData = new WeakReference<>(data);
        }
        return data;
    }

    @Override
    public void setData(byte[] bytes) {
        if (null != weekData) {
            weekData.clear();
        }
        weekData = new WeakReference<>(bytes);
    }

    private byte[] getBytesFromFile() {
        MxImage mxImage = MxImageFactory.decodeFile(getImagePath(), getChannel());
        if (mxImage != null) {
            return mxImage.getData();
        } else {
            return new byte[0];
        }
    }
}
