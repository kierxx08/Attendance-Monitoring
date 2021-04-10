package com.kierasis.attendancemonitoring;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class adapter_00 extends RecyclerView.Adapter<adapter_00.ViewHolder> {
    Context ctx;
    LayoutInflater inflater;
    List<ext_00> data_list;
    private OnDataListener mOnDataListener;

    public adapter_00(Context ctx, List<ext_00> data_list, OnDataListener onDataListener){
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.data_list = data_list;
        this.mOnDataListener = onDataListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_00_layout,parent, false);
        ViewHolder vHolder = new ViewHolder(view, mOnDataListener);

        SharedPreferences activity_info = ctx.getSharedPreferences("activity-info", MODE_PRIVATE);

        vHolder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ctx,vHolder.getAdapterPosition()+": Edit and Remove Function",Toast.LENGTH_LONG).show();

                String text = data_list.get(vHolder.getAdapterPosition()).getId();
                SharedPreferences.Editor editor = activity_info.edit();
                editor.putString("btnmore_adapter_00",text);
                editor.apply();
            }
        });


        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Title.setText(data_list.get(position).getTitle());
        holder.Subtitle.setText(data_list.get(position).getSubtitle());
        holder.Subtitle2.setText(data_list.get(position).getSubtitle2());
        if(data_list.get(position).getImgURL().isEmpty()){
            holder.ImgURL.setVisibility(View.GONE);
            holder.ImgURL_cv.setVisibility(View.GONE);
        }else {
            Picasso.get().load(data_list.get(position).getImgURL()).into(holder.ImgURL);
        }

        /*
        if(data_list.get(position).getTitle().equals("Case 8")){
            holder.cardview_case.setCardBackgroundColor(Color.parseColor("#ef5350"));

        }
         */

    }

    @Override
    public int getItemCount() {
        return data_list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Title, Subtitle, Subtitle2;
        ImageView ImgURL;
        MaterialButton btn_more;
        OnDataListener onDataListener;
        MaterialCardView cardview, ImgURL_cv;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView, OnDataListener onDataListener) {
            super(itemView);

            ImgURL = itemView.findViewById(R.id.list_00_img);
            Title = itemView.findViewById(R.id.list_00_title);
            Subtitle = itemView.findViewById(R.id.list_00_subtitle);
            Subtitle2 = itemView.findViewById(R.id.list_00_subtitle2);
            cardview = itemView.findViewById(R.id.list_00_cardview);
            ImgURL_cv = itemView.findViewById(R.id.list_00_img_cv);
            btn_more = itemView.findViewById(R.id.list_00_more);
            constraintLayout = itemView.findViewById(R.id.list_00_constraintlayout);
            this.onDataListener = onDataListener;


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onDataListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface  OnDataListener{
        void onNoteClick(int position);
    }
}
