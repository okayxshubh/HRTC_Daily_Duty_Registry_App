package com.dit.hp.hrtc_app.Presentation;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dit.hp.hrtc_app.Adapters.AdditionalChargesListAdapter;
import com.dit.hp.hrtc_app.AddStop;
import com.dit.hp.hrtc_app.AllAddaCards;
import com.dit.hp.hrtc_app.AllConductorsCards;
import com.dit.hp.hrtc_app.AllDepotsCards;
import com.dit.hp.hrtc_app.AllDriversCards;
import com.dit.hp.hrtc_app.AttendanceModule.aadhaar_util.modal.kyc.KycResData;
import com.dit.hp.hrtc_app.AttendanceModule.attendanceModals.AttendanceObject;
import com.dit.hp.hrtc_app.DailyDutyRegisterCards;
import com.dit.hp.hrtc_app.Homescreen;
import com.dit.hp.hrtc_app.LoginHRTC;
import com.dit.hp.hrtc_app.ManageEntities;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.lazyloader.ImageLoader;
import com.dit.hp.hrtc_app.utilities.Econstants;
import com.dit.hp.hrtc_app.utilities.Preferences;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomDialog {

    public void showDialog(final Activity activity, String msg) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_custom);

            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
            text.setMovementMethod(new ScrollingMovementMethod());
            text.setText(msg);

            Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

            dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // activity.finish();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    public void showSessionExpiredDialog(final Activity activity, String msg) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_custom);

            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
            text.setMovementMethod(new ScrollingMovementMethod());
            text.setText(msg);

            Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

            dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(activity, LoginHRTC.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            dialog.show();
        }
    }

    public void addCompleteEntityDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, ManageEntities.class);
                startActivity(activity, intent, null);
            }
        });
        dialog.show();
    }

    public void showAddCompleteDriverDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, AllDriversCards.class);
                startActivity(activity, intent, null);
            }
        });
        dialog.show();
    }

    public void showAddCompleteConductorDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, AllConductorsCards.class);
                startActivity(activity, intent, null);
            }
        });
        dialog.show();
    }

    public void showDepotEditCompleteDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, AllDepotsCards.class);
                startActivity(activity, intent, null);
            }
        });

        dialog.show();

    }

    public void showAddaEditCompleteDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, AllAddaCards.class);
                startActivity(activity, intent, null);
            }
        });

        dialog.show();

    }


    public void showDismissActivityDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });

        dialog.show();

    }

    public void showDownloadStartedDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();


    }

    public void showStopAddedDialog(final Activity activity, String msg, RoutePojo routeBeingEdited) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                Intent refreshIntent = new Intent(activity.getApplicationContext(), AddStop.class);
                refreshIntent.putExtra("routeDetails", routeBeingEdited);
                startActivity(activity, refreshIntent, null);
            }
        });

        dialog.show();

    }


    // Face Auth Attendance Success
    public void showeKYCDataFarmer(final Activity activity, final KycResData kycResData) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom_vahan_member);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.95);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageLoader il = new ImageLoader(activity);


        ImageView image = (ImageView) dialog.findViewById(R.id.profile_image);
        TextView name_aadhaar = (TextView) dialog.findViewById(R.id.name_aadhaar);
        TextView dob_aadhaar = (TextView) dialog.findViewById(R.id.dob_aadhaar);
        TextView gender_aadhaar = (TextView) dialog.findViewById(R.id.gender_aadhaar);
        TextView address_aadhaar = (TextView) dialog.findViewById(R.id.address_aadhaar);


        name_aadhaar.setText(kycResData.getUidData().getPoi().getName());
        dob_aadhaar.setText(kycResData.getUidData().getPoi().getDob());
        gender_aadhaar.setText(kycResData.getUidData().getPoi().getGender());


        StringBuilder SB = new StringBuilder();

        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getCo()) ? kycResData.getUidData().getPoa().getCo() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getHouse()) ? kycResData.getUidData().getPoa().getHouse() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getLoc()) ? kycResData.getUidData().getPoa().getLoc() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getVtc()) ? kycResData.getUidData().getPoa().getVtc() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getSubdist()) ? kycResData.getUidData().getPoa().getSubdist() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getState()) ? kycResData.getUidData().getPoa().getState() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getPc()) ? kycResData.getUidData().getPoa().getPc() : "");
        SB.append(" ,");
        SB.append(Econstants.isNotEmpty(kycResData.getUidData().getPoa().getPo()) ? kycResData.getUidData().getPoa().getPo() : "");
        SB.append(" ,");


        address_aadhaar.setText(SB.toString());

        // Decode the base64 string to a byte array
        byte[] decodedBytes = Base64.decode(kycResData.getUidData().getPht(), Base64.DEFAULT);

        // Create a Bitmap from the byte array
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        // Set the Bitmap to the ImageView
        image.setImageBitmap(bitmap);

        String directoryName = "AadhaarPhotos";
        String photoPath = Econstants.saveBase64ImageToFile(kycResData.getUidData().getPht(), directoryName, kycResData.getAadhaarNumber(), activity);

        kycResData.setAadhaarPhotoPath(photoPath);


        Button dialog_ok = (Button) dialog.findViewById(R.id.ok);


        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void markAttendance(final Activity activity, final AttendanceObject surveyObject) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        System.out.println(surveyObject.getAttendanceObjectJson().toString());
        dialog.setContentView(R.layout.dialog_van_attendance);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.95);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Existing layout sections
        LinearLayout aadhaar = dialog.findViewById(R.id.aadhaar);

        // Consent image
        ImageView consent_iv = dialog.findViewById(R.id.consent_iv);
        //consent_iv.setImageBitmap(BitmapFactory.decodeFile(surveyObject.getAadhaarEkyc().getAadhaarPhotoPath()));

        // Aadhaar photo
        ImageView aadhaar_iv = dialog.findViewById(R.id.aadhaar_iv);
        aadhaar_iv.setImageBitmap(BitmapFactory.decodeFile(surveyObject.getAadhaarEkyc().getAadhaarPhotoPath()));


        // Fields to be controlled dynamically
        RadioGroup punchTypeGroup = dialog.findViewById(R.id.punch_type_group);
        Spinner typeOfWorkSpinner = dialog.findViewById(R.id.type_of_work_spinner);
        EditText remarksEditText = dialog.findViewById(R.id.remarks_edittext);
        TextView remarksLabel = dialog.findViewById(R.id.remarks_label);
        TextView workTypeLabel = dialog.findViewById(R.id.work_type_label);

        // Name and Date display
        TextView nameText = dialog.findViewById(R.id.name_text);
        TextView dateText = dialog.findViewById(R.id.date_text);

        nameText.setText("Name: " + surveyObject.getName()); // assuming getName() exists

        try {
            // Parse the datetime string from surveyObject
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date surveyDate = inputFormat.parse(surveyObject.getDateTime());

            if (surveyDate != null) {
                // Format it to desired output (dd-MM-yyyy HH:mm)
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                dateText.setText("Date and Time of Attendance: " + outputFormat.format(surveyDate));
                dateText.setTextColor(Color.RED);
            } else {
                dateText.setText("Date & Time: -");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            dateText.setText("Date & Time: -");
        }


        punchTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_punch_in) {
                remarksEditText.setVisibility(View.GONE);
                remarksLabel.setVisibility(View.GONE);
                workTypeLabel.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_punch_out) {
                remarksEditText.setVisibility(View.VISIBLE);
                remarksLabel.setVisibility(View.VISIBLE);
                workTypeLabel.setVisibility(View.VISIBLE);
            }
        });

        punchTypeGroup.check(R.id.rb_punch_in);
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(v -> {

            int selectedId = punchTypeGroup.getCheckedRadioButtonId();
            String punchType = selectedId == R.id.rb_punch_in ? "Punch In" : "Punch Out";


            String remarks = remarksEditText.getText().toString();

            if (punchType.equals("Punch Out")) {
                surveyObject.setRemarks(remarks);
                surveyObject.setPunchInOut(punchType);
            } else {
                surveyObject.setWorkDone(null); // clear workTypeId as well
                surveyObject.setRemarks(null);
                surveyObject.setPunchInOut(punchType);
            }

            System.out.println(surveyObject.getAttendanceObjectJson());

            if (punchType.equals("Punch Out")) {
                if (surveyObject.getRemarks() != null && !surveyObject.getRemarks().isEmpty()) {
                    Intent intent = new Intent("attendanceObject");
                    intent.setPackage(activity.getPackageName());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ATTENDANCE_OBJECT", surveyObject);
                    intent.putExtras(bundle);
                    activity.sendBroadcast(intent);
                    dialog.dismiss();

                } else {
                    Toast.makeText(activity, "Please Enter Remarks", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Broadcast
                Intent intent = new Intent("attendanceObject");
                intent.setPackage(activity.getPackageName());
                Bundle bundle = new Bundle();
                bundle.putSerializable("ATTENDANCE_OBJECT", surveyObject);
                intent.putExtras(bundle);
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }


        });

        dialog.show();
    }


    // this dialog takes an additional parameter for already existing route name..
    public void showAlreadyExistRecordDialog(final Activity activity, String msg, String alreadyExistingRouteName) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, DailyDutyRegisterCards.class);
                intent.putExtra("AlreadyExistingRoute", alreadyExistingRouteName);
                startActivity(activity, intent, null);
            }
        });

        dialog.show();

    }

    public void showRecordAddedCompleteDialog(final Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.50);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(msg);

        Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                Intent intent = new Intent(activity, DailyDutyRegisterCards.class);
                startActivity(activity, intent, null);
                activity.finish();
            }
        });

        dialog.show();

    }


    public void showAdditionalChargeDialog(final Activity activity, List<AdditonalChargePojo> additionalChargesList) {
        if (activity != null) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_additional_charge);
            final AdditonalChargePojo[] selectedAdditionalCharge = new AdditonalChargePojo[1];

            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.80);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ListView resultListView = (ListView) dialog.findViewById(R.id.resultListView);

            AdditionalChargesListAdapter additionalChargesListAdapter = new AdditionalChargesListAdapter(activity, additionalChargesList);
            resultListView.setAdapter(additionalChargesListAdapter);

            resultListView.setOnItemClickListener((parent, view, position, id) -> {
                additionalChargesListAdapter.selectSingleItem(position); // toggle
                selectedAdditionalCharge[0] = additionalChargesListAdapter.getItem(position); // update selected
            });


            TextView text = (TextView) dialog.findViewById(R.id.dialog_result);
            text.setMovementMethod(new ScrollingMovementMethod());

            Button dialog_ok = (Button) dialog.findViewById(R.id.dialog_ok);
            Button dialog_cancel = (Button) dialog.findViewById(R.id.dialog_cancel);

            dialog_cancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

            dialog_ok.setOnClickListener(v -> {
                if (selectedAdditionalCharge[0] != null) {

                    // Validation for HRTC Department
                    if (!selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentName().trim().equalsIgnoreCase("HIMACHAL ROAD TRANSPORT CORPORATION")) {
                        CustomDialog customDialog = new CustomDialog();
                        customDialog.showDialog(activity, "The selected charge does not belongs to the HRTC Department.");
                        return;
                    }


                    Intent intent = new Intent(activity, Homescreen.class);
                    intent.putExtra("SelectedAdditionalCharge", selectedAdditionalCharge[0]);

                    // Passing Complete list via intent
                    if (!additionalChargesList.isEmpty()) {
                        intent.putExtra("CompleteChargesList", (Serializable) additionalChargesList);
                    }

                    startActivity(activity, intent, null);

                    // Office ID Becomes the Depot ID now
                    Preferences.getInstance().depotId = selectedAdditionalCharge[0].getOfficePojo().getOfficeId();
                    Preferences.getInstance().depotName = selectedAdditionalCharge[0].getOfficePojo().getOfficeName();

//                  // Saving preferences as the selected charge
                    Preferences.getInstance().empId = selectedAdditionalCharge[0].getEmpId();
                    Preferences.getInstance().departmentId = selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentId();

                    Preferences.getInstance().savePreferences(activity);

                    Log.i("Selected Additional Charge: ", String.valueOf(selectedAdditionalCharge[0].getEmpId()));
                    Log.i("Selected Additional Charge: ", selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentName());
                    Log.i("Selected Additional Charge: ", String.valueOf(selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentId()));
                    Log.i("Selected Additional Charge: ", String.valueOf(selectedAdditionalCharge[0].getOfficePojo().getOfficeId()));
                    Log.i("Selected Additional Charge: ", selectedAdditionalCharge[0].getOfficePojo().getOfficeName());
                    Log.i("Selected Additional Charge: ", selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentName());

                    dialog.dismiss();
                    activity.finish();
                } else {
                    CustomDialog customDialog = new CustomDialog();
                    customDialog.showDialog(activity, "Please select an additional charge to proceed.");
                }
            });

            dialog.show();
        }
    }


}
