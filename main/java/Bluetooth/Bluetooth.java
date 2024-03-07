package Bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sample.thehealthscale.R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Bluetooth extends AppCompatActivity {

    private List<BluetoothDevice> devices;
    private AdapterForBluetooth adapter;
    public static BluetoothTools client;
    private RecyclerView recyclerView;
    private Button scan;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView bandBluetooth;
    private BluetoothDevice device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
        scan = findViewById(R.id.scan_btn);
        bandBluetooth = findViewById(R.id.bandBluetooth_text);
        recyclerView = findViewById(R.id.bluetooth_review);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>();
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver,filter);
                mBluetoothAdapter.startDiscovery();
            }
        });

        if(ContextCompat.checkSelfPermission(Bluetooth.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Bluetooth.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在启动时获取连接的设备
        if (client != null)
            getConnectedDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "You denied this request!", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                break;
        }
    }

    private void getConnectedDevice(){
        Set<BluetoothDevice> deviceList = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : deviceList) {
            try {
                //使用反射调用获取设备连接状态方法
                Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                //若设备连接，获取设备名称并显示
                if (isConnected){
                    bandBluetooth.setText(device.getName());
                    return;
                }else
                    continue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void initAdapter() {
        //设置adapter
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new AdapterForBluetooth(devices);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterForBluetooth.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                device = devices.get(pos);
                client = new BluetoothTools(device, handler); //创建一个客户端
                try {
                    client.connect();
                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                }
            }

            @Override
            public void onLongClick(int pos) {

            }
        });
    }

    // 创建一个接受 ACTION_FOUND 的 BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            // 当 Discovery 发现了一个设备
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // 从 Intent 中获取发现的 BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!isExist(device)){
                    devices.add(device);
                }
                if (devices.size() >= 4){
                    // Don't forget to unregister the ACTION_FOUND receiver.
                    unregisterReceiver(mReceiver);
                    initAdapter();
                }
            }
        }
    };

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BluetoothTools.CONNECT_FAILED:
                    Toast.makeText(Bluetooth.this,"连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothTools.CONNECT_SUCCESS:
                    Toast.makeText(Bluetooth.this,"连接成功",Toast.LENGTH_SHORT).show();
                    getConnectedDevice();
                    break;
                case BluetoothTools.READ_FAILED:
                    Toast.makeText(Bluetooth.this,"读取失败",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothTools.WRITE_FAILED:
                    Toast.makeText(Bluetooth.this,"发送失败",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothTools.PIPEI_SUCCESS:
                    Toast.makeText(Bluetooth.this,"正在连接",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothTools.PIPEI_FAILED:
                    Toast.makeText(Bluetooth.this,"连接失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //判断设备是否已经被添加进list中
    private boolean isExist(BluetoothDevice device){
        for (int i = 0;i<devices.size();i++){
            if (device.getAddress().equals(devices.get(i).getAddress()))
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
