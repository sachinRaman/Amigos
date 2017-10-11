package com.amigos.sachin.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigos.sachin.DAO.ChatUsersDAO;
import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatMessageVO;
import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessageVO> {

    private EmojiconTextView chatText;
    private List<ChatMessageVO> chatMessageList = new ArrayList<ChatMessageVO>();
    private Context context;
    private TextView time;
    private ImageView messageSeen;
    String myId;

    @Override
    public void add(ChatMessageVO object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessageVO getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessageVO chatMessageObj = getItem(position);
        View row = convertView;
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        ChatUsersDAO chatUsersDAO = new ChatUsersDAO(context);
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.chat_right, parent, false);
            /*chatUsersDAO.addToChatList(chatMessageObj.userId,myId,chatMessageObj.message,0);*/
        }else{
            row = inflater.inflate(R.layout.chat_left, parent, false);
            /*chatUsersDAO.addToChatList(chatMessageObj.userId,myId,chatMessageObj.message,0);*/
        }
        row.setEnabled(false);
        row.setOnClickListener(null);
        Typeface typeFaceCalibri = Typeface.createFromAsset(context.getAssets(),"fonts/Calibri/Calibri.ttf");
        chatText = (EmojiconTextView) row.findViewById(R.id.msgr);
        chatText.setTypeface(typeFaceCalibri);
        chatText.setText(chatMessageObj.message);
        chatText.setEmojiconSize(75);
        time = (TextView) row.findViewById(R.id.time_right);
        time.setTypeface(typeFaceCalibri);
        time.setText(chatMessageObj.time);

        String seen = chatMessageObj.seen;
        if (chatMessageObj.left){
            messageSeen = (ImageView) row.findViewById(R.id.messageSeen);
            if("1".equalsIgnoreCase(seen)){
                messageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_white_18dp));
            }
            if("2".equalsIgnoreCase(seen)){
                messageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_all_white_18dp));
            }
            if("3".equalsIgnoreCase(seen)){
                messageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_all_white_18dp));
                messageSeen.setColorFilter(Color.parseColor("#40e0d0"));
            }
        }
        return row;
    }
}