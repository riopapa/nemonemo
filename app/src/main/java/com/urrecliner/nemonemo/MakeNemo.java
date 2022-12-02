package com.urrecliner.nemonemo;

import static com.urrecliner.nemonemo.MainActivity.mPackagePath;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class MakeNemo {

    Bitmap build(String fName) {
        Bitmap bitmapOrg = BitmapFactory.decodeFile(mPackagePath+"/"+fName+".jpg");
        int xSize = bitmapOrg.getWidth() / 4; int ySize = bitmapOrg.getHeight() / 4;
        Bitmap bitmapSmall = Bitmap.createScaledBitmap(bitmapOrg, xSize, ySize, false);
        Bitmap bitmapGray = toGrayscale(bitmapSmall);

        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                int rgb = bitmapGray.getPixel(x, y);

                int val =  Color.red(rgb)+Color.green(rgb)+Color.blue(rgb);
                if (val > 125*3)
                    bitmapGray.setPixel(x,y, Color.WHITE);
                else
                    bitmapGray.setPixel(x,y, Color.BLACK);
            }
//            Log.w("line "+y, str);
        }
        Bitmap bitmapCrop = cropBitmap(bitmapGray);
        bitmapGray = Bitmap.createScaledBitmap(bitmapCrop, 160, 160, false);
//        bitmapGray = Bitmap.createScaledBitmap(bitmapGray, 80, 80, false);
//        bitmapGray = Bitmap.createScaledBitmap(bitmapGray, 40, 40, false);
        return Bitmap.createScaledBitmap(bitmapGray, 20, 20, false);
    }

    Bitmap cropBitmap (Bitmap iMap) {
        int xSize = iMap.getWidth(); int ySize = iMap.getHeight();
        int xStart = -1, yStart = -1, xFinish = -1, yFinish = -1;
        // check topline
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                int color = iMap.getPixel(x, y);
                if (color != Color.WHITE) {
                    yStart = y;
                }
            }
            if (yStart != -1)
                break;
        }
        // check bottom line
        for (int y = ySize-1; y > 0; y--) {
            for (int x = 0; x < xSize; x++) {
                int color = iMap.getPixel(x, y);
                if (color != Color.WHITE) {
                    yFinish = y;
                }
            }
            if (yFinish != -1)
                break;
        }
        // check left
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                int color = iMap.getPixel(x, y);
                if (color != Color.WHITE) {
                    xStart = x;
                }
            }
            if (xStart != -1)
                break;
        }
        // check Right
        for (int x = xSize-1; x > 0; x--) {
            for (int y = 0; y < ySize; y++) {
                int color = iMap.getPixel(x, y);
                if (color != Color.WHITE) {
                    xFinish = x;
                }
            }
            if (xFinish != -1)
                break;
        }
        return Bitmap.createBitmap(iMap, xStart, yStart, xFinish-xStart, yFinish-yStart);
    }

    Bitmap toGrayscale(Bitmap srcImage) {

        Bitmap bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);

        return bmpGrayscale;
    }

}