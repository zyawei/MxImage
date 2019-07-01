package com.miaxis.image;


import android.os.Parcel;
import android.util.Log;

import java.util.*;

/**
 * 适用于Intent传参的MXImage,仅当数据长度大于1M时建议使用此类。
 *
 *
 * <p>
 * 使用{@link #recycle()}销毁数据,或者直到所有对象都销毁(通过{@link #MxIntentImage(MxImage)} 或 {@link #MxIntentImage(Parcel)} 两种方式创建)，才会销毁实际的数据。
 * <p>
 * 注意，本类不保证在没调用{@link #recycle()}前一定持有数据。
 *
 * @date: 2018/12/12 9:52
 * @author: zhang.yw
 */
@SuppressWarnings("all")
public class MxIntentImage extends MxImage {

    private static Map<String, byte[]> caches = new HashMap<>();
    private static Map<String, Set<Integer>> references = new HashMap<>();
    private static final String TAG = "MxIntentImage";

    public MxIntentImage(MxImage image) {
        super(null, image.getWidth(), image.getHeight(), image.getFormat(), image.getChannel());
        String tag = String.valueOf(System.currentTimeMillis());
        caches.put(tag, image.getData());
        super.setData(tag.getBytes());
        Log.v(TAG, "Create A MxIntentImage with tag  [" + tag + "],this[" + toString() + "]");
        addReference();
    }


    public MxIntentImage(Parcel in) {
        super(in);
        //Log.d(TAG, "Create by Parcel " + toString());
        addReference();
    }

    public static final Creator<MxIntentImage> CREATOR = new Creator<MxIntentImage>() {
        @Override
        public MxIntentImage createFromParcel(Parcel in) {
            return new MxIntentImage(in);
        }

        @Override
        public MxIntentImage[] newArray(int size) {
            return new MxIntentImage[size];
        }
    };

    @Override
    public void setData(byte[] bytes) {
        caches.put(geIntentTag(), bytes);
    }

    @Override
    public byte[] getData() {
        String tag = geIntentTag();
        return caches.get(tag);
    }

    @Override
    public void recycle() {
        super.recycle();
        caches.remove(geIntentTag());
    }

    @Override
    public boolean isRecycled() {
        return caches.containsKey(geIntentTag());
    }

    private String geIntentTag() {
        return new String(super.getData()).intern();
    }

    private boolean hasReference() {
        Set<Integer> address = references.get(geIntentTag());
        return address != null && address.size() != 0;
    }

    private void addReference() {
        String tag = geIntentTag();
        Set<Integer> address = references.get(tag);
        if (address == null) {
            address = new HashSet<>();
            references.put(tag, address);
        }
        address.add(System.identityHashCode(this));
    }

    private void removeReference() {
        String tag = geIntentTag();
        Set<Integer> address = references.get(tag);
        if (address != null) {
            address.remove(System.identityHashCode(this));
            if (address.size() == 0) {
                references.remove(tag);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        removeReference();
        if (!hasReference()) {
            recycle();
            Log.v(TAG, "Finalize A MxIntentImage which tag  [" + geIntentTag() + "],this[" + toString() + "]");
        }
    }
}
