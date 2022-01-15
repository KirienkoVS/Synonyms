package com.example.synonyms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.synonyms.db.Results
import com.example.synonyms.db.ResultsDao
import com.example.synonyms.db.Synonyms
import com.example.synonyms.db.SynonymsDao
import kotlinx.coroutines.launch

class SynonymsViewModel(private val synonymsDao: SynonymsDao, private val resultsDao: ResultsDao): ViewModel() {

    fun allWords(): LiveData<List<Synonyms>> = synonymsDao.getAll()
    fun category(category: String): LiveData<List<Synonyms>> = synonymsDao.getCategory(category)
    fun word(word: String): LiveData<List<Synonyms>> = synonymsDao.getWord(word)

    fun clearDB() = viewModelScope.launch {
        synonymsDao.deleteAll()
    }

    fun insertResult(result: Results) = viewModelScope.launch {
        resultsDao.insert(result)
    }

    fun updateResult(result: Results) = viewModelScope.launch {
        resultsDao.update(result)
    }

    fun clearResults() = viewModelScope.launch {
        resultsDao.deleteAll()
    }

    fun getResults(): LiveData<List<Results>> = resultsDao.getAll()
}

class SynonymsViewModelFactory(
    private val synonymsDao: SynonymsDao,
    private val resultsDao: ResultsDao
    ): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SynonymsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SynonymsViewModel(synonymsDao, resultsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}