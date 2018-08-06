package com.kalabhedia.gimme;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class About extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout fork;
    private LinearLayout pranjalGithub;
    private LinearLayout divyanshuGithub;
    private LinearLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About");
        setContentView(R.layout.activity_about);
        fork = findViewById(R.id.fork_on_github);
        pranjalGithub = findViewById(R.id.pranjal_about);
        divyanshuGithub = findViewById(R.id.divyanshu_about);
        email = findViewById(R.id.write_an_email);
        fork.setOnClickListener((View.OnClickListener) this::onClick);
        pranjalGithub.setOnClickListener((View.OnClickListener) this::onClick);
        divyanshuGithub.setOnClickListener((View.OnClickListener) this::onClick);
        email.setOnClickListener((View.OnClickListener) this::onClick);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fork_on_github:
                openWebPage(getResources().getString(R.string.fork_on_github_link));
                break;
            case R.id.pranjal_about:
                openWebPage(getResources().getString(R.string.pranjal_github_link));
                break;
            case R.id.divyanshu_about:
                openWebPage(getResources().getString(R.string.divyanshu_github_link));
                break;
            case R.id.write_an_email:
                sendMail();
                break;
            default:
                break;
        }
    }

    private void sendMail() {

        String mailto = "mailto:pranjalg2308@gmail.com" +
                "?cc=" + "shukladivyanshu967@gmail.com" +
                "&subject=" + Uri.encode("Gimme") +
                "&body=" + Uri.encode("Hey,\n");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }

    public void openWebPage(String url) {
        Toast.makeText(About.this, "Wait a while....", Toast.LENGTH_SHORT).show();
        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(implicit);
    }
}
