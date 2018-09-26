package com.example.hsinhwang.android_merchant.InstantEmployeePanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


import com.example.hsinhwang.android_merchant.Classes.EmployeeCall;
import com.example.hsinhwang.android_merchant.R;

import java.util.ArrayList;
import java.util.List;

public class EmployeeCallService extends AppCompatActivity {
    private static final String TAG = "EmployeeCall";
    private LocalBroadcastManager broadcastManager;
    private RecyclerView rvEmployeeCall;
    private List<EmployeeCall> employeeCallList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_call_service);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerInstantReceiver();
        rvEmployeeCall = findViewById(R.id.rvEmployeeCall);
        rvEmployeeCall.setLayoutManager(new LinearLayoutManager(this));
        employeeCallList = new ArrayList<>();
        rvEmployeeCall.setAdapter(new EmployeeCallAdapter(this, employeeCallList));





    }


    private void registerInstantReceiver() {
        IntentFilter instantFilter = new IntentFilter("Call");
        InstantReceiver instantReceiver = new InstantReceiver();
        broadcastManager.registerReceiver(instantReceiver, instantFilter);

    }

    private class InstantReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String message = intent.getStringExtra("message");
//            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
//            String sender = chatMessage.getSender();
//
//            employeeCallList.add(new EmployeeCall(R.drawable.icon_playing,sender));
//
//            rvEmployeeCall.getAdapter().notifyItemInserted(employeeCallList.size());
//            rvEmployeeCall.getAdapter().notifyDataSetChanged();






        }
    }


    private class EmployeeCallAdapter extends RecyclerView.Adapter<EmployeeCallAdapter.MyViewHolder> {
        Context context;
        List<EmployeeCall> employeeCallList;

        public EmployeeCallAdapter(Context context, List<EmployeeCall> employeeCallList) {
            this.context = context;
            this.employeeCallList = employeeCallList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvRoomId;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.ivEmployeeCall);
                tvRoomId = itemView.findViewById(R.id.tvEmployeeCallRoomId);
            }
        }

        @Override
        public int getItemCount() {
            return employeeCallList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.item_status_employee_instant_call, viewGroup, false);

            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final EmployeeCall employeeCall = employeeCallList.get(position);

            myViewHolder.imageView.setImageResource(employeeCall.getImageStatus());
            myViewHolder.tvRoomId.setText(employeeCall.getTvRoomID());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });


        }


    }


}
