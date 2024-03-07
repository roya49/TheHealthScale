package com.sample.thehealthscale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import Login.Login;
import SocketServer.ConnectionThread;
import User.User;
import Utils.StrUtils;
import Utils.Utils;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private SharedPreferences preference;
    private String recv;

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            String [] str = recv.split("/");
            Login.loginUser.setAll(str);
            createFragments();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFragments();
        createBottom();

        //判断是否有用户登录过，登录过则自动获取用户信息
        preference = getSharedPreferences("User",MODE_PRIVATE);
        String login_user = preference.getString("login","");
        if ( Login.loginUser==null && !login_user.equals("")){
            Login.loginUser = new User();
            Login.loginUser.setName(login_user);
            preference = getSharedPreferences(Login.loginUser.getName(),MODE_PRIVATE);
            String date = preference.getString("date","");
            //若重新启动，判断是否是新的一天，若是则将kcal清零，不是则读取今日kcal
            if (HomeFragment.kcal == 0){
                if (date.equals(Utils.getDate())){
                    HomeFragment.kcal = preference.getInt("kcal",0);
                }
            }
        }
        if (Login.loginUser != null){
            String data = StrUtils.getDataString("getInfo", Login.loginUser.getName());
            ConnectionThread connectionThread = new ConnectionThread(mHandler);
            connectionThread.setString(data);
            connectionThread.start();
        }
    }


    //创建底部功能控件
    private void createBottom() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:   //主页
                        setFragmentPosition(0);
                        return true;
                    case R.id.menu_food:    //饮食页
                        setFragmentPosition(1);
                        return true;
                    case R.id.menu_me:      //我的
                        setFragmentPosition(2);
                        return true;
                }
                return false;
            }
        });
    }

    //创建每个Fragment页面
    private void createFragments() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new FoodFragment());
        fragments.add(new MeFragment());
        setFragmentPosition(0);
    }

    //优化切换
    private void setFragmentPosition(int po) {
        Fragment currentFragment = fragments.get(po);
        if (!currentFragment.isAdded()) {
            if (fragment != null)
                getSupportFragmentManager().beginTransaction().hide(fragment).
                        add(R.id.fragment_content, currentFragment).commit();
            else
                getSupportFragmentManager().beginTransaction().
                        add(R.id.fragment_content, currentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().hide(fragment).show(currentFragment).commit();
        }
        fragment = currentFragment;
    }

}
