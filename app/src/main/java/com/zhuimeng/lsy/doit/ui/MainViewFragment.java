package com.zhuimeng.lsy.doit.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.adpter.MyAdapter;
import com.zhuimeng.lsy.doit.util.DataOpreation;
import com.zhuimeng.lsy.doit.util.OnStartDragListener;
import com.zhuimeng.lsy.doit.bmob.PlanItem;
import com.zhuimeng.lsy.doit.util.SimpleItemTouchHelperCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.SaveListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by lsy on 16-4-19.
 */
public class MainViewFragment extends Fragment implements OnStartDragListener, DataOpreation {
    private ItemTouchHelper mItemTouchHelper;
    public static List<PlanItem> planItemList = new ArrayList<>();
    public static List<PlanItem> tempPlanItemList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    protected static MyAdapter myAdapter;
    private int firstVisibleItem = 0;
    private int mPreviousVisibleItem = 0;
    private final static int ADD_PLAN = 1;
    private final static int INIT_LIST = 2;
    private final static int UPLOAD = 3;
    private final static int GET_DATA_FROM_BMOB = 4;
    private final static int RECOVERY_FROM_BMOB = 5;
    private final static int UPDTA_VIEW = 6;
    protected MaterialDialog mMaterialDialog;
    protected static android.support.design.widget.FloatingActionButton fab;
    public static TextView textView;
    protected static CoordinatorLayout root;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_PLAN:
                    addDataToDatabases();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case INIT_LIST:
                    addDatabasesToData();
                    break;
                case UPLOAD:
                    uploadData();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case GET_DATA_FROM_BMOB:
                    tempPlanItemList.clear();
                    queryData();
                    break;
                case RECOVERY_FROM_BMOB:
                    planItemList.addAll(tempPlanItemList);
                    for (PlanItem p : tempPlanItemList)
                        Log.e("P", p.getPlanText());
                    break;
                case UPDTA_VIEW:
                    myAdapter.notifyDataSetChanged();
                    if (planItemList.size() == 0) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public MainViewFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myAdapter = new MyAdapter(planItemList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(myAdapter);
        mHandler.sendEmptyMessage(UPDTA_VIEW);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(myAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshLayout.isRefreshing()) {
//                    Toast.makeText(MainViewFragment.this.getActivity(), "OnRefresh", Toast.LENGTH_SHORT).show();
                    planItemList.clear();
                    mHandler.sendEmptyMessage(GET_DATA_FROM_BMOB);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem > mPreviousVisibleItem) {
//                    menuButton.hideMenu(true);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
//                    menuButton.showMenu(true);
                }
                mPreviousVisibleItem = firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View view= getActivity().getLayoutInflater().inflate(R.layout.add_plan,null);
//                EditText editView=(EditText)view.findViewById(R.id.add_plan);
//                showAddDialog(view,editView);
                AddItemFragment normalItemFragment=new AddItemFragment();
                normalItemFragment.setMyAdapter(myAdapter);
                normalItemFragment.setMyList(planItemList);
                normalItemFragment.setDataCallback(MainViewFragment.this);
                getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out).replace(R.id.content,normalItemFragment).addToBackStack(null).commit();
            }
        });

    }
//
//    private void showAddDialog(View view, final EditText editView) {
//
//        if (mMaterialDialog != null) {
//            mMaterialDialog.setContentView(view)
//                .setPositiveButton("提交", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        LinearLayout vv= (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.add_plan,null);
////                        TextView commitText=(TextView)vv.findViewById(R.id.add_plan);
////                        String text=commitText.getText().toString();
//                        String text=editView.getText().toString();
//                        PlanItem temp=new PlanItem(1,text);
//                        Log.e("commitText",text);
//                        MainViewFragment.planItemList.add(temp);
//                        MainViewFragment.myAdapter.notifyDataSetChanged();
//                        mHandler.sendEmptyMessage(ADD_PLAN);
//                        mMaterialDialog.dismiss();
//                        Snackbar.make(root,"添加成功", Snackbar.LENGTH_SHORT).setAction("Action",null).show();
//                    }
//                })
//                .setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mMaterialDialog.dismiss();
//                    }
//                });
//
//        mMaterialDialog.show();
//    }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
//        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("我的计划");
        fab = (android.support.design.widget.FloatingActionButton) view.findViewById(R.id.fab);
        textView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        root=(CoordinatorLayout)view.findViewById(R.id.rootView);
        //swipeRefreshLayout自动刷新
