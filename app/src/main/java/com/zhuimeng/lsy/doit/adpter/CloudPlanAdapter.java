package com.zhuimeng.lsy.doit.adpter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.ui.CloudItemFragment;

import java.util.List;

/**
 * Created by lsy on 16-5-12.
 */
public class CloudPlanAdapter extends RecyclerView.Adapter<CloudPlanAdapter.MyHolder> {

    private List<String> mList;
    private Activity mActivity;

    public CloudPlanAdapter(List<String> mList,Activity activity) {
        this.mList = mList;
        this.mActivity=activity;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, null));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CloudItemFragment cloudItemFragment = new CloudItemFragment();
                    cloudItemFragment.setTableName(textView.getText().toString());
                    mActivity.getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out).replace(R.id.content, cloudItemFragment).addToBackStack(null).commit();
                }
            });
        }
    }
}
