package Meal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sample.thehealthscale.FoodFragment;
import com.sample.thehealthscale.HomeFragment;
import com.sample.thehealthscale.R;
import java.util.ArrayList;
import java.util.List;
import Bluetooth.*;
import DBHelper.DBHelper;
import Login.Login;
import Food.*;
import Utils.*;

public class AddMeal extends AppCompatActivity {

    final private int DATA = 99;

    private SearchView foodName;
    private RecyclerView recyclerView;
    private List<Food> foods;
    public static Button selected;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior mDialogBehavior;
    private RecyclerView selectedRecyclerView;
    private List<Food> selected_foods;
    public static TextView totalKcal;
    public static Button submit;
    public static TextView noSelected;
    private View view;
    public static int selected_pos = 0;

    private int weight;
    public static int kcal = 0;

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DATA:
                    Bundle b = msg.getData();
                    String recv = b.getString("data");
                    weight = Integer.parseInt(recv);
                    selected_foods.get(selected_pos-1).setWeight(weight);
                    kcal += Math.ceil(((float)weight/100)*selected_foods.get(selected_pos-1).getKcal());
                    Toast.makeText(getApplication(),""+kcal,Toast.LENGTH_SHORT).show();
                    totalKcal.setText(kcal+"千卡");
                    //当获取食物重量后设为可用
                    if (!selected.isEnabled()){
                        noSelected.setVisibility(View.GONE);
                        submit.setEnabled(true);
                        selected.setEnabled(true);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_layout);
        selected_foods = new ArrayList<>();
        recyclerView = findViewById(R.id.food_reView);
        selected = findViewById(R.id.selected_img);
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //底部弹窗显示
                if (bottomSheetDialog.isShowing()){
                    bottomSheetDialog.dismiss();
                    Toast.makeText(getApplication(),"diss",Toast.LENGTH_SHORT).show();
                }else {
                    bottomSheetDialog.show();
                }
            }
        });
        submit = findViewById(R.id.meal_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将用户的食物拼接成食谱
                String meal ="";
                meal = StrUtils.getMealString(selected_foods);
                //将用户的食谱存入数据库
                MealDBHelper dbHelper = new MealDBHelper(new DBHelper(getApplication()));
                dbHelper.insert(Login.loginUser.getName(),Utils.getNetTime(),meal);
                Toast.makeText(getApplication(),"打卡成功！",Toast.LENGTH_SHORT).show();
                //将食物总kcal传入主页
                HomeFragment.kcal = kcal;
                //将食物传入饮食页
                for (Food food : selected_foods){
                    FoodFragment.foods.add(food);
                }
                //将用餐时间传入主页
                HomeFragment.time = Utils.getTime();
                //清空用户选择的食物
                selected_foods.clear();
                //将每餐的kcal清零
                kcal = 0;
                //将选择的食物列表位置归位0
                selected_pos = 0;
                //将底部控件设为不可用
                submit.setEnabled(false);
                selected.setEnabled(false);
            }
        });
        foodName = findViewById(R.id.search_food);
        foodName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //查询食物数据
                FoodDBHelper dbHelper = new FoodDBHelper(new DBHelper(getApplication()));
                foods = dbHelper.searchFood(query);
                setAdapter();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        view = View.inflate(AddMeal.this, R.layout.selected_food_layout, null);
        totalKcal = view.findViewById(R.id.selected_kcal);
        selectedRecyclerView = view.findViewById(R.id.selected_reView);
        noSelected = view.findViewById(R.id.noSelected);
        setBottomSheetDialog();
    }


    private void setAdapter(){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        FoodAdapter adapter = new FoodAdapter(foods);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                if (Bluetooth.client != null){
                    BluetoothTools.Writetask w = Bluetooth.client. new Writetask("start");
                    w.run();
                }else {
                    Toast.makeText(getApplication(),"请先连接蓝牙！",Toast.LENGTH_SHORT).show();
                }
                //将选择的食物添加至list
                selected_pos += 1;
                selected_foods.add(foods.get(pos));
                setSelectedFoodsAdapter();
            }

            @Override
            public void onLongClick(int pos) {

            }
        });
    }

    private void setBottomSheetDialog(){
        selectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FoodAdapter mainAdapter = new FoodAdapter(selected_foods);
        selectedRecyclerView.setAdapter(mainAdapter);

        bottomSheetDialog = new BottomSheetDialog(AddMeal.this,R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(view);
        mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getPeekHeight());

    }

    private void setSelectedFoodsAdapter(){
        final SelectedFoodAdapter mainAdapter = new SelectedFoodAdapter(selected_foods);
        selectedRecyclerView.setAdapter(mainAdapter);
    }

    /**
     * 弹窗高度，默认为屏幕高度的四分之三
     * 子类可重写该方法返回peekHeight
     *
     * @return height
     */
    private int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight - peekHeight / 3;
    }

}
