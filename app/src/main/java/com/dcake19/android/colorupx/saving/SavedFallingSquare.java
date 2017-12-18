package com.dcake19.android.colorupx.saving;


import java.io.Serializable;

public class SavedFallingSquare implements Serializable{

    public float left,top;
    public int value,column;

    public SavedFallingSquare(float left, float top, int value, int column) {
        this.left = left;
        this.top = top;
        this.value = value;
        this.column = column;
    }
}
