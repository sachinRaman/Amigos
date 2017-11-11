package com.amigos.sachin.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.amigos.sachin.VO.InterestsVO;
import com.amigos.sachin.VO.LikedUserVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sachin on 8/29/2017.
 */

public class PeopleILikedDAO extends SQLiteOpenHelper {

    private String TAG="PeopleILikedDAO";
    SQLiteDatabase usersDB;

    public static final String DATABASE_NAME = "peopleIliked2.db";
    public PeopleILikedDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS people_i_liked (id varchar,name varchar, time varchar, status varchar, imageUrl varchar)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<LikedUserVO> getAllPeopleIliked() {
        ArrayList<LikedUserVO> peopleIlikedVOList = new ArrayList<LikedUserVO>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from people_i_liked", null );
        res.moveToFirst();

        try {
            while (res.isAfterLast() == false) {
                LikedUserVO likedUserVO = new LikedUserVO();
                String userId = res.getString(res.getColumnIndex("id"));
                String name = res.getString(res.getColumnIndex("name"));
                String status = res.getString(res.getColumnIndex("status"));
                String time = res.getString(res.getColumnIndex("time"));
                String imageUrl = res.getString(res.getColumnIndex("imageUrl"));
                likedUserVO.setName(name);
                likedUserVO.setId(userId);
                likedUserVO.setStatus(status);
                likedUserVO.setTime(time);
                likedUserVO.setImageUrl(imageUrl);
                peopleIlikedVOList.add(likedUserVO);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return peopleIlikedVOList;
    }

    public boolean addUserToPeopleILikedList(String userId,String name, String status, String imageUrl){
        Log.i(TAG," PeopleILikedDAO::addUserToPeopleILikedList");

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from people_i_liked where id in (?)";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, userId);
            statement.execute();

            ContentValues contentValues = new ContentValues();
            contentValues.put("id", userId);
            contentValues.put("time", time);
            contentValues.put("status", status);
            contentValues.put("name", name);
            contentValues.put("imageUrl", imageUrl);

            db.insert("people_i_liked", null, contentValues);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public boolean removeFromPeopleILikedList(String userId){
        Log.i(TAG," PeopleILikedDAO::addUserToPeopleILikedList");

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sql="delete from people_i_liked where id in (?)";
            SQLiteStatement statement=db.compileStatement(sql);
            statement.bindString(1, userId);
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

    public ArrayList<String> getAllPeopleIlikedId() {
        ArrayList<String> peopleIlikedList = new ArrayList<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from people_i_liked", null );
        res.moveToFirst();

        try {
            while (res.isAfterLast() == false) {
                String userId = res.getString(res.getColumnIndex("id"));
                peopleIlikedList.add(userId);
                res.moveToNext();
            }
        }finally {
            res.close();
            db.close();
        }
        return peopleIlikedList;
    }

    public String getTime(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select time from people_i_liked where id in('" + userId + "')", null);

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

}
