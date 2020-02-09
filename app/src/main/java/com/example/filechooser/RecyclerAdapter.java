package com.example.filechooser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;

public class RecyclerAdapter extends RecyclerView.Adapter<AbsViewHolder>{
    private File[] mDataset;
    private int layoutType = ItemType.LAYOUT_LINEAR;

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public interface OnItemClickListener{
        public void onItemClick(File data);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType == ItemType.LAYOUT_LINEAR){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_recycler, parent, false);
            return new LinearViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_recycler_grid, parent, false);
            return new GridViewHolder(v);
        }
    }

    // 16진수 32비트로 설정하기.
    private static final int POSITION_TAG = 0xFFFFFFF1;

    // 여기에서 UI 관련 된 처리(데이터 뽑아서 넣어주기)를 여기서 하는게 좋다.
    @Override
    public void onBindViewHolder(@NonNull AbsViewHolder holder, int position) {
        holder.setData(mDataset[position]);

        // Map<int, Object> 형태로 되어있음.
        // 태그는 뷰 안에 저장 공간.(동적임 엄청난게 들어가기도 함 ex. bitmap)
        holder.view.setTag(POSITION_TAG,position); //File 객체를 통으로 넣어서 사용해도 된다
        holder.view.setOnClickListener(clickListener);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onItemClick(mDataset[(int)v.getTag(POSITION_TAG)]);
        }
    };

    public File getItem(int position){
        return mDataset[position];
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public int getItemViewType(int position) {
        if(layoutType == ItemType.LAYOUT_LINEAR){
            return ItemType.LAYOUT_LINEAR;
        }
        else{
            return ItemType.LAYOUT_GRID;
        }
    }


    public void setDataList(File[] files){
        mDataset = files;
        notifyDataSetChanged();
    }
}
