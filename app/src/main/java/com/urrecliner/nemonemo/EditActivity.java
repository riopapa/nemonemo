package com.urrecliner.nemonemo;

import static com.urrecliner.nemonemo.MainActivity.mContext;
import static com.urrecliner.nemonemo.MainActivity.mOutPath;
import static com.urrecliner.nemonemo.MainActivity.mPackagePath;
import static com.urrecliner.nemonemo.MainActivity.nemos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class EditActivity extends AppCompatActivity {

    int boxCnt, boxSz, pos, xSz, ySz, x0, y0;
    String fName;
    Bitmap bitmap;
    boolean [][] xyMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        boxCnt = intent.getExtras().getInt("boxCnt", 20);
        pos = intent.getExtras().getInt("pos", 0);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        boxSz = size.x / (boxCnt+4);

        MainActivity.Nemo nemo = nemos.get(pos);
        fName = nemo.fName;
        bitmap = nemo.bitmap;
        ImageView imageView = findViewById(R.id.edImage);
        imageView.setImageBitmap(BitmapFactory.decodeFile(new File(mPackagePath, nemo.fName+".jpg").toString()));
        xSz = bitmap.getWidth(); ySz = bitmap.getHeight();
        xyMap = new boolean[xSz][ySz];
        for (int x = 0; x < xSz; x++)
            for (int y = 0; y < ySz; y++) {
                int pixcel = bitmap.getPixel(x,y);
                xyMap[x][y] = pixcel == Color.BLACK;
            }
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.setMargins(20, 0, 30, 0);

        for(int y = 0; y < ySz; y++) {
            ImageView b;
            LinearLayout rowLayout = new LinearLayout(mContext);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams dotParms = new LinearLayout.LayoutParams(boxSz, boxSz);
            linearLayout.addView(rowLayout);
            for(int x = 0; x < xSz; x++) {
                LinearLayout columnLayout = new LinearLayout(mContext);
                columnLayout.setOrientation(LinearLayout.VERTICAL);
                b = new ImageView(mContext);
                b.setLayoutParams(dotParms);
                b.setImageResource(xyMap[x][y] ? R.drawable.box_blank : R.drawable.box_filled);
                b.setId(x + 100*y);
                columnLayout.addView(b);
                ImageView finalB = b;
                b.setOnClickListener(v -> {
                    int id = v.getId();
                    y0 = id / 100;
                    x0 = id - y0 *100;
                    xyMap[x0][y0] = !xyMap[x0][y0];
                    finalB.setImageResource(xyMap[x0][y0] ? R.drawable.box_blank : R.drawable.box_filled);
                    bitmap.setPixel(x0,y0, xyMap[x0][y0] ? Color.BLACK : Color.WHITE);
                });
                rowLayout.addView(columnLayout);
            }
        }
        LinearLayout boxLayout = findViewById(R.id.edNemo);
        boxLayout.addView(linearLayout);
        TextView tvName = findViewById(R.id.edFName);
        tvName.setText(fName);
        TextView tvMake = findViewById(R.id.edGo);
        tvMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MakePDF().create(bitmap, new File(mOutPath,"nemo_"+fName+"Qz.PDF"), false);
                new MakePDF().create(bitmap, new File(mOutPath,"nemo_"+fName+"Ans.PDF"), true);
                finish();
                Toast.makeText(mContext,fName+" PDF Created", Toast.LENGTH_SHORT).show();
            }
        });
    }
}