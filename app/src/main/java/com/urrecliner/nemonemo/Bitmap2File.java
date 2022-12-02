package com.urrecliner.nemonemo;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Bitmap2File {
    void save(Bitmap bitmap, File fullFile) {
        OutputStream out = null;
        try {
            fullFile.createNewFile();
            out = new FileOutputStream(fullFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}