package com.example.blooddonarfinder;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView image_profile_chat;
    private boolean isActionShown = false;
    Chats chats;
    boolean mFirstLoad;
    String fullName;
    String objectId;
    String currentUserId;
    String receiverObjectId;
    TextView username;
    ImageButton btnBack;
    EditText text_message;
    FloatingActionButton sendButton;
    ImageView cameraImageView;
    ImageView btnFile;
    ImageView btnCamera;
    ImageView emojiImageView;
    Bitmap bmp,userBitmap;
    ParseFile file;
    private ChatsAdapder adapder;
    private List<Chats> list;
    RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ParseUser parseUser;
    private ParseObject object;
    private static final String TAG = "ChatsActivity";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private int IMAGE_GALLERY_REQUEST = 11;
    private Uri imageUri;




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();


        parseUser = ParseUser.getCurrentUser();
        object = new ParseObject("ChatList");
        ParseObject.registerSubclass(Chats.class);




        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        if(bytes!=null) {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            image_profile_chat = (CircleImageView) findViewById(R.id.image_profile_chat);
            image_profile_chat.setImageBitmap(bmp);
        }else{
            image_profile_chat = (CircleImageView) findViewById(R.id.image_profile_chat);
            image_profile_chat.setImageResource(R.drawable.profile_pic_change);
        }

        fullName = getIntent().getStringExtra("fullName");
        objectId = getIntent().getStringExtra("reqObjId");
        currentUserId = getIntent().getStringExtra("currObjId");
        username = (TextView) findViewById(R.id.tv_username);
        username.setText(fullName);


        btnBack = (ImageButton)findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_message = (EditText) findViewById(R.id.text_message);
        sendButton = (FloatingActionButton) findViewById(R.id.btn_send);
        cameraImageView = (ImageView)findViewById(R.id.btn_camera);
        emojiImageView = (ImageView) findViewById(R.id.btn_emoji);


        text_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(text_message.getText().toString())){
                    sendButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
                    cameraImageView.setVisibility(View.VISIBLE);
                } else {
                    sendButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_send_24));
                    cameraImageView.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnFile = (ImageView) findViewById(R.id.btn_file);
        btnCamera  =(ImageView) findViewById(R.id.btn_camera);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        list = new ArrayList<>();
       // adapder = new ChatsAdapder(list,ChatActivity.this);
       // recyclerView.setAdapter(adapder);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        //layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        initBtnClick();
        readChats();
       // refreshMessages();


    }

    private void initBtnClick(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(text_message.getText().toString())){
                    sendTextMessage(text_message.getText().toString());

                    text_message.setText("");
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_profile_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, ProfileActivity.class)
                        .putExtra("objectId",objectId)
                        .putExtra("userProfile",bmp)
                        .putExtra("userName",fullName));
            }
        });

      /*  btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShown){
                    layoutActions.setVisibility(View.GONE);
                    isActionShown = false;
                } else {
                    layoutActions.setVisibility(View.VISIBLE);
                    isActionShown = true;
                }

            }
        });*/

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }


    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            //uploadToFirebase();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void reviewImage(final Bitmap bitmap){
        new DialogReviewSendImage(ChatActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSendClick() {
                // to Upload Image to firebase storage to get url image...
                if (imageUri!=null){
                    final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                    progressDialog.setMessage("Sending image...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    final ParseFile file = new ParseFile("image.png", byteArray);
                    sendImageMessage(file ,progressDialog);


                    //hide action buttonss
                    //layoutActions.setVisibility(View.GONE);
                    //isActionShown = false;

                   /* new ParseObject(ChatActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            // to send chat image//
                            chatService.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });*/
                }

            }
        });
    }

    private void sendImageMessage(ParseFile file, final ProgressDialog progressDialog) {

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        chats = new Chats();
        chats.setDateTime(today+", "+currentTime);
        chats.setImageMessage(file);
        chats.setType("IMAGE");
        chats.setSender(currentUserId);
        chats.setReceiver(objectId);

        chats.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.d("Send", "onSuccess: ");
                    //refreshMessages();
                    readChats();
                    progressDialog.dismiss();
                }else {
                    Log.d("Send", "onFailure: "+e.getMessage());
                }
            }
        });

        ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("ChatList");
        objectParseQuery.whereEqualTo("chatId",objectId);
        objectParseQuery.whereEqualTo("myObjectId",currentUserId);
        objectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e==null){
                    parseObject.put("myObjectId", currentUserId);
                    parseObject.put("lastMsg", chats.getType());
                    parseObject.put("lastMsgTime", chats.getDateTime());
                    parseObject.put("myUserId", parseUser.getObjectId());
                    parseObject.put("chatId", objectId);
                    parseObject.saveInBackground();
                }else{
                    if(e.getCode()==ParseException.OBJECT_NOT_FOUND) {
                        parseObject = new ParseObject("ChatList");
                        parseObject.put("myObjectId", currentUserId);
                        parseObject.put("lastMsg", chats.getType());
                        parseObject.put("lastMsgTime", chats.getDateTime());
                        parseObject.put("myUserId", parseUser.getObjectId());
                        parseObject.put("chatId", objectId);
                        parseObject.saveInBackground();
                    }
                }
            }
        });



       // object.setObjectId(objectId);
        //object.put("chatId",parseUser.getObjectId());

    }

    private void sendTextMessage(String text){

        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(date);

        Calendar currentDateTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(currentDateTime.getTime());

        chats = new Chats();
                chats.setDateTime(today+", "+currentTime);
                chats.setTextMessage(text);
                chats.setType("TEXT");
                chats.setSender(currentUserId);
                chats.setReceiver(objectId);

        chats.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.d("Send", "onSuccess: ");
                    Toast.makeText(ChatActivity.this,chats.getTextMessage(), Toast.LENGTH_SHORT).show();
                    //refreshMessages();
                    readChats();
                }else {
                    Log.d("Send", "onFailure: "+e.getMessage());
                }
            }
        });


        ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("ChatList");
        objectParseQuery.whereEqualTo("chatId",objectId);
        objectParseQuery.whereEqualTo("myObjectId",currentUserId);
        objectParseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e==null){
                    parseObject.put("myObjectId", currentUserId);
                    parseObject.put("lastMsg", chats.getTextMessage());
                    parseObject.put("lastMsgTime", chats.getDateTime());
                    parseObject.put("myUserId", parseUser.getObjectId());
                    parseObject.put("chatId", objectId);
                    parseObject.saveInBackground();
                }else{
                    if(e.getCode()==ParseException.OBJECT_NOT_FOUND) {
                        parseObject = new ParseObject("ChatList");
                        parseObject.put("myObjectId", currentUserId);
                        parseObject.put("lastMsg", chats.getTextMessage());
                        parseObject.put("lastMsgTime", chats.getDateTime());
                        parseObject.put("myUserId", parseUser.getObjectId());
                        parseObject.put("chatId", objectId);
                        parseObject.saveInBackground();
                    }
                }
            }
        });

       // object.setObjectId(objectId);
        //object.put("chatId",parseUser.getObjectId());

    }

    void refreshMessages() {
        ParseQuery<Chats> query = ParseQuery.getQuery(Chats.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Chats>() {
            public void done(List<Chats> chat, ParseException e) {
                if (e == null) {
                    list.clear();

                        list.addAll(chat);
                    Toast.makeText(ChatActivity.this,chat.get(0).getSender(), Toast.LENGTH_SHORT).show();
                    //list.addAll(chat);
                    if (adapder!=null){
                        adapder.notifyDataSetChanged();
                    }else {
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
                        query1.whereEqualTo("MobileNumber",parseUser.getUsername());

                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if(objects.size()>0){
                                        file = (ParseFile) objects.get(0).get("image");
                                        if(file!=null) {
                                            file.getDataInBackground(new GetDataCallback() {
                                                @Override
                                                public void done(byte[] data, ParseException e) {

                                                    if (e == null && data != null) {
                                                        userBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        adapder = new ChatsAdapder(list, ChatActivity.this, userBitmap, bmp,fullName,currentUserId);
                                                        recyclerView.setAdapter(adapder);
                                                    }

                                                }
                                            });

                                        }

                                    }
                                } else {
                                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    //adapder.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        recyclerView.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

        // Create a handler which can run code periodically
        final int POLL_INTERVAL = 1000; // milliseconds
        final Handler myHandler = new android.os.Handler();
        Runnable mRefreshMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                myHandler.postDelayed(this, POLL_INTERVAL);
            }
        };
    }


    private void readChats(){
        try {
            ParseQuery<Chats> query1 = ParseQuery.getQuery(Chats.class);
            query1.whereEqualTo("Sender", currentUserId);
            query1.whereEqualTo("Receiver", objectId);

            // What NotMe sent to ME:
            ParseQuery<Chats> query2 = ParseQuery.getQuery(Chats.class);
            query2.whereEqualTo("Receiver", currentUserId);
            query2.whereEqualTo("Sender", objectId);

            List<ParseQuery<Chats>> queries = new ArrayList<ParseQuery<Chats>>();

            queries.add(query1);
            queries.add(query2);

            // Sort by ascending order:
            ParseQuery<Chats> query = ParseQuery.or(queries);
            query.orderByDescending("createdAt");

            query.findInBackground(new FindCallback<Chats>() {
                @Override
                public void done(List<Chats> chat, ParseException e) {
                    if (e == null) {
                        if (chat.size() > 0) {

                            list.clear();

                            list.addAll(chat);
                            if (adapder!=null){
                                adapder.notifyDataSetChanged();
                            }else {
                                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
                                query1.whereEqualTo("MobileNumber",parseUser.getUsername());

                                query1.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if(objects.size()>0){
                                                file = (ParseFile) objects.get(0).get("image");
                                                if(file!=null) {
                                                    file.getDataInBackground(new GetDataCallback() {
                                                        @Override
                                                        public void done(byte[] data, ParseException e) {

                                                            if (e == null && data != null) {
                                                                userBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                adapder = new ChatsAdapder(list, ChatActivity.this, userBitmap, bmp, fullName, currentUserId);
                                                                recyclerView.setAdapter(adapder);
                                                            }

                                                        }
                                                    });

                                                }

                                            }
                                        } else {
                                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            if (mFirstLoad) {
                                recyclerView.scrollToPosition(0);
                                mFirstLoad = false;
                            }
                        }
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        // Create a handler which can run code periodically
        final int POLL_INTERVAL = 1000; // milliseconds
        final Handler myHandler = new android.os.Handler();
        Runnable mRefreshMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                //refreshMessages();
                readChats();
                myHandler.postDelayed(this, POLL_INTERVAL);
            }
        };

    }

}