package Food;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import DBHelper.DBHelper;

public class FoodDBHelper {

    private String db="Food";
    private DBHelper dbHelper;

    public FoodDBHelper(DBHelper dbHelper){
        this.dbHelper=dbHelper;
    }

    public void insert(List<Food> foods){
        for (Food food : foods){
            Cursor cursor=dbHelper.getReadableDatabase().query(db,null,"food_name = ?",new String[]{food.getFoodName()},null,null,null);
            if (cursor.getCount() != 0)
                continue;
            ContentValues values=new ContentValues();
            values.put("food_name", food.getFoodName());
            values.put("kcal", food.getKcal());
            values.put("protein", food.getProtein());
            values.put("fat", food.getFat());
            values.put("carbohydrate", food.getCarbohydrate());
            dbHelper.getWritableDatabase().insert(db,null,values);
        }
    }

    public int getCount(){
        Cursor cursor=dbHelper.getReadableDatabase().query(db,null,null,null,null,null,null);
        return  cursor.getCount();
    }

    public List<Food> searchFood(String name){
        List<Food> data=new ArrayList<>();
        Cursor cursor=dbHelper.getReadableDatabase().query(db,null,"food_name like ?",new String[]{"%"+name+"%"},null,null,null,null);
        while (cursor.moveToNext()){
            Food food = new Food();
            food.setFoodName(cursor.getString(cursor.getColumnIndex("food_name")));
            food.setKcal(cursor.getFloat(cursor.getColumnIndex("kcal")));
            food.setProtein(cursor.getFloat(cursor.getColumnIndex("protein")));
            food.setFat(cursor.getFloat(cursor.getColumnIndex("fat")));
            food.setCarbohydrate(cursor.getFloat(cursor.getColumnIndex("carbohydrate")));
            data.add(food);
        }
        cursor.close();
        return data;
    }

    public Boolean tableExist() {

        SQLiteDatabase table = null;
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='Food' ";

        Cursor cursor = table.rawQuery(sql, null);

        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    public void close(){
        dbHelper.close();
    }
}
