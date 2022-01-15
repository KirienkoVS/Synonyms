package com.example.synonyms

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.synonyms.databinding.FragmentMistakeBinding
import com.example.synonyms.db.AppDatabase
import com.example.synonyms.db.Results
import kotlin.random.Random

private const val MISTAKE = "mistake"

class MistakeFragment : Fragment() {
    private var _binding: FragmentMistakeBinding? = null
    private val binding get() = _binding!!

    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val viewModel: SynonymsViewModel by viewModels {
        SynonymsViewModelFactory(database.synonymsDao(), database.resultsDao())
    }

    private lateinit var wrongAnswer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wrongAnswer = it.get(MISTAKE).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        //Inflate the layout for this fragment
        _binding = FragmentMistakeBinding.inflate(inflater, container, false)

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

        fun addUniqueWordToList(word: String, list: MutableList<String>) {
            if (word !in list && word != wrongAnswer) {
                list.add(word)
            }
        }

        word.text = wrongAnswer

        //Extracting synonyms for a given word
        viewModel.word(wrongAnswer).observe(viewLifecycleOwner) { sortedWord ->
            val wordsAndSynonyms = mutableListOf<String>()
            var category = ""

            fun clickListener(textView: TextView, list: List<String>) {
                textView.setOnClickListener {
                    val isCorrect: Boolean = if (textView.text in list) {
                        textView.setBackgroundColor(Color.GREEN)
                        true
                    } else {
                        textView.setBackgroundColor(Color.YELLOW)
                        false
                    }
                    viewModel.updateResult(Results(wrongAnswer, textView.text.toString(), isCorrect))
                    findNavController().navigate(R.id.action_mistakeFragment_to_resultsFragment)
                }
            }

            for (i in textViewsList) {
                clickListener(i, wordsAndSynonyms)
            }

            for (i in sortedWord) {
                if (i.word == wrongAnswer) {
                    category = i.category
                }

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}