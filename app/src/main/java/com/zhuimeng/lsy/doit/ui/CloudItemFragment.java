package com.zhuimeng.lsy.doit.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.adpter.AdminAdpter;
import com.zhuimeng.lsy.doit.adpter.SelectAdpater;
import com.zhuimeng.lsy.doit.bmob.PlanItem;
import com.zhuimeng.lsy.doit.bmob.TeamTable;
import com.zhuimeng.lsy.doit.util.DataOpreation;
import com.zhuimeng.lsy.doit.util.SimpleItemTouchHelperCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lsy on 16-5-12.
 */
public class CloudItemFragment extends Fragment implements DataOpreation {
    private ItemTouchHelper mItemTouchHelper;
    private String tableName;
    private String tableId;
    private CoordinatorLayout rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<PlanItem> cloudTempPlan = new ArrayList<PlanItem>();
    private static List<PlanItem> cloudPlan = new ArrayList<PlanItem>();
    private RecyclerView recyclerView;
    private AdminAdpter adminAdpter;
    private SelectAdpater cloudPlanAdapter;
    private Button addButton;
    private int sum = 0;
    private FloatingActionButton fab;
    public static TextView cloudText;
    private boolean isAdmin=false; //false为普通用户,true为管理员
    private boolean isAdminMode = false; //false为用户模式，true为管理员模式
    private final static int CLOUD_IS_ADMIN=0;
    private final static int CLOUD_GET_PLAN_FROM_BMOB = 1;
    private final static int CLOUD_UPDATA_VIEW = 2;
    private final static int CLOUD_UPLAOD = 3;
    private final static int CLOUD_ADD_TO_HOME = 4;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    Handler cloudHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOUD_IS_ADMIN:
                    queryIsAdmin();
                    break;
                case CLOUD_GET_PLAN_FROM_BMOB:
                    queryData();
                    break;
                case CLOUD_UPDATA_VIEW:
                    if (isAdminMode) {
                        adminAdpter.notifyDataSetChanged();
                    } else {
                        cloudPlanAdapter.notifyDataSetChanged();
                    }
                    if (cloudPlan.size() == 0) {
                        cloudText.setVisibility(View.VISIBLE);
                    } else {
                        cloudText.setVisibility(View.GONE);
                    }
                    break;
                case CLOUD_UPLAOD:
                    upLoad();
                    break;
                case CLOUD_ADD_TO_HOME:
                    MainViewFragment homeFragment = new MainViewFragment();
                    MainActivity.currentFrament = homeFragment;
                    getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out).replace(R.id.content, homeFragment, "homeTag").commit();
                    break;
                default:
                    break;
            }
        }
    };




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cloudPlan.clear();
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tableName);
        addButton = ((MainActivity) getActivity()).getFabButton();
        rootView = (CoordinatorLayout) view.findViewById(R.id.rootView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        cloudText = (TextView) view.findViewById(R.id.empty_view);
        cloudText.setText("暂无计划");
       // addButton.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_done_white_24dp);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //swipeRefreshLayout自动刷新
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        cloudHandler.sendEmptyMessage(CLOUD_GET_PLAN_FROM_BMOB);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (cloudPlan.size() == 0)
            cloudText.setVisibility(View.VISIBLE);
        cloudPlanAdapter = new SelectAdpater(cloudPlan);
        adminAdpter = new AdminAdpter(CloudItemFragment.this, cloudPlan);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (!isAdminMode) {
            recyclerView.setAdapter(cloudPlanAdapter);
        } else {
            recyclerView.setAdapter(adminAdpter);
            fab.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.cloud_add));
            fab.setImageResource(R.drawable.ic_add_white_24dp);

        }
        cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cloudPlan.clear();
                cloudHandler.sendEmptyMessage(CLOUD_GET_PLAN_FROM_BMOB);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation scaleDown = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_scale_down);
                final Animation scaleUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
                if (isAdminMode == false) {//用户模式切换为管理员模式
                    scaleDown.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fab.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.cloud_add));
                            fab.setImageResource(R.drawable.ic_add_white_24dp);
                            fab.startAnimation(scaleUp);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addButton.setText("完成");
                    isAdminMode = true;
                    recyclerView.setAdapter(adminAdpter);
                    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adminAdpter);
                    mItemTouchHelper = new ItemTouchHelper(callback);
                    mItemTouchHelper.attachToRecyclerView(recyclerView);
                    cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                } else {//管理员模式切换为用户模式
                    scaleDown.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fab.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.cloud_done));
                            fab.setImageResource(R.drawable.ic_done_white_24dp);
                            fab.startAnimation(scaleUp);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addButton.setText("编辑");
                    isAdminMode = false;
                    recyclerView.setAdapter(cloudPlanAdapter);
                    mItemTouchHelper.attachToRecyclerView(null);
                    cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                }
                fab.startAnimation(scaleDown);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdminMode == true) {
                    AdminCloudItemFragment adminCloudItemFragment = new AdminCloudItemFragment();
                    adminCloudItemFragment.setMyList(cloudPlan);
                    adminCloudItemFragment.setDataCallback(CloudItemFragment.this);
                    getActivity().getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out).replace(R.id.content, adminCloudItemFragment).addToBackStack(null).commit();
                } else {
                    final List<BmobObject> tempList = new ArrayList<BmobObject>();
                    final List<PlanItem> addHomeList=new ArrayList<PlanItem>();
                    for (PlanItem p : cloudPlan) {
                        if (p.getSelect()) {
                            PlanItem temp = new PlanItem(p.getPlanImage(), p.getPlanText());
                            temp.setCreateTime(p.getCreateTime());
                            temp.setPrioiv(p.getPrioiv());
                            temp.setTargetTime(p.getTargetTime());
                            temp.setColorType(p.getColorType());
                            temp.setPlanFrom(tableName);
                            temp.setTableName("Table_" + MainActivity.objectId);
                            tempList.add(temp);
                            addHomeList.add(temp);
                            sum++;
                        }
                    }
                    new BmobObject().insertBatch(getActivity(), tempList, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            MainViewFragment.planItemList.addAll(addHomeList);
                            cloudHandler.sendEmptyMessage(CLOUD_ADD_TO_HOME);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    if (sum == 0) {
                        Snackbar.make(rootView, "请选择想要添加的任务", Snackbar.LENGTH_SHORT).show();
                    }
                    sum = 0;
                }
            }
        });
    }

    private void queryIsAdmin() {
        final String userId=BmobUser.getCurrentUser(getActivity()).getObjectId();
        BmobQuery query=new BmobQuery("User_"+tableId);
        query.addWhereEqualTo("user",BmobUser.getCurrentUser(getActivity()));
        query.findObjects(getActivity(), new FindCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                Log.e("queryIsAdmin",jsonArray.toString());
                for(int i=0;i<jsonArray.length();i++){
                    try{
                        JSONObject object=jsonArray.getJSONObject(i);
                        Boolean isSuperAdmin=object.getBoolean("isSuperAdmin");
                        Boolean isAdminn=object.getBoolean("isAdmin");
                        JSONObject temp=object.getJSONObject("user");
                        String objectId=object.getString("objectId");
                        if(userId.equals(temp.getString("objectId"))){
                            isAdmin=isAdminn;
                        }else{
                            isAdmin=false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isAdmin){
                        addButton.setVisibility(View.VISIBLE);
                    }else{
                        addButton.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("queryIsAdmin","error "+s);

            }
        });
    }


    private void queryData() {
        BmobQuery<TeamTable> query = new BmobQuery<TeamTable>();
        query.addWhereEqualTo("createTableName", tableName);
        query.findObjects(getActivity(), new FindListener<TeamTable>() {
            @Override
            public void onSuccess(List<TeamTable> list) {
                tableId = list.get(0).getObjectId();
                queryIsAdmin();
                BmobQuery query = new BmobQuery("Table_" + tableId);
                query.findObjects(getActivity(), new FindCallback() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        parseJsonArray(jsonArray);
                        cloudPlan.clear();
                        for (PlanItem p : cloudTempPlan)
                            Log.e("queryData cloudPlan", p.getPlanText().toString());
                        cloudPlan.addAll(cloudTempPlan);
                        cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                        Log.e("queryData", "success" + jsonArray);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e("queryData", "fail " + s);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                cloudPlan.clear();
            }
        });
    }

    private void upLoad() {
        cloudTempPlan.clear();
        if (cloudPlan.size() == 0) {
            queryDataAndDelete("Table_" + tableId);
        } else {
            queryDataAndDelete("Table_" + tableId);
            List<BmobObject> tempList=new ArrayList<BmobObject>();
            for (PlanItem item : cloudPlan) {
                PlanItem temp = new PlanItem(item.getPlanImage(), item.getPlanText());
                temp.setCreateTime(item.getCreateTime());
                temp.setPrioiv(item.getPrioiv());
                temp.setColorType(item.getColorType());
                temp.setTargetTime(item.getTargetTime());
                temp.setPlanFrom(tableName);
                temp.setTableName("Table_" + tableId);
                tempList.add(temp);
            }
            new BmobObject().insertBatch(getActivity(), tempList, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.e("CloudItem upload","上传成功");
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    private void queryDataAndDelete(final String name) {
        BmobQuery query = new BmobQuery(name);
        query.findObjects(getActivity(), new FindCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                parseJsonArray(jsonArray);
                deleteAllData(name);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void deleteAllData(String name) {
        for (PlanItem item : cloudTempPlan) {
            Log.e("CloudItemFragment", "deleteAllData  " + name);
            item.setTableName(name);
            item.delete(getActivity());
        }
    }


    private void parseJsonArray(JSONArray jsonArray) {
        cloudTempPlan.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String planText = object.getString("planText");
                String id = object.getString("objectId");
                Integer planImage = object.getInt("planImage");
                Integer colorType=object.getInt("colorType");
                PlanItem item = new PlanItem(planImage, planText);
                item.setObjectId(id);
                item.setColorType(colorType);
                cloudTempPlan.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("jsonArray", jsonArray.toString());
    }

    @Override
    public void onPause() {
        Log.e("where", "CloudItemFragment onPause");
        super.onPause();
        if (addButton != null) {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void dataChange(boolean change, int type) {
        if (change) {
            cloudHandler.sendEmptyMessage(CLOUD_UPLAOD);
            cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
        }
    }
}
