package test.ewoxej.com.testapp;

import java.lang.*;
import java.lang.Math;

public class ComplexNumber{
    private double m_Im;//Imaginary part
    private double m_Rez;//Real part
    public enum NumberType
    {
        Algebraic,
        Exponential,
        Real
    }
    public enum UnitType
    {
        Degree,
        Radians
    }
    private NumberType form;
    private UnitType unitType;
    private boolean f;//true-algebraic,false-exponential
    private boolean d;//true-degrees,false-radians
    private int m_Precision;

    ComplexNumber(String str,int prec,UnitType deg){
        unitType=deg;
        m_Precision=++prec;
        parseString(str);
    }

    ComplexNumber(double R,double I,NumberType type,int prec,UnitType deg){
        m_Rez=R;
        form=type;
        m_Im=I;
        unitType=deg;
        m_Precision=++prec;
    }
    public NumberType getForm(){return form;}
    public double getRez(){return m_Rez;}
    public double getIm(){return m_Im;}

    private void parseString(String str){
        if(str.equals('-')) {m_Rez=0;m_Im=0;return;}
        if(str.equals('j')) {m_Rez=0;m_Im=1;form=NumberType.Algebraic;return;}
        int index_e=str.indexOf('e');
        if(index_e!=-1) {parseExp(str,index_e);return;}
        index_e=str.indexOf("+j");
        if(index_e!=-1) {parseAlg(str,index_e);return;}
        index_e=str.indexOf("-j");
        if(index_e!=-1) {parseAlg(str,index_e);return;}
        m_Rez=Double.parseDouble(str);
        m_Im=0;
        form=NumberType.Real;
    }

    private void parseExp(String str,int index_e){
        form=NumberType.Exponential;
        String Rez_str,Im_str;
        if(index_e==0) m_Rez=1;
        else{
            Rez_str=str.substring(0,index_e);
            if(Rez_str.equals("-")) m_Rez=-1;
            else
            m_Rez=Double.parseDouble(Rez_str);
        }
        int j_index=str.indexOf("j");
        int skr_index=str.indexOf(")");
        Im_str=str.substring(j_index-1,skr_index);
        if(Im_str.equals("-j")) {m_Im=-1;return;}
        if(Im_str.equals("(j")) {m_Im=1;return;}
        Im_str=str.substring(j_index+1,skr_index);
        if(str.charAt(j_index-1)=='-') m_Im=-1;
        else m_Im=1;
        m_Im=m_Im*Double.parseDouble(Im_str);
    }

    private void parseAlg(String str,int index_e){
        form=NumberType.Algebraic;
        if(str.charAt(index_e)=='+') m_Im=1;
        else m_Im=-1;
        String Rez_str,Im_str;
        if(index_e==0) m_Rez=0;
        else{
            Rez_str=str.substring(0,index_e);
            m_Rez=Double.parseDouble(Rez_str);
        }
        Im_str=str.substring(index_e+2);
        if(Im_str.length()==0) m_Im*=1;
        else{
            if(Im_str.equals("j")) m_Im=1;
            else m_Im=m_Im*Double.parseDouble(Im_str);
        }
    }

    public String toString(){
        String res="",Rez_str,Im_str;
        double Im_f=m_Im,Rez_f=m_Rez;
        char sign;
        long prec=(long)Math.pow(10L,m_Precision-1);
            if(m_Im<0) {sign='-';Im_f*=(-1.0);}else sign='+';
            Im_f=((double)Math.round(Im_f * prec)  ) / prec;
            Rez_f=((double)Math.round(Rez_f * prec)  ) / prec;
            int wholeIm=(int)m_Im;
            if((Im_f-wholeIm)==0) Im_str=Integer.toString(wholeIm);
            else Im_str=Double.toString(Im_f);
            int wholeRez=(int)m_Rez;
            if((Rez_f-wholeRez)==0) Rez_str=Integer.toString(wholeRez);
            else Rez_str=Double.toString(Rez_f);
            String signstr=Character.toString(sign);
            System.out.println(signstr);
            if(form == NumberType.Algebraic) res+=(Rez_str+signstr+"j"+Im_str);
            else {res+=(Rez_str+"e^(");
                if(sign=='-') res+=(signstr+"j"+Im_str+")");
                if(sign=='+') res+=("j"+Im_str+")");}
        return res;
    };

    public void transform(){
        if(form == NumberType.Algebraic) toExp();
        else toAlg();
    }

    private void toExp(){
        form = NumberType.Exponential;
        double z=Math.sqrt((m_Rez*m_Rez)+(m_Im*m_Im));
        double phi,arg;
        double div=m_Im/m_Rez;
            phi=div;
            arg=Math.atan(phi);
            if(m_Rez==0&& m_Im>0) m_Im=Math.PI/2;
            if(m_Rez==0&& m_Im<0) m_Im=-Math.PI/2;
            if(m_Rez==0&& m_Im==0) m_Im=0;
            if (m_Rez>0) m_Im=arg;
            if((m_Rez<0)&&(m_Im>=0)) m_Im=Math.PI+arg;
            if((m_Rez<0)&&(m_Im<0)) m_Im=(-Math.PI)+arg;
        m_Rez=z;if(unitType == UnitType.Degree) m_Im=Math.toDegrees(m_Im);
    }

    private void toAlg(){
        form = NumberType.Algebraic;
        double ImR=m_Im;
        if(unitType == UnitType.Degree) ImR=Math.toRadians(m_Im);
        m_Im=(m_Rez*Math.sin(ImR));
        m_Rez=(m_Rez*Math.cos(ImR));
    }

   public ComplexNumber calculate(ComplexNumber second_operand,char _operation_code){
       ComplexNumber first_operand=this;
       NumberType origin_form_a=first_operand.form;
       NumberType origin_form_b=second_operand.form;
       NumberType _form = NumberType.Real;
       if(_operation_code=='+'||_operation_code=='-')
       {
           if (first_operand.form != NumberType.Algebraic) first_operand.toAlg();
           if (second_operand.form != NumberType.Algebraic) second_operand.toAlg();
           _form=NumberType.Algebraic;
       }
       if(_operation_code=='*'||_operation_code=='/')
       {
           if (first_operand.form != NumberType.Exponential) first_operand.toExp();
           if (second_operand.form != NumberType.Exponential ) second_operand.toExp();
           _form=NumberType.Exponential;
       }
       double _Rez=0,_Im=0;
       if(_operation_code=='-') {_Rez=m_Rez-second_operand.m_Rez;_Im=m_Im-second_operand.m_Im;}
       if(_operation_code=='+') {_Rez=m_Rez+second_operand.m_Rez;_Im=m_Im+second_operand.m_Im;}
       if(_operation_code=='*') {_Rez=m_Rez*second_operand.m_Rez;_Im=m_Im+second_operand.m_Im;}
       if(_operation_code=='/') {_Rez=m_Rez/second_operand.m_Rez;_Im=m_Im-second_operand.m_Im;}
       if (_Rez==Double.POSITIVE_INFINITY) _Rez=0;
       ComplexNumber result=new ComplexNumber(_Rez,_Im,_form,m_Precision,unitType);
       if(first_operand.form!=origin_form_a) first_operand.transform();
       if(second_operand.form!=origin_form_b) second_operand.transform();
       if(result.form!=origin_form_a) result.transform();
       return result;
   }
}