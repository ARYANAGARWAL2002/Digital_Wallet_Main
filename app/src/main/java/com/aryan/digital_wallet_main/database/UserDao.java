package com.aryan.digital_wallet_main.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    LiveData<UserEntity> getUserByEmail(String email);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    LiveData<Integer> checkEmailExists(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    LiveData<UserEntity> getUserByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();
}