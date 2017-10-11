package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amigos.sachin.Adapters.ViewPagerAdapter;
import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.CustomViews.CustomViewPager;
import com.amigos.sachin.MainFragments.ChatsFragment;
import com.amigos.sachin.MainFragments.MyProfileFragment;
import com.amigos.sachin.MainFragments.UsersFragment;
import com.amigos.sachin.R;
import com.firebase.client.Firebase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainTabsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private int[] tabIcons = {R.drawable.ic_account_circle_white_18dp,
            R.drawable.ic_group_white_18dp,
            R.drawable.ic_message_white_18dp};
    Context context;
    final int TIME_DELAY = 2000;
    long back_pressed = 0;
    int selectedTab = 1;
    int bottomTab = 0;
    String myId;
    ImageView search_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        context = getApplicationContext();

        //Hide the action bar
        getSupportActionBar().hide();

        Firebase.setAndroidContext(context);

        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/active/");
        activeRef.setValue("1");

        Intent intent = getIntent();
        selectedTab = intent.getIntExtra("tab",1);
        bottomTab = intent.getIntExtra("bottomTab",0);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        search_icon = (ImageView) findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainTabsActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);
        tab.select();
        setupTabIcons();
        updateSharedPreferences();
    }



    private void setupViewPager(ViewPager viewPager) {
        Bundle bundle=new Bundle();
        bundle.putInt("bottomTab", bottomTab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyProfileFragment(), "My Profile");
        adapter.addFragment(new UsersFragment(), "Users");
        ChatsFragment chatsFragment = new ChatsFragment();
        chatsFragment.setArguments(bundle);
        adapter.addFragment(chatsFragment, "Chats");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            //super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            Toast.makeText(context, "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void updateSharedPreferences(){
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        Set<String> set = new HashSet<String>();
        Set set1 = new HashSet<String>();
        set1.addAll(ApplicationCache.peopleIBlockedList);
        set1.addAll(ApplicationCache.peopleWhoBlockedMeList);

        if(ApplicationCache.myUserVO.getInterests() != null) {
            set.addAll(ApplicationCache.myUserVO.getInterests());
        }
        sp.edit().putStringSet("interests", set)
                .putString("name",ApplicationCache.myUserVO.getName())
                .putStringSet("blocked",set1)
                .apply();
    }

    @Override
    protected void onResume() {
        Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/active/");
        activeRef.setValue("1");
        super.onResume();
    }
}
