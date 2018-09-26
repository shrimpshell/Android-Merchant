package com.example.hsinhwang.android_merchant.ManagerPanel;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Events;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EventFragment extends Fragment {
    private final static String TAG = "EventFragment";
    private SwipeRefreshLayout swiperefreshlayout;
    private RecyclerView eventFragmentRecyclerView;
    private FloatingActionButton toAddEvent;
    private CommonTask eventGetAllTask, eventDeleteTask;
    private FragmentActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        swiperefreshlayout = view.findViewById(R.id.swiperefreshlayout);
        eventFragmentRecyclerView = view.findViewById(R.id.eventFragmentRecyclerView);
        toAddEvent = view.findViewById(R.id.toAddEvent);
        eventFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiperefreshlayout.setRefreshing(true);
                showAllEvents();
                swiperefreshlayout.setRefreshing(false);
            }
        });

        toAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        LayoutInflater inflater;
        List<Events> eventList;
        TextView eventName, eventDetail, itemId;
        RelativeLayout eventItem;

        public EventAdapter(Context context, List<Events> eventList) {
            this.inflater = LayoutInflater.from(context);
            this.eventList = eventList;
        }

        @NonNull
        @Override
        public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = inflater.inflate(R.layout.item_event_room, viewGroup, false);
            return new EventAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull EventAdapter.ViewHolder viewHolder, int i) {
            final Events event = eventList.get(i);
            itemId.setText(String.valueOf(event.getEventId()));
            eventName.setText(event.getName());
            eventDetail.setText(event.getDescription());
            eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ManagerEditActivity.class);
                    Bundle bundle = new Bundle();
                    Events innerEvent = new Events(event.getEventId(), event.getName(), event.getDescription(), event.getStart(), event.getEnd(), event.getDiscount());
                    bundle.putSerializable("event", innerEvent);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            eventItem.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_remove:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL + "/EventServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "eventRemove");
                                        jsonObject.addProperty("event", new Gson().toJson(event));
                                        int count = 0;
                                        try {
                                            eventDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = eventDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, R.string.msg_DeleteFail);
                                        } else {
                                            eventList.remove(event);
                                            swiperefreshlayout.setRefreshing(true);
                                            eventFragmentRecyclerView.setAdapter(null);
                                            showAllEvents();
                                            swiperefreshlayout.setRefreshing(false);
                                            Common.showToast(activity, R.string.msg_DeleteSuccess);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.msg_NoNetwork);
                                    }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemId = itemView.findViewById(R.id.itemId);
                eventName = itemView.findViewById(R.id.itemName);
                eventDetail = itemView.findViewById(R.id.itemDetail);
                eventItem = itemView.findViewById(R.id.itemLayout);
            }
        }
    }

    private void showAllEvents() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/EventServlet";
            List<Events> events = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            eventGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = eventGetAllTask.execute().get();
                Type listType = new TypeToken<List<Events>>() {
                }.getType();
                events = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (events == null || events.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoEventsFound);
            } else {
                eventFragmentRecyclerView.setAdapter(new EventAdapter(activity, events));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

}
