package com.example.synonyms.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SynonymsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Synonyms)

    @Query("DELETE FROM synonyms_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM synonyms_table")
    fun getAll(): LiveData<List<Synonyms>>

    @Query("SELECT * FROM synonyms_table WHERE category = :category")
    fun getCategory(category: String): LiveData<List<Synonyms>>

    @Query("SELECT * FROM synonyms_table WHERE word = :word")
    fun getWord(word: String): LiveData<List<Synonyms>>
}

@Dao
interface ResultsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(result: Results)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(result: Results)

    @Query("SELECT * FROM results_table")
    fun getAll(): LiveData<List<Results>>

    @Query("DELETE FROM results_table")
    suspend fun deleteAll()
}