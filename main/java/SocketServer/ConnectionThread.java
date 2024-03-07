package SocketServer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private String ip="192.168.137.1";
    private int port =8090;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String recv;
    private String send = "";
    private Handler mHandler;

    public ConnectionThread(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip,port);
            //获取socket的输入输出流
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            outputStream.write(send.getBytes());
            byte[] bytes = new byte[2048];
            int n = inputStream.read(bytes);
            Log.d("socketN",""+n);
            if (n > 0){
                recv=new String(bytes, 0, n,"utf-8");
                Bundle b =new Bundle();
                b.putString("data",recv);
                Message msg = new Message();
                msg.setData(b);
                mHandler.sendMessage(msg);
                Log.d("socket",recv);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setString(String data){
        send = data;
    }
}
