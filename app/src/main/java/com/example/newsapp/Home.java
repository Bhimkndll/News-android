package com.example.newsapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bottomnavigation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment implements CardAdaptor.OnItemClickListener, CardAdaptor.OnItemLongClickListener{



    public Home() {
        // Required
    }
    String one;
    TextView city;
    TextView temperature;
    TextView desc,descriptions,titles,dates,sections;
    TextView state,divide;
    ImageView img,imgView;
    String stateName;
    CardView cardView;
    String cityname="Gaindakot";
    private RecyclerView mRecyclerView;
    private CardAdaptor mCardAdaptor;
    private ArrayList<CardItem> mExampleList;
    BookmarkItem bk;
    BookmarkItem bk_delete;
    LocationManager locationManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView progress_t;
    public static  String article_url = "article_url";
    View view;
    private ProgressBar spinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        progress_t=view.findViewById(R.id.progress_text);
        spinner = view.findViewById(R.id.progressBar1);
        cardView=view.findViewById(R.id.weather_card);
        temperature=view.findViewById(R.id.temperature);;
        state=view.findViewById(R.id.state);
        cardView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        progress_t.setVisibility(View.VISIBLE);
        city=view.findViewById(R.id.city);
        divide=view.findViewById(R.id.divide);
        divide.setVisibility(View.INVISIBLE);
        desc=view.findViewById(R.id.desc);
        img=view.findViewById(R.id.background);

        imgView = view.findViewById(R.id.image_view_detail);

        titles = view.findViewById(R.id.detailed_title);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Toast.makeText(getActivity().getApplicationContext(),
                        "The favorite list would appear on clicking this icon",
                        Toast.LENGTH_LONG).show();*/
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);

                detailIntent.putExtra("article_url",one);
                startActivity(detailIntent);

            }});
        descriptions= view.findViewById(R.id.detailed_desc);
        dates=view.findViewById(R.id.detailed_date);
        sections=view.findViewById(R.id.detailed_section);
