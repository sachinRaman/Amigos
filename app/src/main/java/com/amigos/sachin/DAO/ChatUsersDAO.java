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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sachin on 8/28/2017.
 */

public class ChatUsersDAO extends SQLiteOpenHelper {

    private String TAG="ChatUsersDAO";
    SQLiteDatabase usersDB;
    Context context;
    public static final String DATABASE_NAME = "chatUsers.db";

    public ChatUsersDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chat_users (to_id varchar,from_id varchar,time varchar, last_message varchar, seen INTEGER) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addToChatList(String toId, String fromId, String lastMessage, int seen){
        Log.i(TAG," ChatDAO::addChat");

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from chat_users where to_id in (?) and from_id in (?) ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, toId);
            statement.bindString(2, fromId);
            statement.execute();

            String sql1="delete from chat_users where to_id in (?) and from_id in (?) ";
            SQLiteStatement statement1=db.compileStatement(sql1);
            statement.bindString(1, fromId);
            statement.bindString(2, toId);
            statement1.execute();

            ContentValues contentValues = new ContentValues();
            contentValues.put("to_id", toId);
            contentValues.put("from_id", fromId);
            contentValues.put("time",time);
            contentValues.put("last_message", lastMessage);
            contentValues.put("seen",seen);

            /*ContentValues contentValues1 = new ContentValues();
            contentValues1.put("to_id", fromId);
            contentValues1.put("from_id", toId);
            contentValues1.put("time",time);
            contentValues1.put("last_message", lastMessage);
            contentValues1.put("seen",seen);*/

            db.insert("chat_users", null, contentValues);
            /*db.insert("chat_users", null, contentValues1);*/
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public void removeFromChatList(String toId){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from chat_users where to_id in (?)";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, toId);
            statement.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }finally {
            db.close();
        }
    }

    public ArrayList<ChatUsersVO> getMyChatList(String myId){
        ArrayList<ChatUsersVO> chatUsersVOArrayList = new ArrayList<ChatUsersVO>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chat_users where from_id in('"+myId+"'"+") ",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                ChatUsersVO chatUsersVO = new ChatUsersVO();
                String fromId = res.getString(res.getColumnIndex("from_id"));
                String toId = res.getString(res.getColumnIndex("to_id"));
                String lastMessage = res.getString(res.getColumnIndex("last_message"));
                String time = res.getString(res.getColumnIndex("time"));
                int seen = res.getInt(res.getColumnIndex("seen"));

                chatUsersVO.setMyId(fromId);
                chatUsersVO.setUserId(toId);
                chatUsersVO.setLastMessage(lastMessage);
                chatUsersVO.setTime(time);
                chatUsersVO.setSeen(seen);

                chatUsersVOArrayList.add(chatUsersVO);

                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }

        return chatUsersVOArrayList;
    }

    public boolean removeFromChatUsersDAO(String userId){
        Log.i(TAG, "removeFromChatUsersDAO():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from chat_users where to_id in (?)";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1,userId);

            String sql1="delete from chat_users where from_id in (?)";
            SQLiteStatement statement1=db.compileStatement(sql1);
            statement1.bindString(1,userId);

            statement.execute();
            statement1.execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public Set<String> getMyChatUsersList(String myId){
        Set<String> chatUsers = new HashSet<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chat_users where from_id in('"+myId+"'"+") ",null);

        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){
                String toId = res.getString(res.getColumnIndex("to_id"));
                chatUsers.add(toId);

                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }

        return chatUsers;
    }

    public Set<String> getAllChatList(String myId){
        Set<String> distinctUsers = new HashSet<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from chat_users",null);


        try{
            res.moveToFirst();
            while(res.isAfterLast() == false){

                String toId=res.getString(res.getColumnIndex("to_id"));
                distinctUsers.add(toId);
                res.moveToNext();
            }
        }finally{
            res.close();
            db.close();
        }
        SharedPreferences sp=context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        final String senderUuid=sp.getString("uuid", "");
        distinctUsers.remove(senderUuid);
        return distinctUsers;
    }

    public String getTimeStamp(String receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select time from chat_users where to_id in('" + receiverId + "')", null);

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

    public String getLastMessage(String receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String msg = "";
        try {
            Cursor res = db.rawQuery("select last_message from chat_users where to_id in('" + receiverId + "')", null);

            try {

                res.moveToFirst();
                while (res.isAfterLast() == false) {

                    msg = res.getString(res.getColumnIndex("last_message"));
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
        return msg;
    }

    public int getSeen(String receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int seen = 0;
        try {
            Cursor res = db.rawQuery("select last_message from chat_users where to_id in('" + receiverId + "')", null);

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

    public boolean changeSeen (String receiverId , int seen) {
        Log.i(TAG, "changeSeen():: Entered");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="UPDATE chat_users set seen = ? where to_id = ? ";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindDouble(1,seen);
            statement.bindString(2,receiverId);


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
