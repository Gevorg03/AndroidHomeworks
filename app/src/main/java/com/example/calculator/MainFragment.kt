package com.example.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.calculator.databinding.FragmentMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

class MainFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var expression: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        expression = binding.expression

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lst = listOf(
            binding.btnZero, binding.btnOne, binding.btnTwo, binding.btnThree,
            binding.btnFour, binding.btnFive, binding.btnSix, binding.btnSeven,
            binding.btnEight, binding.btnNine, binding.btnPoint, binding.btnEqual,
            binding.btnPlus, binding.btnMinus, binding.btnMultiple, binding.btnDivide,
            binding.btnClear, binding.btnBack, binding.btnBracketOpen, binding.btnBracketClose
        )

        lst.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        val btn: Button = v as Button
        val btnSymbol = btn.text.toString()
        var text = expression.text.toString()

        when (v.id) {
            binding.btnPlus.id, binding.btnMinus.id, binding.btnMultiple.id, binding.btnDivide.id
            -> {
                if (text != "" && (text.last()
                        .isDigit() || text.last() == '(' || text.last() == ')')
                )
                    expression.append(btnSymbol)
            }

            binding.btnBack.id -> {
                if (text != "") {
                    expression.setText(text.substring(0, text.length - 1))
                }
            }

            binding.btnClear.id -> expression.setText("")

            binding.btnPoint.id -> {
                for (el in text.reversed()) {
                    if ((el.isOperator() && text.last().isDigit()) || text.all { it.isDigit() }) {
                        val str =
                            text.substring(text.length - text.reversed().indexOf(el), text.length)
                        if (str.count { it == '.' } == 0) {
                            expression.append(".")
                            break
                        }
                    }
                }
            }

            binding.btnBracketOpen.id -> {
                if (text == "") expression.setText("(")
                else if (text.last().isOperator() || text.last() == '(') expression.append("(")
            }

            binding.btnBracketClose.id -> {
                if ((text.last().isDigit() || text.last() == ')')
                    && text.count { it == '(' } > text.count { it == ')' }
                )
                    expression.append(")")
            }

            binding.btnEqual.id -> {
                if (text.count { it == '(' } != text.count { it == ')' })
                    text += ")".repeat(text.count { it == '(' } - text.count { it == ')' })

                //zero division error
                try {
                    val result = ExpressionBuilder(text).build().evaluate()
                    expression.setText(result.toString())
                } catch (e: java.lang.ArithmeticException) {
                    Toast.makeText(context, "The expression is pointless", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> expression.append(btnSymbol) //append the number
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private fun Char.isOperator(): Boolean = (this == '+' || this == '-'
        || this == '*' || this == '/')
