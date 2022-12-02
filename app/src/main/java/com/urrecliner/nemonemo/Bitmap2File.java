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
            // 파일 초기화
            fullFile.createNewFile();

            // OutputStream에 출력될 Stream에 파일을 넣어준다
            out = new FileOutputStream(fullFile);

            // bitmap 압축
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