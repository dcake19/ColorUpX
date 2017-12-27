package com.dcake19.android.colorupx.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class AnimatableRectF extends RectF {

    private Integer mValue = 1;
    Integer mMaxValue = 11;
    private final int mMaxValueColor = 11;
    private int mSquareCornerRadius;
    private Paint mShapeForegroundPaint;
    private Paint mTextPaint;
    private Context mContext;
    private boolean mNewTextSizeSet = false;

    public AnimatableRectF(Context context,float left, float top, float right, float bottom,int squareCornerRadius,int value) {
        super(left, top, right, bottom);
        mContext = context;
        mSquareCornerRadius = squareCornerRadius;
        mValue = value;
        setPaints();
    }

    private void setPaints(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShapeForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int textSize = (int) (0.7*(right - left));
        mTextPaint.setTextSize(textSize);
        setTextSize();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        setColor();
    }

    private void setColor(){
        int color;

        if(mValue<=mMaxValueColor) {
            color = mContext.getResources().getColor(
                    mContext.getResources().getIdentifier(
                            "colorSquare" + mValue, "color", mContext.getPackageName()));
        }
        else{
            color = mContext.getResources().getColor(
                    mContext.getResources().getIdentifier(
                            "colorSquare11", "color", mContext.getPackageName()));
        }

        mShapeForegroundPaint.setColor(color);

        int textColor  = 0xffffffff;
        mTextPaint.setColor(textColor);
        mTextPaint.setFakeBoldText(true);
    }

    public int getColor(){
        return mShapeForegroundPaint.getColor();
    }

    public int getNextColor(){
        int nextValue = mValue<mMaxValueColor ? mValue+1:mMaxValueColor;
        int color = mContext.getResources().getColor(
                mContext.getResources().getIdentifier(
                        "colorSquare"+ nextValue, "color", mContext.getPackageName()));
        return color;
    }

    public void setTop(float top){
        this.top = top;
    }
    public void setBottom(float bottom){
        this.bottom = bottom;
    }
    public void setRight(float right){
        this.right = right;
    }
    public void setLeft(float left){
        this.left = left;
    }

    public void setTranslationX(float left) {
        float width = width();
        this.right = left + width;
        this.left = left;
    }

    public void setTranslationY(float top) {
        float height = height();
        this.top = top;
        this.bottom = top + height;
    }

    public void setScale(float bottom){
        float width = width();
        float change = bottom - this.bottom;
        this.top -= change;
        this.bottom += change;
        this.left -= change;
        this.right += change;
    }

    public int getValue(){
        return mValue;
    }

    public void incrementValue(){
        if(mValue<mMaxValue) {
            mNewTextSizeSet = false;
            mValue++;
        }
        setColor();
    }

    public void setMaxValue(int maxValue){
        mMaxValue = maxValue;
    }

    public Paint getShapeForegroundPaint() {
        return mShapeForegroundPaint;
    }

    public void drawToCanvas(Canvas canvas){
        canvas.drawRoundRect(this,mSquareCornerRadius,mSquareCornerRadius,mShapeForegroundPaint);
        if(mValue > mMaxValueColor && !mNewTextSizeSet) setNewTextSize();
        canvas.drawText(getText(),(right+left)/2,(bottom+top)/2 + mTextPaint.getTextSize()*3/8,mTextPaint);
    }

    private String getText(){
        if(mValue<20) {
            if(mTextPaint.getTextSize()<7.0 && mValue>13){
                Integer i = (int) Math.pow(2, mValue);
                String text = i.toString();
                text = text.substring(0,text.length()-3) + "K";
                setTextSize();
                return text;
            }else {
                Integer text = (int) Math.pow(2, mValue);
                return text.toString();
            }
        }else if(mValue<30){
            Integer text = (int) Math.pow(2, mValue-20);
            return text.toString() + "M";
        }else if(mValue<40){
            Integer text = (int) Math.pow(2, mValue-30);
            return text.toString() + "B";
        }else if(mValue<50){
            Integer text = (int) Math.pow(2, mValue-40);
            return text.toString() + "T";
        }else if(mValue<60){
            Integer text = (int) Math.pow(2, mValue-50);
            return text.toString() + "Q";
        }else {
            return "E"+mValue;
        }
    }

    private void setTextSize(){
        float textWidth = Math.max(mTextPaint.measureText("1024"), mTextPaint.measureText("2048"));
        float squareWidth = right - left;
        while (textWidth > 0.9 * squareWidth) {mTextPaint.setTextSize(mTextPaint.getTextSize() - 1);
            textWidth = Math.max(mTextPaint.measureText("1024"), mTextPaint.measureText("2048"));
        }
    }

    private void setNewTextSize(){
        float textWidth = mTextPaint.measureText(getText());
        float squareWidth = right-left;
        while(textWidth>0.9*squareWidth){
            mTextPaint.setTextSize(mTextPaint.getTextSize()-1);
            textWidth = mTextPaint.measureText(getText());
        }
        mNewTextSizeSet = true;
    }
}
