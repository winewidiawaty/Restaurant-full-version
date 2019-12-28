package com.restaurant.wine.activity.auth;

import android.content.Intent;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.restaurant.wine.R;
import com.restaurant.wine.activity.MainActivity;
import com.restaurant.wine.data.Session;
import com.restaurant.wine.model.login.LoginResponse;
import com.restaurant.wine.model.login.User;
import com.restaurant.wine.util.CommonUtil;
import com.restaurant.wine.util.DialogUtils;

import static com.restaurant.wine.data.Constant.LOGIN;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    TextInputEditText etEmail;
    @BindView(R.id.et_password)
    TextInputEditText etPassword;

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        session = new Session(this);

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                checkValidation();
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    private void checkValidation() {
        ArrayList<View> list = new ArrayList<>();
        list.add(etEmail);
        list.add(etPassword);
        if (CommonUtil.validateEmptyEntries(list)) {
            login();
        }
    }

    private void login() {
        DialogUtils.openDialog(this);

        AndroidNetworking.post(LOGIN)
                .addHeaders("X-Requested-With", "XMLHttpRequest")
                .addBodyParameter("userid", etEmail.getText().toString())
                .addBodyParameter("password", etPassword.getText().toString())
                .build()
                .getAsObject(LoginResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof LoginResponse) {
                            LoginResponse response1 = (LoginResponse) response;
                            DialogUtils.closeDialog();
                            if (response1.getStatus().equalsIgnoreCase("success")) {
                                User user = response1.getUser();

                                session.createLoginSession(user);

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginActivity.this, "Email dan password tidak sesuai !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        DialogUtils.closeDialog();
                        Toast.makeText(LoginActivity.this, "Email dan password tidak sesuai !", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
