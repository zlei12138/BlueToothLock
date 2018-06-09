package com.example.zlei1;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zlei1 on 2018/5/24.
 */


//
public class RecycAdapter extends RecyclerView.Adapter<RecycHolder> {

    private Context context;
    private List<BluetoothDevice> data;

    @Override
    //绑定界面跟条目
    public RecycHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item, null);
        return new RecycHolder(view);
    }

    //调用构造方法
    public RecycAdapter(List<BluetoothDevice> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    //显示数据名称、地址
    public void onBindViewHolder(final RecycHolder holder, int position) {
        if (data != null) {
            holder.textView.setText(data.get(position).getName());
            holder.bt_address.setText(data.get(position).getAddress());
        } else {
            holder.textView.setText("");
            holder.bt_address.setText("");
        }

        //实现条目点击事件
        View itemView = ((RelativeLayout) holder.itemView).getChildAt(0);
        if (mOnItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

    }

    @Override
    //条目数量
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }

    }

    //传输新数据
    public void setData(List data) {
        this.data = data;
    }

    public List<BluetoothDevice> getData(){
        return this.data;
    }
    private OnItemClickListener mOnItemClickListener;//声明接口

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}

