package com.zhuimeng.lsy.doit;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.zhuimeng.lsy.doit.ui.CloudPlanFragment;
import com.zhuimeng.lsy.doit.ui.FinishPlanFragment;
import com.zhuimeng.lsy.doit.ui.MainViewFragment;
import com.zhuimeng.lsy.doit.util.AppData;

import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    public static AppData appData;
    private TextView userNametextView;
    private SQLiteDatabase db;


    public Button fabButton;
    protected static String name;
    public static String objectId;
    public static Boolean isFristLogin = false;
    public static Fragment currentFrament = null;
    private final static int DELETE_DATA = 1;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DELETE_DATA:
                    MainViewFragment.planItemList.clear();
                    db = MainActivity.appData.getWritableDatabase();
                    db.execSQL("delete from unfinishedplan");
                    db.close();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        name = intent.getStringExtra("user_name");
        objectId = intent.getStringExtra("user_object_id");
        isFristLogin = intent.getBooleanExtra("isFristLogin", false);
        Log.e("name", name);
        appData = new AppData(MainActivity.this, "MyAppData.db", null, 1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabButton = (Button) findViewById(R.id.fab_button);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        userNametextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userNametextView.setText(name);
        navigationView.setNavigationItemSelectedListener(this);
        //myHandler.sendEmptyMessage(FINF_CLOUD_PLAN);
        if (savedInstanceState == null) {
            MainViewFragment homeFragment = new MainViewFragment();
            currentFrament = homeFragment;
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out).replace(R.id.content, homeFragment, "homeTag").commit();
        }
    }

    public Button getFabButton() {
        return fabButton;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    getFragmentManager().popBackStack();
                    getSupportActionBar().setTitle("My Plan");
                }
            }

        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_out) {
            finish();
        } else if (id == R.id.nav_loginout) {
            finish();
            BmobUser.logOut(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            Log.e("为什么云端为空", "DELETE_DATA");
            myHandler.sendEmptyMessage(DELETE_DATA);
        } else if (id == R.id.nav_all_plan) {
            if (!(currentFrament instanceof MainViewFragment)) {
                MainViewFragment homeFragment = new MainViewFragment();
                currentFrament = homeFragment;
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out).replace(R.id.content, homeFragment, "homeTag").commit();
            }
        } else if (id == R.id.nav_ok_plan) {
            FinishPlanFragment finishPlanFragment = new FinishPlanFragment();
            currentFrament = finishPlanFragment;
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out).replace(R.id.content, finishPlanFragment).commit();
        } else if (id == R.id.nav_cloud_plan) {
            CloudPlanFragment cloudPlanFragment = new CloudPlanFragment();
            currentFrament = cloudPlanFragment;
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out).replace(R.id.content, cloudPlanFragment).commit();
        }/*else if(id==R.id.nav_new_cloud_plan){
            final View view = getLayoutInflater().inflate(R.layout.create_plan, null);

            final MaterialDialog materialDialog = new MaterialDialog(MainActivity.this);
            materialDialog.setTitle("创建云计划").setContentView(view)
                    .setCanceledOnTouchOutside(true).setPositiveButton("提交", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText name = (EditText) view.findViewById(R.id.create_plan_name);
                    EditText password = (EditText) view.findViewById(R.id.create_plan_password);
                    Log.e("name", name.getText().toString());
                    Log.e("password", password.getText().toString());
                    TeamTable teamTable=new TeamTable();
                    teamTable.setCreateTableName(name.getText().toString());
                    teamTable.setPassword(password.getText().toString());
                    teamTable.setCreateUser(BmobUser.getCurrentUser(MainActivity.this));
                    teamTable.save(MainActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Snackbar.make(view,"创建成功",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    materialDialog.dismiss();
                }
            }).show();
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
