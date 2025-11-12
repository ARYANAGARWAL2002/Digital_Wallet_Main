package com.aryan.digital_wallet_main.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {CardEntity.class, UserEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "digital_wallet_db")
                    .fallbackToDestructiveMigration() // Use this only during development
                    .build();
        }
        return INSTANCE;
    }

    public abstract CardDao cardDao();
    public abstract UserDao userDao();
}