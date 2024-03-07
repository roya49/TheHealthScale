package Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.sample.thehealthscale.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import Login.Login;
import SocketServer.ConnectionThread;
import Utils.StrUtils;

public class SettingDialog extends DialogFragment {

    private EditText etAge;
    private EditText etHeight;
    private EditText etWeight;
    private RadioButton male;
    private RadioButton female;
    private String height = "";
    private String age = "";
    private String weight = "";
    private String sex = "";
    private String recv;

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            Log.d("recv",recv);
            if (recv.equals("1")){
                Toast.makeText(getActivity(),"保存成功！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.setting_title));
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_layout, null);
        builder.setView(view);
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
        builder.setPositiveButton(getString(R.string.setting_submit), new DialogInterface.OnClickListener() {
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
                });
        return builder.create();
    }
}
