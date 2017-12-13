package com.example.android.colorupx.game.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class AnimatableRectF extends RectF {

    private Integer mValue = 1;
    private Integer mMaxValue = 10;
    private int mSquareCornerRadius;
    private Paint mShapeForegroundPaint;
    private Paint mTextPaint;
    private Context mContext;
    private float mTextSize;

    public AnimatableRectF(Context context,float left, float top, float right, float bottom,int squareCornerRadius,int value) {
        super(left, top, right, bottom);
        mContext = context;
        mSquareCornerRadius = squareCornerRadius;
        mValue = value;
        setPaints();
        //setTextSize();
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

        if(mValue<=mMaxValue) {
            color = mContext.getResources().getColor(
                    mContext.getResources().getIdentifier(
                            "colorSquare" + mValue, "color", mContext.getPackageName()));
        }
        else{
            color = mContext.getResources().getColor(
                    mContext.getResources().getIdentifier(
                            "colorSquare10", "color", mContext.getPackageName()));
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
        int nextValue = mValue<mMaxValue ? mValue+1:mMaxValue;
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
        mValue++;
        setColor();
    }

    public Paint getShapeForegroundPaint() {
        return mShapeForegroundPaint;
    }

    public void drawToCanvas(Canvas canvas){
        canvas.drawRoundRect(this,mSquareCornerRadius,mSquareCornerRadius,mShapeForegroundPaint);
        //changeTextSize();
        //setTextSize();
        //mTextPaint.setTextSize(mTextSize);
        canvas.drawText(getText(),(right+left)/2,(bottom+top)/2 + mTextPaint.getTextSize()*3/8,mTextPaint);
    }

    private String getText(){
        Integer text = (int) Math.pow(2,mValue);
        return text.toString();
    }

//    private void changeTextSize(){
//        float textWidth = mTextPaint.measureText(getText());
//        float squareWidth = right-left;
//        while(textWidth>0.9*squareWidth){
//            mTextPaint.setTextSize(mTextPaint.getTextSize()-4);
//            textWidth = mTextPaint.measureText(Integer.toString(mValue));
//        }
//    }

    private void setTextSize(){
        float textWidth = mTextPaint.measureText("1024");
        float squareWidth = right-left;
        while(textWidth>0.9*squareWidth){
            mTextPaint.setTextSize(mTextPaint.getTextSize()-1);
            textWidth = mTextPaint.measureText("1024");
        }
        mTextSize = mTextPaint.getTextSize();
    }
}
