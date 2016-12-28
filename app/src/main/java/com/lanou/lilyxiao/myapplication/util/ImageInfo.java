package com.lanou.lilyxiao.myapplication.util;


import java.io.File;
import java.net.URL;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ImageInfo implements Parcelable {

    private String mPath;
    private String mName;
    private int mId;
    private long mSize;
    public long mModifiedDate;
    private String mDescription;
    public int thumbWidth;
    public int thumbHeight;


    public ImageInfo() {
    }

    public ImageInfo(String path) throws InvalidImageException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new InvalidImageException("image file not exist!");
        }
        mPath = file.getAbsolutePath();
        mName = file.getName();
        mSize = file.length();
        mModifiedDate = file.lastModified();
    }

    private ImageInfo(Parcel parcel) {
        mPath = parcel.readString();
        mName = parcel.readString();
        mSize = parcel.readLong();
        mModifiedDate = parcel.readLong();
        mDescription = parcel.readString();
    }

    public static String getUrl(ImageInfo photoInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(photoInfo.mPath);
        sb.append("|");
        sb.append(photoInfo.thumbWidth);
        sb.append("|");
        sb.append(photoInfo.thumbHeight);
        sb.append("|");
        sb.append(photoInfo.mModifiedDate);
        sb.append("|");
        sb.append(0);
        // version
        sb.append("|");
        sb.append("201403010");
//      sb.append("|");
//      sb.append(photoInfo.index);
        return sb.toString();
    }

    // 例如:path|width|height|lastmodify|index
    public static ImageInfo parseUrl(URL url) {
        try {
            String path = url.getFile();
            String[] args = path.split("\\|");
            ImageInfo photoInfo = new ImageInfo();
            photoInfo.mPath = args[0];
            photoInfo.thumbWidth = Integer.parseInt(args[1]);
            photoInfo.thumbHeight = Integer.parseInt(args[2]);
            photoInfo.mModifiedDate = Long.parseLong(args[3]);
            if (args.length > 6)
                photoInfo.mId = Integer.parseInt(args[6]);
            return photoInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public String getPath() {
        return mPath;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getDate() {
        return mModifiedDate;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }


    @Override
    public int hashCode() {
        return (mName + mSize + mModifiedDate).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof ImageInfo))
            return false;

        return compare((ImageInfo) object);
    }

    private boolean compare(ImageInfo cmp) {
        return mName.equals(cmp.mName)
                && mSize == cmp.mSize
                && mModifiedDate == cmp.mModifiedDate
                && mPath.equals(cmp.mPath);
    }

    public static ImageInfo create(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        ImageInfo imageInfo = null;
        try {
            imageInfo = new ImageInfo(path);
        } catch (InvalidImageException e) {
            imageInfo = null;
        }
        return imageInfo;
    }

    /**
     * 判断添加的照片文件是否存在
     */
    public static boolean checkImageExists(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return false;
        }

        File file = new File(imagePath);
        if (file.exists() && file.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    public class InvalidImageException extends Exception {

        public InvalidImageException(String detailMessage) {
            super(detailMessage);
        }
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(mPath);
        dest.writeString(mName);
        dest.writeLong(mSize);
        dest.writeLong(mModifiedDate);
        dest.writeString(mDescription);
    }

    public final static Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {

        @Override
        public ImageInfo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ImageInfo[size];
        }
    };
}
