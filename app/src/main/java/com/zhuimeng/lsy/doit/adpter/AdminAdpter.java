package com.zhuimeng.lsy.doit.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.bmob.PlanItem;
import com.zhuimeng.lsy.doit.util.DataOpreation;
import com.zhuimeng.lsy.doit.util.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by lsy on 16-5-16.
 */
public class AdminAdpter extends RecyclerView.Adapter<AdminAdpter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    public DataOpreation dataOpreation;
    public List<PlanItem> mList;


    public AdminAdpter(DataOpreation dataOpreation, List<PlanItem> mList) {
        this.dataOpreation = dataOpreation;
        this.mList = mList;
    }


    @Override
    public AdminAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(AdminAdpter.MyViewHolder holder, int position) {
        holder.textView.setText(mList.get(position).getPlanText());
        if (mList.get(position).getColorType() == 0) {
            holder.view.setBackgroundResource(R.color.shape_null);
        }
        if (mList.get(position).getColorType() == 1) {
            holder.view.setBackgroundResource(R.color.shape1);
        }
        if (mList.get(position).getColorType() == 2) {
            holder.view.setBackgroundResource(R.color.shape2);
        }
        if (mList.get(position).getColorType() == 3) {
            holder.view.setBackgroundResource(R.color.shape3);
        }
        if (mList.get(position).getColorType() == 4) {
            holder.view.setBackgroundResource(R.color.shape4);
        }
        if (mList.get(position).getColorType() == 5) {
            holder.view.setBackgroundResource(R.color.shape5);
        }
        if (mList.get(position).getColorType() == 6) {
            holder.view.setBackgroundResource(R.color.shape6);
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        dataOpreation.dataChange(true, 3);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        dataOpreation.dataChange(true, 2);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = (ImageView) itemView.findViewById(R.id.card_color);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
