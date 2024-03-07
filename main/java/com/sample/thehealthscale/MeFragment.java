package com.sample.thehealthscale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import DBHelper.DBHelper;
import Dialog.AboutDialog;
import Dialog.ResignDialog;
import Food.*;
import Login.Login;
import SocketServer.ConnectionThread;
import Utils.StrUtils;

import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout setting;
    private LinearLayout getFoods;
    private LinearLayout about;
    private LinearLayout resign;
    private RelativeLayout login;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private TextView userBMI;
    private TextView userWeight;
    private Intent intent = new Intent();
    private List<Food> foodList;
    private String recv;
    private EditText etAge;
    private EditText etHeight;
    private EditText etWeight;
    private RadioButton male;
    private RadioButton female;
    private String height = "";
    private String age = "";
    private String weight = "";
    private String sex = "";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            if (recv.equals("1")){
                userBMI.setText("BMI:" + Login.loginUser.getBMI());
                userWeight.setText("体重:" + Login.loginUser.getWeight() + "kg");
                Toast.makeText(getActivity(),"保存成功！",Toast.LENGTH_SHORT).show();
            }else {
                String[] foods = recv.split("\\|");
                foodList = new ArrayList<>();
                for (String foodInfo : foods) {
                    Food food = new Food();
                    food.setAll(foodInfo.split("/"));
                    foodList.add(food);
                }
                FoodDBHelper dbHelper = new FoodDBHelper(new DBHelper(getActivity()));
                int dataCount = dbHelper.getCount();
                if (dataCount == foodList.size()) {
                    Toast.makeText(getActivity(), "数据已下载！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dbHelper.insert(foodList);
                    Toast.makeText(getActivity(), "数据下载完成!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        setting = view.findViewById(R.id.setting);
        setting.setOnClickListener(this);
        getFoods = view.findViewById(R.id.Foods);
        getFoods.setOnClickListener(this);
        about = view.findViewById(R.id.about);
        about.setOnClickListener(this);
        resign = view.findViewById(R.id.resign);
        resign.setOnClickListener(this);
        login = view.findViewById(R.id.login);
        login.setOnClickListener(this);
        preference = getContext().getSharedPreferences("User",MODE_PRIVATE);
        userBMI = view.findViewById(R.id.userBMI);
        userWeight = view.findViewById(R.id.weight);
        if (Login.loginUser != null){
            userBMI.setText("BMI:" + Login.loginUser.getBMI());
            userWeight.setText("体重:" + Login.loginUser.getWeight() + "kg");
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                if (Login.loginUser == null){
                    intent.setClass(getActivity(), Login.class);
                    startActivity(intent);
                }
                break;
            case R.id.Foods:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.get_foods));
                builder.setMessage("下载食物数据\n若不下载食物数据，将影响应用使用状况！");
                builder.setIcon(R.drawable.warning);
                builder.setPositiveButton(getString(R.string.setting_submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionThread connectionThread = new ConnectionThread(mHandler);
                        connectionThread.setString("food");
                        connectionThread.start();
                    }
                })
                        .setNegativeButton(getString(R.string.setting_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                    .show();
                break;
            case R.id.about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(),"about");
                break;
            case R.id.setting:
                if (Login.loginUser == null){
                    Toast.makeText(getContext(),"请先登录！",Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder settingDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.setting_title));
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_layout, null);
                settingDialog.setView(view);
                etAge = view.findViewById(R.id.et_age);
                etHeight = view.findViewById(R.id.et_height);
                etWeight = view.findViewById(R.id.et_wight);
                male = view.findViewById(R.id.setting_male);
                female = view.findViewById(R.id.setting_female);
                height = "" + Login.loginUser.getHeight();
                age = "" +Login.loginUser.getAge();
                weight = "" + Login.loginUser.getWeight();
                sex = Login.loginUser.getSex();
                if (!height.equals("0.0")){
                    etHeight.setText(height);
                }
                if (!age.equals("0")){
                    etAge.setText(age);
                }
                if (!weight.equals("0.0")){
                    etWeight.setText(weight);
                }
                if (sex != null) {
                    if (sex.equals("男")) {
                        male.setChecked(true);
                    } else if (sex.equals("女")) {
                        female.setChecked(true);
                    }
                }
                settingDialog.setPositiveButton(getString(R.string.setting_submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        height = etHeight.getText().toString();
                        age = etAge.getText().toString();
                        weight = etWeight.getText().toString();
                        if (TextUtils.isEmpty(height)){
                            Toast.makeText(getContext(),"身高不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (TextUtils.isEmpty(age)){
                            Toast.makeText(getContext(),"年龄不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (TextUtils.isEmpty(weight)){
                            Toast.makeText(getContext(),"体重不能为空！", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (!male.isChecked() && !female.isChecked()){
                            Toast.makeText(getContext(),"请选择性别！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (male.isChecked()){
                            sex = male.getText().toString();
                        }else if (female.isChecked()){
                            sex = female.getText().toString();
                        }
                        Login.loginUser.setHeight(Float.parseFloat(height));
                        Login.loginUser.setAge(Integer.parseInt(age));
                        Login.loginUser.setWeight(Float.parseFloat(weight));
                        Login.loginUser.setSex(sex);
                        Login.loginUser.setKcal();
                        Login.loginUser.setBMI();
                        Login.loginUser.setState();
                        String data = StrUtils.getDataString("info", Login.loginUser.getName(),height,age,weight,sex,
                                String.valueOf(Login.loginUser.getKcal()), String.valueOf(Login.loginUser.getBMI()),Login.loginUser.getState());
                        ConnectionThread connectionThread = new ConnectionThread(mHandler);
                        connectionThread.setString(data);
                        connectionThread.start();
                    }
                })
                        .setNegativeButton(getString(R.string.setting_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                break;
            case R.id.resign:
                if (Login.loginUser == null){
                    Toast.makeText(getContext(),"无登录信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
                ResignDialog resignDialog = new ResignDialog();
                resignDialog.show(getFragmentManager(),"dialog");
                break;
        }
    }
}
