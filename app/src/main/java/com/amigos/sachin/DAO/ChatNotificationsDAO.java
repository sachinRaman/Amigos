package com.amigos.sachin.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sachin.VO.NotificationVO;

import java.util.ArrayList;

/**
 * Created by ADMIN on 12/5/2017.
 */

public class ChatNotificationsDAO extends SQLiteOpenHelper {

    private String TAG="ChatNotificationsDAO";
    SQLiteDatabase usersDB;
    Context context;
    public static final String DATABASE_NAME = "chatNotofications.db";
    String myId;

    public ChatNotificationsDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chatNotifications (user_id varchar, name varchar, time_stamp varchar, message varchar) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addToNotificationList(String userId, String name, String message, String timeStamp){
        Log.i(TAG," ChatNotificationsDAO::addToNotificationList");


        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userId);
            contentValues.put("name", name);
            contentValues.put("time_stamp",timeStamp);
            contentValues.put("message", message);

            db.insert("chatNotifications", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public ArrayList<NotificationVO> getAllNotificationsList(){
        ArrayList<NotificationVO> notificationsVOArrayList = new ArrayList<NotificationVO>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select DISTINCT * from chatNotifications ORDER BY user_id, time_stamp ASC",null);
        String prevId = "";
        NotificationVO notificationVO = new NotificationVO();
        boolean flag = false;
        ArrayList<String> messageArrList = new ArrayList<String>();

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){
                flag = true;

                String userId = res.getString(res.getColumnIndex("user_id"));
                String name = res.getString(res.getColumnIndex("name"));
                String message = res.getString(res.getColumnIndex("message"));
                String timeStamp = res.getString(res.getColumnIndex("time_stamp"));

                if("".equalsIgnoreCase(prevId)) {
                    prevId = userId;
                }else{
                    if(prevId.equalsIgnoreCase(userId)){
                    }else{
                        notificationsVOArrayList.add(notificationVO);
                        notificationVO = new NotificationVO();
                        prevId = userId;
                        messageArrList = new ArrayList<String>();
                    }
                }

                messageArrList.add(message);

                notificationVO.setId(userId);
                notificationVO.setName(name);
                notificationVO.setTime(timeStamp);
                notificationVO.setMessageArrayList(messageArrList);

                res.moveToNext();
            }
            if(flag){
                notificationsVOArrayList.add(notificationVO);
            }
        }
        catch(Exception e){

        }finally{
            res.close();
            db.close();
        }

        return notificationsVOArrayList;
    }

    public boolean removeDuplicateEntries (){
        Log.i(TAG, "removeDuplicateEntries():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE FROM chatNotifications WHERE rowid NOT IN (  SELECT MIN(rowid) FROM chatNotifications GROUP BY time_stamp)";
            SQLiteStatement statement=db.compileStatement(sql);

            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public boolean deleteUserNotifications(String userId){
        Log.i(TAG, "deleteUserChat():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE FROM chatNotifications WHERE user_id IN (?)";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,userId);

            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }
}
