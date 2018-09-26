package com.example.hsinhwang.android_merchant.Classes;

import android.app.Activity;
import android.util.Log;

import com.example.hsinhwang.android_merchant.R;
import com.google.gson.JsonObject;

public class LogIn {
    private final static String TAG = "LogIn";
    private static boolean isLogInCustomer = false;
    private static boolean isLogInEmployee = false;
    private static int isValid = 0;
    private static CommonTask loginGetAllTask;

    //消費端登入
    public static boolean CustomerLogIn(Activity activity, String email, String password) {
        if (userExist(activity, email) && isValid(activity, email, password) > 0) {
            isLogInCustomer = true;
            isLogInEmployee = false;
        } else if (!userExist(activity, email)) {
            Common.showToast(activity, "User does not exist");
        } else if (isValid(activity, email, password) <= 0) {
            Common.showToast(activity, "Invalid username or password");
        }
        return isLogInCustomer;
    }

    //商家端登入
    public static boolean EmployeeLogIn(Activity activity, String email, String password) {
        if (employeeExist(activity, email) && employeeIsValid(activity, email, password) > 0) {
            isLogInEmployee = true;
            isLogInCustomer = false;
        } else if (!employeeExist(activity, email)){
            Common.showToast(activity, "User does not exist");
        } else if (!(employeeIsValid(activity, email, password) > 0)) {
            Common.showToast(activity, "Invalid username or password");
        }
        return isLogInEmployee;
    }

    //判斷該員工是否可以登入
    private static boolean employeeExist(Activity activity, String email) {
        boolean doesExist = false;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/EmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userExist");
            jsonObject.addProperty("email", email);
            String jsonOut = jsonObject.toString();
            loginGetAllTask = new CommonTask(url, jsonOut);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                doesExist = Boolean.valueOf(result);
                return doesExist;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
        return doesExist;
    }

    //檢查員工登入帳密
    public static int employeeIsValid(Activity activity, String email, String password) {
        int idEmployee = 0;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/EmployeeServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "employeeValid");
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("password", password);
            String jsonOut = jsonObject.toString();
            loginGetAllTask = new CommonTask(url, jsonOut);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                idEmployee = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
        return idEmployee;
    }

    //檢查消費者email是否已存在
    private static boolean userExist(Activity activity, String email) {
        boolean doesExist = false;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userExist");
            jsonObject.addProperty("email", email);
            String jsonOut = jsonObject.toString();
            loginGetAllTask = new CommonTask(url, jsonOut);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                doesExist = Boolean.valueOf(result);
                return doesExist;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
        return doesExist;
    }


    //檢查消費者登入帳密
    public static int isValid(Activity activity, String email, String password) {
        int idCustomer = 0;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userValid");
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("password", password);
            String jsonOut = jsonObject.toString();
            loginGetAllTask = new CommonTask(url, jsonOut);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                idCustomer = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
        return idCustomer;
    }
}
