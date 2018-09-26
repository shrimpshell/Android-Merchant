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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Employees;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EmployeeFragment extends Fragment {
    private final static String TAG = "EmployeeFragment";
    private SwipeRefreshLayout swiperefreshlayout;
    private RecyclerView employeeFragmentRecyclerView;
    private RelativeLayout employeeLayout;
    private ImageView employeeImageView;
    private TextView employeeName;
    private FloatingActionButton toAddEmployee;
    private CommonTask employeeGetAllTask, employeeDeleteTask;
    private FragmentActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllEmployees();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        employeeFragmentRecyclerView = view.findViewById(R.id.employeeFragmentRecyclerView);
        swiperefreshlayout = view.findViewById(R.id.swiperefreshlayout);
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiperefreshlayout.setRefreshing(true);
                showAllEmployees();
                swiperefreshlayout.setRefreshing(false);
            }
        });
        toAddEmployee = view.findViewById(R.id.toAddEmployee);
            toAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });
            employeeFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            return view;
    }

    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
        private LayoutInflater inflater;
        private List<Employees> innerEmployeeList;

        public EmployeeAdapter(Context context, List<Employees> innerEmployeeList) {
            this.inflater = LayoutInflater.from(context);
            this.innerEmployeeList = innerEmployeeList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_employee, viewGroup, false);
            return new EmployeeAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final Employees employee = innerEmployeeList.get(i);
            employeeName.setText(employee.getName());
            employeeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ManagerEmployeeEditActivity.class);
                    Bundle bundle = new Bundle();
                    Log.d(TAG, "" + employee.getId()+employee.getCode()+employee.getName()+employee.getPassword()+employee.getEmail()+employee.getGender()+employee.getPhone()+employee.getAddress()+employee.getDepartmentId());
                    Employees innerEmployee = new Employees(employee.getId(), employee.getCode(), employee.getName(), employee.getPassword(), employee.getEmail(), employee.getGender(), employee.getPhone(), employee.getAddress(), employee.getDepartmentId());
                    bundle.putSerializable("employee", innerEmployee);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            employeeLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        String url = Common.URL + "/EmployeeServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "employeeRemove");
                                        jsonObject.addProperty("employee", new Gson().toJson(employee));
                                        int count = 0;
                                        try {
                                            employeeDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = employeeDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, R.string.msg_DeleteFail);
                                        } else {
                                            innerEmployeeList.remove(employee);
                                            swiperefreshlayout.setRefreshing(true);
                                            employeeFragmentRecyclerView.setAdapter(null);
                                            showAllEmployees();
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
            return innerEmployeeList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                employeeLayout = itemView.findViewById(R.id.employeeLayout);
                employeeImageView = itemView.findViewById(R.id.employeeImageView);
                employeeName = itemView.findViewById(R.id.employeeName);
            }
        }
    }

    private void showAllEmployees() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/EmployeeServlet";
            List<Employees> employees = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            employeeGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = employeeGetAllTask.execute().get();
                Type listType = new TypeToken<List<Employees>>() {
                }.getType();
                employees = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (employees == null || employees.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoEmployeesFound);
            } else {
                employeeFragmentRecyclerView.setAdapter(new EmployeeAdapter(activity, employees));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }

}

