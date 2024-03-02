package com.example.wordle

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import kotlin.math.log
import kotlin.properties.Delegates
import com.github.jinatonic.confetti.CommonConfetti
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.confetto.BitmapConfetto


class MainActivity : AppCompatActivity() {
    lateinit var userInput:EditText
    lateinit var buttonC: Button
    lateinit var buttonR: Button
    lateinit var finalResult: TextView
    val wordGenerator: FourLetterWordList = FourLetterWordList()
    lateinit var wordToGuess:String
    lateinit var guessWord: TextView
    lateinit var guessResult: TextView
    var count= 0
    var streak=0
    lateinit var container:ViewGroup
    lateinit var highScore:TextView
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userInput=findViewById(R.id.userInput)
        buttonC=findViewById<Button>(R.id.buttonCheck)
        buttonR=findViewById<Button>(R.id.buttonReset)
        finalResult=findViewById(R.id.textView)
        wordToGuess=wordGenerator.getRandomFourLetterWord()
        container=findViewById(R.id.container)
        highScore=findViewById(R.id.streak)

        highScore.text="Streak: " + streak.toString()

        Log.d("Debug","Word to guess:" + this.wordToGuess)

        buttonC.setOnClickListener(){
            if(!userInput.text.isEmpty() && userInput.text.toString().length == 4 && charCheck(userInput.text.toString())){

                if(count == 0){
                    guessWord=findViewById(R.id.guess1)
                    guessResult=findViewById(R.id.result1)

                }
                else if(count == 1) {
                    guessWord=findViewById(R.id.guess2)
                    guessResult=findViewById(R.id.result2)

                }
                else if (count == 2) {
                    guessWord = findViewById(R.id.guess3)
                    guessResult = findViewById(R.id.result3)

                }
                //Correct guess
                count++

                guessWord.text=userInput.text.toString()
                userInput.text.clear()
                guessResult.text=checkGuess(guessWord.text.toString().uppercase())
                val guessResultText = guessResult.text
                val isAllGreen = if (guessResultText is SpannableString) {
                    guessResultText.getSpans(0, guessResultText.length, BackgroundColorSpan::class.java)
                        .all { span ->
                            span.backgroundColor == Color.GREEN
                        }
                } else {
                    false // Handle the case when guessResultText is not a SpannableString
                }
                if (isAllGreen) {
                    win()
                } else if (count == 2 && !isAllGreen) {
                    stuck()
                }
            }
            else{
                Toast.makeText(it.context,"Please type in a 4-letter word",Toast.LENGTH_SHORT).show()
            }
        }

        buttonR.setOnClickListener(){
            count = 0
            findViewById<TextView>(R.id.guess1).text=""
            findViewById<TextView>(R.id.result1).text=""
            findViewById<TextView>(R.id.guess2).text=""
            findViewById<TextView>(R.id.result2).text=""
            findViewById<TextView>(R.id.guess3).text=""
            findViewById<TextView>(R.id.result3).text=""
            findViewById<TextView>(R.id.congrat).visibility=View.INVISIBLE
            findViewById<TextView>(R.id.sorry).visibility=View.INVISIBLE
            userInput.isEnabled= TRUE
            wordToGuess=wordGenerator.getRandomFourLetterWord()
            finalResult.text=""
            buttonR.visibility=View.INVISIBLE
            buttonC.visibility=View.VISIBLE
        }

        }



    /**
     * Parameters / Fields:
     *   wordToGuess : String - the target word the user is trying to guess
     *   guess : String - what the user entered as their guess
     *
     * Returns a String of 'O', '+', and 'X', where:
     *   'O' represents the right letter in the right place
     *   '+' represents the right letter in the wrong place
     *   'X' represents a letter not in the target word
     */
    /*
    private fun checkGuess(guess: String) : String {
        var result = ""
        for (i in 0..3) if (guess[i] == wordToGuess[i]) {
            result += "O"
        }
        else if (guess[i] in wordToGuess) {
            result += "+"
        }
        else {
            result += "X"
        }
        return result
    }
    */
    private fun checkGuess(guess: String): SpannableString {
        val result = SpannableString(guess)

        for (i in guess.indices) {
            val guessChar = guess[i]
            val targetChar = wordToGuess[i]

            val color = when {
                guessChar == targetChar -> Color.GREEN // Correct letter and correct position (green)
                targetChar in guess -> Color.YELLOW // Correct letter but incorrect position (yellow)
                else -> Color.RED // Incorrect letter (red)
            }

            result.setSpan(BackgroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return result
    }
   private fun endGame(){
       buttonC.visibility=View.INVISIBLE
       buttonR.visibility=View.VISIBLE
       userInput.isEnabled = FALSE
       finalResult.text=wordToGuess

   }
    private fun win(){
        val confettiSource = ConfettiSource(0, -100, container.width, -100)
        findViewById<TextView>(R.id.congrat).visibility=View.VISIBLE
        streak++
        highScore.text="Streak: " + streak.toString()
        endGame()

    }
    private fun stuck(){
        findViewById<TextView>(R.id.sorry).visibility=View.VISIBLE
        endGame()
    }
    private fun charCheck(a:String): Boolean {
        var isAlpha=true
        for(char in a){
            if(!char.isLetter()){
                isAlpha=false
                break
            }
        }
        return isAlpha
    }

}