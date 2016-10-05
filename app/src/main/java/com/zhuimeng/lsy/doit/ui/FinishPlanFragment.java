package com.zhuimeng.lsy.doit.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.adpter.FinishedAdapter;
import com.zhuimeng.lsy.doit.bmob.PlanItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsy on 16-5-3.
 */
public class FinishPlanFragment extends Fragment {
    protected static FinishedAdapter finishedAdapter;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final static int READ_DATABASES = 1;
    private final static int UPDATE_VIEW=2;
    protected static List<PlanItem> finishPlan = new ArrayList<PlanItem>();
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READ_DATABASES:
                    SQLiteDatabase db = MainActivity.appData.getWritableDatabase();
                    Cursor cursor = db.query("finishedplan", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        finishPlan.clear();
                        do {
                            String name = cursor.getString(cursor.getColumnIndex("plantext"));
                            Integer resId = cursor.getInt(cursor.getColumnIndex("plantypeimage"));
                            Integer prioiv = cursor.getInt(cursor.getColumnIndex("prioiv"));
                            PlanItem temp = new PlanItem(resId, name);
                            temp.setPrioiv(prioiv);
                            finishPlan.add(temp);
                            //Log.e("name", name);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    db.close();
                    if (finishPlan.size() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("没有完成的计划，加油！！！");
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    break;
                case UPDATE_VIEW:
                    finishedAdapter.notifyDataSetChanged();
                    break;
                default:break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("已完成计划");
        textView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swiperefreshlayout);
        fab=(FloatingActionButton)view.findViewById(R.id.fab);
        handler.sendEmptyMessage(READ_DATABASES);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setEnabled(false);
        finishedAdapter=new FinishedAdapter(finishPlan);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(finishedAdapter);
        fab.setVisibility(View.INVISIBLE);
        handler.sendEmptyMessage(UPDATE_VIEW);
    }
}
