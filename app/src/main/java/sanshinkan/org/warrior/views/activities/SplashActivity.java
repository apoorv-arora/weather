package sanshinkan.org.warrior.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Timer;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.SApplication;
import sanshinkan.org.warrior.utils.CommonLib;
import sanshinkan.org.warrior.utils.VPrefsReader;

/**
 * Created by apoorvarora on 11/02/19.
 */

public class SplashActivity extends AppCompatActivity {

    // Generic activity stuffs
    private boolean destroyed = false;
    private Activity mActivity;
    private SApplication vapp;

    // Play services stuffs
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int requestCode = 101;

    public static final int RC_SIGN_IN = 0;
    private VPrefsReader prefs;

    private Timer timer;
    private int description;

    private Animation animation1, animation2;
    private ProgressDialog z_ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        vapp = (SApplication) getApplication();
        mActivity = SplashActivity.this;
        prefs = VPrefsReader.getInstance();

        animateSplash();
    }

    private void animateSplash() {
        try {
            animation1 = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up_center);
            animation1.setDuration(700);
            animation1.restrictDuration(700);
            animation1.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // should run second animation or not
                    triggerFlow();
                }
            });
            animation1.scaleCurrentDuration(1);
            findViewById(R.id.logo_container).setAnimation(animation1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    public synchronized void triggerFlow() {
        navigateToHome();
    }

    private void navigateToHome() {
        // play_service_check is true only for first time
        // if it's true, we have to check whether phone supports google play services or not
        // if not, we do not allow to proceed
        if (checkPlayServices()) {
            Intent intent = new Intent(mActivity, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mActivity.finish();
        }
    }

    private boolean checkPlayServices() {
        if (prefs.getOneTimePref(CommonLib.PROPERTY_PLAY_SERVICES_CHECK, true)) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            requestCode = googleAPI.isGooglePlayServicesAvailable(mActivity);
            if (requestCode == ConnectionResult.SUCCESS) {
                prefs.setOneTimePref(CommonLib.PROPERTY_PLAY_SERVICES_CHECK, false);
                return true;
            } else {
                googleAPI.showErrorDialogFragment(mActivity, requestCode, requestCode, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(mActivity, getResources().getString(R.string.update_play_services),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                return false;
            }
        } else
            return true;
    }

}