package com.zhuimeng.lsy.doit.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.util.DataOpreation;
import com.zhuimeng.lsy.doit.bmob.PlanItem;

import java.util.List;

/**
 * Created by lsy on 16-4-20.
 */
public class AddItemFragment extends Fragment {
    private Button commitButton;
    private TextInputEditText commitText;
    private DataOpreation dataOpreation;
    private List<PlanItem> myList;
    private RecyclerView.Adapter myAdapter;
    public ImageView imageView1;
    public ImageView imageView2;
    public ImageView imageView3;
    public ImageView imageView4;
    public ImageView imageView5;
    public ImageView imageView6;
    public int choose = 0;


    public void setMyList(List<PlanItem> myList) {
        this.myList = myList;
    }

    public void setMyAdapter(RecyclerView.Adapter myAdapter) {
        this.myAdapter = myAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("添加计划");
        View view=inflater.inflate(R.layout.add_normal_item, container, false);
        commitButton= (Button) view.findViewById(R.id.normal_commit_button);
        commitText= (TextInputEditText) view.findViewById(R.id.normal_add_text);
        imageView1=(ImageView)view.findViewById(R.id.colorView1);
        imageView2=(ImageView)view.findViewById(R.id.colorView2);
        imageView3=(ImageView)view.findViewById(R.id.colorView3);
        imageView4=(ImageView)view.findViewById(R.id.colorView4);
        imageView5=(ImageView)view.findViewById(R.id.colorView5);
        imageView6=(ImageView)view.findViewById(R.id.colorView6);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==1){
                    return;
                }else {
                    imageView1.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=1;
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==2){
                    return;
                }else {
                    imageView2.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=2;
                }
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==3){
                    return;
                }else {
                    imageView3.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=3;
                }
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==4){
                    return;
                }else {
                    imageView4.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=4;
                }
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==5){
                    return;
                }else {
                    imageView5.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=5;
                }
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choose==6){
                    return;
                }else {
                    imageView6.setImageResource(R.drawable.ic_done_white_36dp);
                    select(choose);
                    choose=6;
                }
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commitText.getText().toString().trim().equals("")){
                    Snackbar.make(v, "好像什么都没发生", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    PlanItem temp=new PlanItem(1,commitText.getText().toString());
                    temp.setColorType(choose);
                    temp.setPlanFrom("NULL");
//                    MainViewFragment.planItemList.add(temp);
//                    MainViewFragment.myAdapter.notifyDataSetChanged();
                    myList.add(temp);
//                    myAdapter.notifyDataSetChanged();
                    if(dataOpreation !=null){
                        dataOpreation.dataChange(true,1);
                    }else {
                        SQLiteDatabase db = MainActivity.appData.getWritableDatabase();
                        ContentValues valuse = new ContentValues();
                        valuse.put("plantext", temp.getPlanText());
                        valuse.put("plantypeimage", temp.getPlanImage());
                        valuse.put("prioiv", temp.getPrioiv());
                        valuse.put("colortype",temp.getColorType());
                        db.insert("unfinishedplan", null, valuse);
                    }
                    getActivity().getFragmentManager().popBackStack();
//                    Snackbar.make(MainViewFragment.root, "添加成功", Snackbar.LENGTH_SHORT)
//                            .setAction("Action", null).show();
                }

            }
        });
    }

    private void select(int choose) {
        if (choose == 1) {
            imageView1.setImageDrawable(null);
        }
        if (choose == 2) {
            imageView2.setImageDrawable(null);
        }
        if (choose == 3) {
            imageView3.setImageDrawable(null);
        }
        if (choose == 4) {
            imageView4.setImageDrawable(null);
        }
        if (choose == 5) {
            imageView5.setImageDrawable(null);
        }if(choose==6){
            imageView6.setImageDrawable(null);
        }
    }

    public void setDataCallback(DataOpreation callback){
        this.dataOpreation =callback;
    }
}

