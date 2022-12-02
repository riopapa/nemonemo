package com.urrecliner.nemonemo;

import static com.urrecliner.nemonemo.MainActivity.getNemo;
import static com.urrecliner.nemonemo.MainActivity.mActivity;
import static com.urrecliner.nemonemo.MainActivity.mContext;
import static com.urrecliner.nemonemo.MainActivity.mPackagePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class NemoAdaptor extends RecyclerView.Adapter<NemoAdaptor.ViewHolder> {

    @Override
    public int getItemCount() {
        return MainActivity.nemos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImageOrg, ivImageNemo;
        TextView tvFName;

        ViewHolder(final View itemView) {
            super(itemView);
            tvFName = itemView.findViewById(R.id.nemo_file);
            ivImageOrg = itemView.findViewById(R.id.org_image);
            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                edit_Memo(pos);
            });
            ivImageNemo = itemView.findViewById(R.id.nemo_image);
            ivImageNemo.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                edit_Memo(pos);
            });
        }

        void edit_Memo(int pos) {

            Intent intent = new Intent(mContext, EditActivity.class);
            intent.putExtra("pos", pos);
            mActivity.startActivity(intent);
//            Toast.makeText(context, "PDF generated "+nemo.fName, Toast.LENGTH_LONG).show();
        }

    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nemo_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        MainActivity.Nemo nemo = getNemo(pos);
        if (nemo.bitmap == null) {
            nemo.bitmap = new MakeNemo().build(nemo.fName);
            MainActivity.setNemo(pos, nemo);
        }
        holder.tvFName.setText(nemo.fName);
        File file = new File(mPackagePath, nemo.fName+".jpg");
        Bitmap orgMap = BitmapFactory.decodeFile(file.toString());
        holder.ivImageOrg.setImageBitmap(orgMap);
        Bitmap nemoMap = Bitmap.createScaledBitmap(nemo.bitmap, orgMap.getWidth(), orgMap.getHeight(), false);
        holder.ivImageNemo.setImageBitmap(nemoMap);
    }

    // a : width / 4, 20
    // b : width / 8, 20
    // c : width / 4, scale 80, scale 20
    // d : width / 4, scale 160, scale 80, scale 40, scale 20
}