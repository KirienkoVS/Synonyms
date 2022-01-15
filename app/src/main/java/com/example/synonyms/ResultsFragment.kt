package com.example.synonyms

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.synonyms.databinding.FragmentResultsBinding
import com.example.synonyms.db.AppDatabase


class ResultsFragment : Fragment() {
    private var _binding: FragmentResultsBinding? = null
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
        _binding = FragmentResultsBinding.inflate(inflater, container, false)

        val newGameButton = binding.newGame

        val word1 = binding.word1
        val word2 = binding.word2
        val word3 = binding.word3
        val word4 = binding.word4
        val word5 = binding.word5
        val wordsList = listOf(word1, word2, word3, word4, word5)

        val answer1 = binding.answer1
        val answer2 = binding.answer2
        val answer3 = binding.answer3
        val answer4 = binding.answer4
        val answer5 = binding.answer5
        val answersList = listOf(answer1, answer2, answer3, answer4, answer5)

        newGameButton.setOnClickListener {
            viewModel.clearResults()
            findNavController().navigate(R.id.action_resultsFragment_to_synonymsFragment)
        }

        viewModel.getResults().observe(viewLifecycleOwner) { results ->
            for (i in results.indices) {
                var wrongAnswer = ""
                for (word in wordsList) {
                    wordsList[i].text = results[i].word
                    if (!results[i].isCorrect) {
                        wrongAnswer = wordsList[i].text.toString()
                    }
                }
                for (answer in answersList) {
                    answersList[i].text = results[i].answer
                    if (results[i].isCorrect) {
                       answersList[i].setBackgroundColor(Color.GREEN)
                    } else {
                        answersList[i].setBackgroundColor(Color.YELLOW)
                        answersList[i].isClickable = true
                        answersList[i].setOnClickListener {
                            val action = ResultsFragmentDirections.actionResultsFragmentToMistakeFragment(mistake = wrongAnswer)
                            findNavController().navigate(action)
                        }
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