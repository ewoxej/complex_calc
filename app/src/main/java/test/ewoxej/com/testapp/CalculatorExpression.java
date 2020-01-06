package test.ewoxej.com.testapp;

import android.widget.TextView;

public class CalculatorExpression
{
    ComplexNumber operandA;
    ComplexNumber operandB;
    String operator="";
    TextView expressionView;
    String getIntermediateString()
    {
        if(operandA == null)
            return "0";
        return operandA.toString() + operator;
    }
    String getResult()
    {
        return operandA.calculate(operandB,operator.charAt(0)).toString();
    }
}
