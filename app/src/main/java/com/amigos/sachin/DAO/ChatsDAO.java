package com.amigos.sachin.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sachin.VO.ChatUsersVO;

import java.util.ArrayList;

/**
 * Created by Sachin on 11/4/2017.
 */

public class ChatsDAO extends SQLiteOpenHelper {

    private String TAG="ChatsDAO";
    SQLiteDatabase usersDB;
    Context context;
    public static final String DATABASE_NAME = "chats.db";
    String myId;

    public ChatsDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chats (user_id varchar, message_id varchar, message varchar, time varchar, seen INTEGER, user_side INTEGER) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addToChatList(String userId, String mesaageId, String message, String time, int seen, int userSide){
        Log.i(TAG," ChatDAO::addChat");


        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userId);
            contentValues.put("message_id", mesaageId);
            contentValues.put("time",time);
            contentValues.put("message", message);
            contentValues.put("seen",seen);
            contentValues.put("user_side",userSide);

            db.insert("chats", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public ArrayList<ChatUsersVO> getMyChatList(String user_id){
        ArrayList<ChatUsersVO> chatUsersVOArrayList = new ArrayList<ChatUsersVO>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select DISTINCT * from chats where user_id in('"+user_id+"'"+") ORDER BY message_id ASC",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                ChatUsersVO chatUsersVO = new ChatUsersVO();
                String userId = res.getString(res.getColumnIndex("user_id"));
                String messageId = res.getString(res.getColumnIndex("message_id"));
                String message = res.getString(res.getColumnIndex("message"));
                String time = res.getString(res.getColumnIndex("time"));
                int seen = res.getInt(res.getColumnIndex("seen"));
                int userSide = res.getInt(res.getColumnIndex("user_side"));


                chatUsersVO.setMyId(myId);
                chatUsersVO.setUserId(userId);
                chatUsersVO.setMessageId(messageId);
                chatUsersVO.setLastMessage(message);
                chatUsersVO.setTime(time);
                chatUsersVO.setSeen(seen);
                chatUsersVO.setUserSide(userSide);

                chatUsersVOArrayList.add(chatUsersVO);

                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }

        return chatUsersVOArrayList;
    }

    public boolean removeDuplicateEntries (String userId){
        Log.i(TAG, "removeDuplicateEntries():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE FROM chats WHERE rowid NOT IN (  SELECT MIN(rowid) FROM chats GROUP BY message_id)";
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


    public boolean deleteUserChat(String userId){
        Log.i(TAG, "deleteUserChat():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE FROM chats WHERE user_id IN (?)";
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

    public String getTimeStamp(String messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select time from chats where message_id in('" + messageId + "')", null);

        String timeStamp = "";
        try {
            res.moveToFirst();
            while (res.isAfterLast() == false) {

                timeStamp = res.getString(res.getColumnIndex("time"));
                res.moveToNext();
            }
        } finally {
            res.close();
            db.close();
        }
        return timeStamp;
    }

    public String getUserId(String messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select user_id from chats where message_id in('" + messageId + "')", null);

        String userId = "";
        try {
            res.moveToFirst();
            while (res.isAfterLast() == false) {

                userId = res.getString(res.getColumnIndex("user_id"));
                res.moveToNext();
            }
        } finally {
            res.close();
            db.close();
        }
        return userId;
    }

    public String getMessage(String messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select message from chats where message_id in('" + messageId + "')", null);

        String message = "";
        try {
            res.moveToFirst();
            while (res.isAfterLast() == false) {

                message = res.getString(res.getColumnIndex("message"));
                res.moveToNext();
            }
        } finally {
            res.close();
            db.close();
        }
        return message;
    }

    public int getSeen(String messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int seen = 0;
        try {
            Cursor res = db.rawQuery("select seen from chats where message_id in('" + messageId + "')", null);

            try {

                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    seen = res.getInt(res.getColumnIndex("seen"));
                    res.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.close();
                db.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return seen;
    }

    public int getUserSide(String messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userSide = 0;
        try {
            Cursor res = db.rawQuery("select user_side from chats where message_id in('" + messageId + "')", null);

            try {

                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    userSide = res.getInt(res.getColumnIndex("user_side"));
                    res.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                res.close();
                db.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return userSide;
    }

    public boolean changeSeen (String messageId , int seen) {
        Log.i(TAG, "changeSeen():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE chats set seen = ? where message_id = ? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindDouble(1,seen);
            statement.bindString(2,messageId);


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

    public boolean setMessageId(String messageId, String userId, String message, String time){
        Log.i(TAG, "setMessageId():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE chats set message_id = ? where user_id = ? AND message = ? AND time = ?";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,messageId);
            statement.bindString(2,userId);
            statement.bindString(3,message);
            statement.bindString(4,time);



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

    public boolean markMessageAsSeen(String userId, String messageId){
        Log.i(TAG, "markMessageAsSeen():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE chats set seen = 3 where user_id IN (?) AND seen IN (1,2) AND message_id <= ?";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,userId);
            statement.bindString(2,messageId);

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

    public boolean markMessageAsReached(String userId, String messageId){
        Log.i(TAG, "markMessageAsSeen():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE chats set seen = 2 where user_id IN (?) AND seen IN (1) AND message_id <= ?";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,userId);
            statement.bindString(2,messageId);

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

    public boolean deleteChat(String userId){
        Log.i(TAG, "markMessageAsSeen():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="DELETE from chats where user_id IN (?) ";
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
