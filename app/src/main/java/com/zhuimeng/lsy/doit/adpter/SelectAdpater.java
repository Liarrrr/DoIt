package com.zhuimeng.lsy.doit.adpter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rey.material.widget.CheckBox;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.bmob.PlanItem;

import java.util.List;

/**
 * Created by lsy on 16-5-14.
 */
public class SelectAdpater extends RecyclerView.Adapter<SelectAdpater.MyViewHolder> {
    private List<PlanItem> list;

    public SelectAdpater(List<PlanItem> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_cloud_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.checkBox.setText(list.get(position).getPlanText().toString());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    list.get(position).setSelect(false);
                }else if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(true);
                    list.get(position).setSelect(true);
                }
            }
        });
        if(list.get(position).getColorType()==0){
            holder.view.setBackgroundResource(R.color.shape_null);
        }if (list.get(position).getColorType()==1){
            holder.view.setBackgroundResource(R.color.shape1);
        }if (list.get(position).getColorType()==2){
            holder.view.setBackgroundResource(R.color.shape2);
        }if (list.get(position).getColorType()==3){
            holder.view.setBackgroundResource(R.color.shape3);
        }if (list.get(position).getColorType()==4){
            holder.view.setBackgroundResource(R.color.shape4);
        }if (list.get(position).getColorType()==5){
            holder.view.setBackgroundResource(R.color.shape5);
        }if (list.get(position).getColorType()==6){
            holder.view.setBackgroundResource(R.color.shape6);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public ImageView view;
        public MyViewHolder(View itemView) {
            super(itemView);
            view=(ImageView)itemView.findViewById(R.id.select_card_view); 
            checkBox = (CheckBox) itemView.findViewById(R.id.item_select);
        }
    }
}
