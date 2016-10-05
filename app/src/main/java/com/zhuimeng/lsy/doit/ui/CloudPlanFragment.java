package com.zhuimeng.lsy.doit.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.rey.material.widget.CheckBox;
import com.zhuimeng.lsy.doit.MainActivity;
import com.zhuimeng.lsy.doit.R;
import com.zhuimeng.lsy.doit.adpter.CloudPlanAdapter;
import com.zhuimeng.lsy.doit.bmob.TeamTable;
import com.zhuimeng.lsy.doit.bmob.TeamTableUsers;
import com.zhuimeng.lsy.doit.bmob.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by lsy on 16-5-11.
 */
public class CloudPlanFragment extends Fragment {
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton addPlan;
    private FloatingActionButton createPlan;
    private CoordinatorLayout rootView;
    private int firstVisibleItem = 0;
    private int mPreviousVisibleItem = 0;
    private CloudPlanAdapter cloudPlanAdapter;
    private final static int CLOUD_UPDATA_VIEW = 1;
    private final static int FINF_CLOUD_PLAN = 2;
    private final static int CREATE_USER_TABLE = 3;
    private static List<String> cloudPlanList = new ArrayList<String>();


    Handler cloudHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOUD_UPDATA_VIEW:
                    cloudPlanAdapter.notifyDataSetChanged();
                    if (cloudPlanList.size() == 0) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    break;
                case FINF_CLOUD_PLAN:
                    findCloudPlan();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case CREATE_USER_TABLE:
                    String tableName = (String) msg.obj;
                    createUserTable(tableName);
                default:
                    break;
            }
        }
    };

    //创建云计划用户组
    private void createUserTable(String tableName) {
        Log.e("hahahahahahahahaha", tableName);
        TeamTableUsers teamTableUsers = new TeamTableUsers(tableName);
        teamTableUsers.setAdmin(true);
        teamTableUsers.setSuperAdmin(true);
        teamTableUsers.setUser(BmobUser.getCurrentUser(getActivity()));
        teamTableUsers.save(getActivity(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.e("hahahahahahahahaha", "成功了");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("hahahahahahahahaha", "失败了");
            }
        });
    }


    //查找用户已有的云计划组
    private void findCloudPlan() {
        if (BmobUser.getCurrentUser(getActivity()) != null) {
            String objectId = BmobUser.getCurrentUser(getActivity()).getObjectId();
            BmobQuery<User> query = new BmobQuery<User>();
            query.getObject(getActivity(), objectId, new GetListener<User>() {
                @Override
                public void onSuccess(User user) {
                    //planNameList.addAll(user.getMyCloudPlan());
                    cloudPlanList.clear();
                    cloudPlanList.addAll(user.getMyCloudPlan());
                }

                @Override
                public void onFailure(int i, String s) {
                }
            });
        } else {
            cloudPlanList.clear();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cloud_plan, container, false);
        textView = (TextView) view.findViewById(R.id.cloud_empty_view);
        textView.setText("暂无云计划组");
        if (cloudPlanList.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("我的云计划");
        recyclerView = (RecyclerView) view.findViewById(R.id.cloud_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.cloud_swiperefreshlayout);
        fabMenu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        addPlan = (FloatingActionButton) view.findViewById(R.id.add_cloud_plan);
        createPlan = (FloatingActionButton) view.findViewById(R.id.create_cloud_plan);
        rootView = (CoordinatorLayout) view.findViewById(R.id.cloud_root);
//        swipeRefreshLayout.setRefreshing(true);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabMenu.showMenu(true);
                fabMenu.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                fabMenu.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
            }
        }, 500);
        cloudHandler.sendEmptyMessage(FINF_CLOUD_PLAN);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cloudPlanAdapter = new CloudPlanAdapter(cloudPlanList, getActivity());
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cloudPlanAdapter);
        cloudHandler.sendEmptyMessageDelayed(CLOUD_UPDATA_VIEW, 1000);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing()) {
                    cloudHandler.sendEmptyMessage(FINF_CLOUD_PLAN);
                    cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem > mPreviousVisibleItem) {
                    fabMenu.hideMenu(true);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
                    fabMenu.showMenu(true);
                }
                mPreviousVisibleItem = firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
        createPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.create_plan, null);
                fabMenu.toggle(true);
                final MaterialDialog materialDialog = new MaterialDialog(getActivity());
                materialDialog.setTitle("创建云计划").setContentView(view)
                        .setCanceledOnTouchOutside(true).setPositiveButton("提交", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean commit = true;
                        final EditText name = (EditText) view.findViewById(R.id.create_plan_name);
                        final EditText password = (EditText) view.findViewById(R.id.create_plan_password);
                        if (name.getText().toString().equals("")) {
                            name.setError("计划名不能为空");
                            name.setText("");
                            commit = false;
                        }
                        if (password.length() < 6) {
                            password.setError("密码不能少于6位");
                            password.setText("");
                            commit = false;
                        }
                        if (commit) {
                            Log.e("name", name.getText().toString());
                            Log.e("password", password.getText().toString());
                            TeamTable teamTable = new TeamTable();
                            teamTable.setCreateTableName(name.getText().toString());
                            teamTable.setPassword(password.getText().toString());
                            teamTable.setCreateUser(BmobUser.getCurrentUser(getActivity()));
                            teamTable.save(getActivity(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    String objectId = BmobUser.getCurrentUser(getActivity()).getObjectId();
                                    User user = new User();
                                    user.addUnique("myCloudPlan", name.getText().toString());
                                    user.update(getActivity(), objectId, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            cloudPlanList.add(name.getText().toString());
                                            cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                                            BmobQuery<TeamTable> query = new BmobQuery<TeamTable>();
                                            query.addWhereEqualTo("createTableName", name.getText().toString());
                                            query.findObjects(getActivity(), new FindListener<TeamTable>() {
                                                @Override
                                                public void onSuccess(List<TeamTable> list) {
                                                    Message message = new Message();
                                                    message.what = CREATE_USER_TABLE;
                                                    message.obj = list.get(0).getObjectId();
                                                    cloudHandler.sendMessage(message);
                                                    Snackbar.make(rootView, "创建成功", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Snackbar.make(rootView, "创建失败" + s, Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();

                                }
                            });
                            materialDialog.dismiss();
                        }
                    }
                }).show();
            }
        });
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.add_cloud_plan, null);
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.add_cloud_plan_is_admin);
                final EditText nameEditText = (EditText) view.findViewById(R.id.add_plan_name);
                final EditText phoneEditText = (EditText) view.findViewById(R.id.add_plan_phone);
                final EditText passwordEditText = (EditText) view.findViewById(R.id.add_plan_password);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("addPlan onclick", "clicked");
                        if (!checkBox.isChecked()) {
                            passwordEditText.setEnabled(false);
                        } else {
                            passwordEditText.setEnabled(true);
                        }
                    }
                });
                fabMenu.toggle(true);
                final MaterialDialog addPlanDialog = new MaterialDialog(getActivity());
                addPlanDialog.setTitle("添加云计划").setContentView(view)
                        .setCanceledOnTouchOutside(true).setPositiveButton("提交", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = nameEditText.getText().toString();
                        final String phone = phoneEditText.getText().toString();
                        BmobQuery<TeamTable> query = new BmobQuery<TeamTable>();
                        query.include("createUser");
                        query.addWhereEqualTo("createTableName", name);
                        Log.e("createTableName", name);
                        query.findObjects(getActivity(), new FindListener<TeamTable>() {
                            @Override
                            public void onSuccess(List<TeamTable> list) {
                                if (list.size() == 0) {
                                    Log.e("addPlan onclick", "not exits");
                                    Snackbar.make(rootView, "计划名或ID有误，请重新输入", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    for (final TeamTable t : list) {
                                        if (t.getCreateUser() != null) {
                                            if (t.getCreateUser().getMobilePhoneNumber().equals(phone)) {
                                                String objectId = BmobUser.getCurrentUser(getActivity()).getObjectId();
                                                User user = new User();
                                                user.addUnique("myCloudPlan", name);
                                                user.update(getActivity(), objectId, new UpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        cloudPlanList.add(name);
                                                        cloudHandler.sendEmptyMessage(CLOUD_UPDATA_VIEW);
                                                        if (checkBox.isChecked()) {
                                                            String passwd=passwordEditText.getText().toString();
                                                            Log.e("lalalalalal", t.getPassword());
                                                            if (passwd.equals(t.getPassword())) {
                                                                String tableId = t.getObjectId();
                                                                Log.e("t", tableId);
                                                                TeamTableUsers user = new TeamTableUsers(tableId);
                                                                user.setUser(BmobUser.getCurrentUser(getActivity()));
                                                                user.setAdmin(true);
                                                                user.setSuperAdmin(false);
                                                                user.save(getActivity(), new SaveListener() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        Log.e("user save","success");
                                                                    }

                                                                    @Override
                                                                    public void onFailure(int i, String s) {
                                                                        Log.e("user save","Fail");
                                                                    }
                                                                });
                                                                Snackbar.make(rootView, "添加成功", Snackbar.LENGTH_SHORT).show();
                                                            } else {
                                                                passwordEditText.setError("密码错误");
                                                            }
                                                        } else {
                                                            Log.e("lalalalalal", "hahahaha");
                                                            String tableId = t.getObjectId();
                                                            TeamTableUsers user = new TeamTableUsers(tableId);
                                                            user.setUser(BmobUser.getCurrentUser(getActivity()));
                                                            user.setAdmin(false);
                                                            user.setSuperAdmin(false);
                                                            user.save(getActivity(), new SaveListener() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    Log.e("user save","success");
                                                                }

                                                                @Override
                                                                public void onFailure(int i, String s) {
                                                                    Log.e("user save","Fail");
                                                                }
                                                            });
                                                            Snackbar.make(rootView, "添加成功", Snackbar.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }


                                Log.e("addPlan onclick", "find success");
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.e("addPlan onclick", "find error");
                            }
                        });
                        addPlanDialog.dismiss();
                    }
                });
                addPlanDialog.show();

            }
        });
    }
}
