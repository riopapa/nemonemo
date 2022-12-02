package com.urrecliner.nemonemo;

import static com.urrecliner.nemonemo.MainActivity.mContext;

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

    final int OUTER_BOX = 4;    // or 5
    void create(Bitmap bitmap, File outFile, boolean drawBlack) {

        int xBit = bitmap.getWidth();   // may be 20
        int yBit = bitmap.getHeight();  // may be 20
        int outBit = OUTER_BOX;
        int pgWidth = 210*6, pgHeight = 297*6;  // A4 size canvas

        int xStart = 10, yStart = 30;
        int xHintArea = 220;
        int yHintArea = 280;
        int bSZ = 36;   // fixed because drawing box should not be so big
        PdfDocument.Page docPage = null;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pgWidth, pgHeight, 1).create();
        docPage = document.startPage(pageInfo);
        Canvas canvas = docPage.getCanvas();

        boolean [][] xyVal = new boolean[xBit][yBit];
        int [] counts;
        Paint pText = new Paint();  // text drawing
        pText.setColor(Color.DKGRAY);
        pText.setStyle(Paint.Style.STROKE);
        pText.setTextSize(20);
        pText.setStrokeWidth(0);
        pText.setStyle(Paint.Style.FILL_AND_STROKE);
        pText.setTypeface(mContext.getResources().getFont(R.font.bahnschrift)); // use condensed font
        Paint pDotted = new Paint();    // inner dotted lines
        pDotted.setColor(Color.DKGRAY);
        pDotted.setStyle(Paint.Style.STROKE);
        pDotted.setStrokeWidth(1);
        pDotted.setStyle(Paint.Style.FILL_AND_STROKE);
        pDotted.setPathEffect(new DashPathEffect(new float[] {1,2}, 0));
        pDotted.setStyle(Paint.Style.STROKE);
        Paint pOuter = new Paint(); // outer bold lines
        pOuter.setColor(Color.DKGRAY);
        pOuter.setStyle(Paint.Style.STROKE);
        pOuter.setStrokeWidth(3);
        pOuter.setStyle(Paint.Style.FILL_AND_STROKE);
        pOuter.setPathEffect(null);
        pOuter.setStyle(Paint.Style.STROKE);
        Paint pBlack = new Paint();   // draw black dot
        pBlack.setColor(Color.DKGRAY);
        pBlack.setStyle(Paint.Style.STROKE);
        pBlack.setStrokeWidth(3);
        pBlack.setStyle(Paint.Style.FILL_AND_STROKE);

        // create figure + lines
        for (int x = 0; x < xBit; x++) {
            for (int y = 0; y < yBit; y++) {
                xyVal[x][y] = (bitmap.getPixel(x, y) == Color.BLACK);
                if (drawBlack && xyVal[x][y])
                    canvas.drawRect(xStart+xHintArea + x*bSZ+8, yStart+yHintArea+y*bSZ+8,
                            xStart+xHintArea + x*bSZ + bSZ-8,
                            yStart+yHintArea+y*bSZ + bSZ-8, pBlack);

                canvas.drawRect(xStart+xHintArea + x*bSZ, yStart+yHintArea+y*bSZ,
                        xStart+xHintArea + x*bSZ + bSZ,
                        yStart+yHintArea+y*bSZ + bSZ, pDotted);
            }
        }

        // draw outer bold lines
        for (int x = 0; x < xBit; x += outBit) {
            for (int y = 0; y < yBit; y += outBit) {
                int xEnd = x + outBit;
                if (xEnd > xBit)
                    xEnd = xBit;
                int yEnd = y + outBit;
                if (yEnd > yBit)
                    yEnd = yBit;
                canvas.drawRect(xStart+xHintArea + x*bSZ, yStart+yHintArea+y*bSZ,
                        xStart+xHintArea + xEnd*bSZ,
                        yStart+yHintArea + yEnd*bSZ, pOuter);
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
            int xPos = xHintArea- 20;
            for (int i = idx-1; i >= 0; i--) {
                if (counts[i] > 9)
                    xPos -= 7;
                canvas.drawText(""+counts[i], xPos, yStart+yHintArea+y*bSZ+bSZ*2/3, pText);
                xPos -= 18;
            }
            canvas.drawLine( xStart, yStart+yHintArea+y*bSZ, xStart+xHintArea, yStart+yHintArea+y*bSZ, pDotted);
            canvas.drawLine( xStart, yStart+yHintArea+y*bSZ+bSZ, xStart+xHintArea, yStart+yHintArea+y*bSZ+bSZ, pDotted);
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
                canvas.drawText(""+counts[i], xStart+xHintArea+x*bSZ+bSZ/3-((counts[i]> 9) ? 6:0), yStart+yHintArea+(i-idx)*22, pText);
            }
            canvas.drawLine( xStart+xHintArea+x*bSZ, yStart, xStart+xHintArea+x*bSZ, yStart+yHintArea, pText);
            canvas.drawLine( xStart+xHintArea+x*bSZ+bSZ, yStart, xStart+xHintArea+x*bSZ+bSZ, yStart+yHintArea, pDotted);
        }
        // file name on right bottom
        String fName = outFile.getName();
        fName = fName.substring(0, fName.length()-4);
        canvas.drawText(fName, pgWidth - 10 - 20*fName.length(),pgHeight- 40, pText);
        document.finishPage(docPage);
        try {
            document.writeTo(new FileOutputStream(outFile));
        } catch (IOException e) {
            Log.e("PDF", "write error " + e);
        }
        // close the document
        document.close();
    }

}