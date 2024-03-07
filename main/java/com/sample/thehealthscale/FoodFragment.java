package com.sample.thehealthscale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Bluetooth.Bluetooth;
import Food.Food;
import Login.Login;
import Meal.*;
import Utils.Utils;

import static android.content.Context.MODE_PRIVATE;

public class FoodFragment extends Fragment {

    private PieChart pieChart;
    private LinearLayout warningLayout;
    private TextView warning;
    private Button addMeal;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    public static List<Food> foods = new ArrayList<>();
    private float protein = 0;
    private float fat = 0;
    private float carbohydrate = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_fragment, container, false);
        setHasOptionsMenu(true);

        warningLayout = view.findViewById(R.id.linear_warning);
        warning = view.findViewById(R.id.food_warning);
        addMeal = view.findViewById(R.id.add_meal);
        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddMeal.class);
                startActivity(intent);
            }
        });

        pieChart = view.findViewById(R.id.consume_pie_chart);
        Pie();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setData(foods);
        saveFoodInfo();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bluetooth:
                Intent intent = new Intent(getContext(), Bluetooth.class);
                startActivity(intent);
        }
        return false;
    }

    private void Pie(){
        pieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        pieChart.setDescription("营养摄入状况");//设置描述
        pieChart.setDescriptionTextSize(20);//设置描述字体大小
        //pieChart.setDescriptionColor(); //设置描述颜色
        //pieChart.setDescriptionTypeface();//设置描述字体

        pieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量

        pieChart.setDragDecelerationFrictionCoef(0.7f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        pieChart.setDrawCenterText(true);//是否绘制中间的文字
        pieChart.setCenterTextColor(Color.RED);//中间的文字颜色
        pieChart.setCenterTextSize(24);//中间的文字字体大小

        pieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小

        pieChart.setTransparentCircleColor(Color.BLACK);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
        pieChart.setRotationAngle(10);//设置饼状图旋转的角度

        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

        // entry label styling
        pieChart.setDrawEntryLabels(true);//设置是否绘制Label
        pieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
        //pieChart.setEntryLabelTypeface(mTfRegular);
        pieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小

        //pieChart.setOnChartValueSelectedListener();//设值点击时候的回调
        pieChart.animateY(500, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画
        setData(foods);
    }

    private void setData(List<Food> foods){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        if (Login.loginUser != null){
            preference = getActivity().getSharedPreferences(Login.loginUser.getName(),MODE_PRIVATE);
            String date = preference.getString("date","");
            //若重新启动，判断是否是新的一天，若是则将kcal清零，不是则读取今日kcal
            if (date.equals(Utils.getDate())){
                protein = preference.getFloat("protein",0);
                fat = preference.getFloat("fat",0);
                carbohydrate = preference.getFloat("carbohydrate",0);
            }
            if (foods.size() != 0){
                for (Food food :foods) {
                    protein += food.getProtein();
                    fat += food.getFat();
                    carbohydrate += food.getCarbohydrate();
                }
            }
        }
        setWarning();
        pieEntries.add(new PieEntry(protein, "蛋白质"));
        pieEntries.add(new PieEntry(fat, "脂肪"));
        pieEntries.add(new PieEntry(carbohydrate, "碳水化合物"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        int[] colors = {
                Color.parseColor("#8CEBFF"),
                Color.parseColor("#C5FF8C"),
                Color.parseColor("#FFF78C")
        };
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLUE);

        pieChart.setData(pieData);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    public void setWarning(){
        if (Login.loginUser != null){
            float total = protein + fat + carbohydrate;
            float protein_percent =(float) Math.round(protein/total);
            float fat_percent = (float) Math.round(fat/total);
            float carbohydrate_percent = (float) Math.round(carbohydrate/total);

            if (protein_percent > fat_percent && protein_percent > carbohydrate_percent && protein_percent > 0.4){
                warningLayout.setVisibility(View.VISIBLE);
                warning.setText("蛋白质摄入过多,建议多摄入蔬菜等平衡膳食");
            }else if (fat_percent > protein_percent && fat_percent > carbohydrate_percent && fat_percent > 0.3){
                warningLayout.setVisibility(View.VISIBLE);
                warning.setText("脂肪摄入过多,建议多摄入肉类、蔬菜等平衡膳食");
            }else if (carbohydrate_percent > fat_percent && carbohydrate_percent > protein_percent && carbohydrate_percent > 0.4){
                warningLayout.setVisibility(View.VISIBLE);
                warning.setText("碳水摄入过多,建议少摄入主食，多摄入肉类、蔬菜等平衡膳食");
            }
        }
    }

    public void saveFoodInfo(){
        if (Login.loginUser != null){
            preference = getActivity().getSharedPreferences(Login.loginUser.getName(),MODE_PRIVATE);
            editor = preference.edit();
            editor.putFloat("protein", protein);
            editor.putFloat("fat", fat);
            editor.putFloat("carbohydrate", carbohydrate);
            editor.putString("date", Utils.getDate());
            editor.apply();
        }
    }

}
