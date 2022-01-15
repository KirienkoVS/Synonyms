package com.example.synonyms.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synonyms_table")
data class Synonyms(
    @PrimaryKey val word: String,
    val synonym1: String,
    val synonym2: String,
    val category: String
)

@Entity(tableName = "results_table")
data class Results(
    @PrimaryKey val word: String,
    val answer: String,
    val isCorrect: Boolean
)