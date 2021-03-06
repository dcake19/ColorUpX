package com.dcake19.android.colorupx.saving;


import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveGame {
    // use
    // String[] files = context.getFilesDir().list();
    // for (String file:files)
    // to retrieve all names of files

    // delete files using
    // File dir = context.getFilesDir();
    // File file = new File(dir, "my_filename");
    // boolean deleted = file.delete();

    private final Integer VERSION = 1;
    private Context mContext;
    private String mFileName = "game_state.txt";

    public SaveGame(Context context,String gameType) {
        mContext = context;
        mFileName =  VERSION.toString() + "_" + gameType + "_" + mFileName;
    }

    public boolean saveGameToFile(SaveGameState saveGameState){
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(saveGameState);
            oos.close();

        } catch (FileNotFoundException e){
            return false;
        } catch (IOException e){
            return false;
        } finally {
            try {
                fos.close();
            } catch (Exception e ){
                return false;
            }
        }

        return true;
    }


    public SaveGameState loadGameState(){
        SaveGameState saveGameState = null;
        try {
            // read object from file
            FileInputStream fis = mContext.openFileInput(mFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            saveGameState = (SaveGameState) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }

        return saveGameState;
    }

}