//        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                mSwipeRefreshLayout.setRefreshing(true);
//            }
//        });
        if (MainActivity.isFristLogin) {
            mSwipeRefreshLayout.setRefreshing(true);
            mHandler.sendEmptyMessage(GET_DATA_FROM_BMOB);
            MainActivity.isFristLogin=false;
        } else {
            if(planItemList.isEmpty()) {
                mHandler.sendEmptyMessage(INIT_LIST);
            }
            mHandler.sendEmptyMessage(UPDTA_VIEW);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Plan");
        mMaterialDialog=new MaterialDialog(getActivity());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e("为什么云端为空","onPause");
//       mHandler.sendEmptyMessage(UPLOAD);
    }


    private void uploadData() {
//        Log.e("uploadData", planItemList.size() + "");
        tempPlanItemList.clear();
        if (planItemList.size() == 0) {
            queryDataAndDelete("Table_" + MainActivity.objectId);
        } else {
            queryDataAndDelete("Table_" + MainActivity.objectId);
            List<BmobObject> tempList=new ArrayList<BmobObject>();
            for (PlanItem item : planItemList) {
                PlanItem temp = new PlanItem(item.getPlanImage(), item.getPlanText());
                temp.setCreateTime(item.getCreateTime());
                temp.setPrioiv(item.getPrioiv());
                temp.setTargetTime(item.getTargetTime());
                temp.setColorType(item.getColorType());
                temp.setPlanFrom(item.getPlanFrom());
                temp.setTableName("Table_" + MainActivity.objectId);
                tempList.add(temp);
            }
            new BmobObject().insertBatch(getActivity(), tempList, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.e("MainViewFragment upload","上传成功");
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    private void deleteAllData(String tableName) {
        for (PlanItem item : tempPlanItemList) {
            item.setTableName(tableName);
            item.delete(getActivity());
        }
    }

    private void queryDataAndDelete(final String tableName) {
        BmobQuery query = new BmobQuery(tableName);
        query.findObjects(getActivity(), new FindCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                parseJsonArray(jsonArray);
                deleteAllData(tableName);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void queryData() {
        BmobQuery query = new BmobQuery("Table_" + MainActivity.objectId);
        query.findObjects(getActivity(), new FindCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                parseJsonArray(jsonArray);
                Log.e("queryData", "success");
                mHandler.sendEmptyMessage(RECOVERY_FROM_BMOB);
                mHandler.sendEmptyMessage(ADD_PLAN);
                mHandler.sendEmptyMessage(UPDTA_VIEW);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("queryData", "success " + s);
            }
        });
    }


    private void parseJsonArray(JSONArray jsonArray) {
        Log.e("length of JsonArray",String.valueOf(jsonArray.length()));
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String planText = object.getString("planText");
                String id = object.getString("objectId");
                Integer planImage = object.getInt("planImage");
                Integer colorType=object.getInt("colorType");
                String planFrom=object.getString("planFrom");
                PlanItem item = new PlanItem(planImage, planText);
                item.setPlanFrom(planFrom);
                item.setObjectId(id);
                item.setColorType(colorType);
                tempPlanItemList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("jsonArray", jsonArray.toString());
        for (PlanItem p : tempPlanItemList)
            Log.e("P", p.getPlanText());
    }

    private void addDataToDatabases() {
        SQLiteDatabase db = MainActivity.appData.getWritableDatabase();
        db.execSQL("delete from unfinishedplan");
        for (PlanItem item : planItemList) {
            ContentValues valuse = new ContentValues();
            valuse.put("plantext", item.getPlanText());
            valuse.put("plantypeimage", item.getPlanImage());
            valuse.put("prioiv", item.getPrioiv());
            valuse.put("colortype",item.getColorType());
            valuse.put("planfrom",item.getPlanFrom());
            db.insert("unfinishedplan", null, valuse);
        }
        db.close();

    }


    private void addDatabasesToData() {
        SQLiteDatabase db = MainActivity.appData.getWritableDatabase();
        Cursor cursor = db.query("unfinishedplan", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            planItemList.clear();
            do {
                String name = cursor.getString(cursor.getColumnIndex("plantext"));
                Integer resId = cursor.getInt(cursor.getColumnIndex("plantypeimage"));
                Integer prioiv = cursor.getInt(cursor.getColumnIndex("prioiv"));
                Integer colorType=cursor.getInt(cursor.getColumnIndex("colortype"));
                String from=cursor.getString(cursor.getColumnIndex("planfrom"));
                PlanItem temp = new PlanItem(resId, name);
                temp.setPrioiv(prioiv);
                temp.setPlanFrom(from);
                temp.setColorType(colorType);
                planItemList.add(temp);
                //Log.e("name", name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (planItemList.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void dataChange(boolean change,int i) {
        if (change) {
            mHandler.sendEmptyMessage(ADD_PLAN);
            mHandler.sendEmptyMessage(UPLOAD);
//            BmobUser bmobUser=BmobUser.getCurrentUser(getActivity());
//            String objectId=bmobUser.getObjectId();
//            User user=new User();
//            user.setObjectId(objectId);
//            user.setMyCloudPlan(Arrays.asList("1","2","3"));
//            user.update(getActivity(), new UpdateListener() {
//                @Override
//                public void onSuccess() {
//                    Log.e("lalalalala","成功了");
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    Log.e("lalalalala","失败了");
//                }
//            });
            if(i==2)
            Snackbar.make(root, "删除成功", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }
}
