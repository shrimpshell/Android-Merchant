package com.example.hsinhwang.android_merchant.InstantEmployeePanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hsinhwang.android_merchant.Classes.ChatMessage;
import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.EmployeeClean;
import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EmployeeCleanService extends AppCompatActivity {
    private static final String TAG = "EmployeeClean";
    private LocalBroadcastManager broadcastManager;
    RecyclerView rvEmployeeClean;
    List<EmployeeClean> employeeCleanList;
    SharedPreferences preferences;
    private String employeeName;
    EmployeeCleanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_clean_service);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerInstantReceiver();

        rvEmployeeClean = findViewById(R.id.rvEmployeeClean);
        rvEmployeeClean.setLayoutManager(new LinearLayoutManager(this));
        employeeCleanList = getEmployeeCleanList();

        adapter = new EmployeeCleanAdapter(this, employeeCleanList);
        rvEmployeeClean.setAdapter(adapter);

        preferences = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
        employeeName = preferences.getString("email", "");

        Common.connectServer(this, employeeName, "1");

    }
    private List<EmployeeClean> getEmployeeCleanList() {
        List<EmployeeClean> employeeCleanList = new ArrayList<>();


        return employeeCleanList;
    }

    private void registerInstantReceiver() {
        IntentFilter cleanFilter = new IntentFilter("1");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, cleanFilter);

    }


    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            String sender = chatMessage.getSenderId();

        }


    }

    private class EmployeeCleanAdapter extends RecyclerView.Adapter<EmployeeCleanAdapter.MyViewHolder> {
        Context context;
        List<EmployeeClean> employeeCleanList;

        EmployeeCleanAdapter(Context context, List<EmployeeClean> employeeCleanList) {
            this.context = context;
            this.employeeCleanList = employeeCleanList;


        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvRoomId, tvStatusNumber;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.ivEmployeeClean);
                tvRoomId = itemView.findViewById(R.id.tvEmployeeCleanRoomId);
                tvStatusNumber = itemView.findViewById(R.id.tvEmployeeCleanStatus);

            }
        }

        @Override
        public int getItemCount() {
            return employeeCleanList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.item_status_employee_instant_clean, viewGroup, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final EmployeeClean employeeClean = employeeCleanList.get(position);

            myViewHolder.imageView.setImageResource(employeeClean.getImageStatus());
            myViewHolder.tvRoomId.setText(employeeClean.getTvRooId());
            myViewHolder.tvStatusNumber.setText(employeeClean.getTvStatusNumber());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.disconnectServer();
    }
}
