package Login;

import androidx.appcompat.app.AppCompatActivity;
import SocketServer.ConnectionThread;
import User.User;
import Utils.MD5Utils;
import Utils.StrUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.sample.thehealthscale.MainActivity;
import com.sample.thehealthscale.R;

public class Login extends AppCompatActivity {

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private String user=null;
    private String passWord=null;
    private LinearLayout linear_back;
    private String recv = "";
    public static User loginUser;

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            Bundle b = msg.getData();
            recv = b.getString("data");
            Log.d("recv",recv);
            if (recv.equals("-1")){
                Toast.makeText(Login.this,"登录成功！",Toast.LENGTH_SHORT).show();
                editor = preference.edit();
                editor.putString("login", user);
                editor.apply();
                loginUser = new User();
                loginUser.setName(user);
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            } else if(recv.equals("0")){
                Toast.makeText(Login.this,"密码错误，请重试！",Toast.LENGTH_SHORT).show();
            } else if(recv.equals("1")){
                Toast.makeText(Login.this,"该用户名不存在，请注册后登录！",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        final EditText userName = findViewById(R.id.login_name);
        final EditText userPassword = findViewById(R.id.login_password);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        linear_back = findViewById(R.id.login_back);
        linear_back.getBackground().setAlpha(180);
        final CheckBox remember = findViewById(R.id.remember_password);
        preference = getSharedPreferences("User",MODE_PRIVATE);
        boolean isRemember = preference.getBoolean("remember_password",false);
        if(isRemember){
            String account=preference.getString("name","");
            String password=preference.getString("password","");
            userName.setText(account);
            userPassword.setText(password);
            remember.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=userName.getText().toString().trim();
                passWord=userPassword.getText().toString().trim();
                if(TextUtils.isEmpty(user)){
                    Toast.makeText(Login.this,"请输入用户名！",Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(passWord)){
                    Toast.makeText(Login.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    String data = StrUtils.getDataString("login", user, MD5Utils.md5(passWord));
                    ConnectionThread connectionThread = new ConnectionThread(mHandler);
                    connectionThread.setString(data);
                    connectionThread.start();
                    if (remember.isChecked()) {
                        editor = preference.edit();
                        editor.putString("name", user);
                        editor.putString("password", passWord);
                        editor.putBoolean("remember_password", true);
                    } else {
                        editor = preference.edit();
                        editor.putString("name","");
                        editor.putString("password","");
                        editor.putBoolean("remember_password", false);
                    }
                    editor.apply();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }
}
