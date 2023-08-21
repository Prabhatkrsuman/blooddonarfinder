package com.example.blooddonarfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private List<Chatlist> list;
    private Handler handler = new Handler();

    private ArrayList<String> allUserID;

    private ArrayList<String> lastMsgChatAll;
    private ArrayList<String> lastMsgTimeAll;

    private ChatListAdapter adapter;
    RecyclerView recyclerView;
    ParseUser parseUser;
    ProgressBar progressBar;
    String userID;
    String currentUserId;
    String lastMsg;
    String time;
    LinearLayout linearLayout;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        setTitle("Chats");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();
        lastMsgChatAll = new ArrayList<>();
        lastMsgTimeAll = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        linearLayout = (LinearLayout) findViewById(R.id.ln_invite);
        parseUser = ParseUser.getCurrentUser();

        Intent intent = getIntent();
        currentUserId=intent.getStringExtra("currObjId");


       // LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        //layoutManager.setStackFromEnd(true);
       // layoutManager.setReverseLayout(true);
       // recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        adapter = new ChatListAdapter(list,ChatListActivity.this,currentUserId);
        recyclerView.setAdapter(adapter);

        if (parseUser!=null) {
            getChatList();
        }
    }

    private void getChatList() {
        progressBar.setVisibility(View.VISIBLE);
        list.clear();
        allUserID.clear();
        lastMsgChatAll.clear();
        lastMsgTimeAll.clear();

        ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("ChatList");
        objectParseQuery.whereEqualTo("myObjectId",currentUserId);
        objectParseQuery.whereEqualTo("myUserId",ParseUser.getCurrentUser().getObjectId());
       // objectParseQuery.include("lastMsg");
        objectParseQuery.orderByDescending("updatedAt");
        objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                for(ParseObject object:objects) {
                    userID = (String) object.get("chatId");
                    lastMsg = object.getString("lastMsg");
                    time = object.getString("lastMsgTime");
                    //ParseObject id = object.getParseObject("lastMsg");
                    //String msg = id.getString("TextMessage");
                    //Log.i("Message",msg);

                    allUserID.add(userID);
                    lastMsgChatAll.add(lastMsg);
                    lastMsgTimeAll.add(time);
                    progressBar.setVisibility(View.GONE);
                }
                    getUserInfo();
                }else {

                    ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("ChatList");
                    objectParseQuery.whereEqualTo("chatId",currentUserId);
                    //objectParseQuery.whereEqualTo("receiverObjectId",ParseUser.getCurrentUser().getObjectId());
                    // objectParseQuery.include("lastMsg");
                    objectParseQuery.orderByDescending("updatedAt");
                    objectParseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e==null){
                                for(ParseObject object:objects) {
                                    userID = (String) object.get("myObjectId");
                                    lastMsg = object.getString("lastMsg");
                                    time = object.getString("lastMsgTime");
                                    //ParseObject id = object.getParseObject("lastMsg");
                                    //String msg = id.getString("TextMessage");
                                    //Log.i("Message",msg);

                                    allUserID.add(userID);
                                    lastMsgChatAll.add(lastMsg);
                                    lastMsgTimeAll.add(time);
                                    progressBar.setVisibility(View.GONE);
                                }
                                getUserInfo();
                            }else{
                                Toast.makeText(ChatListActivity.this, "not fetched", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

               // getUserInfo();

            }
        });

    }

    private void getUserInfo() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                    for (int i=0; i<allUserID.size() && i<lastMsgChatAll.size() && i<lastMsgTimeAll.size() ;i++){
                       String userId = allUserID.get(i);
                       final String lastMsg = lastMsgChatAll.get(i);
                       final String time = lastMsgTimeAll.get(i);
                        ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("UserDetails");
                        objectParseQuery.getInBackground(userId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                try {

                                    Chatlist chat = new Chatlist();
                                    chat.setUserID(object.getObjectId());
                                    chat.setUserName(object.getString("FullName"));
                                    chat.setDescription(lastMsg);
                                    chat.setDateTime(time);
                                    chat.setImageProfile(object.getParseFile("image"));

                                    list.add(chat);

                                } catch (Exception e1) {
                                    Log.d(TAG, "onSuccess: " + e1.getMessage());
                                }
                                if (adapter != null) {
                                    adapter.notifyItemInserted(0);
                                    adapter.notifyDataSetChanged();

                                    Log.d(TAG, "onSuccess: adapter " + adapter.getItemCount());
                                }
                            }

                        });

                    }
                }
        });

    }

   /* private void showList() {
        try {
        ParseQuery<Chatlist> query = ParseQuery.getQuery(Chatlist.class);
        query.orderByDescending("createdAt");

            query.findInBackground(new FindCallback<Chatlist>() {
                @Override
                public void done(List<Chatlist> chat, ParseException e) {
                    if (e == null) {
                        if (chat.size() > 0) {

                            list.clear();

                            list.addAll(chat);
                            if (adapter!=null){
                                adapter.notifyDataSetChanged();
                            }else {
                                adapter = new ChatListAdapter(list,ChatListActivity.this, currentUserId);
                                recyclerView.setAdapter(adapter);
                            }

                        }
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }

   /* private void readChats(){
        try {
            ParseQuery<Chats> query1 = ParseQuery.getQuery(Chats.class);
            query1.whereEqualTo("Sender", ParseUser.getCurrentUser().getObjectId());
            query1.whereEqualTo("Receiver", objectId);

            // What NotMe sent to ME:
            ParseQuery<Chats> query2 = ParseQuery.getQuery(Chats.class);
            query2.whereEqualTo("Receiver", ParseUser.getCurrentUser().getObjectId());
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
    }*/


}