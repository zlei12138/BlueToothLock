//导包
package com.example.zlei1;

//导入类
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by zlei1 on 2018/5/24.
 */
//操作蓝牙子线程；
public class BTThread extends Thread{
    private BluetoothDevice device=null;
    BluetoothAdapter bluetoothAdapter = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //说明蓝牙的作用；
    private BluetoothSocket bluetoothSocket;
    private String TAG = "BT";
    private OutputStream outputStream;
    private MainActivity mainActivity;

    @Override
    public void run() {
        super.run();
        if(device!=null && bluetoothAdapter!=null){
            try {
                mainActivity.showToast("正在连接");
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);//返回蓝牙套接字
                bluetoothAdapter.cancelDiscovery();//取消搜索，保险，确定取消搜索后连接
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                mainActivity.showToast("成功连接");
                Log.e(TAG, "run: 已连接" );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "run: 没连上" );
                mainActivity.showToast("连接失败，请重试");
            }
        }

    }
//断开连接
    public void cancelConnect(){
        try {
            if (bluetoothSocket != null){
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {

        }
//        stop();
    }
//传输具体内容
    public void setDeviceAndAdapter(BluetoothDevice device, BluetoothAdapter bluetoothAdapter,MainActivity mainActivity) {
        this.device = device;
        this.bluetoothAdapter = bluetoothAdapter;
        this.mainActivity = mainActivity;
    }

    public void sendData(String order) {
        if (outputStream != null){
            try {
                outputStream.write(order.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.e(TAG, "sendData: 还没有连接" );
        }
    }
}
