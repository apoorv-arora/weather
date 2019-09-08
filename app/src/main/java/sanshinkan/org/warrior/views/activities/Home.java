package sanshinkan.org.warrior.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import sanshinkan.org.warrior.R;
import sanshinkan.org.warrior.SApplication;
import sanshinkan.org.warrior.utils.CommonLib;
import sanshinkan.org.warrior.utils.VPrefsReader;
import sanshinkan.org.warrior.views.fragments.FeedFragment;

public class Home extends AppCompatActivity {

    private VPrefsReader prefs;
    private SApplication sApplication;
    private ProgressDialog zProgressDialog;
    private boolean destroyed = false;
    private Activity mActivity;

    private Fragment mFragment;
    private static final String FRAGMENT_TAG = "request_fragment_container";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;
        prefs = VPrefsReader.getInstance();
        sApplication = (SApplication) getApplication();

        mFragment = new FeedFragment();
        mFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFragment, FRAGMENT_TAG)
                .commit();

        findViewById(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                findViewById(R.id.editable_search_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.back_icon).setVisibility(View.VISIBLE);
                findViewById(R.id.title).setVisibility(View.GONE);
                CommonLib.showSoftKeyboard(mActivity, findViewById(R.id.editable_search_layout));
            }
        });

        findViewById(R.id.cross_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.editable_search_text)).setText("");
            }
        });

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                findViewById(R.id.editable_search_layout).setVisibility(View.GONE);
                findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.title).setVisibility(View.VISIBLE);
                CommonLib.hideKeyboard(mActivity);
            }
        });

        ((EditText)findViewById(R.id.editable_search_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                ((FeedFragment)mFragment).filter(s);
            }
        });
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        if (zProgressDialog != null && zProgressDialog.isShowing())
            zProgressDialog.dismiss();

        super.onDestroy();
    }
}