//        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.tool_bar);
//        getActivity().getApplicationContext().setSupportActionBar(myToolbar);
//        checkLocationPermission();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                spinner.setVisibility(View.GONE);
                progress_t.setVisibility(View.GONE);
                top_headlines();

                find_weather();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        mExampleList = new ArrayList<>();


            top_headlines();
            find_weather();





        return view;
    }


    public void find_weather(){

        String url="https://api.openweathermap.org/data/2.5/weather?q="+"gaindakot"+"&units=metric&appid=c9e1fb4ec1fc41fcf15d56e4a865689c";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonobj=response.getJSONObject("main");
                            JSONArray arr=response.getJSONArray("weather");
                            JSONObject obj=arr.getJSONObject(0);

                            temperature.setText(String.valueOf(Math.round(jsonobj.getDouble("temp")))+" ℃");
                            String description=obj.getString("main");

                            String city1=response.getString("name");
                            // String summary=obj.getString()

                            if(description.equals("Clouds")) {
                                img.setImageResource(R.drawable.cloudy);
                            }
                            else if(description.equals("Clear")) {
                                img.setImageResource(R.drawable.clear);
                            }
                            else if(description.equals("Snow")) {
                                img.setImageResource(R.drawable.snowy);
                            }
                            else if(description.equals("Rainy")) {
                                img.setImageResource(R.drawable.rainy);
                            }
                            else if(description.equals("Drizzle")) {
                                img.setImageResource(R.drawable.rainy);
                            }
                            else if(description.equals("Thunderstorm")) {
                                img.setImageResource(R.drawable.thunder);
                            }
                            else  {
                                img.setImageResource(R.drawable.sunny);
                            }


                            city.setText(city1);
                            desc.setText(description);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        int socketTimeout = 10000; //10 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
//        spinner.setVisibility(View.INVISIBLE);
//        progress_t.setVisibility(View.INVISIBLE);



    }
    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                cityname = addresses.get(0).getLocality();

                Log.i("city name", cityname);
                stateName = addresses.get(0).getAdminArea();
                state.setText("California");
            } catch (Exception e) {

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private void top_headlines() {
//        spinner.setVisibility(View.GONE);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext()));
        mExampleList = new ArrayList<>();
        final ArrayList<CardItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            String url= Global.bhim;
        //String url = "https://dummyjson.com/products";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(getActivity() != null) {
                            JSONArray results=response.getJSONArray("posts");
                            //JSONArray main=response.getJSONArray("post");

                            int a=0;

                            JSONArray small = response.getJSONArray("post");

                            JSONObject content = small.getJSONObject(0);
                            //Log.e("laxmu",ram.getString("name"));
                            JSONObject catt = content.getJSONObject("category");
                            one=content.getString("id");

                            String main_title=content.getString("heading");
                             //String cates=ram.getString("name");
                            String datee=content.getString("created_at");
                            String date3=formatDate(datee);
                            System.out.println(date3);
                            dates.setText(date3);
                            String main_image=(String) content.get("image");
                            String main_image_url= Global.neimage+main_image;
                            String main_section=(String) catt.get("name");
                            String main_description=content.getString("description");

                            //String sectionso=ram.getString("name");
                            titles.setText(main_title);
                           Picasso.with(img.getContext()).load(main_image_url).fit().centerInside().into(imgView);

                            sections.setText(main_section);
        divide.setVisibility(View.VISIBLE);
                            cardView.setVisibility(View.VISIBLE);






                            //JSONObject data = response.getJSONObject("data");
//                            JSONObject response = data.getJSONObject("response");
                            //JSONObject json = data.getJSONObject("response");
                            //JSONArray results=json.getJSONArray("results");
                            //JSONArray results=response.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                System.out.println(results.length());
                                JSONObject index = results.getJSONObject(i);
                                JSONObject cat = index.getJSONObject("category");


                                //JSONObject blocks=index.getJSONObject("blocks");
                                //String imageUrl;
                               /* if(blocks.has("main")) {
                                    JSONObject main = blocks.getJSONObject("main");
                                    JSONArray elements = main.getJSONArray("elements");
                                    JSONObject ele_index = elements.getJSONObject(0);

                                    JSONArray assets = ele_index.getJSONArray("assets");
                                    if(assets.length()!=0) {
                                        JSONObject ass_index = assets.getJSONObject(0);
                                        imageUrl = ass_index.getString("file");

                                    }
                                    else
                                        imageUrl="https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";

                                }
                                else
                                    imageUrl="https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
*/
                                String title=index.getString("heading");
                                String time= (String) index.get("created_at");
                                String date2=formatDate(time);
                                time= format_time(time);

                                String section=(String) cat.get("name");
                                String imaged=(String) index.get("image");
                                String image= Global.neimage+imaged;
                                article_url =index.getString("id");
                                spinner.setVisibility(View.INVISIBLE);
      progress_t.setVisibility(View.INVISIBLE);
                                mExampleList1.add(new CardItem(image, title, time, section, article_url,title,date2));
                            }
                            if(mCardAdaptor==null)
                            {
                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                Log.i("&*","{null"+" "+mExampleList);
                                //mCardAdaptor = new CardAdaptor(Section.this.getContext(), mExampleList);
                                mCardAdaptor = new CardAdaptor(Objects.requireNonNull(getActivity()).getApplicationContext(), mExampleList);
                                mRecyclerView.setAdapter(mCardAdaptor);
                                mCardAdaptor.setOnItemClickListener(Home.this);
                                mCardAdaptor.setOnItemLongClickListener(Home.this);
                            }
                            else{
                                Log.i("adapter empty","not null");
                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                mCardAdaptor.notifyDataSetChanged();

                            }


                        }}
                        //catch (JSONException | ParseException e)
                        catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }


    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        CardItem clickedItem = mExampleList.get(position);
        detailIntent.putExtra("article_url", clickedItem.getArticle_url());
        startActivity(detailIntent);
    }
    String format_time(String dataDate) {
        String convTime = null;
        String suffix = "ago";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("PST");
        dateFormat.setTimeZone(gmtTime);
        Date pasTime = null;
        Date nowTime = new Date();

        try { pasTime = dateFormat.parse(dataDate); }
        catch (ParseException e) { e.printStackTrace(); }

        long dateDiff = nowTime.getTime() - pasTime.getTime();
        long second =Math.abs( TimeUnit.MILLISECONDS.toSeconds(dateDiff));
        long minute = Math.abs(TimeUnit.MILLISECONDS.toMinutes(dateDiff));
        long hour   = Math.abs(TimeUnit.MILLISECONDS.toHours(dateDiff));
        long day  = Math.abs(TimeUnit.MILLISECONDS.toDays(dateDiff));

        if (second < 60) { convTime = second+"s "+suffix; }
        else if (minute < 60) { convTime = minute+"m "+suffix; }
        else if (hour < 24) { convTime = hour+"h "+suffix; }
        else { convTime = day+"d "+suffix; }

        return convTime;
    }
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            //Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    public void onItemLongClick(int position, final ImageView card_bookmarks, final ImageView card_bookmarked) {
        final CardItem clickedItem = mExampleList.get(position);
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);
        ImageView ram = view.findViewById(R.id.image_view_detail);
        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(clickedItem.getTitle());
        ImageView img = dialog.findViewById(R.id.imageDialog);
        Picasso.with(getContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(img);

        ImageView twitter = dialog.findViewById(R.id.twitter);


        final ImageView bookmarks = dialog.findViewById(R.id.bookmark);
        final ImageView bookmarked=dialog.findViewById(R.id.bookmarked);
        if(check_bookmarked(clickedItem.getTitle(),clickedItem.getArticle_url())==1){
            bookmarks.setVisibility(View.INVISIBLE);
            bookmarked.setVisibility(View.VISIBLE);
            card_bookmarks.setVisibility(View.INVISIBLE);
            card_bookmarked.setVisibility(View.VISIBLE);
        }
        else{
            bookmarks.setVisibility(View.VISIBLE);
            bookmarked.setVisibility(View.INVISIBLE);
            card_bookmarks.setVisibility(View.VISIBLE);
            card_bookmarked.setVisibility(View.INVISIBLE);

        }


        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarks.setVisibility(View.INVISIBLE);
                bookmarked.setVisibility(View.VISIBLE);
                card_bookmarks.setVisibility(View.INVISIBLE);
                card_bookmarked.setVisibility(View.VISIBLE);
                bk=new BookmarkItem(clickedItem.getTitle(),clickedItem.getSection(),clickedItem.getImageUrl(),clickedItem.getBookmark_date(),clickedItem.getArticle_url());
                try {
                    bookmark_item();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(v.getContext(),
                        '"'+clickedItem.getTitle()+'"'+ " was added to bookmarks", Toast.LENGTH_SHORT).show();
            }
        });

        bookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarks.setVisibility(View.VISIBLE);
                bookmarked.setVisibility(View.INVISIBLE);
                card_bookmarks.setVisibility(View.VISIBLE);
                card_bookmarked.setVisibility(View.INVISIBLE);

                bk_delete=new BookmarkItem(clickedItem.getTitle(),clickedItem.getSection(),clickedItem.getImageUrl(),clickedItem.getBookmark_date(),clickedItem.getArticle_url());
                remove_bookmark_item();

                Toast.makeText(v.getContext(),
                        '"'+clickedItem.getTitle()+'"'+ " was removed from bookmarks", Toast.LENGTH_SHORT).show();
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Share this News";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        Global.bhim +"/mark?id="+clickedItem.getArticle_url());
                startActivity(Intent.createChooser(sharingIntent,"Share using"));


              /*  Log.i("twitter url",clickedItem.getArticle_url());
                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                        urlEncode("Check out this Link:"),
                        urlEncode(clickedItem.getWebTitle()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent)*/;
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    String filter_time(String dataDate) {
        String convTime = null;
        String suffix = "ago";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("PST");
        dateFormat.setTimeZone(gmtTime);
        Date pasTime = null;
        Date nowTime = new Date();

        try { pasTime = dateFormat.parse(dataDate); }
        catch (ParseException e) { e.printStackTrace(); }

        assert pasTime != null;
        long dateDiff = nowTime.getTime() - pasTime.getTime();
        long second = Math.abs(TimeUnit.MILLISECONDS.toSeconds(dateDiff));
        long minute = Math.abs(TimeUnit.MILLISECONDS.toMinutes(dateDiff));
        long hour   =Math.abs( TimeUnit.MILLISECONDS.toHours(dateDiff));
        long day  = Math.abs(TimeUnit.MILLISECONDS.toDays(dateDiff));

        if (second < 60) { convTime = second+"s "+suffix; }
        else if (minute < 60) { convTime = minute+"m "+suffix; }
        else if (hour < 24) { convTime = hour+"h "+suffix; }
        else { convTime = day+"d "+suffix; }

        return convTime;
    }


    private String formatDate(String str_date) throws ParseException {

        str_date=str_date.substring(0,10);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(str_date);
        String date = format.format(date1);

        if(date.endsWith("01") && !date.endsWith("11"))
            format = new SimpleDateFormat("dd MMM");

        else if(date.endsWith("02") && !date.endsWith("12"))
            format = new SimpleDateFormat("dd MMM");

        else if(date.endsWith("03") && !date.endsWith("13"))
            format = new SimpleDateFormat("dd MMM");

        else
            format = new SimpleDateFormat("dd MMM");

        String yourDate = format.format(date1);
        return yourDate;
    }
    public void bookmark_item() throws JSONException {

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        //sharedPreferences.edit().clear().commit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        if(arr==null){
            arr=new ArrayList<>();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        arr.add(bk);
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();

    }
    public void remove_bookmark_item(){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()))
                arr.remove(i);

        }

        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();

    }
    public int check_bookmarked(String title,String url){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);

        if(arr!=null) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getTitle().equals(title) ) {
                    return 1;
                }
            }
        }
        return 0;
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


                //////
                locationManager=(LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
                Log.i("loc", String.valueOf(locationManager));
            }
        } else {


            /////
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        LocationManager locationManager = null;
                        String provider = null;
                        assert locationManager != null;
                        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
                    }

                } else {


                    getActivity().finish();
                    System.exit(0);


                }
                return;
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

//        }

    }

}
