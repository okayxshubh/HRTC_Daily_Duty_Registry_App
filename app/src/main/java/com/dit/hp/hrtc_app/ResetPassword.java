package com.dit.hp.hrtc_app;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.dit.hp.hrtc_app.Asyncs.ShubhAsyncGet;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SuccessResponse;
import com.dit.hp.hrtc_app.Modals.UploadObject;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.crypto.AESCrypto;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerPost;
import com.dit.hp.hrtc_app.json.JsonParse;
import com.dit.hp.hrtc_app.utilities.AppStatus;
import com.dit.hp.hrtc_app.utilities.Econstants;

import org.json.JSONException;

import java.net.URLEncoder;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class ResetPassword extends AppCompatActivity implements ShubhAsyncTaskListenerGet, ShubhAsyncTaskListenerPost {

    CustomDialog CD = new CustomDialog();

    AESCrypto aesCrypto = new AESCrypto();
    Button submitOtpBtn, changeNumber, resendOTP, getOtp, resetPassBtn;
    LinearLayout enterOTPLayout, enterMobileNoLayout, resetPassLayout;
    TextView enteredNumTV;
    CardView backCard;

    EditText newPass1, newPass2;
    String newPass1Str, newPass2Str;

    private CountDownTimer timer;
    TextView countdownTimer;

    EditText[] otpFields = new EditText[6];  // Assuming 6 OTP fields

    EditText mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        enterOTPLayout = findViewById(R.id.enterOTPLayout);
        enterOTPLayout.setVisibility(View.GONE);

        resetPassLayout = findViewById(R.id.resetPassLayout);
        resetPassLayout.setVisibility(View.GONE);

        enterMobileNoLayout = findViewById(R.id.mobileNoLayout);
        enterMobileNoLayout.setVisibility(View.VISIBLE);
        mobileNo = findViewById(R.id.mobileNo);

        submitOtpBtn = findViewById(R.id.submitOtpBtn);
        changeNumber = findViewById(R.id.changeNumberBtn);
        enteredNumTV = findViewById(R.id.enteredNumTV);
        countdownTimer = findViewById(R.id.countdownTimer);
        getOtp = findViewById(R.id.getOtp);
        backCard = findViewById(R.id.backCard);

        // OTP TVs
        otpFields[0] = findViewById(R.id.otp_tv1);
        otpFields[1] = findViewById(R.id.otp_tv2);
        otpFields[2] = findViewById(R.id.otp_tv3);
        otpFields[3] = findViewById(R.id.otp_tv4);
        otpFields[4] = findViewById(R.id.otp_tv5);
        otpFields[5] = findViewById(R.id.otp_tv6);

        newPass1 = findViewById(R.id.newPass1);
        newPass2 = findViewById(R.id.newPass2);
        resetPassBtn = findViewById(R.id.resetPassBtn);

        resendOTP = findViewById(R.id.resendOtpBtn);
        resendOTP.setEnabled(false);
        resendOTP.setVisibility(View.GONE);

        backCard.setOnClickListener(v -> {
            ResetPassword.this.finish();
        });

        // GET OTP BTN
        getOtp.setOnClickListener(v -> {
            if (AppStatus.getInstance(this).isOnline()) {
                if (Econstants.isNotEmpty(mobileNo.getText().toString().trim())) {
                    if (mobileNo.getText().toString().trim().length() == 10) {

                        callOtpService(mobileNo.getText().toString().trim());  // Proceed to OTP service call

                    } else {
                        CD.showDialog(this, "Please enter a valid 10-digit contact number");
                    }
                } else {
                    CD.showDialog(this, "Please enter a contact number");
                }
            } else {
                CD.showDialog(this, "Please connect to the internet");
            }
        });

        // TEXT WATCHER
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i; // Capture the index

            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Move to the next field if a digit is entered
                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        }
                    } else if (s.length() == 0 && before > 0) {
                        // Handle backspace: Move to the previous field
                        if (index > 0) {
                            otpFields[index - 1].requestFocus();
                            otpFields[index - 1].setSelection(otpFields[index - 1].getText().length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            // Handle backspace key event explicitly
            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpFields[index].getText().toString().isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus();
                        otpFields[index - 1].setSelection(otpFields[index - 1].getText().length());
                    }
                }
                return false; // Allow default behavior for other keys
            });
        }


        // Paste OTP
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        otpFields[0].setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && clipboard.hasPrimaryClip() && clipboard.getPrimaryClip().getItemCount() > 0) {
                CharSequence pastedText = clipboard.getPrimaryClip().getItemAt(0).getText();
                if (pastedText != null && pastedText.length() == otpFields.length) {
                    for (int i = 0; i < otpFields.length; i++) {
                        otpFields[i].setText(String.valueOf(pastedText.charAt(i)));
                    }
                }
            }
        });


        // Button click to submit OTP
        submitOtpBtn.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText field : otpFields) {
                otp.append(field.getText().toString());
            }
            String concatenatedOtp = otp.toString();

            if (Econstants.isNotEmpty(concatenatedOtp)) {
                // Verify OTP
                verifyOtpService(mobileNo.getText().toString().trim(), concatenatedOtp);
            } else {
                CD.showDialog(this, "Please enter the received OTP");
            }

        });


        // Change Number Button
        changeNumber.setOnClickListener(v -> {
            mobileNo.setText("");
            enterOTPLayout.setVisibility(View.GONE);
            enterMobileNoLayout.setVisibility(View.VISIBLE);
            resendOTP.setEnabled(false);
            resendOTP.setVisibility(View.GONE);

            // Clear all OTP TVs
            for (EditText field : otpFields) {
                field.setText("");
            }

            // Clear new password fields
            newPass1.setText("");
            newPass2.setText("");
        });


        // PASSWORD CHANGE
        resetPassBtn.setOnClickListener(v -> {
            newPass1Str = newPass1.getText().toString();
            newPass2Str = newPass2.getText().toString();

            if (Econstants.isNotEmpty(newPass1Str)) {
                if (Econstants.isNotEmpty(newPass2Str)) {
                    if (newPass1Str.equals(newPass2Str)) {

                        callChangePasswordService(mobileNo.getText().toString().trim(), newPass2.getText().toString());

                    } else {
                        CD.showDialog(this, "New Password does not match the re-entered password");
                    }
                } else {
                    CD.showDialog(this, "Please re-enter the same password");
                }
            } else {
                CD.showDialog(this, "Please enter a new password");
            }
        });

        // Resend OTP Btn
        resendOTP.setOnClickListener(v -> {
            callOtpService(mobileNo.getText().toString().trim());
            resendOTP.setEnabled(false);
            resendOTP.setVisibility(View.GONE);
            startCountdown();
        });

    }


    // GET OTP Service
    private void callOtpService(String enteredNumber) {
        try {
            enteredNumTV.setText("OTP sent on number: " + enteredNumber);
            if (AppStatus.getInstance(ResetPassword.this).isOnline()) {
                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/login/getOtp"
                        + "?mobileNo="
                        + URLEncoder.encode(aesCrypto.encrypt(enteredNumber), "UTF-8")); // Encrypt the mobile number
                uploadObject.setTasktype(TaskType.GET_OTP);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                new ShubhAsyncGet(ResetPassword.this, ResetPassword.this, TaskType.GET_OTP).execute(uploadObject);

            } else {
                CD.showDialog(ResetPassword.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verify OTP Service
    private void verifyOtpService(String enteredNumber, String entered_otp) {
        try {

            if (AppStatus.getInstance(ResetPassword.this).isOnline()) {
                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/login/validateOtp"
                        + "?mobileNo="
                        + URLEncoder.encode(aesCrypto.encrypt(enteredNumber), "UTF-8")
                        + "&otp="
                        + URLEncoder.encode(aesCrypto.encrypt(entered_otp), "UTF-8")); // Encrypt the mobile number

                uploadObject.setTasktype(TaskType.VERIFY_OTP);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                new ShubhAsyncGet(ResetPassword.this, ResetPassword.this, TaskType.VERIFY_OTP).execute(uploadObject);

            } else {
                CD.showDialog(ResetPassword.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reset Pass Service
    private void callChangePasswordService(String enteredNumber, String newPassword) {
        try {

            if (AppStatus.getInstance(ResetPassword.this).isOnline()) {
                UploadObject uploadObject = new UploadObject();
                uploadObject.setUrl(Econstants.base_url);
                uploadObject.setMethordName("/login/updatePassword"
                        + "?mobile="
                        + URLEncoder.encode(aesCrypto.encrypt(enteredNumber), "UTF-8")
                        + "&password="
                        + URLEncoder.encode(aesCrypto.encrypt(newPassword), "UTF-8")); // Encrypt the password to change

                uploadObject.setTasktype(TaskType.RESET_PASSWORD);
                uploadObject.setAPI_NAME(Econstants.API_NAME_HRTC);
                new ShubhAsyncGet(ResetPassword.this, ResetPassword.this, TaskType.RESET_PASSWORD).execute(uploadObject);

            } else {
                CD.showDialog(ResetPassword.this, "Internet not Available. Please Connect to the Internet and try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Function to start countdown
    private void startCountdown() {
        // Cancel the existing timer if it is already running
        if (timer != null) {
            timer.cancel();
        }

        // Disable the button during countdown
        resendOTP.setEnabled(false);

        // Timer for 1 minute (60000 ms)
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the countdown timer text
                long seconds = millisUntilFinished / 1000;
                countdownTimer.setText(String.format(Locale.getDefault(), "0:%02d", seconds));
            }

            @Override
            public void onFinish() {
                // Enable the button after countdown finishes
                countdownTimer.setText("0:00");
                resendOTP.setEnabled(true);
                resendOTP.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

        // GET OTP
        if (TaskType.GET_OTP == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject);

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        // Successfully OTP Sent
                        CD.showDialog(ResetPassword.this, response.getMessage());
                        startCountdown();
                        enterOTPLayout.setVisibility(View.VISIBLE);
                        enterMobileNoLayout.setVisibility(View.GONE);
                    }

                    // Mobile no not registered
                    else if (response.getStatus().equalsIgnoreCase("BAD_REQUEST")) {
                        CD.showDialog(ResetPassword.this, response.getData());
                    } else {
                        CD.showDialog(ResetPassword.this, response.getMessage());
                        enterOTPLayout.setVisibility(View.GONE);
                        enterMobileNoLayout.setVisibility(View.VISIBLE);
                    }
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_UNAUTHORIZED))) {
                    // Handle HTTP 401 Unauthorized response (session expired)
                    CD.showSessionExpiredDialog(this, "Session Expired. Please login again.");
                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_BAD_REQUEST))) {
                    CD.showDialog(this, "Mobile number not registered");
                } else {
                    CD.showDialog(ResetPassword.this, "Not able to connect to the server");
                    enterOTPLayout.setVisibility(View.GONE);
                    enterMobileNoLayout.setVisibility(View.VISIBLE);
                }
            } else {
                CD.showDialog(ResetPassword.this, "Issue getting OTP Try Again");
            }
        }

        // VERIFY OTP
        if (TaskType.VERIFY_OTP == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject);

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    // OK
                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        // Successfully Verified OTP
                        CD.showDialog(ResetPassword.this, response.getMessage());
                        enterOTPLayout.setVisibility(View.GONE);
                        enterMobileNoLayout.setVisibility(View.GONE);
                        resetPassLayout.setVisibility(View.VISIBLE);
                    }

                    // Wrong OTP
                    else if (response.getStatus().equalsIgnoreCase("BAD_REQUEST")) {
                        CD.showDialog(ResetPassword.this, response.getData());
                    }

                    // any other case
                    else {
                        CD.showDialog(ResetPassword.this, response.getMessage());
                        enterOTPLayout.setVisibility(View.GONE);
                        enterMobileNoLayout.setVisibility(View.VISIBLE);
                    }

                } else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_BAD_REQUEST))) {
                    CD.showDialog(ResetPassword.this, "Invalid OTP");
                }

            } else {
                CD.showDialog(ResetPassword.this, "Not able to connect to the server");
            }
        }

        // VERIFY OTP
        if (TaskType.RESET_PASSWORD == taskType) {
            SuccessResponse response = null;

            if (responseObject != null) {
                Log.i("AddBusDetails", "Response Obj" + responseObject);

                if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_OK))) {
                    response = JsonParse.getDecryptedSuccessResponse(responseObject.getResponse());
                    Log.e("Response", response.toString());
                    Log.e("Response", responseObject.getResponse());

                    if (response.getStatus().equalsIgnoreCase("OK")) {
                        // Successfully Verified OTP
                        CD.showDismissActivityDialog(ResetPassword.this, response.getMessage());
                    }

                    // Mobile no not registered++
                    else if (response.getStatus().equalsIgnoreCase("BAD_REQUEST")) {
                        CD.showDismissActivityDialog(ResetPassword.this, response.getData());
                    } else {
                        CD.showDismissActivityDialog(ResetPassword.this, response.getMessage());
                    }

                }

                // Response 404 on Live.. Trying Without JWT
                else if (responseObject.getResponseCode().equalsIgnoreCase(Integer.toString(HttpsURLConnection.HTTP_NOT_FOUND))) {
                    CD.showDismissActivityDialog(ResetPassword.this, responseObject.getResponseCode() + responseObject.getResponse());
                }

                else{
                    Log.e("Response: ", responseObject.getResponseCode() + responseObject.getResponse());
                    CD.showDismissActivityDialog(ResetPassword.this, "Something went wrong. Please try again");
                }
            } else {
                CD.showDialog(ResetPassword.this, "Could not connect to server");
            }
        }


    }
}