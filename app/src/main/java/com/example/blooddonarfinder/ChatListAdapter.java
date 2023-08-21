package com.example.blooddonarfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private List<Chatlist> list;
    private Context context;
    private Bitmap chatImage;
    private String currentUserId;

    public ChatListAdapter(List<Chatlist> list, Context context, String currentUserId) {
        this.list = list;
        this.context = context;
        this.currentUserId=currentUserId;
}
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final Chatlist chatlist = list.get(position);

        holder.tvName.setText(chatlist.getUserName());
        holder.tvDesc.setText(chatlist.getDescription());
        holder.tvDate.setText(chatlist.getDateTime());

        ParseFile file = chatlist.getImageProfile();


        // for image we need library ...
        if (file==null){
            holder.profile.setImageResource(R.drawable.profile_pic_change);  // set  default image when profile user is null
        } else {
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {

                        if (e == null && data != null) {
                           Bitmap profileImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                            holder.profile.setImageBitmap(profileImage);
                        }

                    }
                });

           // Glide.with(context).load(chatlist.getUrlProfile()).into(holder.profile);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFile parseFile = chatlist.getImageProfile();
                if(parseFile!=null){
                    parseFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null && data != null) {
                                chatImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                                if(chatImage!=null) {
                                    Bitmap converetdImage = getResizedBitmap(chatImage, 750);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    context.startActivity(new Intent(context, ChatActivity.class)
                                            .putExtra("reqObjId", chatlist.getUserID())
                                            .putExtra("currObjId", currentUserId)
                                            .putExtra("fullName", chatlist.getUserName())
                                            .putExtra("bitmapbytes", bytes));
                                }
                            }
                        }
                    });
                }else{
                    context.startActivity(new Intent(context, ChatActivity.class)
                            .putExtra("reqObjId", chatlist.getUserID())
                            .putExtra("currObjId", currentUserId)
                            .putExtra("fullName", chatlist.getUserName()));
                }

            }
        });
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
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDesc, tvDate;
        private CircleImageView profile;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
        }
    }
}

