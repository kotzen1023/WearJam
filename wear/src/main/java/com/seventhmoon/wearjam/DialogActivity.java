package com.seventhmoon.wearjam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static com.seventhmoon.wearjam.FileChooseActivity.deleteList;
import static com.seventhmoon.wearjam.MainActivity.searchList;


public class DialogActivity extends WearableActivity {
    private static final String TAG = DialogActivity.class.getName();

    Context context;
    private TextView txtTitle;
    private BoxInsetLayout mContainerView;

    private CircledImageView btnCancel;
    private CircledImageView btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        Log.d(TAG, "onCreate");

        setContentView(R.layout.dialog_layout);

        context = getBaseContext();

        Bundle extras = getIntent().getExtras();


        mContainerView = findViewById(R.id.dialog_container);

        txtTitle = findViewById(R.id.txtTitle);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk = findViewById(R.id.btn_ok);



        txtTitle.setText(extras.getString("DELETE"));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for(int i=0; i<deleteList.size(); i++) {
                //    Log.e(TAG, "delete "+deleteList.get(i));
                //}

                //Bundle bundle = new Bundle();
                //bundle.putString("phonenumber", phoneNumber.getText().toString());
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();


            }
        });

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");


        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(Color.BLACK);
            txtTitle.setTextColor(Color.WHITE);

        } else {
            mContainerView.setBackground(null);
            txtTitle.setTextColor(Color.BLACK);

        }
    }
}
