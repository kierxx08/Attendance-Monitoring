package com.kierasis.attendancemonitoring;

public class ext_00 {
    private String id;
    private String title;
    private String subtitle;
    private String subtitle2;
    private String imgURL;

    public ext_00(){}
    public ext_00(String id,String title, String subtitle, String subtitle2, String imgURL){
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.subtitle2 = subtitle2;
        this.imgURL = imgURL;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitle2() {
        return subtitle2;
    }

    public void setSubtitle2(String subtitle2) {
        this.subtitle2 = subtitle2;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
