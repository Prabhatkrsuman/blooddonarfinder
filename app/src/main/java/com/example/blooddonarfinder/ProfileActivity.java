package com.example.blooddonarfinder;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.blooddonarfinder.fragment.DeatilsFragment;
import com.example.blooddonarfinder.fragment.HomeFragment;
import com.example.blooddonarfinder.fragment.LogOutFragment;
import com.example.blooddonarfinder.fragment.NotificationFragment;
import com.example.blooddonarfinder.fragment.SettingFragment;
//import com.example.blooddonarfinder.other.CircleTransform;
import com.example.blooddonarfinder.other.CircleTransform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile , imgProfileChange;
    private TextView txtName, txtEmail;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    private FloatingActionButton fab;
    private Toolbar toolbar;
    Bitmap bitmap;
    ParseFile file;
    String currentUserId;

    TextView textNotificationItemCount,textChatsItemCount;
    int mNotifyItemCount = 10;
    int mChatsNotifyItemCount = 10;
    private static final int galleryPicker = 1;
    // urls to load navigation header background image
    // and profile image
    //  private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    // private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_YOUR_REQUEST = "your request";
    private static final String TAG_NOTIFICATION = "notification";
    private static final String TAG_SETTING = "setting";
    private static final String TAG_LOGOUT = "logout";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);



    // Navigation view header
    navHeader =navigationView.getHeaderView(0);
    txtName =(TextView)navHeader.findViewById(R.id.name);
    txtEmail =(TextView)navHeader.findViewById(R.id.email);
    imgNavHeaderBg =(ImageView)navHeader.findViewById(R.id.img_header_bg);
    imgProfile =(ImageView)navHeader.findViewById(R.id.img_profile);
    imgProfileChange =(ImageView)navHeader.findViewById(R.id.img_plus);


        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
        query1.whereEqualTo("MobileNumber", ParseUser.getCurrentUser().getUsername());

        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects.size()>0){
                            file = (ParseFile) objects.get(0).get("image");
                            currentUserId = objects.get(0).getObjectId();
                            if(file!=null) {
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {

                                        if (e == null && data != null) {
                                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                            //ImageView imageView = new ImageView(getApplicationContext());
                                                    /*imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                            ViewGroup.LayoutParams.MATCH_PARENT
                                                    ));*/
                                            ImageView imageView = (ImageView) findViewById(R.id.img_profile);
                                            imageView.setImageBitmap(bitmap);


                                        }

                                    }
                                });
                            }else{
                                // Loading profile image
                                Glide.with(ProfileActivity.this).load(R.drawable.profile_pic_change)
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(getApplicationContext()))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imgProfile);
                            }

                            // name, email
                            txtName.setText(objects.get(0).getString("FullName"));
                            txtEmail.setText(objects.get(0).getString("Email"));



                    }
                } else {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


      /*  imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this,ProfilePhotoActivity.class);
                startActivity(intent);
            /*Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,galleryPicker);
            }
        });*/

        imgProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,ProfilePhotoActivity.class);
                if(bitmap!=null) {
                    Bitmap converetdImage = getResizedBitmap(bitmap, 750);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    intent.putExtra("bitmapbytes", bytes);
                    Log.i("Bitmapcount", String.valueOf(bytes.length));
                    startActivity(intent);
                }else {
                    startActivity(intent);
                }
            }
        });


       // int badgeCount = 16;
      //  ShortcutBadger.applyCount(ProfileActivity.this, badgeCount);

    // load toolbar titles from string resources
    activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

    // load nav menu header data
    loadNavHeader();

    // initializing navigation menu
    setUpNavigationView();

        if(savedInstanceState ==null)

    {
        navItemIndex = 0;
        CURRENT_TAG = TAG_HOME;
        loadHomeFragment();
    }

}





    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {

        // loading header background image
        Glide.with(this).load(R.drawable.background_image1)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);


        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                // photos
                DeatilsFragment deatilsFragment = new DeatilsFragment();
                return deatilsFragment;
            case 2:
                // movies fragment
                YourRequestFragment yourRequestFragment = new YourRequestFragment();
                return yourRequestFragment;
            case 3:
                // movies fragment
                NotificationFragment notificationFragment = new NotificationFragment();
                return notificationFragment;
            case 4:
                // notifications fragment
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;

            case 5:
                // settings fragment
                LogOutFragment logOutFragment = new LogOutFragment();
                return logOutFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_details:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DETAILS;
                        break;
                    case R.id.nav_your_request:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_YOUR_REQUEST;
                        break;
                    case R.id.nav_notification:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATION;
                        break;
                    case R.id.nav_setting:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTING;
                        break;
                    case R.id.nav_log_out:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_LOGOUT;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ProfileActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ProfileActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_contact_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ProfileActivity.this, ContactUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_help_and_feedback:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ProfileActivity.this, HelpAndFeedbackActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);

            final MenuItem menuItemNotification = menu.findItem(R.id.option_notification);

            View actionViewNotify = menuItemNotification.getActionView();
            textNotificationItemCount = (TextView) actionViewNotify.findViewById(R.id.notification_badge);

            setupBadge();

            actionViewNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItemNotification);
                }
            });

        }

        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.profile, menu);

            final MenuItem menuItem = menu.findItem(R.id.option_chat);

            View actionView = menuItem.getActionView();
            textChatsItemCount = (TextView) actionView.findViewById(R.id.chats_badge);

            setupChatsBadge();

            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItem);
                }
            });

            final MenuItem menuItemNotification = menu.findItem(R.id.option_notification);

            View actionViewNotify = menuItemNotification.getActionView();
            textNotificationItemCount = (TextView) actionViewNotify.findViewById(R.id.notification_badge);

            setupBadge();

            actionViewNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItemNotification);
                }
            });


        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }*/

        if (id == R.id.option_notification) {
            Toast.makeText(getApplicationContext(), "All notifications shows!", Toast.LENGTH_LONG).show();
        }

        if (id == R.id.option_chat) {
            Toast.makeText(getApplicationContext(), "All Chats shows!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),ChatListActivity.class);
            intent.putExtra("currObjId",currentUserId);
            startActivity(intent);
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (textNotificationItemCount != null) {
            if (mNotifyItemCount == 0) {
                if (textNotificationItemCount.getVisibility() != View.GONE) {
                    textNotificationItemCount.setVisibility(View.GONE);
                }
            } else {
                textNotificationItemCount.setText(String.valueOf(Math.min(mNotifyItemCount, 99)));
                if (textNotificationItemCount.getVisibility() != View.VISIBLE) {
                    textNotificationItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setupChatsBadge() {

        if (textChatsItemCount != null) {
            if (mChatsNotifyItemCount == 0) {
                if (textChatsItemCount.getVisibility() != View.GONE) {
                    textChatsItemCount.setVisibility(View.GONE);
                }
            } else {
                textChatsItemCount.setText(String.valueOf(Math.min(mNotifyItemCount, 99)));
                if (textChatsItemCount.getVisibility() != View.VISIBLE) {
                    textChatsItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}

