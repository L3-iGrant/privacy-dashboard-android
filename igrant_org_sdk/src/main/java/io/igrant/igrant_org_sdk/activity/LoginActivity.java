package io.igrant.igrant_org_sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.customViews.LoginEditText;
import io.igrant.igrant_org_sdk.OrganizationDetailActivity;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.models.Login.LoginRequest;
import io.igrant.igrant_org_sdk.models.Login.LoginResponse;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.MessageUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import io.igrant.igrant_org_sdk.utils.TextUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JMAM on 8/15/18.
 */

public class LoginActivity extends AppCompatActivity {

    private CustomTextView tvRevealPassword;
    private CustomTextView btnLogin;
    private LoginEditText letPassword;
    private LoginEditText letEmail;
    private Toolbar toolbar;
    private CustomTextView tvAction;
    private LinearLayout llProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
//        letEmail.setError("helll");
        clickListener();
    }

    private void initViews() {
        tvRevealPassword = findViewById(R.id.tvRevealPassword);
        btnLogin = findViewById(R.id.btnLogin);
        letPassword = findViewById(R.id.letPassword);
        letEmail = findViewById(R.id.letEmail);
        tvAction = findViewById(R.id.tvAction);
        llProgressBar = findViewById(R.id.llProgressBar);
    }

    private void clickListener() {
        /**
         * Reveal password action
         */
        tvRevealPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealPasswordAction();
            }
        });

        /**
         * forgot password action
         */
//        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forgotPasswordAction();
//            }
//        });

        /**
         * login action
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFunction();
            }
        });

        /**
         * login action
         */
        letPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginFunction();
                    return true;
                }
                return false;
            }
        });

        /**
         * reveal password
         */
        letPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                letPassword.setError("");
                tvRevealPassword.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvAction.setVisibility(View.VISIBLE);
        tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void revealPasswordAction() {
        if (tvRevealPassword.getText().toString().equalsIgnoreCase(getResources().getString(R.string.txt_login_show_password))) {
            letPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            tvRevealPassword.setText(getResources().getString(R.string.txt_login_hide_password));
        } else {
            letPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            tvRevealPassword.setText(getResources().getString(R.string.txt_login_show_password));
        }
    }

//    private void forgotPasswordAction() {
//        //todo forgot password action
//
//        Intent landingIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//        startActivity(landingIntent);
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//    }

    private void loginFunction() {
        if (NetWorkUtil.isConnectedToInternet(LoginActivity.this)) {
            if (validate()) {
                //todo login function

                llProgressBar.setVisibility(View.VISIBLE);
                LoginRequest request = new LoginRequest();
                request.setUsername(letEmail.getText().toString());
                request.setPassword(letPassword.getText().toString());

                Callback<LoginResponse> callback = new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        llProgressBar.setVisibility(View.GONE);

                        if (response.code() == 200) {

                            try {
                                DataUtils.saveStringValues(LoginActivity.this, DataUtils.EXTRA_TAG_USERID, response.body().getUser().getID());
                                DataUtils.saveStringValues(LoginActivity.this, DataUtils.EXTRA_TAG_TOKEN, response.body().getToken().getAccess_token());
//                                DataUtils.saveStringValues(LoginActivity.this,DataUtils.EXTRA_TAG_EXPIRES_IN,response.body().getToken().getExpires_in());
                                ApiManager.resetApi();
//                                registerDevice();
//                                Log.d("milna", "onResponse: "+FirebaseInstanceId.getInstance().getToken());
//                                registerFirbaseToken(FirebaseInstanceId.getInstance().getToken());
                                Intent loginIntent = new Intent(LoginActivity.this, OrganizationDetailActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                LoginActivity.this.finish();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else if (response.code() == 401) {
//                            letPassword.setError(getResources().getString(R.string.err_invalid_email_or_password));
                            MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_invalid_email_or_password));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        llProgressBar.setVisibility(View.GONE);
                    }
                };
                ApiManager.getApi("",
                        DataUtils.getStringValue(LoginActivity.this, DataUtils.EXTRA_TAG_BASE_URL)).getService().loginService(request).enqueue(callback);
            }
        } else {
            //todo internet error
        }
    }

//    private void registerFirbaseToken(String token) {
//        Callback<ResultResponse> callback = new Callback<ResultResponse>() {
//            @Override
//            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResultResponse> call, Throwable t) {
//
//            }
//        };
//        ApiManager.getApi().getService().registerDevice(new RegisterDeviceRequest(token)).enqueue(callback);
//    }

//    private void registerDevice() {
//        if (NetWorkUtil.isConnectedToInternet(LoginActivity.this)) {
//            RegisterDeviceRequest registerDeviceRequest = new RegisterDeviceRequest(FirebaseInstanceId.getInstance().getToken());
//            Callback<ResultResponse> callback = new Callback<ResultResponse>() {
//                @Override
//                public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
//
//                }
//
//                @Override
//                public void onFailure(Call<ResultResponse> call, Throwable t) {
//
//                }
//            };
//            ApiManager.getApi().getService().registerDevice(registerDeviceRequest).enqueue(callback);
//        }
//    }

    private boolean validate() {
        if (!TextUtils.isValidEmail(letEmail.getText().toString())) {
//            letEmail.setError(getResources().getString(R.string.err_invalid_email));
            MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_invalid_email));
            return false;
        } else if (letPassword.getText().toString().isEmpty()) {
//            letPassword.setError(getResources().getString(R.string.err_empty_password));
            MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_empty_password));
            return false;
        }
        return true;
    }
}
