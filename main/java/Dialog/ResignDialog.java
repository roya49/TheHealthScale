package Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.sample.thehealthscale.MainActivity;
import com.sample.thehealthscale.R;

import Login.Login;

import static android.content.Context.MODE_PRIVATE;

public class ResignDialog  extends DialogFragment {

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preference = getContext().getSharedPreferences("User",MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor = preference.edit();
                        editor.putString("login", "");
                        editor.apply();
                        Toast.makeText(getContext(), "退出登录！", Toast.LENGTH_SHORT).show();
                        Login.loginUser = null;
                        //注销登录，清空任务栈，防止用户点击返回键返回上层活动
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
