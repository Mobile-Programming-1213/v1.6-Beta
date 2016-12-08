package ssjk.cafein;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.DKGRAY;
import static android.graphics.Color.MAGENTA;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mPager;
    private long lastTimeBackPressed;
    private int inc = 1;
    private DatabaseOpenHelper database;
    private double longitude;
    private double latitude;
    private LocationManager locationManager;
    //private DisplayMetrics displaymetrics;
    private int width;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new DatabaseOpenHelper(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        registerLocationUpdates();
        setLayout();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
        mPager.setOnPageChangeListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        /*LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.mainscroll);*/
        switch (v.getId()) {
            case R.id.btn_toCafe:
                setCurrentInflateItem(0);
                break;
            case R.id.btn_toCoffee:
                setCurrentInflateItem(1);
                break;
            case R.id.btn_toMenu:
                setCurrentInflateItem(2);
                break;
            case R.id. btn_search:
                intent = new Intent(MainActivity.this, BeginSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.notice:
            case R.id.ask:
            case R.id.developers:
                intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.putExtra("Extra", v.getId());
                startActivity(intent);
                break;
            /*case R.id.alphabet:
                v = mInflater.inflate(R.layout.inflate_one, null);
                setfirstview(v, "alphabet");
                break;
            case R.id.location:
                v = mInflater.inflate(R.layout.inflate_one, null);
                setfirstview(v, "location");
                break;*/
        }
    }

    @Override
    public void onPageScrolled(int state, float positionOffset, int positionOffsetPixels){    }

    @Override
    public void onPageSelected(int position){
        switch(position){
            case 0:
                btn_toCafe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_cafe_focused, 0, 0);
                btn_toCoffee.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_coffee, 0, 0);
                btn_toMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_menu, 0, 0);
                break;
            case 1:
                btn_toCafe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_cafe, 0, 0);
                btn_toCoffee.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_coffee_focused, 0, 0);
                btn_toMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_menu, 0, 0);

                break;
            case 2:
                btn_toCafe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_cafe, 0, 0);
                btn_toCoffee.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_coffee, 0, 0);
                btn_toMenu.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_button_menu_focused, 0, 0);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state){    }

    private void setCurrentInflateItem(int type){
        if(type==0){
            mPager.setCurrentItem(0);
        }else if(type==1){
            mPager.setCurrentItem(1);
        }else if(type==2){
            mPager.setCurrentItem(2);
        }
    }


    private Button btn_toCafe;
    private Button btn_toCoffee;
    private Button btn_toMenu;

    private void setLayout(){
        btn_toCafe = (Button) findViewById(R.id.btn_toCafe);
        btn_toCoffee = (Button) findViewById(R.id.btn_toCoffee);
        Button btn_search = (Button) findViewById(R.id. btn_search);
        btn_toMenu = (Button) findViewById(R.id. btn_toMenu);

        btn_toMenu.setOnClickListener(MainActivity.this);
        btn_search.setOnClickListener(MainActivity.this);
        btn_toCafe.setOnClickListener(MainActivity.this);
        btn_toCoffee.setOnClickListener(MainActivity.this);
    }

    private class PagerAdapterClass extends PagerAdapter {

        private LayoutInflater mInflater;

        private PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            if(position==0){
                v = mInflater.inflate(R.layout.inflate_one, null);
                setfirstview(v);
            }
            else if(position==1){
                v = mInflater.inflate(R.layout.inflate_two, null);
                setsecondview(v);
            }
            else{
                v = mInflater.inflate(R.layout.inflate_three, null);
                setthirdview(v);
            }

            ((ViewPager)pager).addView(v, 0);

            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
    }


    private void setfirstview(final View v){
        getfirstview(v, "alphabet");
        final LinearLayout layout = (LinearLayout) v.findViewById(R.id.mainscroll);
        Button btn_location = (Button) v.findViewById(R.id.location);
        Button btn_alphabet = (Button) v.findViewById(R.id.alphabet);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                layout.removeAllViews();
                String s = "location";
                getfirstview(v, s);
            }
        });
        btn_alphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                layout.removeAllViews();
                String s = "alphabet";
                getfirstview(v, s);
            }
        });
    }

    private void getfirstview(View v, String s){
        // view of the first viewpager
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.mainscroll);
        String select = "SELECT * FROM TABLE_SUGGESTION WHERE FIELD_TYPE = 'CAFE'";
        Cursor cur = database.searchDatabase(select);
        int count = cur.getCount();
        cur.moveToFirst();
        String[] cafearray = new String[count];
        double[] distarray = new double[count];
        String[] orderarray = new String[count];
            for (int i = 0; i < count; i++) {
                cafearray[i] = cur.getString(1);
                orderarray[i] = cafearray[i].substring(0, 1);
                double dist = distance(latitude, longitude, cur.getDouble(5), cur.getDouble(6));
                if (dist > 10){
                    distarray[i] = -1;
                }
                else
                    distarray[i] = dist;
                cur.moveToNext();
            }
        //Arrange cafes in Alphabetical order
        if(s.equals("alphabet")) {
            String stmp, stmp2;
            double dtmp;
            for (int i = 0; i < count - 1; i++) {
                int compare = orderarray[i].compareTo(orderarray[i + 1]);
                if (compare > 0) {
                    stmp = cafearray[i];
                    cafearray[i] = cafearray[i + 1];
                    cafearray[i + 1] = stmp;
                    stmp2 = orderarray[i];
                    orderarray[i] = orderarray[i + 1];
                    orderarray[i + 1] = stmp2;
                    dtmp = distarray[i];
                    distarray[i] = distarray[i+1];
                    distarray[i+1] = dtmp;
                    i = -1;
                }
            }
        }
        else {
            String stmp;
            double dtmp;
            for (int i = 0; i < count - 1; i++) {
                if (distarray[i] > distarray[i+1]) {
                    stmp = cafearray[i];
                    dtmp = distarray[i];
                    cafearray[i] = cafearray[i+1];
                    distarray[i] = distarray[i+1];
                    cafearray[i+1] = stmp;
                    distarray[i+1] = dtmp;
                    i = -1;
                }
            }
        }
        cur.close();

        for (int i = 0; i < count; i++) {
            String selectc = String.format("SELECT * FROM TABLE_SUGGESTION WHERE FIELD_SUGGESTION = '%s' and FIELD_TYPE = 'CAFE'", cafearray[i]);
            final Cursor curc = database.searchDatabase(selectc);
            curc.moveToFirst();
            LinearLayout innerlayout = new LinearLayout(v.getContext());
            innerlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            innerlayout.setOrientation(LinearLayout.HORIZONTAL);
            layout.addView(innerlayout);

            TextView nullview = new TextView(v.getContext());
            nullview.setWidth(width/5);
            innerlayout.addView(nullview);

            Button myButton = new Button(v.getContext());
            final String text = curc.getString(1);
            final String ename = curc.getString(4);
            myButton.setId(i);
            myButton.setText(text);
            myButton.setTextColor(BLACK);
            myButton.setWidth(width*3/5);
            myButton.setBackgroundResource(R.drawable.ic_button_big);
            innerlayout.addView(myButton);
            myButton = ((Button) v.findViewById(myButton.getId()));
            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CafeInformation.class);
                    intent.putExtra("CafeName", text);
                    intent.putExtra("ENGname", ename);
                    startActivity(intent);

                }
            });
            if (distarray[i] != -1) {
                nullview = new TextView(v.getContext());
                nullview.setWidth(width/15);
                innerlayout.addView(nullview);
                TextView t_v = new TextView(v.getContext());
                String dist = String.format(Locale.KOREA, "%.2fm", distarray[i] * 1000);
                t_v.setText(dist);
                t_v.setTextSize(10);
                t_v.setTextColor(DKGRAY);
                innerlayout.addView(t_v);
            }
            curc.close();
        }

        TextView nullview = new TextView(v.getContext());
        nullview.setHeight(15);
        layout.addView(nullview);

    }
    private void setsecondview(View v) {
        //List drinks in this order
        setview("COFFEE", v);
        setview("TEA", v );
        setview("ICE BLENDED", v);
        setview("BEVERAGE", v);
    }
    private void setthirdview(View v){
        Button btn_notice = (Button) v.findViewById(R.id.notice);
        Button btn_ask = (Button) v.findViewById(R.id.ask);
        Button btn_developers = (Button) v.findViewById(R.id.developers);

        btn_notice.setOnClickListener(this);
        btn_ask.setOnClickListener(this);
        btn_developers.setOnClickListener(this);
    }
    private void setview(final String s, View v){
        LinearLayout layout = null;
        switch(s){
            case "COFFEE":
                layout = (LinearLayout) v.findViewById(R.id.secondscroll_coffee);
                break;
            case "ICE BLENDED":
                layout = (LinearLayout) v.findViewById(R.id.secondscroll_ice);
                break;
            case "TEA":
                layout = (LinearLayout) v.findViewById(R.id.secondscroll_tea);
                break;
            case "BEVERAGE":
                layout = (LinearLayout) v.findViewById(R.id.secondscroll_beverage);
                break;

        }
        String select = String.format("SELECT * FROM TABLE_SUGGESTION WHERE FIELD_TYPE = '%s' and ITEM_NUM > 4", s);
        Cursor cur = database.searchDatabase(select);
        cur.moveToFirst();
        int count = cur.getCount();
        int [] numarray = new int[count];
        String [] drinkarray = new String[count];
        //arrange drinks according to number of cafes selling the drink
        for (int i = 0; i < count; i ++){
            numarray[i] = cur.getInt(3);
            drinkarray[i] = cur.getString(1);
            cur.moveToNext();
        }
        int tmp;
        String stmp;
        for (int i = 0; i < count - 1; i ++){
            if(numarray[i] < numarray[i+1]){
                tmp = numarray[i];
                stmp = drinkarray[i];
                numarray[i] = numarray[i+1];
                drinkarray[i] = drinkarray[i+1];
                numarray[i+1] = tmp;
                drinkarray[i+1] = stmp;
                i = -1;
            }
        }
        cur.close();
        for (int i = 0; i < count; i++) {
            String selectc = String.format("SELECT * FROM TABLE_SUGGESTION WHERE FIELD_SUGGESTION = '%s' and FIELD_TYPE = '%s'", drinkarray[i], s);
            Cursor curc = database.searchDatabase(selectc);
            curc.moveToFirst();
            if (curc.getCount() != 0) {
                LinearLayout innerlayout = new LinearLayout(v.getContext());
                innerlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                innerlayout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(innerlayout);

                TextView nullview = new TextView(v.getContext());
                nullview.setWidth(width / 5);
                innerlayout.addView(nullview);

                Button myButton = new Button(v.getContext());
                final String text = curc.getString(1);
                myButton.setId(inc++);
                myButton.setText(text);
                myButton.setTextColor(BLACK);
                myButton.setWidth(width * 3 / 5);
                myButton.setBackgroundResource(R.drawable.ic_button_big);
                innerlayout.addView(myButton);
                myButton = ((Button) v.findViewById(myButton.getId()));
                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, CoffeeList.class);
                        intent.putExtra("CoffeeName", text);
                        intent.putExtra("DrinkType", s);
                        startActivity(intent);

                    }
                });
            }
            curc.close();
        }
    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-lastTimeBackPressed<1500){
            finish();
            return;
        }

        Toast.makeText(this,"'뒤로' 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();

        lastTimeBackPressed = System.currentTimeMillis();

    }

    private void registerLocationUpdates() {
        if (locationManager != null) {
            //checks for permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //get location based on network provider
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                //get location based on gps provider (more accurate)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }}
//1000은 1초마다, 1은 1미터마다 해당 값을 갱신한다는 뜻으로, 딜레이마다 호출하기도 하지만
//위치값을 판별하여 일정 미터단위 움직임이 발생 했을 때에도 리스너를 호출 할 수 있다.
    }


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
//여기서 위치값이 갱신되면 이벤트가 발생한다.
//값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            longitude = location.getLongitude();    //경도
            latitude = location.getLatitude();         //위도

//                float accuracy = location.getAccuracy();        //신뢰도
        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
// circular buttons size 55dip, padding 4