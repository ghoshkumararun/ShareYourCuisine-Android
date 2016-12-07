package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Menu;
import com.toe.shareyourcuisine.service.MenuService;
import com.toe.shareyourcuisine.utils.SYCUtils;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by HQu on 12/4/2016.
 */

public class CreateMenuActivity extends AppCompatActivity implements MenuService.CreateNewMenuListener{

    private static final String TAG = "ToeCMenuActivity:";
    private MaterialEditText mTitleET;
    private MaterialBetterSpinner mCookingTimeSpin;
    private MaterialEditText mContentET;
    private Button mSelectImgBtn;
    private LinearLayout mContentImgLayout;
    private LayoutInflater mContentImgLayoutInflater;
    private ImageView mSelectedImageIV;
    private Button mSubmitBtn;
    private ArrayList<String> mContentImgUrls;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
            }
        };

        mTitleET = (MaterialEditText)findViewById(R.id.title_et);
        mCookingTimeSpin = (MaterialBetterSpinner)findViewById(R.id.cooking_time_spin);
        mContentET = (MaterialEditText)findViewById(R.id.content_et);
        mSelectImgBtn = (Button)findViewById(R.id.select_img_btn);
        mContentImgLayout = (LinearLayout)findViewById(R.id.content_img_layout);
        mSelectedImageIV = (ImageView)findViewById(R.id.selected_img);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mContentImgLayoutInflater = LayoutInflater.from(CreateMenuActivity.this);

        final String[] COUNTRIES = new String[] {
                "Belgium", "France", "Italy", "Germany", "Spain"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        mCookingTimeSpin.setAdapter(adapter);

        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(CreateMenuActivity.this)
                        .setActionBarColor(Color.rgb(211, 47, 47), Color.rgb(211, 47, 47))
                        .setPickerCount(5)
                        .setCamera(true)
                        .startAlbum();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu = new Menu();
                menu.setTitle(mTitleET.getText().toString());
                menu.setCookingTime("asd");
                menu.setDisplayImgUrl("aa");
                menu.setContent(mContentET.getText().toString());
                menu.setContentImgUrls(mContentImgUrls);
                menu.setCreatedBy(mUser.getUid());
                menu.setCreatedAt(SYCUtils.getCurrentEST());
                menu.setLastCommentedAt(SYCUtils.getCurrentEST());
                MenuService menuService = new MenuService();
                menuService.createMenu(menu, mContentImgUrls.get(0));
                menuService.setCreateNewMenuListener(CreateMenuActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    mContentImgUrls = data.getStringArrayListExtra(Define.INTENT_PATH);
                    mContentImgLayout.removeAllViews();
                    //Initiate first image as default selected
                    Picasso.with(CreateMenuActivity.this).load(new File(mContentImgUrls.get(0))).fit().centerCrop().into(mSelectedImageIV);
                    for (int i = 0; i < mContentImgUrls.size(); i++) {
                        View view = mContentImgLayoutInflater.inflate(R.layout.content_img_item, mContentImgLayout, false);
                        ImageView iv = (ImageView) view.findViewById(R.id.content_img);
                        final String imgUrl = mContentImgUrls.get(i);
                        Picasso.with(CreateMenuActivity.this).load(new File(imgUrl)).fit().centerCrop().into(iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Picasso.with(CreateMenuActivity.this).load(new File(imgUrl)).fit().centerCrop().into(mSelectedImageIV);
                            }
                        });
                        mContentImgLayout.addView(view);
                    }
                }
        }
    }

    @Override
    public void createNewMenuSucceed() {
        Toast.makeText(CreateMenuActivity.this, "Create menu successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void createNewMenuFail(String errorMsg) {
        Toast.makeText(CreateMenuActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}