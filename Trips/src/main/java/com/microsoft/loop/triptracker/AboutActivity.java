package com.microsoft.loop.triptracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView learnmore_aboutloop;
    private TextView learnmore_code;
    private TextView learnmore_uv;
    private View backAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        learnmore_aboutloop = (TextView) findViewById(R.id.learnmore_aboutloop);
        learnmore_aboutloop.setText(Html.fromHtml("To learn more about LOOP and signup for a free developer account visit <a href=https://www.loop.ms>www.loop.ms</a>"));
        learnmore_aboutloop.setMovementMethod(LinkMovementMethod.getInstance());

        learnmore_code = (TextView) findViewById(R.id.learnmore_code);
        learnmore_code.setText(Html.fromHtml("Our code for Trip Tracker is available on GitHub where you can view the source, file issues, or even fork our repo to build your own trip tracking app. View our code at <a href=\"https://github.com/Microsoft/Loop-Sample-Trips-Android\">GitHub</a>"));
        learnmore_code.setMovementMethod(LinkMovementMethod.getInstance());

        learnmore_uv = (TextView) findViewById(R.id.learnmore_uv);
        learnmore_uv.setText(Html.fromHtml("Questions, bugs, or suggestions? Connect with us on <a href=https://msloop.uservoice.com>UserVoice</a>"));
        learnmore_uv.setMovementMethod(LinkMovementMethod.getInstance());

        backAction = (View)findViewById(R.id.about_activity_back);
        backAction.setClickable(true);

        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
