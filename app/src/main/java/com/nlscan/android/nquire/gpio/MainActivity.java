package com.nlscan.android.nquire.gpio;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.nq1000.NQManager;

public class MainActivity extends Activity {

    private static Button physicalBtn;
    private static Button write;
    private static Button gpio015;
    private static Button gpio029;
    private static Button gpio030;
    private static Button gpio031;
    private static Button uart101;
    private static TextView irText;
    private IRThread thread;
    private Spinner spinner;
    private static String TAG = "Nquire GPIO";
    private NQManager nqm;
    static int count015 = 0;
    static int count029 = 0;
    static int count101 = 0;
    static boolean threadFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nqm = NQManager.get(this);

        setContentView(R.layout.activity_main);
        physicalBtn = (Button) findViewById(R.id.button1);
        gpio015 = (Button) findViewById(R.id.button3);
        gpio029 = (Button) findViewById(R.id.button4);
        gpio030 = (Button) findViewById(R.id.button5);
        gpio031 = (Button) findViewById(R.id.button6);
        uart101 = (Button) findViewById(R.id.button7);
        spinner = (Spinner) findViewById(R.id.spinner);
        irText = findViewById(R.id.irText);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectecItem = parent.getItemAtPosition(position).toString();
                switch (selectecItem) {
                    case "":
                        break;
                    case "bar scan on":
                        selectecItem = "barscanonn";
                        break;
                    case "bar scan power on":
                        selectecItem = "barscanpoweronn";
                        break;
                    case "bar scan power off":
                        selectecItem = "barscanpoweroffn";
                        break;
                    case "bar scan trg on":
                        selectecItem = "barscantrgonn";
                        break;
                    case "bar scan trg off":
                        selectecItem = "barscantrgoffn";
                        break;
                    default:
                        break;

                }
                if (!TextUtils.isEmpty(selectecItem)) {
                    nqm.setThreshold(selectecItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        gpio015.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "gpio015");
                if ((count015 % 2) == 0) {
                    nqm.setdoorThreshold("150");
                    gpio015.setText("GPIO0_15_HIGH");
                } else {
                    nqm.setdoorThreshold("151");
                    gpio015.setText("GPIO0_15_LOW");

                }
                count015++;
            }
        });

        gpio029.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "gpio029");
                if ((count029 % 2) == 0) {
                    nqm.setdoorThreshold("290");
                    gpio029.setText("GPIO0_29_HIGH");
                } else {
                    nqm.setdoorThreshold("291");
                    gpio029.setText("GPIO0_29_LOW");

                }
                count029++;
            }
        });

        uart101.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "uart101");
                if ((count101 % 2) == 0) {
                    nqm.setdoorThreshold("1010");
                    uart101.setText("UART_DISABLED");
                } else {
                    nqm.setdoorThreshold("1011");
                    uart101.setText("UART_ENABLED");

                }
                count101++;
            }
        });

        gpio030.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "nqm.getdoorData30:" + nqm.getdoorData());
                String str_030 = nqm.getdoorData();
                int num = Integer.parseInt(str_030);
                if ((num / 10) == 0) {
                    gpio030.setText("GPIO0_30__HIGH");
                } else {
                    gpio030.setText("GPIO0_30_LOW");

                }
            }
        });

        gpio031.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "nqm.getdoorData31:" + nqm.getdoorData());
                String str_031 = nqm.getdoorData();
                int num = Integer.parseInt(str_031);

                if ((num % 10) == 0) {
                    gpio031.setText("GPIO0_31__HIGH");
                } else {
                    gpio031.setText("GPIO0_31_LOW");

                }
            }
        });

        physicalBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "red");
                if (nqm.ifKeysEnabled()) {
                    nqm.enableKeys(false);
                    physicalBtn.setText("Physical Button Disabled");
                } else {
                    nqm.enableKeys(true);
                    physicalBtn.setText("Physical Button Enabled");
                }
            }
        });

        thread = new IRThread();
    }

    public class IRThread extends Thread {
        @Override
        public void run() {
            while (threadFlg) {
                final String irData = nqm.getIrData();
                Log.d(TAG, "irData:" + irData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(irData)) {
                            irText.setText(irData);

                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        threadFlg = true;
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadFlg = false;
    }
}
