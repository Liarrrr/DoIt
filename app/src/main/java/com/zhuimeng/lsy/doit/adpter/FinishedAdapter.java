package com.zhuimeng.lsy.doit.adpter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.bmob.PlanItem;

import java.util.List;

/**
 * Created by lsy on 16-5-3.
 */
public class FinishedAdapter extends RecyclerView.Adapter<FinishedAdapter.FinishedViewHolder> {

    private List<PlanItem> list;

    public FinishedAdapter(List<PlanItem> list) {
        this.list = list;
    }

    @Override
    public FinishedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FinishedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(FinishedViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getPlanText());
        holder.textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if(list.get(position).getPlanImage()==1) {
            holder.imageView.setImageResource(R.drawable.ic_date_range_blue_grey_600_36dp);
        }else {
            holder.imageView.setImageResource(R.drawable.ic_alarm_blue_grey_600_36dp);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class FinishedViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public FinishedViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }
}
