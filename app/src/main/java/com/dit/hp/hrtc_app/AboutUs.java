package com.dit.hp.hrtc_app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AboutUs extends AppCompatActivity {

    TextView aboutUsTV;
    CardView backCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        aboutUsTV = findViewById(R.id.tvAboutUsDescription);
        backCard = findViewById(R.id.backCard);
        aboutUsTV.setText(Html.fromHtml(getString(R.string.ddtg_info)));


        backCard.setOnClickListener(v -> {
            AboutUs.this.finish();
        });

    }

}