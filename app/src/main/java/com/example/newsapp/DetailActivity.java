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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

public class DetailActivity extends AppCompatActivity implements CardAdaptor.OnItemClickListener, CardAdaptor.OnItemLongClickListener{
    private RecyclerView mRecyclerView;
    View view;
    public static  String article_url = "article_url";
    private ArrayList<CardItem> mExampleList;
    private CardAdaptor mCardAdaptor;
    private String section_name;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView progress_t;
    TextView progress_t1;

    BookmarkItem bk;
    BookmarkItem bk_delete;
    String query;
    Toolbar toolbar;
    TextView no_search1;
    private ProgressBar spinner;

    private ProgressBar spinner1;

    ImageView imgView;
    TextView title1,reporter;
    TextView desc;
    TextView date1;
    TextView section1;
    TextView full_news;
    private MenuItem bookmark;
    private MenuItem bookmarked;
    private MenuItem twitter;
    CardView cardView;
    String main_title;
    String full_news1;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_detail);
        Intent intent=getIntent();
        url=intent.getStringExtra("article_url");
        parseJSON(url);

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        progress_t=findViewById(R.id.progress_text);
        no_search1=findViewById(R.id.no_search);
        spinner = findViewById(R.id.progressBar1);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
        spinner.setVisibility(View.VISIBLE);
        progress_t.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mExampleList = new ArrayList<>();
       // no_search1.setVisibility(View.GONE);
        search_results(url);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                spinner.setVisibility(View.GONE);
                progress_t.setVisibility(View.GONE);
                search_results(url);
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

       // setContentView(R.layout.activity_detail);

        imgView = findViewById(R.id.image_view_detail);
        title1 = findViewById(R.id.detailed_title);
        reporter = findViewById(R.id.report);

        desc = findViewById(R.id.detailed_desc);
        date1=findViewById(R.id.detailed_date);
        section1=findViewById(R.id.detailed_section);
//        full_news=findViewById(R.id.detailed_url);
        cardView=findViewById(R.id.card_view);
        progress_t1=findViewById(R.id.progress_text1);
        spinner1 = findViewById(R.id.progressBar2);
        spinner1.setVisibility(View.VISIBLE);
        progress_t1.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        Log.e("hhh",url);

    }
    private void search_results(final String url) {


       // toolbar.setTitle("Search Results for "+query);


        final ArrayList<CardItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        // String url = Global.neimage+"api/client/post/search?q="+query;

        String uri=Global.bhim +"/mark?id="+url;
       // JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,

        //String url = Global.neimage+"api/client/post/mark?id=35";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("hero",response.toString());
                            //JSONObject data = response.getJSONObject("data");
                            //JSONObject response = data.getJSONObject("response");
                            //JSONObject json = data.getJSONObject("response");
                            JSONArray results=response.getJSONArray("posts");
                            JSONArray relates=response.getJSONArray("posties");

                            if(results.length()==0)
                            {
                                no_search1.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                                progress_t.setVisibility(View.GONE);
                                return;
                            }
                            int a=0;
                            JSONObject content = results.getJSONObject(a);
                            JSONObject ram = content.getJSONObject("category");
                            JSONArray user = content.getJSONArray("users");
                            JSONObject name = user.getJSONObject(a);

                            Log.e("laxmu",ram.getString("name"));

                            String title=content.getString("heading");

                            Log.e("mul",title);
                            String date=content.getString("created_at");
                            String date2=formatDate(date);
                            System.out.println(date2);
                            date1.setText(date2);
                            String imaged=(String) content.get("image");
                            String imageUrl= Global.neimage+imaged;

                            String section=ram.getString("name");
                            String rep=name.getString("name");
                            reporter.setText("By "+rep);

                                    ;
//                            Log.e("mul",section);
                            String des=content.getString("description");

                            full_news1=content.getString("description");
                            // Log.e("bhim",full_news1.toString());

                            if(check_bookmarked(title,url)==1)
                            {
                                bookmark.setVisible(false);
                                // show the menu item
                                bookmarked.setVisible(true);
                                twitter.setVisible(true);
                            }
                            else{
                                bookmarked.setVisible(false);
                                bookmark.setVisible(true);
                                twitter.setVisible(true);
                            }
                            title1.setText(title);
                            main_title=title;
                            section1.setText(section);
                            toolbar.setTitle(title);
                            String desc1="";

                            bk=new BookmarkItem(title,section,imageUrl,bookmark_date(date),article_url);

                            Spanned sp = Html.fromHtml(des);
                            desc.setText(sp);
                            Picasso.with(DetailActivity.this).load(imageUrl).fit().centerInside().into(imgView);
                            spinner1.setVisibility(View.GONE);
                            progress_t1.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);

                            for (int i = 0; i < relates.length(); i++) {

                                JSONObject index = relates.getJSONObject(i);
                                JSONObject cc = index.getJSONObject("category");

                                // JSONObject blocks=index.getJSONObject("blocks");
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
                             /*   String title=index.getString("webTitle");
                                String time= (String) index.get("webPublicationDate");
                                String date2=formatDate(time);
                                time= format_time(time);

                                String section=(String) index.get("sectionName");
                                String webUrl=(String) index.get("webUrl");
                                article_url =index.getString("id");
                                mExampleList1.add(new CardItem(imageUrl, title, time, section, article_url,webUrl,date2));
*/
                                String title1=index.getString("heading");
                                String time= (String) index.get("created_at");

                                String date3=formatDate(time);


                                String section1=(String) cc.get("name");
                                String imaged1=(String) index.get("image");
                                String image= Global.neimage+imaged1;
                                article_url =index.getString("id");
                                mExampleList1.add(new CardItem(image, title1, date3, section1, article_url,title1,date3));


                            }
                            if(mCardAdaptor==null)
                            {
                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                mCardAdaptor = new CardAdaptor(Objects.requireNonNull(getApplicationContext()), mExampleList);
                                mRecyclerView.setAdapter(mCardAdaptor);
                                mCardAdaptor.setOnItemClickListener(DetailActivity.this);
                                mCardAdaptor.setOnItemLongClickListener(DetailActivity.this);
                            }
                            else{

                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                mCardAdaptor.notifyDataSetChanged();

                            }
                            spinner.setVisibility(View.GONE);
                            progress_t.setVisibility(View.GONE);

                        } catch (JSONException | ParseException e) {
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
    @Override
    public void onResume()
    {
        super.onResume(); // paste toast here

        parseJSON(url);
    }
    public int check_bookmarked(String title,String url){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        if(arr!=null) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getTitle().equals(title)) {

                    return 1;
                }


            }
        }
        return 0;

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar, menu);
        bookmark = menu.findItem(R.id.bookmark);
        bookmarked = menu.findItem(R.id.bookmarked);
        twitter=menu.findItem(R.id.share);
        bookmarked.setVisible(false);
        bookmark.setVisible(false);
        twitter.setVisible(false);

        return true;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            share(); // close this activity and return to preview activity (if there is any)
        }
