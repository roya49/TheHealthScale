package Utils;

import java.util.ArrayList;
import java.util.List;

import Food.Food;

public class StrUtils {

    public static String getDataString(String ... datas){
        String data = "";
        for(int i = 0; i<datas.length; i++){
            if(i != datas.length - 1)
                data = data + datas[i]+"/";
            else
                data = data +datas[i];
        }
        return data;
    }

    public static String getMealString(List<Food>  foods){
        String data = "";
        for(int i = 0; i<foods.size(); i++){
            if(i != foods.size() - 1)
                data = data + foods.get(i).getFoodName() + ",";
            else
                data = data +foods.get(i).getFoodName();
        }
        return data;
    }
}
