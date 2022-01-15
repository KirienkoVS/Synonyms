package com.example.synonyms

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.synonyms.databinding.FragmentSynonymsBinding
import com.example.synonyms.db.AppDatabase
import com.example.synonyms.db.Results
import kotlin.random.Random

class SynonymsFragment : Fragment() {
    private var _binding: FragmentSynonymsBinding? = null
    private val binding get() = _binding!!

    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val viewModel: SynonymsViewModel by viewModels {
        SynonymsViewModelFactory(database.synonymsDao(), database.resultsDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSynonymsBinding.inflate(inflater, container, false)

        val word = binding.word
        val synonym1 = binding.synonym1
        val synonym2 = binding.synonym2
        val synonym3 = binding.synonym3
        val synonym4 = binding.synonym4
        val synonym5 = binding.synonym5
        val textViewsList = mutableListOf(synonym1, synonym2, synonym3, synonym4, synonym5)

        fun randomWordIndex(list: List<Any>): Int {
            return Random.nextInt(0, list.size)
        }

        fun textViewSelection(synonym: String) {
            val randomTextView = textViewsList[Random.nextInt(0, textViewsList.size)]
            randomTextView.text = synonym
            textViewsList.remove(randomTextView)
        }

        viewModel.allWords().observe(viewLifecycleOwner) { wordsList ->
            val randomNumber = Random.nextInt(0, wordsList.size)
            val randomWord = wordsList[randomNumber].word
            val category = wordsList[randomNumber].category
            word.text = randomWord

            fun addUniqueWordToList(word: String, list: MutableList<String>) {
                if (word !in list && word != randomWord) {
                    list.add(word)
                }
            }

            //Extracting synonyms for random word
            viewModel.word(randomWord).observe(viewLifecycleOwner) { sortedWord ->
                val wordsAndSynonyms = mutableListOf<String>()

                fun clickListener(textView: TextView, list: List<String>) {
                    viewModel.getResults().observe(viewLifecycleOwner) { results ->
                        textView.setOnClickListener {
                            val isCorrect: Boolean = if (textView.text in list) {
                                textView.setBackgroundColor(Color.GREEN)
                                true
                            } else {
                                textView.setBackgroundColor(Color.YELLOW)
                                false
                            }
                            if (results.size < 4) {
                                viewModel.insertResult(Results(randomWord, textView.text.toString(), isCorrect))
                                findNavController().navigate(R.id.synonymsFragment, arguments, NavOptions.Builder()
                                    .setPopUpTo(R.id.synonymsFragment, true)
                                    .build()
                                )
                            } else {
                                //                                viewModel.clearResults()
                                viewModel.insertResult(Results(randomWord, textView.text.toString(), isCorrect))
                                findNavController().navigate(R.id.action_synonymsFragment_to_resultsFragment)
                            }

                        }
                    }
                }

                for (i in textViewsList) {
                    clickListener(i, wordsAndSynonyms)
                }

                for (i in sortedWord) {
                    if(i.synonym2.isBlank()) {
                        textViewSelection(i.synonym1)
                        addUniqueWordToList(i.word, wordsAndSynonyms)
                        addUniqueWordToList(i.synonym1, wordsAndSynonyms)
                    } else {
                        val synonymsList = listOf(i.synonym2, i.synonym1)
                        textViewSelection(synonymsList[randomWordIndex(synonymsList)])
                        addUniqueWordToList(i.word, wordsAndSynonyms)
                        addUniqueWordToList(i.synonym1, wordsAndSynonyms)
                        addUniqueWordToList(i.synonym2, wordsAndSynonyms)
                    }
                }

                viewModel.category(category).observe(viewLifecycleOwner) { sortedByCategoryList ->
                    val fullCategoryList = mutableListOf<String>()
                    for (i in sortedByCategoryList) {
                        if (i.synonym2.isBlank()) {
                            addUniqueWordToList(i.word, fullCategoryList)
                            addUniqueWordToList(i.synonym1, fullCategoryList)
                        } else {
                            addUniqueWordToList(i.word, fullCategoryList)
                            addUniqueWordToList(i.synonym1, fullCategoryList)
                            addUniqueWordToList(i.synonym2, fullCategoryList)
                        }
                    }

                    for (i in 1..4) {
                        textViewSelection(fullCategoryList[randomWordIndex(fullCategoryList)])
                    }
                }

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}