package com.example.blooddonarfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ChatsAdapder extends RecyclerView.Adapter<ChatsAdapder.ViewHolder> {
    private List<Chats> list;
    private Context context;
    private Bitmap bitmap,userBitmap;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
   // private ParseUser parseUser;
    private String fullname;
    private String currentUserId;

    public ChatsAdapder(List<Chats> list, Context context, Bitmap bitmap, Bitmap bmp, String fullName, String currentUserId) {
        this.list = list;
        this.context = context;
        this.bitmap = bmp;
        this.userBitmap = bitmap;
        this.fullname = fullName;
        this.currentUserId=currentUserId;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        private  TextView msg_time;
        private  TextView msgg_time;
        private ImageView profileChatLeft;
        private ImageView profileChatRight;
        private ImageView profileChattLeft;
        private ImageView profileChattRight;
        private RelativeLayout layout;
        private RelativeLayout layoutChatImage;
        private  TextView chatDate;
        private ImageView imageChat;
        private Bitmap chatImage;
        private TextView profileName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.text);
            msg_time = itemView.findViewById(R.id.msg_time);
            msgg_time = itemView.findViewById(R.id.msgg_time);
            profileChatLeft = itemView.findViewById(R.id.profile_chat_left);
            profileChatRight = itemView.findViewById(R.id.profile_chat_Right);
            profileChattLeft = itemView.findViewById(R.id.profile_chatt_left);
            profileChattRight = itemView.findViewById(R.id.profile_chatt_Right);
            layout = itemView.findViewById(R.id.layoutChat);
            layoutChatImage = itemView.findViewById(R.id.layoutChatImage);
            imageChat = itemView.findViewById(R.id.image_chat);
            chatDate =itemView.findViewById(R.id.chatDate);
            //profileName = itemView.findViewById(R.id.profileName);
          //  imageMessage = itemView.findViewById(R.id.image_chat);
            if(profileChatLeft!=null && profileChattLeft!=null) {
                if(bitmap!=null) {
                    profileChatLeft.setImageBitmap(bitmap);
                    profileChattLeft.setImageBitmap(bitmap);
                }else{
                    profileChatLeft.setImageResource(R.drawable.profile_pic_change);
                    profileChattLeft.setImageResource(R.drawable.profile_pic_change);
                }
            }
            //Toast.makeText(context,bitmap.getGenerationId(), Toast.LENGTH_SHORT).show();
            if(profileChatRight!=null && profileChattRight!=null) {
                if(userBitmap!=null) {
                    profileChatRight.setImageBitmap(userBitmap);
                    profileChattRight.setImageBitmap(userBitmap);
                }else{
                    profileChatRight.setImageResource(R.drawable.profile_pic_change);
                    profileChattRight.setImageResource(R.drawable.profile_pic_change);
                }
            }


        }


        public void bind(Chats chats) {

            switch (chats.getType()){
                case "TEXT" :
                    layout.setVisibility(View.VISIBLE);
                    layoutChatImage.setVisibility(View.GONE);

                    textMessage.setText(chats.getTextMessage());
                    // String replace = chats.getDateTime();
                    String edit = chats.getDateTime();
                    if(edit!=null && !edit.isEmpty()) {
                        final String[] replace = edit.split(",");
                        msg_time.setText(replace[1]);

                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(chatDate.getVisibility()==View.INVISIBLE) {
                                    chatDate.setText(replace[0]);
                                    chatDate.setVisibility(View.VISIBLE);
                                }else{
                                    chatDate.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                    break;
                case "IMAGE" :
                    layout.setVisibility(View.GONE);
                    layoutChatImage.setVisibility(View.VISIBLE);
                    ParseFile file = chats.getImageMessage();
                    final String date = chats.getDateTime();

                    if(date!=null && !date.isEmpty()) {
                        final String[] replace = date.split(",");
                        msgg_time.setText(replace[1]);
                    }

                    if(file!=null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if (e == null && data != null) {
                                    chatImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imageChat.setImageBitmap(chatImage);

                                    imageChat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(context, "Image Clicked", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(context,ViewImageActivity.class);
                                            if(chatImage!=null) {
                                                Bitmap converetdImage = getResizedBitmap(chatImage, 750);
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                byte[] bytes = stream.toByteArray();
                                                intent.putExtra("bitmapbytes", bytes);
                                                intent.putExtra("date",date);
                                                intent.putExtra("FullName",fullname);
                                                context.startActivity(intent);
                                            }


                                        }
                                    });

                                }

                            }
                        });

                    }



                    break;
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

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).get("Sender").equals(currentUserId)){
            return MSG_TYPE_RIGHT;
        } else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
