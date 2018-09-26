package com.example.hsinhwang.android_merchant.EmployeePanel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.Employees;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EmployeeEditActivity extends AppCompatActivity {
    private final static String TAG = "EmployeeEditActivity";
    private TextView employeeEditId, employeeEditName, employeeEditGender, employeeEditEmail, employeeEditDept;
    private EditText employeeEditPassword, employeeEditPhone, employeeEditAddress;
    private Button employeeEditSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_edit);
        handleViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadInfo();
    }

    private void handleViews() {
        employeeEditId = findViewById(R.id.employeeEditId);
        employeeEditName = findViewById(R.id.employeeEditName);
        employeeEditGender = findViewById(R.id.employeeEditGender);
        employeeEditEmail = findViewById(R.id.employeeEditEmail);
        employeeEditDept = findViewById(R.id.employeeEditDept);
        employeeEditPassword = findViewById(R.id.employeeEditPassword);
        employeeEditPhone = findViewById(R.id.employeeEditPhone);
        employeeEditAddress = findViewById(R.id.employeeEditAddress);
        employeeEditSubmit = findViewById(R.id.employeeEditSubmit);
    }

    private void loadInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final Object obj = bundle.getSerializable("employee");
            if (obj != null) {
                final Employees employee = (Employees) obj;
                employeeEditId.setText(getText(R.string.employeeEditId).toString() + ":" + employee.getId());
                employeeEditName.setText(getText(R.string.employeeEditName).toString() + ":" + employee.getName());
                employeeEditGender.setText(getText(R.string.employeeEditGender).toString() + ":" + employee.getGender());
                employeeEditEmail.setText(getText(R.string.employeeEditEmail).toString() + ":" + employee.getEmail());
                switch (employee.getDepartmentId()) {
                    case 1:
                        employeeEditDept.setText(getText(R.string.employeeEditDept).toString() + ":清潔部門");
                        break;
                    case 2:
                        employeeEditDept.setText(getText(R.string.employeeEditDept).toString() + ":房務部門");
                        break;
                    case 3:
                        employeeEditDept.setText(getText(R.string.employeeEditDept).toString() + ":餐飲部門");
                        break;
                    case 4:
                        employeeEditDept.setText(getText(R.string.employeeEditDept).toString() + ":櫃檯部門");
                        break;
                    case 5:
                        employeeEditDept.setText(getText(R.string.employeeEditDept).toString() + ":主管");
                        break;
                }
                employeeEditPassword.setText(employee.getPassword());
                employeeEditPhone.setText(employee.getPhone());
                employeeEditAddress.setText(employee.getAddress());
                employeeEditSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = employeeEditPassword.getText().toString();
                        String phone = employeeEditPhone.getText().toString();
                        String address = employeeEditAddress.getText().toString();
                        Employees emp = new Employees(employee.getId(), employee.getCode(), employee.getName(),
                                password, employee.getEmail(), employee.getGender(), phone,
                                address, employee.getDepartmentId());
                        if (Common.networkConnected(EmployeeEditActivity.this)) {
                            String url = Common.URL + "/EmployeeServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "employeeUpdate");
                            jsonObject.addProperty("employee", new Gson().toJson(emp));
                            jsonObject.addProperty("imageBase64", "");
                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);
                                Log.e(TAG, String.valueOf(count));
                                finish();
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(EmployeeEditActivity.this, R.string.msg_UpdateFail);
                            } else {
                                Common.showToast(EmployeeEditActivity.this, R.string.msg_UpdateSuccess);
                            }
                        } else {
                            Common.showToast(EmployeeEditActivity.this, R.string.msg_NoNetwork);
                        }
                    }
                });
            }
        }
    }
}
