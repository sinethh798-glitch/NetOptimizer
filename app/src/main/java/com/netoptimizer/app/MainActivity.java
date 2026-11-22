package com.netoptimizer.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.net.TrafficStats;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    Handler handler = new Handler();
    boolean monitoring = false;
    long lastRx, lastTx;

    TextView status, download, upload, connection, ping;
    Button toggle;

    int blue = Color.rgb(0,190,255);
    int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24,35,24,30);
        root.setBackgroundColor(Color.rgb(7,12,23));
        scroll.addView(root);

        TextView title = addText(root,"NET OPTIMIZER",27,blue);
        title.setTypeface(null,Typeface.BOLD);

        addText(root,"V8.0 | PREMIUM EDITION",12,Color.LTGRAY);

        status = addText(root,"CONNECTION STATUS: READY",16,blue);
        connection = addText(root,"Network: Checking...",15,white);

        download = addCard(root,"DOWNLOAD SPEED","0.00 Mbps");
        upload = addCard(root,"UPLOAD SPEED","0.00 Mbps");
        ping = addCard(root,"NETWORK STATUS","Waiting...");

        toggle = new Button(this);
        toggle.setText("START STABILITY MONITOR");
        root.addView(toggle);

        toggle.setOnClickListener(v -> {
            monitoring = !monitoring;

            if(monitoring) {
                lastRx = TrafficStats.getTotalRxBytes();
                lastTx = TrafficStats.getTotalTxBytes();
                toggle.setText("STOP MONITORING");
                status.setText("MONITORING ACTIVE");
                handler.post(updateTask);
            } else {
                handler.removeCallbacks(updateTask);
                toggle.setText("START STABILITY MONITOR");
                status.setText("MONITORING STOPPED");
            }
        });

        addText(root,"SMART STABILITY ENGINE",20,blue);
        addText(root,"Monitor your network activity.",14,white);
        addText(root,"LIVE NETWORK MONITOR",18,blue);
        addText(root,"SETTINGS  |  HISTORY  |  OFFLINE MODE",13,white);

        setContentView(scroll);
        checkNetwork();
    }

    TextView addText(LinearLayout root,String value,int size,int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0,12,0,12);
        root.addView(t);
        return t;
    }

    TextView addCard(LinearLayout root,String label,String value) {
        TextView t = new TextView(this);
        t.setText(label+"\n"+value);
        t.setTextColor(white);
        t.setTextSize(19);
        t.setPadding(22,25,22,25);
        t.setBackgroundColor(Color.rgb(20,32,51));
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,8,0,8);
        root.addView(t,p);
        return t;
    }

    void checkNetwork() {
        ConnectivityManager cm =
            (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        Network n = cm.getActiveNetwork();
        NetworkCapabilities c =
            n == null ? null : cm.getNetworkCapabilities(n);

        if(c != null) {
            connection.setText(c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                ? "Network: Wi-Fi Connected"
                : "Network: Mobile / Other Connected");
        } else {
            connection.setText("Network: Disconnected");
        }
    }

    Runnable updateTask = new Runnable() {
        public void run() {
            if(!monitoring) return;

            long rx = TrafficStats.getTotalRxBytes();
            long tx = TrafficStats.getTotalTxBytes();

            double down = Math.max(0,(rx-lastRx)*8.0/1000000);
            double up = Math.max(0,(tx-lastTx)*8.0/1000000);

            download.setText(String.format(java.util.Locale.US,
                "DOWNLOAD SPEED\n%.2f Mbps",down));
            upload.setText(String.format(java.util.Locale.US,
                "UPLOAD SPEED\n%.2f Mbps",up));

            lastRx=rx;
            lastTx=tx;
            checkNetwork();

            ping.setText("NETWORK STATUS\nMonitoring active");
            handler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateTask);
        super.onDestroy();
    }
}
