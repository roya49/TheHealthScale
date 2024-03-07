package Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.sample.thehealthscale.R;
import java.util.ArrayList;
import java.util.List;
import DBHelper.DBHelper;
import Food.*;
import SocketServer.ConnectionThread;

public class FoodsDialog extends DialogFragment {

    private List<Food> foodList;
    private String recv;
    private FoodDBHelper dbHelper = new FoodDBHelper(new DBHelper(getActivity()));

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            String[] foods = recv.split("\\|");
            foodList = new ArrayList<>();
            for (String foodInfo : foods) {
                Food food = new Food();
                food.setAll(foodInfo.split("/"));
                foodList.add(food);
            }

            int dataCount = dbHelper.getCount();
            if (dataCount == foodList.size()){
                Toast.makeText(getActivity(), "数据已下载！", Toast.LENGTH_SHORT).show();
                return;
            }else {
                dbHelper.insert(foodList);
                Toast.makeText(getActivity(), "数据下载完成!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.get_foods));
        builder.setMessage("下载食物数据\n若不下载食物数据，将影响应用使用状况！");
        //builder.setCancelable(false);
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
                });
        return builder.create();
    }
}
