package com.sample.thehealthscale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import CircleProgress.CircleProgress;
import DBHelper.*;
import Meal.MealDBHelper;
import Login.Login;
import Meal.*;
import Utils.Utils;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    public static CircleProgress circleProgress;
    private LinearLayout linearLayout;
    private List<Meal> meals = new ArrayList<>();
    private TextView warning;
    private RecyclerView recyclerView;
    private TextView noView;
    public static int kcal =0;
    public static String time = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        warning = view.findViewById(R.id.text_warning);
        recyclerView = view.findViewById(R.id.meal_reView);
        noView = view.findViewById(R.id.no_meal);
        circleProgress = view.findViewById(R.id.circle_progress);
        circleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login.loginUser == null){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), Login.class);
                    startActivity(intent);
                }
            }
        });

        linearLayout = view.findViewById(R.id.lin_progress);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login.loginUser == null){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), Login.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    private void setAdapter(){
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        MealAdapter adapter = new MealAdapter(meals);
        recyclerView.setAdapter(adapter);
        //添加分割线
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        Drawable drawable=getResources().getDrawable(R.drawable.view_splitline);
        dividerItemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Login.loginUser != null){
            //设置用户摄入最大的kcal
            if (Login.loginUser.getKcal() > 0)
                circleProgress.setMaxValue(Login.loginUser.getKcal());
            MealDBHelper dbHelper=new MealDBHelper(new DBHelper(getContext()));
            meals = dbHelper.getAll(Login.loginUser.getName());
            //获取用户的用餐记录
            if (meals.size() > 0){
                noView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                setAdapter();
            }
            //设置用户已摄入的Kcal
            circleProgress.setValue(kcal);
            saveKcal();
        }
        //设置用户用餐时间提醒
        setTimeWarning();
    }


    private void setTimeWarning(){
        if (Login.loginUser != null){
            if (!time.equals("")){
                if ((time.compareTo("09:00") > 0) && (time.compareTo("11:30") < 0)){
                    warning.setText(R.string.morning);
                }else if ((time.compareTo("12:30") > 0) && (time.compareTo("17:30") < 0)){
                    warning.setText(R.string.lunch);
                }else if ((time.compareTo("18:30") > 0)){
                    warning.setText(R.string.dinner);
                }
                saveTime();
            }else {
                preference = getActivity().getSharedPreferences(Login.loginUser.getName(), MODE_PRIVATE);
                time = preference.getString("time", "");
                if (!time.equals("")) {
                    if ((time.compareTo("09:00") > 0) && (time.compareTo("11:30") < 0)) {
                        warning.setText(R.string.morning);
                    } else if ((time.compareTo("12:30") > 0) && (time.compareTo("17:30") < 0)) {
                        warning.setText(R.string.lunch);
                    } else if ((time.compareTo("18:30") > 0)) {
                        warning.setText(R.string.dinner);
                    }
                }
            }
        }
    }

    private void saveTime(){
        if (Login.loginUser != null){
            preference = getActivity().getSharedPreferences(Login.loginUser.getName(),MODE_PRIVATE);
            editor = preference.edit();
            editor.putString("time",time);
            editor.apply();
        }

    }

    private void saveKcal(){
        //存储今日摄入的kcal
        if (Login.loginUser != null){
            preference = getActivity().getSharedPreferences(Login.loginUser.getName(),MODE_PRIVATE);
            editor = preference.edit();
            editor.putInt("kcal", kcal);
            editor.putString("date",Utils.getDate());
            editor.apply();
        }

    }

}
