package com.dcake19.android.colorupx.saving;


import java.io.Serializable;

public class SavedAnimatableRectF implements Serializable{
    public int iCoord,jCoord;
    public float left,top;
    public int value;

    public SavedAnimatableRectF(int iCoord,int jCoord,float left, float top, int value) {
        this.iCoord = iCoord;
        this.jCoord = jCoord;
        this.left = left;
        this.top = top;
        this.value = value;
    }
}
