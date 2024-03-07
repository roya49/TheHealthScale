package Meal;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import DBHelper.DBHelper;

public class MealDBHelper {

    private String db="Meal";
    private DBHelper dbHelper;

    public MealDBHelper(DBHelper dbHelper){
        this.dbHelper=dbHelper;
    }

    public long insert(String name, String date, String meal){
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("date",date);
        values.put("meal",meal);
        return dbHelper.getWritableDatabase().insert(db,null,values);
    }

    public List<Meal> getAll(String name){
        List<Meal> data=new ArrayList<>();
        Cursor cursor=dbHelper.getReadableDatabase().query(db,null,"name = ?",new String[]{name},null,null,"date desc","10");
        while (cursor.moveToNext()){
            Meal meal = new Meal();
            meal.setMeal(cursor.getString(cursor.getColumnIndex("meal")));
            meal.setDate(cursor.getString(cursor.getColumnIndex("date")));
            data.add(meal);
        }
        cursor.close();
        return data;
    }

    public void close(){
        dbHelper.close();
    }
}
