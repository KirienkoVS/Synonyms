package com.example.synonyms

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.synonyms.databinding.ActivityMainBinding
import com.example.synonyms.db.AppDatabase

class MainActivity : AppCompatActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: SynonymsViewModel by viewModels {
        SynonymsViewModelFactory(database.synonymsDao(), database.resultsDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.clearResults()
    }
}