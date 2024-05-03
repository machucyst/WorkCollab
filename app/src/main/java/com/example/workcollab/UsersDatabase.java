package com.example.workcollab;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UsersDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "tbl_users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_PASSWORD = "user_password";
    public static final String COLUMN_USER_FIRST_NAME = "user_first_name";
    public static final String COLUMN_USER_LAST_NAME = "user_last_name";
    public static final String COLUMN_USER_MIDDLE_INITIAL = "user_middle_initial";
    public static final String COLUMN_USER_AGE = "user_age";
    public static final String COLUMN_USER_CONTACT_NUMBER = "user_contact_number";
    public static final String COLUMN_USER_ADDRESS = "user_address";
    public static final String COLUMN_USER_PROFILE = "user_profile";
    public static final String COLUMN_USER_GROUPS_JSON ="user_groups";

    public UsersDatabase(@Nullable Context context) {
        super(context, TABLE_USERS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL
            (
                "CREATE TABLE IF NOT EXISTS "+TABLE_USERS+" " +
                "("+COLUMN_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_USER_PASSWORD +" TEXT, " +
                COLUMN_USER_FIRST_NAME +" TEXT, " +
                COLUMN_USER_LAST_NAME +" TEXT, " +
                COLUMN_USER_MIDDLE_INITIAL +" TEXT, " +
                COLUMN_USER_AGE +" TEXT, " +
                COLUMN_USER_CONTACT_NUMBER +" TEXT, " +
                COLUMN_USER_ADDRESS +" TEXT, " +
                COLUMN_USER_GROUPS_JSON +" TEXT, " +
                COLUMN_USER_PROFILE +" BLOB )"

            );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        Gson gson = new Gson();

        cv.put(COLUMN_USER_EMAIL, user.getEmail());
        cv.put(COLUMN_USER_NAME, user.getName());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(TABLE_USERS, null, cv);
    }
    @SuppressLint("Range")
    public User findUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USERS+" WHERE "+COLUMN_USER_EMAIL+" = ?", new String[]{email});

        User user = null;

        if (cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return user;
    }
    
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues cv = new ContentValues();

        Gson gson = new Gson();
        String getGroups = gson.toJson(user.getGroups());

        cv.put(COLUMN_USER_EMAIL, user.getEmail());
        cv.put(COLUMN_USER_NAME, user.getName());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());
        cv.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
        cv.put(COLUMN_USER_LAST_NAME, user.getLastName());
        cv.put(COLUMN_USER_MIDDLE_INITIAL, user.getMiddleInitial());
        cv.put(COLUMN_USER_AGE, user.getAge());
        cv.put(COLUMN_USER_CONTACT_NUMBER, user.getContactNumber());
        cv.put(COLUMN_USER_PROFILE, user.getProfile());
        cv.put(COLUMN_USER_ADDRESS,user.getAddress());
        cv.put(COLUMN_USER_GROUPS_JSON,getGroups);


        db.update(TABLE_USERS, cv, COLUMN_USER_EMAIL + " = ? ", new String[] {user.getEmail()});
    }
    public int editData(String userEmail, String column, String newData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column,newData);
        return db.update(TABLE_USERS,cv,COLUMN_USER_EMAIL + " = ?" ,new String[]{userEmail});
    }

    public int editData(String userEmail, String column, byte[] newData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column,newData);
        return db.update(TABLE_USERS,cv,COLUMN_USER_EMAIL + " = ?" ,new String[]{userEmail});
    }

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                allUsers.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());

        }

        db.close();
        cursor.close();

        for (User user :
                allUsers) {
            System.out.println(user.getName());
        }

        return allUsers;
    }
    @SuppressLint("Range")
    private User getUserFromCursor(Cursor cursor) {
        String user_name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME));
        String user_email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
        String user_password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
        int user_age = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_AGE));
        String first_name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME));
        String last_name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME));
        String middle_name = cursor.getString(cursor.getColumnIndex(COLUMN_USER_MIDDLE_INITIAL));
        String contactnumb = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CONTACT_NUMBER));
        String address = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ADDRESS));
        byte[] profile = cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_PROFILE));
        String usergroups = cursor.getString(cursor.getColumnIndex(COLUMN_USER_GROUPS_JSON));

        Type GroupType = new TypeToken<List<Group>>(){}.getType();

        Gson gson = new Gson();
        List<Group>groups = gson.fromJson(usergroups,GroupType);

        return new User(user_name, user_email, user_password, first_name,last_name, middle_name,user_age, contactnumb, profile ,address,groups);
    }
}
