package com.lasys.app.nearme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.lasys.app.nearme.R;

import static com.lasys.app.nearme.intrface.AppConstants.SPLASHTIME;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_splash_screen);

        Thread logoTimer = new Thread()
        {
            public void run() {
                try {
                    int logoTimer = 0;
                    while (logoTimer < SPLASHTIME) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }

                    Intent intent = new Intent(SplashScreen.this, DashBoard.class);
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally
                {
                    finish();
                }
            }
        };
        logoTimer.start();

    }
}
