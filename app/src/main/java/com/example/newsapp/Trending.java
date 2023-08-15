package com.example.newsapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SuppressLint("ValidFragment")
public class Trending extends Fragment implements CardAdaptor.OnItemClickListener, CardAdaptor.OnItemLongClickListener{
    private RecyclerView mRecyclerView;
    View view;
    public static  String article_url = "article_url";
    private ArrayList<CardItem> mExampleList;
    private CardAdaptor mCardAdaptor;
    private String section_name;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView progress_t;
    BookmarkItem bk;
    BookmarkItem bk_delete;
    private ProgressBar spinner;
    int position;
    @SuppressLint("ValidFragment")

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        Section tabFragment = new Section();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_trending, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        progress_t=view.findViewById(R.id.progress_text);
        spinner = view.findViewById(R.id.progressBar1);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(Trending.this.getContext()));

        spinner.setVisibility(View.VISIBLE);
        progress_t.setVisibility(View.VISIBLE);
        mExampleList = new ArrayList<>();
        section_headlines();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                spinner.setVisibility(View.GONE);
                progress_t.setVisibility(View.GONE);
                section_headlines();
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
        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume(); // paste toast here
        Log.i("position", String.valueOf(position)+"*r");
       section_headlines();
    }

    private void section_headlines() {

        final ArrayList<CardItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
        //String url = "http://192.168.1.3:8000/api/client/post";
        String url = Global.bhim+"/trending";

        // String url = "https://dummyjson.com/products/search?q="+section_name;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(getActivity() != null) {

                                Log.e("gita", response.toString());

                                JSONArray res = response.getJSONArray("posts");
                                JSONArray ca = response.getJSONArray("cates");

                                //JSONObject data = response.getJSONObject("data");
//                            JSONObject response = data.getJSONObject("response");
                                //JSONObject json = data.getJSONObject("response");
                                //JSONArray results=json.getJSONArray("results");
                                //JSONArray results=response.getJSONArray("results");
                                String section = "";

                                for (int i = 0; i < res.length(); i++) {
                                    System.out.println(res.length());
                                    JSONObject mm = res.getJSONObject(i);
                                    JSONObject index = mm.getJSONObject("post");
                                    //JSONObject cat = index.getJSONObject("category");
                                    for (int a = 0; a < ca.length(); a++) {
                                        JSONObject cat = ca.getJSONObject(a);

                                        if (cat.getString("id") == index.getString("category_id")) {
                                            section = (String) cat.getString("name");

                                        }


                                    }

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
                                    String title = index.getString("heading");
                                    String time = (String) index.get("created_at");
                                    String date2 = formatDate(time);
                                    time = format_time(time);

                                    String imaged = (String) index.get("image");
                                    String image = Global.neimage + imaged;
                                    article_url = index.getString("id");
                                    mExampleList1.add(new CardItem(image, title, time, section, article_url, title, date2));
                                }
                                if (mCardAdaptor == null) {
                                    mExampleList.clear();

                                    mExampleList.addAll(mExampleList1);
                                    Log.i("&*", "{null" + " " + mExampleList);
                                    //mCardAdaptor = new CardAdaptor(Section.this.getContext(), mExampleList);
                                    mCardAdaptor = new CardAdaptor(Objects.requireNonNull(getActivity()).getApplicationContext(), mExampleList);

                                    mRecyclerView.setAdapter(mCardAdaptor);
                                    mCardAdaptor.setOnItemClickListener(Trending.this);
                                    mCardAdaptor.setOnItemLongClickListener(Trending.this);
                                } else {
                                    Log.i("adapter empty", "not null");
                                    mExampleList.clear();
                                    mExampleList.addAll(mExampleList1);
                                    mCardAdaptor.notifyDataSetChanged();

                                }
                                spinner.setVisibility(View.GONE);
                                progress_t.setVisibility(View.GONE);

                            }} catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 10000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);

    }

    public void onItemClick(int position) {
        Bundle bundle=new Bundle();

        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        CardItem clickedItem = mExampleList.get(position);

        detailIntent.putExtra("article_url", clickedItem.getArticle_url());
        startActivity(detailIntent);
    }
    public void onItemLongClick(int position, final ImageView card_bookmarks, final ImageView card_bookmarked) {
        final CardItem clickedItem = mExampleList.get(position);
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);

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




            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
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
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    public void bookmark_item() throws JSONException {

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
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
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()) )
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


}