package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.JumpJson;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class AdJson implements Parcelable {
    private AdItemJson pic_ad;
    private List<AdItemJson> text_ad;

    public AdItemJson getPic_ad() {
        return pic_ad;
    }

    public void setPic_ad(AdItemJson pic_ad) {
        this.pic_ad = pic_ad;
    }

    public List<AdItemJson> getText_ad() {
        return text_ad;
    }

    public void setText_ad(List<AdItemJson> text_ad) {
        this.text_ad = text_ad;
    }

   public static class AdItemJson extends JumpJson implements Parcelable {
        private String author;
        private String datetime;
        private String href;
        private String picture;
        private int position;
        private String title;
        private String type;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.author);
            dest.writeString(this.datetime);
            dest.writeString(this.href);
            dest.writeString(this.picture);
            dest.writeInt(this.position);
            dest.writeString(this.title);
            dest.writeString(this.type);
        }

        public AdItemJson() {
        }

        protected AdItemJson(Parcel in) {
            this.author = in.readString();
            this.datetime = in.readString();
            this.href = in.readString();
            this.picture = in.readString();
            this.position = in.readInt();
            this.title = in.readString();
            this.type = in.readString();
        }

        public static final Creator<AdItemJson> CREATOR = new Creator<AdItemJson>() {
            @Override
            public AdItemJson createFromParcel(Parcel source) {
                return new AdItemJson(source);
            }

            @Override
            public AdItemJson[] newArray(int size) {
                return new AdItemJson[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pic_ad, flags);
        dest.writeTypedList(this.text_ad);
    }

    public AdJson() {
    }

    protected AdJson(Parcel in) {
        this.pic_ad = in.readParcelable(AdItemJson.class.getClassLoader());
        this.text_ad = in.createTypedArrayList(AdItemJson.CREATOR);
    }

    public static final Creator<AdJson> CREATOR = new Creator<AdJson>() {
        @Override
        public AdJson createFromParcel(Parcel source) {
            return new AdJson(source);
        }

        @Override
        public AdJson[] newArray(int size) {
            return new AdJson[size];
        }
    };
}
