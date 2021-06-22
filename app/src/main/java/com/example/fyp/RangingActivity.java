package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
    //
    private NotificationManager mNotificationManager;
    private static Context mContext;
    private double a,b,c;
    //get target shopname
    Intent intent = getIntent();
    String shopname ;
    //
    TextView target,route1,route1m,route2,route2m,route3,route3m,des1,des2, tvdes,tvdes2;
    Button btncancel;
    private String DinTaiFung, Nike;
    private double rangtoDin, rangtoNike, rangtoPizza, max, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        Bundle extras = getIntent().getExtras();
        target = findViewById(R.id.tvtarget);
        route1 = findViewById(R.id.tvroute1);
        route1m = findViewById(R.id.tvroute1m);
        route2 = findViewById(R.id.tvroute2);
        route2m = findViewById(R.id.tvroute2m);
        route3 = findViewById(R.id.tvroute3);
        route3m = findViewById(R.id.tvroute3m);
        des1 = findViewById(R.id.tvdes);
        des2 = findViewById(R.id.tvdes2);
        tvdes = findViewById(R.id.tvdes);
        tvdes2 = findViewById(R.id.tvdes2);
        btncancel = findViewById(R.id.cancel);
        shopname = extras.getString("SNAME");
        target.setText(shopname);

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });
        //draw map
//        LinearLayout layout=(LinearLayout) findViewById(R.id.root);
//        final DrawView view = new DrawView(this);
//        view.setMinimumHeight(1000);
//        view.setMinimumWidth(1000);
//        view.invalidate();
//        layout.addView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon.setHardwareEqualityEnforced(true);
                for (Beacon b : beacons) {
                    run();
                    if(shopname.equals("Nike")){
                        tvdes.setText("Distance from Nike:");
                        tvdes2.setText(String.format("%.2f",getNike())+ " m");
                    }else if(shopname.equals("Din Tai Fung")){
                        tvdes.setText("Distance from Din Tai Fung:");
                        tvdes2.setText(String.format("%.2f",getDinTaiFungDis())+ " m");
                    }else if(shopname.equals("Pizza Express")){
                        tvdes.setText("Distance from Pizza Express:");
                        tvdes2.setText(String.format("%.2f",getPizza())+ " m");
                    }
//                    if (b.getBluetoothAddress().equals("05:9B:08:75:DB:A5")) {
//                        //Din Tai Fung
//                        String DinTaiFung ="Din Tai Fung is about " + String.format("%.2f", b.getDistance()) + " meters away.";
//                        double rangtoDin = b.getDistance();
//                    } else if (b.getBluetoothAddress().equals("01:2D:80:1B:91:B1")) {
//                        //Nike
//                        String Nike = String.format("%.2f", b.getDistance()) + " meters away. ";
//                        double rangtoNike = b.getDistance();
//                    }
                }
            }

        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }


    public void run() {
        a = getNike();
        b = getDinTaiFungDis();
        c = getPizza();
        if (a >= b)
            if (a >= c) {
                max = a;
                route3.setText("Nike");
                route3m.setText(String.format("%.2f", a)+ " m");
                if (b >= c) {
                    min = c;
                    route1.setText("Pizza");
                    route1m.setText(String.format("%.2f",c)+ " m");
                    route2.setText("Din Tai Fung");
                    route2m.setText(String.format("%.2f",b)+ " m");
                }
                else {
                    min = b;
                    route1.setText("Din Tai Fung");
                    route1m.setText(String.format("%.2f",b)+ " m");
                    route2.setText("Pizza");
                    route2m.setText(String.format("%.2f",c)+ " m");
                }
            } else {
                max = c;
                min = b;
                route1.setText("Din Tai Fung");
                route1m.setText(String.format("%.2f",b)+ " m");
                route2.setText("Nike");
                route2m.setText(String.format("%.2f",a)+ " m");
                route3.setText("Pizza");
                route3m.setText(String.format("%.2f",c)+ " m");
            }
        else if (b >= c) {
            max = b;
            route3.setText("Din Tai Fung");
            route3m.setText(String.format("%.2f",b)+ " m");
            if (a >= c){ min = c;
                route1.setText("Pizza Express");
                route1m.setText(String.format("%.2f",c)+ " m");
                route2.setText("Nike");
                route2m.setText(String.format("%.2f",a)+ " m");
            }
            else{ min = a;
                route1.setText("Nike");
                route1m.setText(String.format("%.2f",a)+ " m");
                route2.setText("Pizza Express");
                route2m.setText(String.format("%.2f",c)+ " m");
            }
        } else {
            max = c;
            route3.setText("Pizza Express");
            route3m.setText(String.format("%.2f",c)+ " m");
            if (a >= b){ min = b;
                route1.setText("Din Tai Fung");
                route1m.setText(String.format("%.2f",b)+ " m");
                route2.setText("Nike");
                route2m.setText(String.format("%.2f",a)+ " m");
            }
            else{ min = a;
                route2.setText("Din Tai Fung");
                route2m.setText(String.format("%.2f",b) + " m");
                route1.setText("Nike");
                route1m.setText(String.format("%.2f",a)+ " m");
            }
        }
    }

    public double getDinTaiFungDis() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon.setHardwareEqualityEnforced(true);
                for (Beacon b : beacons) {
                    if (b.getBluetoothAddress().equals("05:9B:08:75:DB:A5")) {
                        //Din Tai Fung
                        DinTaiFung = "Din Tai Fung is about " + String.format("%.2f", b.getDistance()) + " meters away.";
                        Log.d(TAG, "HELPME "+DinTaiFung);
                        rangtoDin = b.getDistance();
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
        return rangtoDin;
    }

    public double getNike() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon.setHardwareEqualityEnforced(true);
                for (Beacon b : beacons) {
                    if (b.getBluetoothAddress().equals("01:2D:80:1B:91:B1")) {
                        //Din Tai Fung
                        DinTaiFung = "Nike is about " + String.format("%.2f", b.getDistance()) + " meters away.";
                        rangtoNike = b.getDistance();
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
        return rangtoNike ;
    }

    public double getPizza() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Beacon.setHardwareEqualityEnforced(true);
                for (Beacon b : beacons) {
                    if (b.getBluetoothAddress().equals("59:15:D4:A0:CB:71")) {
                        //Din Tai Fung
                        DinTaiFung = "Pizza Express is about " + String.format("%.2f", b.getDistance()) + " meters away.";
                        rangtoPizza = b.getDistance();
                    }
                }
            }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
        return rangtoPizza;
    }

    //draw map
    public class DrawView extends View {

        public DrawView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(0, 0, 0));
            myPaint.setStrokeWidth(10);
            myPaint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(1000, 0, 1000, 20, myPaint);
            canvas.drawRect(100, 100, 950, 950, myPaint);


        }
    }
}

