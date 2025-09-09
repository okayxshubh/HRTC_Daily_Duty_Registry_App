package com.dit.hp.hrtc_app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dit.hp.hrtc_app.Adapters.SalaryCardsAdapter;
import com.dit.hp.hrtc_app.Modals.ResponsePojoGet;
import com.dit.hp.hrtc_app.Modals.SalaryPojo;
import com.dit.hp.hrtc_app.Presentation.CustomDialog;
import com.dit.hp.hrtc_app.enums.TaskType;
import com.dit.hp.hrtc_app.interfaces.OnSalaryCardClickListener;
import com.dit.hp.hrtc_app.interfaces.ShubhAsyncTaskListenerGet;
import com.dit.hp.hrtc_app.utilities.Econstants;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewSalary extends AppCompatActivity implements OnSalaryCardClickListener, ShubhAsyncTaskListenerGet {

    Button back;

    ImageView switchYearIV;
    TextView financialYearTV;
    RecyclerView salaryRecyclerView;
    SalaryCardsAdapter salaryCardsAdapter;

    CustomDialog CD = new CustomDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_salary);

        // Avoid Dark Theme Switching on Crash  // Theme Acc to Preferences
        Econstants.loadPrefsNApplyTheme(this);

        back = findViewById(R.id.back);

        switchYearIV = findViewById(R.id.switchYearIV);
        salaryRecyclerView = findViewById(R.id.recyclerView);
        financialYearTV = findViewById(R.id.financialYearTV);

        // Show Popup If any clicked
        switchYearIV.setOnClickListener(v -> {
            showChangeFinancialYearPopUp();
        });

        // Back Btn
        back.setOnClickListener(v -> {
            ViewSalary.this.finish();
        });


        List<SalaryPojo> salaryList = new ArrayList<>();
        salaryList.add(new SalaryPojo("Jan 2024", 1200.0, 28800.0, "Paid"));
        salaryList.add(new SalaryPojo("Mar 2024", 1500.0, 28500.0, "Pending"));
        salaryList.add(new SalaryPojo("Feb 2024", 1000.0, 29000.0, "Paid"));
        salaryList.add(new SalaryPojo("Mar 2024", 1500.0, 28500.0, "Pending"));
        salaryList.add(new SalaryPojo("Apr 2024", 1300.0, 28700.0, "Paid"));
        salaryList.add(new SalaryPojo("Mar 2024", 1500.0, 28500.0, "Pending"));
        salaryList.add(new SalaryPojo("May 2024", 2000.0, 28000.0, "Failed"));
        salaryList.add(new SalaryPojo("Feb 2024", 1000.0, 29000.0, "Paid"));

        salaryCardsAdapter = new SalaryCardsAdapter(salaryList, this);
        salaryRecyclerView.setAdapter(salaryCardsAdapter);
        salaryRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Add this line to show cards
    }

    private void showChangeFinancialYearPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_financial_year_picker, null);
        builder.setView(dialogView);

        // Custom Title
        TextView titleTextView = new TextView(this);
        titleTextView.setText("Select Financial Year");
        titleTextView.setTextSize(20);
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextView.setTextColor(Color.BLACK);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setPadding(0, 30, 0, 30);
        builder.setCustomTitle(titleTextView);

        // Spinner
        Spinner financialYearSpinner = dialogView.findViewById(R.id.financialYearSpinner);

        builder.setPositiveButton("Select", (dialog, which) -> {
            CD.showDialog(this, "Change Year Title \n \n Change Search Year");
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        // Set adapter AFTER dialog is shown
        alertDialog.getWindow().getDecorView().post(() -> {
            List<String> years = new ArrayList<>();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = currentYear - 4; i <= currentYear + 1; i++) {
                years.add(i + "-" + (i + 1));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            financialYearSpinner.setAdapter(adapter);

            String currentFY = currentYear + "-" + (currentYear + 1);
            int index = years.indexOf(currentFY);

            Log.e("FY_DEBUG", "Years: " + years.size() + ", Index: " + index);

            if (index >= 0 && index < adapter.getCount()) {
                financialYearSpinner.setSelection(index, false);
            }
        });

        // Dim background
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(lp);
        }
    }


    @Override
    public void onTaskCompleted(ResponsePojoGet responseObject, TaskType taskType) throws JSONException {

    }

    // Salary Card Click Listener
    @Override
    public void onCardClickListener(SalaryPojo itemPojo, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_more_info_salary, null);

        // Bind views
        TextView depotId = dialogView.findViewById(R.id.depotId);
        TextView depotName = dialogView.findViewById(R.id.depotName);
        TextView depotCode = dialogView.findViewById(R.id.depotCode);

        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        dialog.show();
    }


}
