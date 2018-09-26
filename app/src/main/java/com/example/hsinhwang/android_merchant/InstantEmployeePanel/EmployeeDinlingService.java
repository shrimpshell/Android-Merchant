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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.ChatMessage;
import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.EmployeeDinling;
import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class EmployeeDinlingService extends AppCompatActivity {
    private static final String TAG = "EmployeeDinling";
    private LocalBroadcastManager broadcastManager;
    RecyclerView rvEmployeeDinling;
    SharedPreferences preferences, type;
    private String employeeName;
    private CommonTask employeeStatus;
    EmployeeDinlingAdapter adapter;
    List<EmployeeDinling> employeeDinlings;
    int idInstantDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dinling_service);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerInstantReceiver();

        rvEmployeeDinling = findViewById(R.id.rvEmployeeDinling);
        rvEmployeeDinling.setLayoutManager(new LinearLayoutManager(this));
        employeeDinlings = getEmployeeDinlingList();
        adapter = new EmployeeDinlingAdapter(this, employeeDinlings);
        rvEmployeeDinling.setAdapter(adapter);

        preferences = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
        employeeName = preferences.getString("email", "");

        Common.connectServer(this, employeeName, "3");

    }

    private List<EmployeeDinling> getEmployeeDinlingList() {
        List<EmployeeDinling> employeeDinlingList = new ArrayList<>();





        return employeeDinlingList;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/InstantServlet";
            List<EmployeeDinling> employeeDinlings = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getEmployeeStatus");
            jsonObject.addProperty("idInstantService", 3);
            String jsonOut = jsonObject.toString();
            employeeStatus = new CommonTask(url, jsonOut);
            try {
                String jsonIn = employeeStatus.execute().get();
                Type listType = new TypeToken<List<EmployeeDinling>>() {
                }.getType();
                employeeDinlings = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (employeeDinlings == null || employeeDinlings.isEmpty()) {
                Common.showToast(this, R.string.msg_NoInstantFound);
            } else {
                rvEmployeeDinling.setAdapter
                        (new EmployeeDinlingAdapter(this, employeeDinlings));
            }

        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }


    }


    private void registerInstantReceiver() {
        IntentFilter dinlingFilter = new IntentFilter("3");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, dinlingFilter);


    }

    public class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            idInstantDetail = chatMessage.getInstantNumber();
            if (idInstantDetail != 0 ) {

                if (Common.networkConnected(EmployeeDinlingService.this)) {
                    String url = Common.URL + "/InstantServlet";
                    List<EmployeeDinling> employeeDinlings = null;
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getEmployeeStatus");
                    jsonObject.addProperty("idInstantService", 3);
                    String jsonOut = jsonObject.toString();
                    employeeStatus = new CommonTask(url, jsonOut);
                    try {
                        String jsonIn = employeeStatus.execute().get();
                        Type listType = new TypeToken<List<EmployeeDinling>>() {
                        }.getType();
                        employeeDinlings = new Gson().fromJson(jsonIn, listType);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (employeeDinlings == null || employeeDinlings.isEmpty()) {
                        Common.showToast(EmployeeDinlingService.this, R.string.msg_NoInstantFound);
                    } else {
                        rvEmployeeDinling.setAdapter(null);
                        rvEmployeeDinling.setAdapter
                                (new EmployeeDinlingAdapter(EmployeeDinlingService.this, employeeDinlings));
                    }

                } else {
                    Common.showToast(EmployeeDinlingService.this, R.string.msg_NoNetwork);
                }

                rvEmployeeDinling.getAdapter().notifyDataSetChanged();

            }


        }


    }


    private class EmployeeDinlingAdapter extends
            RecyclerView.Adapter<EmployeeDinlingAdapter.MyViewHolder> {
        Context context;
        List<EmployeeDinling> employeeDinlings;

        EmployeeDinlingAdapter(Context context, List<EmployeeDinling> employeeDinlings) {
            this.context = context;
            this.employeeDinlings = employeeDinlings;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvRoomId, tvItem, tvQuantity, tvStatusNumber;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.ivEmployeeDinling);
                tvRoomId = itemView.findViewById(R.id.tvEmployeeDinlingRoomId);
                tvItem = itemView.findViewById(R.id.tvEmployeeDinlingItem);
                tvQuantity = itemView.findViewById(R.id.tvEmployeeDinlingQuantity);
                tvStatusNumber = itemView.findViewById(R.id.tvEmployeeDinlingStatus);


            }
        }

        @Override
        public int getItemCount() {
            return employeeDinlings.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int typeView) {
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.item_status_employee_instant_dinling,
                            viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final EmployeeDinling employeeDinling = employeeDinlings.get(position);

            switch (employeeDinling.getStatus()) {
                case 1:
                    myViewHolder.imageView.setImageResource(R.drawable.icon_unfinish);
                    break;
                case 2:
                    myViewHolder.imageView.setImageResource(R.drawable.icon_playing);
                    break;
                case 3:
                    myViewHolder.imageView.setImageResource(R.drawable.icon_finish);
                    break;
                default:
                    break;
            }

            switch (employeeDinling.getIdInstantType()) {
                case 1:
                    myViewHolder.tvItem.setText(R.string.service_type_1);
                    break;
                case 2:
                    myViewHolder.tvItem.setText(R.string.service_type_2);
                    break;
                case 3:
                    myViewHolder.tvItem.setText(R.string.service_type_3);
                    break;
                default:
                    break;
            }


            myViewHolder.tvRoomId.setText(employeeDinling.getRoomNumber());
            myViewHolder.tvQuantity.setText(String.valueOf(employeeDinling.getQuantity()));


            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int status = 0;
                    String roomNumber = employeeDinling.getRoomNumber();
                    idInstantDetail = employeeDinling.getIdInstantDetail();
                    if (employeeDinling.getStatus() == 1) {

                        status = 2;

                    } else if (employeeDinling.getStatus() == 2) {

                        status = 3;

                    }
                    if (Common.networkConnected(EmployeeDinlingService.this)) {
                        String url = Common.URL + "/InstantServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "updateStatus");
                        jsonObject.addProperty("idInstantDetail", new Gson().toJson(idInstantDetail));
                        jsonObject.addProperty("status", new Gson().toJson(status));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(EmployeeDinlingService.this, R.string.msg_UpdateFail);
                        } else {
                            Common.showToast(EmployeeDinlingService.this, R.string.msg_UpdateSuccess);
                        }
                    } else {
                        Common.showToast(EmployeeDinlingService.this, R.string.msg_NoNetwork);
                    }

                    if (Common.networkConnected(EmployeeDinlingService.this)) {
                        String url = Common.URL + "/InstantServlet";
                        List<EmployeeDinling> employeeDinlings = null;
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "getEmployeeStatus");
                        jsonObject.addProperty("idInstantService", 3);
                        String jsonOut = jsonObject.toString();
                        employeeStatus = new CommonTask(url, jsonOut);
                        try {
                            String jsonIn = employeeStatus.execute().get();
                            Type listType = new TypeToken<List<EmployeeDinling>>() {
                            }.getType();
                            employeeDinlings = new Gson().fromJson(jsonIn, listType);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (employeeDinlings == null || employeeDinlings.isEmpty()) {
                            Common.showToast(EmployeeDinlingService.this, R.string.msg_NoInstantFound);
                        } else {
                            rvEmployeeDinling.setAdapter(null);
                            rvEmployeeDinling.setAdapter
                                    (new EmployeeDinlingAdapter(EmployeeDinlingService.this, employeeDinlings));
                        }

                    } else {
                        Common.showToast(EmployeeDinlingService.this, R.string.msg_NoNetwork);
                    }

                    ChatMessage chatMessage =
                            new ChatMessage(employeeName, roomNumber, "3",
                                    "0", 3, idInstantDetail);
                    String chatMessageJson = new Gson().toJson(chatMessage);
                    Common.chatwebSocketClient.send(chatMessageJson);
                    Log.d(TAG, "output: " + chatMessageJson);

                    adapter.notifyDataSetChanged();

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
