package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var screen: TextView
    private lateinit var results: TextView
    private var addOperator = false
    private var addDecimal = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screen = findViewById(R.id.screen)
        results = findViewById(R.id.results)
    }

    fun onClearClick(view: View) {
        screen.text = ""
        results.text = ""
    }

    fun onBackspaceClick(view: View) {
        if (screen.length() > 0)
            screen.text = screen.text.subSequence(0, screen.length() - 1)
    }

    fun onOperationClick(view: View) {
        if (view is Button && addOperator) {
            screen.append(view.text)
            addOperator = false
            addDecimal = false
        }
    }

    fun onNumberClick(view: View) {
        if (view is Button) {
            screen.append(view.text)
            addOperator = true
            addDecimal = true
        }
    }

    fun onDecimalClick(view: View) {
        if (view is Button) {
            if (screen.text.isEmpty())
                screen.append("0" + view.text)
            else if (addDecimal) {
                screen.append(view.text)
                addDecimal = false
            }
        }
    }

    fun onEqualsClick(view: View) {
        results.text = calculateResults()
    }

    /*
     * Following functions used from codeWithCal kotlinCalculator
     * https://github.com/codeWithCal/KotlinCalculator/blob/master/app/src/main/java/code/with/cal/kotlincalculatorapp/MainActivity.kt
     */

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in screen.text) {
            if (character.isDigit() || character == '.')
                currentDigit += character
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}