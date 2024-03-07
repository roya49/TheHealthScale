package Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.sample.thehealthscale.R;

import java.util.Timer;

import SocketServer.ConnectionThread;
import Utils.MD5Utils;
import Utils.StrUtils;

public class Register extends AppCompatActivity {

    private String user;
    private String userPassword1;
    private String userPassword2;
    private LinearLayout register_back;
    private Button register;
    private String recv = "";

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            if (recv.equals("1")){
                Toast.makeText(Register.this,"注册成功！",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Register.this,"该用户名已存在！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        final EditText userName=findViewById(R.id.register_name);
        final EditText password1=findViewById(R.id.password1);
        final EditText password2=findViewById(R.id.password2);
        register_back = findViewById(R.id.register_back);
        register_back.getBackground().setAlpha(180);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=userName.getText().toString().trim();
                userPassword1=password1.getText().toString().trim();
                userPassword2=password2.getText().toString().trim();
                if(TextUtils.isEmpty(user)){
                    Toast.makeText(Register.this,"请输入用户名！",Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(userPassword1)){
                    Toast.makeText(Register.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(userPassword2)){
                    Toast.makeText(Register.this,"请再次输入密码！",Toast.LENGTH_SHORT).show();
                    return;
                } else if(!userPassword1.equals(userPassword2)){
                    Toast.makeText(Register.this,"两次输入的密码不一致！",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String data = StrUtils.getDataString("register", user, MD5Utils.md5(userPassword1));
                    ConnectionThread connectionThread = new ConnectionThread(mHandler);
                    connectionThread.setString(data);
                    connectionThread.start();
                    //saveRegisterInfo(user,userPassword1);
                }
            }
        });
    }

    private void saveRegisterInfo(String user, String password) {
        String passwordMD5= MD5Utils.md5(password);
        SharedPreferences preferences=getSharedPreferences("User",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(user,passwordMD5);
        editor.apply();
    }
}
