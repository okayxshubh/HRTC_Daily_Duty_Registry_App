package com.dit.hp.hrtc_app.Presentation;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dit.hp.hrtc_app.Adapters.AdditionalChargesListAdapter;
import com.dit.hp.hrtc_app.AddStop;
import com.dit.hp.hrtc_app.AllAddaCards;
import com.dit.hp.hrtc_app.AllConductorsCards;
import com.dit.hp.hrtc_app.AllDepotsCards;
import com.dit.hp.hrtc_app.AllDriversCards;
import com.dit.hp.hrtc_app.DailyDutyRegisterCards;
import com.dit.hp.hrtc_app.Homescreen;
import com.dit.hp.hrtc_app.LoginHRTC;
import com.dit.hp.hrtc_app.ManageEntities;
import com.dit.hp.hrtc_app.Modals.AdditonalChargePojo;
import com.dit.hp.hrtc_app.Modals.RoutePojo;
import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.utilities.Preferences;

import java.util.List;

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
                    if (!selectedAdditionalCharge[0].getDepartmentPojo().getDepartmentName().trim().equalsIgnoreCase("HIMACHAL ROAD TRANSPORT CORPORATION"))
                    {
                        CustomDialog customDialog = new CustomDialog();
                        customDialog.showDialog(activity, "The selected charge does not belongs to the HRTC Department.");
                        return;
                    }


                    Intent intent = new Intent(activity, Homescreen.class);
                    intent.putExtra("SelectedAdditionalCharge", selectedAdditionalCharge[0]);
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
                    customDialog.showDialog(activity, "Please select an additional charge.");
                }
            });

            dialog.show();
        }
    }


}
