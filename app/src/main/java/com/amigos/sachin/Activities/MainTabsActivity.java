package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    ImageView search_icon, overflow_icon;


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

        overflow_icon = (ImageView) findViewById(R.id.overflow_icon);

        Intent intent = getIntent();
        selectedTab = intent.getIntExtra("tab",1);
        bottomTab = intent.getIntExtra("bottomTab",0);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);

        tab.select();
        setupTabIcons();
        changeTabsIconColor();

        int tabIconColor = ContextCompat.getColor(context, R.color.md_white_1000);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(context, R.color.md_white_1000);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(context, R.color.md_grey_600);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

        overflow_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,overflow_icon);
                popup.getMenuInflater().inflate(R.menu.main_tabs_overflow_menu, popup.getMenu());
                //Toast.makeText(ChatActivity.this,"You Clicked : ",Toast.LENGTH_SHORT).show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.people_i_liked:
                                Intent intent1  = new Intent(context, PeopleILikedActivity.class);
                                startActivity(intent1);
                                return true;
                            case R.id.search_icon:
                                Intent intent  = new Intent(context, SearchActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.refresh_icon:
                                UsersFragment.reloadAllUsers();
                                UsersFragment.updateSwipeUsersAdapter(0);
                                return true;
                            case R.id.settings:
                                Intent intent2 = new Intent(context, SettingsActivity.class);
                                startActivity(intent2);
                                return true;

                        }
                        return true;
                    }
                });

                /*MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), overflow_icon);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();*/
                popup.show();
            }
        });

        //updateSharedPreferences();
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

    private void changeTabsIconColor() {
        int tabIconColor = ContextCompat.getColor(context, R.color.md_grey_600);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
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



    @Override
    protected void onResume() {
        Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/"+myId+"/active/");
        activeRef.setValue("1");
        super.onResume();
    }
}
