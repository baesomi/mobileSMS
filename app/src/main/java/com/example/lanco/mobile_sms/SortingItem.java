package com.example.lanco.mobile_sms;

import android.graphics.drawable.Drawable;

/**
 * Created by Lanco on 2016-05-29.
 */
public class SortingItem {
    private Drawable dIcon;
    private String text1;
    private String text2;
    private String text3;
//seticon

    public void setIcon(Drawable icon) {
        dIcon = icon ;
    }

    //set contents
    public void setName(String name) {text1 = name ;}
    public void setDate(String date) {text2 = date ;}
    public void setContent(String content){text3 = content;}

    //get icon
    public Drawable getIcon() {
        return this.dIcon ;
    }
    //get appname
    public String getName() {return this.text1;}
    public String getDate() {return this.text2;}
    public String getContent(){ return  this.text3;}
}
