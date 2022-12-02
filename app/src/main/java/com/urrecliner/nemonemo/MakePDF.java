package com.urrecliner.nemonemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class MakePDF {

    void create(Bitmap bitmap, File outFile, boolean drawBlack) {

        int xBit = bitmap.getWidth();   // 20
        int yBit = bitmap.getHeight();  // 20
        int pgWidth = 200*6, pgHeight = 290*6;

        int xStart = 10, yStart = 30;
        int xNumSize = 220;
        int yNumSize = 280;
        int bSZ = 36;
        PdfDocument.Page docPage = null;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pgWidth, pgHeight, 1).create();
        docPage = document.startPage(pageInfo);
        Canvas canvas = docPage.getCanvas();

        boolean [][] xyVal = new boolean[xBit][yBit];
        int count = 0;
        int [] counts;
        Paint pText = new Paint();
        pText.setColor(Color.DKGRAY);
        pText.setStyle(Paint.Style.STROKE);
        pText.setTextSize(20);
        pText.setStrokeWidth(0);
        pText.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint pDotted = new Paint();
        pDotted.setColor(Color.DKGRAY);
        pDotted.setStyle(Paint.Style.STROKE);
        pDotted.setStrokeWidth(1);
        pDotted.setStyle(Paint.Style.FILL_AND_STROKE);
        pDotted.setPathEffect(new DashPathEffect(new float[] {1,2}, 0));
        pDotted.setStyle(Paint.Style.STROKE);
        Paint pOuter = new Paint();
        pOuter.setColor(Color.DKGRAY);
        pOuter.setStyle(Paint.Style.STROKE);
        pOuter.setStrokeWidth(3);
        pOuter.setStyle(Paint.Style.FILL_AND_STROKE);
        pOuter.setPathEffect(null);
        pOuter.setStyle(Paint.Style.STROKE);
        Paint pBlack = new Paint();
        pBlack.setColor(Color.DKGRAY);
        pBlack.setStyle(Paint.Style.STROKE);
        pBlack.setStrokeWidth(3);
        pBlack.setStyle(Paint.Style.FILL_AND_STROKE);

        for (int x = 0; x < xBit; x++) {
            for (int y = 0; y < yBit; y++) {
                xyVal[x][y] = (bitmap.getPixel(x, y) == Color.BLACK);
                if (drawBlack && xyVal[x][y])
                    canvas.drawRect(xStart+xNumSize + x*bSZ+8, yStart+yNumSize+y*bSZ+8,
                            xStart+xNumSize + x*bSZ + bSZ-8,
                            yStart+yNumSize+y*bSZ + bSZ-8, pBlack);

                canvas.drawRect(xStart+xNumSize + x*bSZ, yStart+yNumSize+y*bSZ,
                        xStart+xNumSize + x*bSZ + bSZ,
                        yStart+yNumSize+y*bSZ + bSZ, pDotted);
            }
        }

        for (int x = 0; x < xBit; x += 5) {
            for (int y = 0; y < yBit; y += 5) {
                canvas.drawRect(xStart+xNumSize + x*bSZ, yStart+yNumSize+y*bSZ,
                        xStart+xNumSize + x*bSZ + bSZ*5,
                        yStart+yNumSize+y*bSZ + bSZ*5, pOuter);
            }
        }

        // X line hints
        for (int y = 0; y < yBit; y++) {
            int cnt = 0, idx = 0;
            boolean black = xyVal[0][y];
            counts = new int[xBit];
            for (int x = 0; x < xBit; x++) {
                if (black && xyVal[x][y]) {
                    cnt++;
                } else if (black && !xyVal[x][y]) {
                    counts[idx] = cnt;
                    black = false;
                    cnt = 0;
                    idx++;
                } else if (!black && !xyVal[x][y]) {

                } else if (!black && xyVal[x][y]) {
                    black = true;
                    cnt = 1;
                }
            }
            if (black) {
                counts[idx] = cnt;
                idx++;
            }
            int xPos = xNumSize- 20;
            for (int i = idx-1; i >= 0; i--) {
                if (counts[i] > 9)
                    xPos -= 7;
                canvas.drawText(""+counts[i], xPos, yStart+yNumSize+y*bSZ+bSZ*2/3, pText);
                xPos -= 16;
            }
            canvas.drawLine( xStart, yStart+yNumSize+y*bSZ, xStart+xNumSize, yStart+yNumSize+y*bSZ, pDotted);
            canvas.drawLine( xStart, yStart+yNumSize+y*bSZ+bSZ, xStart+xNumSize, yStart+yNumSize+y*bSZ+bSZ, pDotted);
        }

        // Y line hints
        pText.setColor(Color.DKGRAY);
        for (int x = 0; x < xBit; x++) {
            int cnt = 0, idx = 0;
            boolean black = xyVal[x][0];
            counts = new int[xBit];
            for (int y = 0; y < yBit; y++) {
                if (black && xyVal[x][y]) {
                    cnt++;
                } else if (black && !xyVal[x][y]) {
                    counts[idx] = cnt;
                    black = false;
                    cnt = 0;
                    idx++;
                } else if (!black && !xyVal[x][y]) {

                } else if (!black && xyVal[x][y]) {
                    black = true;
                    cnt = 1;
                }
            }
            if (black) {
                counts[idx] = cnt;
                idx++;
            }
            for (int i = 0; i < idx; i++) {
                canvas.drawText(""+counts[i], xStart+xNumSize+x*bSZ+bSZ/3-((counts[i]> 9) ? 6:0), yStart+yNumSize+(i-idx)*22, pText);
            }
            canvas.drawLine( xStart+xNumSize+x*bSZ, yStart, xStart+xNumSize+x*bSZ, yStart+yNumSize, pText);
            canvas.drawLine( xStart+xNumSize+x*bSZ+bSZ, yStart, xStart+xNumSize+x*bSZ+bSZ, yStart+yNumSize, pDotted);

        }
        String fName = outFile.getName();
        fName = fName.substring(0, fName.length()-4);
        canvas.drawText(fName, pgWidth - 20 - 24*fName.length(),pgHeight- 40, pText);
        document.finishPage(docPage);
        try {
            document.writeTo(new FileOutputStream(outFile));
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
        }
        // close the document
        document.close();
    }

}