package com.sohot.hot.model;

import java.io.Serializable;

/**
 * Created by jsion on 15/11/27.
 */
public class ChangeSkinModel implements Serializable {
    /**
     * 皮肤标题
     */
    private String skinTitle;
    /**
     * 皮肤icon(暂时没用)
     */
    private int skinIcon;
    /**
     * 皮肤选中图标
     */
    private int skinCheckedIcon;
    /**
     * 是否显示选中icon
     */
    private boolean isShowCheckIcon;

    private int cardColor;

    /**
     * 用来标示那种颜色用于换肤
     */
    private String skinFlag;


    public ChangeSkinModel(String skinTitle, int skinIcon, int skinCheckedIcon, boolean isShowCheckIcon, int cardColor, String skinFlag) {
        this.skinTitle = skinTitle;
        this.skinIcon = skinIcon;
        this.skinCheckedIcon = skinCheckedIcon;
        this.isShowCheckIcon = isShowCheckIcon;
        this.cardColor = cardColor;
        this.skinFlag = skinFlag;
    }

    public String getSkinFlag() {
        return skinFlag;
    }

    public void setSkinFlag(String skinFlag) {
        this.skinFlag = skinFlag;
    }

    public String getSkinTitle() {
        return skinTitle;
    }

    public void setSkinTitle(String skinTitle) {
        this.skinTitle = skinTitle;
    }

    public int getSkinIcon() {
        return skinIcon;
    }

    public void setSkinIcon(int skinIcon) {
        this.skinIcon = skinIcon;
    }

    public int getSkinCheckedIcon() {
        return skinCheckedIcon;
    }

    public void setSkinCheckedIcon(int skinCheckedIcon) {
        this.skinCheckedIcon = skinCheckedIcon;
    }

    public boolean isShowCheckIcon() {
        return isShowCheckIcon;
    }

    public void setIsShowCheckIcon(boolean isShowCheckIcon) {
        this.isShowCheckIcon = isShowCheckIcon;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
    }
}
