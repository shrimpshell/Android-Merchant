package com.example.hsinhwang.android_merchant.Authentication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hsinhwang.android_merchant.Classes.Common;
import com.example.hsinhwang.android_merchant.Classes.CommonTask;
import com.example.hsinhwang.android_merchant.Classes.LogIn;
import com.example.hsinhwang.android_merchant.EmployeePanel.EmployeeHomeActivity;
import com.example.hsinhwang.android_merchant.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginDialogActivity";
    private Window window;
    private EditText etEmail;
    private EditText etPassword;
    private Context context;
    private Button btLogIn, btJoin;
    private CommonTask loginTask;
    private RadioGroup rgLogin;
    private RadioButton rbEmployee;
    int IdEmployee = 0;
    String idEmployee = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setResult(RESULT_CANCELED);
        initialization();
        btLogIn.setOnClickListener(btLogInListener);

        rbEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btJoin.setEnabled(false);
            }
        });
    }

    //LogIn Button
    private Button.OnClickListener btLogInListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            int selectedRole = rgLogin.getCheckedRadioButtonId();
            SharedPreferences preferences = getSharedPreferences(Common.EMPLOYEE_LOGIN, MODE_PRIVATE);
            switch (selectedRole) {
                case R.id.rbEmployee:
                    if (LogIn.EmployeeLogIn(LoginActivity.this, email, password)) {
                        IdEmployee = LogIn.employeeIsValid(LoginActivity.this, email, password);

                        SharedPreferences page = getSharedPreferences(Common.PAGE, MODE_PRIVATE);
                        preferences.edit().putBoolean("login", true)
                                .putString("email", email)
                                .putString("password", password)
                                .putInt("IdEmployee", IdEmployee)
                                .apply();
                        page.edit().putInt("page", 3).apply();
                        Intent intent = new Intent(LoginActivity.this, EmployeeHomeActivity.class);
                        startActivity(intent);
                    } else {
                        preferences.edit().putBoolean("login", false)
                                .putString("email", "")
                                .putString("password", "")
                                .putInt("IdEmployee", 0)
                                .apply();
                        new AlertDialog.Builder(context)
                                .setTitle("SS Hotel")
                                .setMessage("登入失敗")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                    break;
            }

        }
    };


    private void initialization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.setStatusBarColor(Color.parseColor("#01728B"));
        }
    }

    private void findViews() {

        rbEmployee = findViewById(R.id.rbEmployee);
        btLogIn = (Button) findViewById(R.id.btLogIn);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogIn.setOnClickListener(btLogInListener);
        rgLogin = (RadioGroup) findViewById(R.id.rgLogin);
        context = LoginActivity.this;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (loginTask != null) {
            loginTask.cancel(true);
            loginTask = null;
        }
    }

    //登入失敗警示訊息
    private void showMessage() {
        new AlertDialog.Builder(context)
                .setTitle("SS Hotel")
                .setMessage("登入失敗")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int count = getSupportFragmentManager().getBackStackEntryCount();//取得裡面存了幾個歷史紀錄
        if (keyCode == KeyEvent.KEYCODE_BACK && count == 0) {
            new AlertDialogFragment().show(getSupportFragmentManager(), "exit");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class AlertDialogFragment
            extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new android.app.AlertDialog.Builder(getContext())
                    .setTitle(R.string.text_Exit)
                    .setIcon(R.drawable.ic_alert)
                    .setMessage(R.string.msg_WantToExit)
                    .setPositiveButton(R.string.text_Yes, this)
                    .setNegativeButton(R.string.text_No, this)
                    .create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                    break;
                default:
                    dialog.cancel();
                    break;
            }
        }
    }

}

