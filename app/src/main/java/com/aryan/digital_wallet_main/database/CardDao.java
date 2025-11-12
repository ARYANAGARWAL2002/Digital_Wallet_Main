package com.aryan.digital_wallet_main.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCard(CardEntity card);


    @Update
    void updateCard(CardEntity card);

    @Delete
    void deleteCard(CardEntity card);

    @Query("SELECT * FROM cards ORDER BY cardName ASC")
    LiveData<List<CardEntity>> getAllCards();

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY cardName ASC")
    LiveData<List<CardEntity>> getCardsByUserId(String userId);


    @Query("SELECT * FROM cards WHERE category = :category ORDER BY cardName ASC")
    LiveData<List<CardEntity>> getCardsByCategory(String category);


    @Query("SELECT * FROM cards WHERE userId = :userId AND category = :category ORDER BY cardName ASC")
    LiveData<List<CardEntity>> getCardsByUserIdAndCategory(String userId, String category);


    @Query("SELECT * FROM cards WHERE id = :id")
    LiveData<CardEntity> getCardById(int id);

    @Query("SELECT * FROM cards WHERE expiryDate <= :date AND expiryDate != ''")
    List<CardEntity> getCardsAboutToExpire(String date);

    @Query("SELECT * FROM cards WHERE userId = :userId AND expiryDate <= :date AND expiryDate != ''")
    List<CardEntity> getCardsAboutToExpireByUserId(String userId, String date);


    @Query("SELECT * FROM cards WHERE userId = :userId AND expiryDate <= :date AND expiryDate != '' ORDER BY expiryDate ASC")
    LiveData<List<CardEntity>> getLiveCardsAboutToExpireByUserId(String userId, String date);
}