//
        return super.onOptionsItemSelected(item);
    }
    public void share(){

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Share this News";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                Global.bhim +"/mark?id="+full_news1);
        startActivity(Intent.createChooser(sharingIntent,"Share using"));
/*
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                urlEncode("Check out this Link:"),
                urlEncode(full_news1));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        startActivity(intent);*/
    }



    private void parseJSON(final String url){
        final ArrayList<CardItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        String uri=Global.bhim +"/mark?id="+url;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("mula",response.toString());

                            String article_url=url;
                            JSONArray results=response.getJSONArray("posts");

                           // JSONObject data = response.getJSONObject("data");
//                            JSONObject response = response.getJSONObject("response");
                            //JSONObject json = data.getJSONObject("response");
                            //JSONObject json = response.getJSONObject("response");
                            int a=0;
                            JSONObject content = results.getJSONObject(a);
                            JSONObject ram = content.getJSONObject("category");
                            Log.e("laxmu",ram.getString("name"));

                            String title=content.getString("heading");

                            Log.e("mul",title);
                            String date=content.getString("created_at");
                            String date2=formatDate(date);
                            System.out.println(date2);
                            date1.setText(date2);
                            String imaged=(String) content.get("image");
                            String imageUrl= Global.neimage+imaged;

                            String section=ram.getString("name")
                                    ;
//                            Log.e("mul",section);
                            String des=content.getString("description");

                            full_news1=content.getString("description");
                           // Log.e("bhim",full_news1.toString());

                            if(check_bookmarked(title,url)==1)
                            {
                                bookmark.setVisible(false);
                                // show the menu item
                                bookmarked.setVisible(true);
                                twitter.setVisible(true);
                            }
                            else{
                                bookmarked.setVisible(false);
                                bookmark.setVisible(true);
                                twitter.setVisible(true);
                            }
                            title1.setText(title);
                            main_title=title;
                            section1.setText(section);
                            toolbar.setTitle(title);
                            full_news.setClickable(true);
                            Spanned html = Html.fromHtml(
                                    "<a href='"+full_news1+"'>View Full Article</a>");

                            full_news.setMovementMethod(LinkMovementMethod.getInstance());

                            // Set TextView text from html
                            full_news.setText(html);
//

                            //JSONObject blocks=content.getJSONObject("category");
                            /*String imageUrl;
                            if(blocks.has("main")) {
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
*//*
                            JSONArray body=response.getJSONArray("posts");
                            String desc1="";
                            for(int i=0;i<body.length();i++){
                                JSONObject index = body.getJSONObject(i);
                                String bodyHTML=index.getString("description");
                                desc1=bodyHTML+desc1;
                            }
*/
                            JSONArray tad=content.getJSONArray("tags");
/*
                            for (int i = 0; i < tad.length(); i++) {

                                    JSONObject tadu = tad.getJSONArray("Tag_name");

                                JSONObject index = tad.getJSONObject(i);
                                String bodyHTML=index.getString("description");
                                desc1=bodyHTML+desc1;
                            }*/
                            String desc1="";

                            bk=new BookmarkItem(title,section,imageUrl,bookmark_date(date),article_url);

                            Spanned sp = Html.fromHtml(des);
                            desc.setText(sp);
                            Picasso.with(DetailActivity.this).load(imageUrl).fit().centerInside().into(imgView);
                            spinner.setVisibility(View.GONE);
                            progress_t.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
//
    }
    @SuppressLint("SimpleDateFormat")
    private String formatDate(String str_date) throws ParseException {

        str_date=str_date.substring(0,10);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(str_date);
        String date = format.format(date1);

        if(date.endsWith("01") && !date.endsWith("11"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else if(date.endsWith("02") && !date.endsWith("12"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else if(date.endsWith("03") && !date.endsWith("13"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else
            format = new SimpleDateFormat("dd MMM yyyy");

        String yourDate = format.format(date1);
        return yourDate;
    }
    private String bookmark_date(String str_date) throws ParseException {

        str_date=str_date.substring(0,10);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(str_date);
        String date = format.format(date1);

        if(date.endsWith("01") && !date.endsWith("11"))
            format = new SimpleDateFormat("d MMM");

        else if(date.endsWith("02") && !date.endsWith("12"))
            format = new SimpleDateFormat("d MMM");

        else if(date.endsWith("03") && !date.endsWith("13"))
            format = new SimpleDateFormat("d MMM");

        else
            format = new SimpleDateFormat("d MMM");

        String yourDate = format.format(date1);
        return yourDate;
    }

    public void set_bookmark(MenuItem item) {

        Toast.makeText(getApplicationContext(),
                '"'+bk.getTitle()+'"'+ " was added to bookmarks", Toast.LENGTH_SHORT).show();
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        bookmark.setVisible(false);
        bookmarked.setVisible(true);


    }
    public void onItemLongClick(int position, final ImageView card_bookmarks, final ImageView card_bookmarked) {
        Log.i("in item long click","longggg");
        final CardItem clickedItem = mExampleList.get(position);
//        final Dialog dialog = new Dialog(Objects.requireNonNull(getApplicationContext()));
        final Dialog dialog = new Dialog(DetailActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);

        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(clickedItem.getTitle());
        ImageView img = dialog.findViewById(R.id.imageDialog);
        Picasso.with(getApplicationContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(img);

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
                        '"'+clickedItem.getTitle()+'"'+ " was removed to bookmarks", Toast.LENGTH_SHORT).show();
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


                /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                String a = urlEncode(clickedItem.getWebTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT,a);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                */

                /*String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                        urlEncode("Check out this Link:"),
                        urlEncode(clickedItem.getWebTitle()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent);*/
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    public void remove_bookmark_item(){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()) && arr.get(i).getDetailed_url().equals(bk_delete.getDetailed_url()))
                arr.remove(i);

        }
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();

    }
    public void bookmark_item() throws JSONException {

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
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


    public void onItemClick(int position) {

        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        CardItem clickedItem = mExampleList.get(position);
        detailIntent.putExtra("article_url", clickedItem.getArticle_url());
        startActivity(detailIntent);
    }
    public void remove_bookmark(MenuItem item) {

        Toast.makeText(getApplicationContext(),
                '"'+bk.getTitle()+'"'+ " was removed from bookmarks", Toast.LENGTH_SHORT).show();
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk.getTitle()))
                arr.remove(i);

        }
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();
        bookmark.setVisible(true);
        bookmarked.setVisible(false);

    }
}
