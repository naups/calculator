package com.naups.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import com.naups.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var inputValue1: Double? = 0.0
    private var inputValue2: Double? = null
    private var currentOperator: Operator? = null
    private var result: Double? = null
    private val equation: StringBuilder = StringBuilder().append(ZERO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setNightModeIndicator()
    }

    private fun setListeners(){
        for (button in getNumericButtons()){
            button.setOnClickListener {onNumberClick(button.text.toString())}
        }
        with(binding){
            buttonZero.setOnClickListener { onZeroClick() }
            buttonDoubleZero.setOnClickListener{ onDoubleZeroClick() }
            buttonDecimalPoint.setOnClickListener{ onDecimalPointClick() }
            buttonAddition.setOnClickListener{ onOperatorClick(Operator.ADDITION) }
            buttonSubtraction.setOnClickListener{ onOperatorClick(Operator.SUBTRACTION) }
            buttonMultiplication.setOnClickListener{ onOperatorClick(Operator.MULTIPLICATION) }
            buttonDivision.setOnClickListener{ onOperatorClick(Operator.DIVISION) }
            buttonEquals.setOnClickListener{ onEqualsClick() }
            buttonAllClear.setOnClickListener{ onAllClearClick() }
            buttonPlusMinus.setOnClickListener{ onPlusMinusClick() }
            buttonPercentage.setOnClickListener{ onPercentageClick() }
            imageNightMode.setOnClickListener{ toggleNightMode() }
        }
    }

    private fun toggleNightMode() {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }

    private fun setNightModeIndicator() {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.imageNightMode.setImageResource(R.drawable.ic_sun)
        } else {
            binding.imageNightMode.setImageResource(R.drawable.ic_moon)
        }
    }

    private fun onPercentageClick(){
        if (inputValue2 == null){
            val percentage = getInputValue1() / 100
            inputValue1 = percentage
            equation.clear().append(percentage)
            updateInputOnDisplay()
        } else {
            val percentageOfValue1 = (getInputValue1() * getInputValue2()) / 100
            val percentageOfValue2 = getInputValue2() /100

            result = when (requireNotNull(currentOperator)){
                Operator.ADDITION -> getInputValue1() + percentageOfValue1
                Operator.SUBTRACTION -> getInputValue1() - percentageOfValue1
                Operator.MULTIPLICATION -> getInputValue1() * percentageOfValue2
                Operator.DIVISION -> getInputValue1() / percentageOfValue2
            }

            equation.clear().append(ZERO)
            updateResultOnDisplay(isPercentage = true)
            inputValue1 = result
            result = null
            inputValue2 = null
            currentOperator = null
        }
    }

    private fun onPlusMinusClick(){
        if (equation.startsWith(MINUS)){
            equation.deleteCharAt(0)
        } else {
            equation.insert(0, MINUS)
        }
        setInput()
        updateInputOnDisplay()
    }

    private fun onAllClearClick(){
        inputValue1 = 0.0
        inputValue2 = null
        currentOperator = null
        result = null
        equation.clear().append(ZERO)
        clearDisplay()
    }

    private fun onOperatorClick(operator: Operator){
        onEqualsClick()
        currentOperator = operator
    }

    private fun onEqualsClick(){
        if (inputValue2 != null){
            result = calculate()
            equation.clear().append(ZERO)
            updateResultOnDisplay()
            inputValue1 = result
            result = null
            inputValue2 = null
            currentOperator = null
        } else {
            equation.clear().append(ZERO)
        }
    }

    private fun calculate(): Double {
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION -> getInputValue1() + getInputValue2()
            Operator.DIVISION -> getInputValue1() / getInputValue2()
            Operator.MULTIPLICATION -> getInputValue1() * getInputValue2()
            Operator.SUBTRACTION -> getInputValue1() - getInputValue2()
        }
    }

    private fun onDecimalPointClick(){
        if (equation.contains(DECIMAL_POINT))return
        equation.append(DECIMAL_POINT)
        setInput()
        updateInputOnDisplay()
    }

    private fun onZeroClick(){
        if (equation.startsWith(ZERO)) return
        onNumberClick(ZERO)
    }

    private fun onDoubleZeroClick(){
        if (equation.startsWith(ZERO)) return
        onNumberClick(DOUBLE_ZERO)
    }

    private fun getNumericButtons() = with(binding){
        arrayOf(
            buttonOne,
            buttonTwo,
            buttonThree,
            buttonFour,
            buttonFive,
            buttonSix,
            buttonSeven,
            buttonEight,
            buttonNine
        )
    }

    private fun onNumberClick(numberText: String){
        if (equation.startsWith(ZERO)){
            equation.deleteCharAt(0)
        }else if (equation.startsWith("$MINUS$ZERO")){
            equation.deleteCharAt(1)
        }
        equation.append(numberText)
        setInput()
        updateInputOnDisplay()
    }

    private fun setInput(){
        if (currentOperator == null){
            inputValue1 = equation.toString().toDouble()
        }else{
            inputValue2 = equation.toString().toDouble()
        }
    }

    private fun clearDisplay(){
        with(binding){
            textInput.text = getFormattedDisplayValue(value = getInputValue1())
            textEquation.text = null
        }
    }

    private fun updateResultOnDisplay(isPercentage: Boolean = false){
        binding.textInput.text = getFormattedDisplayValue(value = result)
        var input2Text = getFormattedDisplayValue(value = getInputValue2())
        if (isPercentage) input2Text = "$input2Text${getString(R.string.percentage)}"
        binding.textEquation.text = String.format(
            "%s %s %s",
            getFormattedDisplayValue(value = getInputValue1()),
            getOperatorSymbol(),
            input2Text
        )
    }

    private fun updateInputOnDisplay(){
        if (result == null){
            binding.textEquation.text = null
        }
        binding.textInput.text = equation
    }

    private fun getInputValue1() = inputValue1 ?: 0.0
    private fun getInputValue2() = inputValue2 ?: 0.0

    private fun getOperatorSymbol(): String{
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION -> getString(R.string.addition)
            Operator.MULTIPLICATION -> getString(R.string.multiplication)
            Operator.SUBTRACTION -> getString(R.string.subtraction)
            Operator.DIVISION -> getString(R.string.division)
        }
    }

    private fun getFormattedDisplayValue(value: Double?): String? {
        val originalValue = value ?: return null
        return if (originalValue %1 == 0.0){
            originalValue.toInt().toString()
        } else {
            originalValue.toString()
        }
    }

    enum  class Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION
    }

    private companion object{
        const val DECIMAL_POINT = "."
        const val ZERO = "0"
        const val DOUBLE_ZERO = "00"
        const val MINUS = "-"
    }
}