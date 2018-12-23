package test.ewoxej.com.testapp;

import java.lang.*;
import java.lang.Math;

public class ComplexNumber{
    private double m_Im;//Imaginary part
    private double m_Rez;//Real part
    private boolean m_Form;//true-algebraic,false-exponential
    private boolean m_Degrees;//true-degrees,false-radians
    private int m_Precision;

    ComplexNumber(String _str,int _prec,boolean _deg){
        m_Degrees=_deg;
        m_Precision=++_prec;
        parseString(_str);
    }

    ComplexNumber(double _R,double _I,boolean _form,int _prec,boolean _deg){
        m_Rez=_R;
        m_Form=_form;
        m_Im=_I;
        m_Degrees=_deg;
        m_Precision=++_prec;
    }
    public boolean getForm(){return m_Form;}
    public double getRez(){return m_Rez;}
    public double getIm(){return m_Im;}

    private void parseString(String str){
        if(str.equals('-')) {m_Rez=0;m_Im=0;return;}
        if(str.equals('j')) {m_Rez=0;m_Im=1;m_Form=true;return;}
        int index_e=str.indexOf('e');
        if(index_e!=-1) {parseExp(str,index_e);return;}
        index_e=str.indexOf("+j");
        if(index_e!=-1) {parseAlg(str,index_e);return;}
        index_e=str.indexOf("-j");
        if(index_e!=-1) {parseAlg(str,index_e);return;}
        m_Rez=Double.parseDouble(str);
        m_Im=0;
        m_Form=true;
    }

    private void parseExp(String str,int index_e){
        m_Form=false;
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
        m_Form=true;
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
            if(m_Form) res+=(Rez_str+signstr+"j"+Im_str);
            else {res+=(Rez_str+"e^(");
                if(sign=='-') res+=(signstr+"j"+Im_str+")");
                if(sign=='+') res+=("j"+Im_str+")");}
        return res;
    };

    public void transform(){
        if(m_Form) toExp();
        else toAlg();
    }

    private void toExp(){
        m_Form=!m_Form;
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
        m_Rez=z;if(m_Degrees) m_Im=Math.toDegrees(m_Im);
    }

    private void toAlg(){
        m_Form=!m_Form;
        double ImR=m_Im;
        if(m_Degrees) ImR=Math.toRadians(m_Im);
        m_Im=(m_Rez*Math.sin(ImR));
        m_Rez=(m_Rez*Math.cos(ImR));
    }

   public ComplexNumber calculate(ComplexNumber second_operand,char _operation_code){
       ComplexNumber first_operand=this;
       boolean origin_form_a=first_operand.m_Form;
       boolean origin_form_b=second_operand.m_Form;
       boolean _form=false;
       if(_operation_code=='+'||_operation_code=='-')
       {
           if (!first_operand.m_Form) first_operand.toAlg();
           if (!second_operand.m_Form) second_operand.toAlg();
           _form=true;
       }
       if(_operation_code=='*'||_operation_code=='/')
       {
           if (first_operand.m_Form) first_operand.toExp();
           if (second_operand.m_Form) second_operand.toExp();
           _form=false;
       }
       double _Rez=0,_Im=0;
       if(_operation_code=='-') {_Rez=m_Rez-second_operand.m_Rez;_Im=m_Im-second_operand.m_Im;}
       if(_operation_code=='+') {_Rez=m_Rez+second_operand.m_Rez;_Im=m_Im+second_operand.m_Im;}
       if(_operation_code=='*') {_Rez=m_Rez*second_operand.m_Rez;_Im=m_Im+second_operand.m_Im;}
       if(_operation_code=='/') {_Rez=m_Rez/second_operand.m_Rez;_Im=m_Im-second_operand.m_Im;}
       if (_Rez==Double.POSITIVE_INFINITY) _Rez=0;
       ComplexNumber result=new ComplexNumber(_Rez,_Im,_form,m_Precision,m_Degrees);
       if(first_operand.m_Form!=origin_form_a) first_operand.transform();
       if(second_operand.m_Form!=origin_form_b) second_operand.transform();
       if(result.m_Form!=origin_form_a) result.transform();
       return result;
   }
}