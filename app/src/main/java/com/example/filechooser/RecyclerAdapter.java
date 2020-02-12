package com.example.filechooser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<AbsViewHolder>{
    private ArrayList<File> mDataset;
    private int layoutType = ItemType.LAYOUT_LINEAR;
    private boolean isEditMode = ItemType.EDIT_MODE_OFF;

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public interface OnItemClickListener{
        public void onItemClick(File data);
        public void onItemClick(View view, File file, int index);
    }
    public interface OnItemLongClickListener{
        public void onItemLongClick();
    }

    private OnItemLongClickListener itemLongClickListener;
    private OnItemClickListener listener;


    public void setOnItemLongClickListener(OnItemLongClickListener listener){ this.itemLongClickListener = listener; }
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
        holder.setData(mDataset.get(position));
        holder.setEditMode(isEditMode);

        // Map<int, Object> 형태로 되어있음.
        // 태그는 뷰 안에 저장 공간.(동적임 엄청난게 들어가기도 함 ex. bitmap)
        holder.view.setTag(POSITION_TAG,position); //File 객체를 통으로 넣어서 사용해도 된다
        holder.view.setOnClickListener(clickListener);
        holder.view.setOnLongClickListener(longClickListener);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isEditMode){
                listener.onItemClick(v, mDataset.get((int)v.getTag(POSITION_TAG)), (int)v.getTag(POSITION_TAG));
            } else{
                listener.onItemClick(mDataset.get((int)v.getTag(POSITION_TAG)));
            }
        }
    };

    View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            if(!isEditMode){
                itemLongClickListener.onItemLongClick();
                notifyDataSetChanged();
            }
            return true;
        }
    };


    public File getItem(int position){
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
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


    public void setDataList(ArrayList<File> files){
        mDataset = files;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean isEditMode){
        this.isEditMode = isEditMode;
        notifyDataSetChanged();
    }

    public void addData(File file){
        mDataset.add(file);
        Log.d("RecyclerAdapter111", "addData: "+mDataset.size());
        notifyItemInserted(mDataset.size()-1);
    }

    public void deleteData(File file, int position){
        Log.d("DeleteFile_RecAd", "deleteData: "+position);
        mDataset.remove(file);
        notifyItemRemoved(position);
    }

    public int getPosition(View view){
        return (int)view.getTag(POSITION_TAG);
    }

}
