package com.amigos.sachin.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amigos.sachin.R;
import com.amigos.sachin.VO.ChatMessageVO;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessageVO> {

    private TextView chatText;
    private List<ChatMessageVO> chatMessageList = new ArrayList<ChatMessageVO>();
    private Context context;
    private TextView time;

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
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.chat_right, parent, false);
        }else{
            row = inflater.inflate(R.layout.chat_left, parent, false);
        }
        row.setEnabled(false);
        row.setOnClickListener(null);
        Typeface typeFaceCalibri = Typeface.createFromAsset(context.getAssets(),"fonts/Calibri/Calibri.ttf");
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setTypeface(typeFaceCalibri);
        chatText.setText(chatMessageObj.message);
        time = (TextView) row.findViewById(R.id.time_right);
        time.setTypeface(typeFaceCalibri);
        time.setText(chatMessageObj.time);
        return row;
    }
}