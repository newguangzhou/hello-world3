package com.xiaomaoqiu.old.ui.mainPages.pageMe.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomaoqiu.old.ui.mainPages.pageMe.bean.PetInfo;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/17.
 */

public class PetVarietyAdapter extends RecyclerView.Adapter<PetVarietyAdapter.VarietyViewHolder> {

    private List<PetInfo> pets;
    private onVerityItemClickListener listener;

    public void setPetsList(List<PetInfo> pets){
        this.pets=pets;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(onVerityItemClickListener listener){
        this.listener=listener;
    }

    public void setPetList(Context context){
        if(null == pets){
            pets=new ArrayList<>();
        }
        String[] petNames=context.getResources().getStringArray(R.array.petlist_name);
        TypedArray imgs=context.getResources().obtainTypedArray(R.array.petlist_img);
        int defaValue= R.drawable.pet_variety_1;
        for(int i=0;i<petNames.length;i++){
            pets.add(new PetInfo(imgs.getResourceId(i,defaValue),petNames[i]));
        }
        imgs.recycle();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        //设置底部间隔
        recyclerView.setClipToPadding(false);
        int padding=recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.pet_varitey_padding);
        recyclerView.setPadding(0,padding,0,padding);
    }

    @Override
    public VarietyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VarietyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_variety,parent,false));
    }

    @Override
    public void onBindViewHolder(VarietyViewHolder holder, int position) {
        final PetInfo tmp=pets.get(position);
        holder.imageView.setImageResource(tmp.mResID);
        holder.textView.setText(tmp.mName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != listener){
                    listener.onItemClick(v,tmp.mName);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(null == pets){
            return 0;
        }
        return pets.size();
    }

    class VarietyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public VarietyViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.pet_variety_item_img);
            textView=(TextView)itemView.findViewById(R.id.pet_variety_item_name);

        }
    }

    public interface onVerityItemClickListener{
        void onItemClick(View view,String name);
    }
}
