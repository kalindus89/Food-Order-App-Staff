package com.foodorderappstaff;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {

    public void setUserName(Context con, String number, String name, String login) {
        SharedPreferences.Editor editor = con.getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("phone", number);
        editor.putString("name", name);
        editor.putString("login", login);
        editor.apply();
    }

    public String getPhone(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);
        return prefs.getString("phone", "No name defined");
    }

    public String getName(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);
        return prefs.getString("name", "No name defined");
    }

    public boolean isLogin(Context con) {
        SharedPreferences prefs = con.getSharedPreferences("userDetails", MODE_PRIVATE);

        if (prefs.getString("login", "No name defined").equals("yes")) {
            return true;
        } else {
            return false;
        }
    }
}
