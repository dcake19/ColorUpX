package com.dcake19.android.colorupx.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.dcake19.android.colorupx.R;

public class TextUtil {

    public static Spannable getMultiColorString(Context context,String string){
        return getMultiColorString(context, string,0);
    }

    public static Spannable getMultiColorString(Context context,String string,int startColor){
        Spannable wordtoSpan = new SpannableString(string);
        int colorCounter = startColor;
        for(int i=0;i<wordtoSpan.length();i++){
            colorCounter = colorCounter % 10;
            if(wordtoSpan.charAt(i)!=' ') colorCounter++;
            wordtoSpan.setSpan(new ForegroundColorSpan(getLetterColor(context,colorCounter)), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return wordtoSpan;
    }

    private static int getLetterColor(Context context,int i){
        int color;
        int resourceId;
        String value = String.valueOf(i);

        try {
            resourceId = R.color.class.getField("colorSquare"+value).getInt(null);
        } catch (IllegalAccessException e) {
            resourceId = R.color.colorBackground;
        } catch (NoSuchFieldException e) {
            resourceId = R.color.colorBackground;
        }

        if(Build.VERSION.SDK_INT <= 22){
            color = context.getResources().getColor(resourceId);
        }else {
            color = getApi23LetterColor(context,resourceId);
        }

        return color;
    }

    @TargetApi(23)
    private static int getApi23LetterColor(Context context,int colorId){
        return context.getColor(colorId);
    }


}
