package com.example.zlei1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zlei1 on 2018/5/24.
 */

//具体的类，用于调用
public class RecycHolder extends RecyclerView.ViewHolder {

    TextView textView;
    TextView bt_address;

    public RecycHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.bt_name);
        bt_address = itemView.findViewById(R.id.bt_address);
    }
}