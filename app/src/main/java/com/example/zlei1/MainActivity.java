package com.example.zlei1;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static String BT_PASSWORD = "123456";//设置初始密码
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private RecycAdapter recycAdapter;
    BTThread btThread;
    String order = "a";
    private TextView tv_showdata;
    private Switch lock_switch;
    private SwipeRefreshLayout refresh;//刷新


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //加载页面
        setContentView(R.layout.activity_main);

        //获取页面控件
        refresh = findViewById(R.id.refresh);
        RecyclerView recycList = findViewById(R.id.recycList);
        tv_showdata = findViewById(R.id.tv_showdata);
        lock_switch = findViewById(R.id.lock_switch);

        //设置控件可视化
        refresh.setVisibility(View.VISIBLE);
        tv_showdata.setVisibility(View.GONE);
        lock_switch.setVisibility(View.GONE);

        //设置表格格式
        recycList.setLayoutManager(new LinearLayoutManager(this));
        recycAdapter = new RecycAdapter(null, MainActivity.this);
        recycList.setAdapter(recycAdapter);


        /**
         * 搜索新蓝牙
         */
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2018/5/24  搜索新蓝牙
//                List list = new ArrayList();
//                list.add("111");
//                list.add("222" + Math.random());
//                recycAdapter.setData(list);
//                recycAdapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
        });

        //点击事件
        recycAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position) {

                LayoutInflater factory = LayoutInflater.from(MainActivity.this);//提示框
                final View viewEdit = factory.inflate(R.layout.editbox_layout, null);//这里必须是final的
                final EditText edit=(EditText)viewEdit.findViewById(R.id.editText);//获得输入框对象
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("输入密码：")//提示框标题
                        .setView(viewEdit)
                        .setPositiveButton("确定",//提示框的两个按钮
                                //验证密码
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        String password = edit.getText().toString().trim();
                                        if (BT_PASSWORD.equals(password)) {
                                            Toast.makeText(MainActivity.this,"密码正确",Toast.LENGTH_SHORT).show();
                                            //获取设备
                                            List<BluetoothDevice> data = recycAdapter.getData();
                                            BluetoothDevice device = data.get(position);
                                            //      Toast.makeText(MainActivity.this, "dianji" + device.getName() + device.getAddress(), Toast.LENGTH_SHORT).show();

                                            //连接蓝牙
                                            connect(device);
                                        }else {
                                            Toast.makeText(MainActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).setNegativeButton("取消", null).create().show();
            }
        });







        //开关点击事件
        lock_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    order = "a";
                }else {
                    order = "b";
                }
                //显示操作记录
                tv_showdata.setText(tv_showdata.getText()+"\n       "+order);
                if (tv_showdata.getText().length()>125){
                    tv_showdata.setText("");
                }
                btThread.sendData(order);
            }
        });

    }

    //启动连接蓝牙
    private void connect(final BluetoothDevice device) {

//        btThread.cancelConnect();
        btThread = new BTThread();
        btThread.setDeviceAndAdapter(device,bluetoothAdapter,MainActivity.this);
        btThread.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 这条表示加载菜单文件，第一个参数表示通过那个资源文件来创建菜单
        // 第二个表示将菜单传入那个对象中。这里我们用Menu传入menu
        // 这条语句一般系统帮我们创建好
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    // 菜单的监听方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fist:
                //开启或关闭蓝牙
                if (bluetoothAdapter == null) {
                    Toast.makeText(this, "本机没有蓝牙", 0).show();
                } else {
                    //判断蓝牙状态
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    } else {
                        bluetoothAdapter.disable();
                    }
                }
                break;
            case R.id.second:
                //搜索蓝牙

                refresh.setVisibility(View.VISIBLE);
                tv_showdata.setVisibility(View.GONE);
                lock_switch.setVisibility(View.GONE);
                searchBT();
                break;
            case R.id.third:

                //断开连接
                if(btThread !=null){
                    btThread.cancelConnect();
                    btThread = null;
                    Toast.makeText(this, "已断开连接", 0).show();
                }

                break;

            case R.id.forth:
                refresh.setVisibility(View.GONE);
                tv_showdata.setVisibility(View.VISIBLE);
                lock_switch.setVisibility(View.VISIBLE);
                break;

            case R.id.fifth:

                //修改密码
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);//提示框
                final View viewEdit = factory.inflate(R.layout.editbox_layout2, null);//这里必须是final的
                final EditText old_password =(EditText)viewEdit.findViewById(R.id.old_password);//获得输入框对象
                final EditText new_password =(EditText)viewEdit.findViewById(R.id.new_password);//获得输入框对象
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("输入原密码和新密码：")//提示框标题
                        .setView(viewEdit)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //事件
                                        String password_old = old_password.getText().toString().trim();
                                        String password_new = new_password.getText().toString().trim();
                                        if (BT_PASSWORD.equals(password_old)) {
                                            BT_PASSWORD = password_new;
                                            Toast.makeText(MainActivity.this, "密码修改成功", 0).show();
                                        }else {
                                            Toast.makeText(MainActivity.this, "原密码错误", 0).show();
                                        }

                                    }
                                }).setNegativeButton("取消", null).create().show();


                break;
            default:
                break;
        }
        return true;

    }

    /**
     * 搜索蓝牙
     */
    //具体函数
    private void searchBT() {
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : bondedDevices) {
            list.add(device);
        }
        //放数据
        recycAdapter.setData(list);
        //更新界面
        recycAdapter.notifyDataSetChanged();
    }


    //提示框
    public void showToast(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,state,Toast.LENGTH_LONG).show();
            }
        });

    }
}
