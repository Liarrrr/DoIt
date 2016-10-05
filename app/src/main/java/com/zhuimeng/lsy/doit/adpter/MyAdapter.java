package com.zhuimeng.lsy.doit.adpter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.ui.MainViewFragment;
import com.zhuimeng.lsy.doit.util.DataOpreation;
import com.zhuimeng.lsy.doit.util.ItemTouchHelperAdapter;
import com.zhuimeng.lsy.doit.bmob.PlanItem;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by lsy on 16-3-20.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {


    public DataOpreation dataOpreation;
    public MainViewFragment mFragment;
    public List<PlanItem> mList;
    protected MaterialDialog mMaterialDialog;


    public MyAdapter(List mList, MainViewFragment mFragment) {
        this.mList = mList;
        this.mFragment = mFragment;
        dataOpreation = mFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_plan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTextView.setText(mList.get(position).getPlanText());
        String str=mList.get(position).getPlanFrom();
        if (str.equals("NULL")) {
            holder.from.setText("");
            holder.imageView.setImageResource(R.drawable.ic_date_range_black_36dp);
        } else {
            holder.from.setText(mList.get(position).getPlanFrom());
            holder.imageView.setImageResource(R.drawable.ic_clou_plan_black_36dp);
        }

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
        SQLiteDatabase db = MainActivity.appData.getWritableDatabase();
        ContentValues valuse = new ContentValues();
        valuse.put("plantext", mList.get(position).getPlanText());
        valuse.put("plantypeimage", mList.get(position).getPlanImage());
        valuse.put("prioiv", mList.get(position).getPrioiv());
        mList.remove(position);
        dataOpreation.dataChange(true, 2);
        notifyItemRemoved(position);
        db.insert("finishedplan", null, valuse);
        db.close();
        if (MainViewFragment.planItemList.size() == 0) {
            MainViewFragment.textView.setVisibility(View.VISIBLE);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView from;
        public ImageView imageView;
        public ImageView view;

        public MyViewHolder(View itemView) {
            super(itemView);
//            itemView.setBackgroundResource(R.color.shape6);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog = new MaterialDialog(mFragment.getActivity());
                    mMaterialDialog.setMessage(mTextView.getText().toString())
                            .setPositiveButton("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mMaterialDialog != null) {
                                        mMaterialDialog.dismiss();
                                    }
                                }
                            });
                    mMaterialDialog.show();
                }
            });
            from = (TextView) itemView.findViewById(R.id.my_plan_from);
            view = (ImageView) itemView.findViewById(R.id.my_plan_card_color);
            mTextView = (TextView) itemView.findViewById(R.id.my_plan_item_name);
            imageView = (ImageView) itemView.findViewById(R.id.my_plan_item_image);
        }

//        @Override
//        public void onItemSelected() {
//            MainViewFragment.mMaterialDialog.setMessage(mTextView.getText().toString())
//                    .setPositiveButton("知道了", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            MainViewFragment.mMaterialDialog.dismiss();
//                        }
//                    });
//            MainViewFragment.mMaterialDialog.show();
//        }
//
//        @Override
//        public void onItemClear() {
//
//        }


    }


}
