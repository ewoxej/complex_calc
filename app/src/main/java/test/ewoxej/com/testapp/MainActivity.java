package test.ewoxej.com.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.String;

public class MainActivity extends AppCompatActivity {
    //SA fields
    private ComplexNumber res;
    private char state = 'r';
        //r real part of number
        //i imagine part of alg number form
        // e imagine part of exp number form
    private char gl_state = 'a';//a,b,r result;
    private char operator = '0';
    private String exp_string1 = "0",
            exp_string2 = "0",
            first_operand;
    private int E1fontSize;
    public TextView tv1, tv2;
    //PREFERENCES
    private int prec = 3;
    private boolean sign = true;
    private boolean deg = true;
    public static final String APP_PREF = "preferences";
    public static final String APP_PREF_DEGRAD = "degrad";
    public static final String APP_PREF_PREC = "prec";
    private SharedPreferences mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSetting = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        settingRestore();
        Button btn;
        btn = findViewById(R.id.key_clr);
        Button hlp_btn=findViewById(R.id.hlp_key);
        tv1 = findViewById(R.id.form);
        tv2 = findViewById(R.id.form2);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                exp_string1 = "0";
                exp_string2 = "0";
                gl_state = 'a';
                state = 'r';
                setText(tv1, "0", 40);
                setText(tv2, "0", 33);
                return true;
            }
        });
        hlp_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
Toast toast=Toast.makeText(getApplicationContext(),"Если калькулятор считает правильно, то его написал Илья Чапарин.Если неправильно,то не знаю кто.",Toast.LENGTH_LONG);
toast.show();
                return true;
            }
        });
        TableLayout kbd = findViewById(R.id.kbd);
        LinearLayout tv_l = findViewById(R.id.textViewLayout);
        ViewGroup.LayoutParams kbd_p = kbd.getLayoutParams();
        ViewGroup.LayoutParams tv_1_p = tv_l.getLayoutParams();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        kbd_p.height = (int) (size.y / 1.6);
        tv_1_p.height = size.y / 3;
        kbd.requestLayout();
        tv_l.requestLayout();

    }

    public void settingRestore() {
        Button btnd,btnp;
        btnp=findViewById(R.id.prec_key);
        btnd = findViewById(R.id.degrad_key);
        if (mSetting.contains(APP_PREF_DEGRAD)) {
            deg = mSetting.getBoolean(APP_PREF_DEGRAD, true);
        }
        if(mSetting.contains(APP_PREF_PREC)){
            prec=mSetting.getInt(APP_PREF_PREC,3);
        }
        btnp.setText(Integer.toString(prec));
        if(!deg) btnd.setText("RAD");
    }

    public void setText(TextView tv, CharSequence text, int fontSize) {
        int tmpFS = returnfontSize(exp_string1.length(), fontSize);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tmpFS);
        tv.setText(text);
    }

    public void MakeZeroString(boolean first, boolean second) {
        if (first) exp_string1 = "0";
        if (second) exp_string2 = "0";
    }

    public void changeState(char gl_st, char st) {
        if (gl_st != '-') gl_state = gl_st;
        if (st != '-') state = st;
    }

    public String withoutLastSubstring(String str) {
        return str.substring(0, str.length() - 1);
    }

    public void k0_click(View view) {
        if (gl_state == 'r') {
            changeState('a', '-');
            MakeZeroString(true, true);
            stringToScreen();
            recalc();
            return;
        }
        //if gl_state=a,b
        String tempstr = exp_string1;
        if (state == 'r' || state == 'i') {
            if (state == 'i') tempstr = exp_string1.substring(tempstr.indexOf('j') + 1);
            if ((tempstr.equals("0")) || (tempstr.equals("-0"))) return;
            if (!exp_string1.equals("0")) exp_string1 += "0";
            else MakeZeroString(true, false);
        }
        if (state == 'e') {
            String tmpstr = exp_string1.substring(exp_string1.indexOf('j') + 1);
            if ((tmpstr.equals("0)")) || (tmpstr.equals("-0)"))) return;
            exp_string1 = withoutLastSubstring(exp_string1);
            exp_string1 += "0)";
        }
        stringToScreen();
    }

    public char lastChar(String str) {
        return str.charAt(exp_string1.length() - 1);
    }

    public int returnfontSize(int strlen, int init_font_size) {
        if (strlen > 10) init_font_size -= 5;
        if (strlen > 15) init_font_size -= 5;
        if (strlen > 23) init_font_size -= 5;
        if (strlen > 33) init_font_size -= 5;
        return init_font_size;
    }

    public void stringToScreen() {
        CharSequence sp_text = "";
        String str = " " + Character.toString(operator) + " ";
        if (gl_state == 'a') sp_text = getSpannedText(exp_string1);
        if (gl_state == 'b')
            sp_text = TextUtils.concat(getSpannedText(first_operand), str, getSpannedText(exp_string1));
        E1fontSize = returnfontSize(sp_text.length(), 40);
        if (sp_text.length() >= 60) {
            Button btn;
            btn = findViewById(R.id.key_clr);
            btn.performClick();
            return;
        }
        setText(tv1, sp_text, E1fontSize);
        recalc();
    }

    public void digit_click(String es, String digit) {
        if (gl_state == 'r') {
            gl_state = 'a';
            exp_string2 = digit;
            exp_string1 = digit;
            stringToScreen();
            recalc();
            return;
        }

        if (state == 'r' || state == 'i') {
            if (!es.equals("0")) es += digit;
            else es = digit;
        }
        if (state == 'e') {
            es = es.substring(0, exp_string1.length() - 1);
            es += digit;
            es += ")";
        }
        exp_string1 = es;

        stringToScreen();

    }

    public void k1_click(View view) {
        digit_click(exp_string1, "1");
        recalc();
    }

    public void k2_click(View view) {
        digit_click(exp_string1, "2");
        recalc();
    }

    public void k3_click(View view) {
        digit_click(exp_string1, "3");
        recalc();
    }

    public void k4_click(View view) {
        digit_click(exp_string1, "4");
        recalc();
    }

    public void k5_click(View view) {
        digit_click(exp_string1, "5");
        recalc();
    }

    public void k6_click(View view) {
        digit_click(exp_string1, "6");
        recalc();
    }

    public void k7_click(View view) {
        digit_click(exp_string1, "7");
        recalc();
    }

    public void k8_click(View view) {
        digit_click(exp_string1, "8");
        recalc();
    }

    public void k9_click(View view) {
        digit_click(exp_string1, "9");
        recalc();
    }

    public char getStateFirst() {
        ComplexNumber tmpnum;
        tmpnum = new ComplexNumber(first_operand, prec, deg);
        if (first_operand.indexOf('j')==-1) return 'r';
        if (tmpnum.getForm()) return 'i';
        else return 'e';
    }

    public void clr_click(View view) {
        if (gl_state == 'r') {
            gl_state = 'a';
            exp_string2 = "0";
            exp_string1 = "0";
            stringToScreen();
            return;
        }
        if ((exp_string1.isEmpty()) && gl_state == 'b') {
            exp_string1 = first_operand;
            gl_state = 'a';
            state = getStateFirst();
            stringToScreen();
            return;
        }
        if (state == 'r' || state == 'i') {
            if (lastChar(exp_string1) == 'j') {
                exp_string1 = exp_string1.substring(0, exp_string1.length() - 2);
                state = 'r';
            } else exp_string1 = withoutLastSubstring(exp_string1);
        }
        if (state == 'e') {
            if ((lastChar(exp_string1) == ')') && (exp_string1.charAt(exp_string1.length() - 2) == 'j')) {
                if (sign)
                    exp_string1 = exp_string1.substring(0, exp_string1.length() - 5);
                else {
                    exp_string1 = exp_string1.substring(0, exp_string1.length() - 6);
                    sign = false;
                }
                state = 'r';
            } else
                exp_string1 = exp_string1.substring(0, exp_string1.length() - 2) + ")";
        }
        if (exp_string1.length() == 0 && gl_state == 'a') exp_string1 = "0";
        stringToScreen();
    }

    public void exp_click(View view) {
        if (gl_state == 'r') {
            gl_state = 'a';
            state = 'e';
            exp_string1 = "e^(j)";
            stringToScreen();
            return;
        }
        if (state == 'r') {
            if (exp_string1.equals("-")) exp_string1 = "-e^(j)";
            else {
                if (!exp_string1.equals("0")) exp_string1 += "e^(j)";
                else exp_string1 = "e^(j)";
            }
            state = 'e';
        }
        stringToScreen();
    }

    public void alg_click(View view) {
        if (gl_state == 'r') {
            gl_state = 'a';
            state = 'i';
            exp_string1 = "+j";
            stringToScreen();
            return;
        }
        if (state == 'r') {
            if (exp_string1.equals("-")) exp_string1 = "-j";
            else {
                if (!exp_string1.equals("0")) exp_string1 += "+j";
                else exp_string1 = "+j";
            }
            state = 'i';
        }
        stringToScreen();
    }

    public void point_click(View view) {
        if (gl_state == 'r') {
            gl_state = 'a';
            state = 'r';
            exp_string1 = "0.";
            stringToScreen();
            return;
        }
        String tmpstr;
        if (exp_string1.isEmpty()) exp_string1 = "0.";
        if ((exp_string1.length() == 1 && exp_string1.charAt(0) == '-')) exp_string1 = "-0.";
        else {
            if (state == 'r') {
                if (exp_string1.indexOf('.') == -1) {
                    if (exp_string1.charAt(exp_string1.length() - 1) == 'j') return;
                    if (!exp_string1.equals("0")) exp_string1 += ".";
                    else exp_string1 = "0.";
                }
            }
            if (state == 'i') {
                int index = exp_string1.indexOf('j');
                tmpstr = exp_string1.substring(index - 1);
                if (tmpstr.equals("+j") || tmpstr.equals("-j")) {
                    exp_string1 += "0.";
                } else {
                    if (tmpstr.indexOf('.') == -1) {
                        exp_string1 += '.';
                    }
                }
            }
            if (state == 'e') {
                if ((lastChar(exp_string1) == ')') && (exp_string1.charAt(exp_string1.length() - 2) == 'j')) {
                    exp_string1 = withoutLastSubstring(exp_string1);
                    exp_string1 += "0.)";
                } else {
                    int index = exp_string1.indexOf('(');
                    tmpstr = exp_string1.substring(index);
                    if (tmpstr.indexOf('.') == -1) {
                        exp_string1 = withoutLastSubstring(exp_string1);
                        exp_string1 += ".)";
                    }
                }
            }
        }
        stringToScreen();
    }

    public void sign_click(View view) {
        if (gl_state == 'r') {
            gl_state = 'a';
            state = 'r';
            exp_string1 = "-";
            stringToScreen();
            return;
        }
        String tempstring;
        if (state == 'r') {
            if (gl_state == 'b' && exp_string1.isEmpty()) {
                exp_string1 = "-";
                stringToScreen();
                return;
            }
            if (exp_string1.equals("-")) exp_string1 = "0";
            else {
                if (exp_string1.equals("0")) exp_string1 = "-";
                else {
                    if (exp_string1.charAt(0) != '-') exp_string1 = '-' + exp_string1;
                    else exp_string1 = exp_string1.substring(1);
                }
            }
        }
        if (state == 'i') {
            int index = exp_string1.indexOf('j');
            tempstring = exp_string1.substring(0, index - 1);
            if (exp_string1.charAt(index - 1) == '+') tempstring += '-';
            else tempstring += '+';
            tempstring += exp_string1.substring(index);
            exp_string1 = tempstring;
        }
        if (state == 'e') {
            sign = !sign;
            int index = exp_string1.indexOf('j');
            tempstring = exp_string1.substring(0, index);
            if (exp_string1.charAt(index - 1) != '-') {
                tempstring += '-';
            } else {
                tempstring = tempstring.substring(0, tempstring.length() - 1);
            }
            tempstring += exp_string1.substring(index);
            exp_string1 = tempstring;
        }
        stringToScreen();
    }

    public void recalc() {
ComplexNumber tmpnum;
        if (gl_state == 'b') {
            tmpnum = new ComplexNumber(first_operand, prec,deg);
            tmpnum.transform();
            if (state == 'r')
                exp_string2 = tmpnum.toString() + " " + Character.toString(operator) + " " + exp_string1;
            else {
                res = new ComplexNumber(exp_string1, prec,deg);
                res.transform();
                exp_string2 = tmpnum.toString() + " " + Character.toString(operator) + " " + res.toString();
            }
        } else {//a or r
            if (state == 'r') exp_string2 = exp_string1;
            else {
                tmpnum = new ComplexNumber(exp_string1, prec, deg);
                tmpnum.transform();
                exp_string2 = tmpnum.toString();
            }

        }
        int fontSize = E1fontSize-7;
        if (exp_string2.length() >= 60) {
            Button btn;
            btn = findViewById(R.id.key_clr);
            btn.performClick();
            return;
        }
        //tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        setText(tv2, getSpannedText(exp_string2), fontSize);
    }

    public void operator_click(char oper) {
        int op_index;
        if (gl_state == 'b') {
            if (exp_string1.isEmpty()) operator = oper;
            else {
                Button btn;
                btn = findViewById(R.id.key_assign);
                btn.performClick();
            }
        }
        if (gl_state == 'r') {
            gl_state = 'b';
            first_operand = exp_string1;
            operator = oper;
            exp_string1 = "";
        }
        if (gl_state == 'a') {
            operator = oper;
            op_index = (exp_string1.length() - 1);
            exp_string1 += Character.toString(oper);
            first_operand = exp_string1.substring(0, op_index + 1);
            exp_string1 = exp_string1.substring(op_index + 2);
            gl_state = 'b';
        }
        state = 'r';
        stringToScreen();

    }

    public void add_click(View view) {
        operator_click('+');
    }

    public void sub_click(View view) {
        operator_click('-');
    }

    public void mul_click(View view) {
        operator_click('*');
    }

    public void dvd_click(View view) {
        operator_click('/');
    }

    public void assign_click(View view) {
        ComplexNumber tmpnum,tmpnum1;
        if (gl_state == 'b') {
            tmpnum = new ComplexNumber(first_operand, prec, deg);
            if (exp_string1.isEmpty()) tmpnum1 = new ComplexNumber("0", prec,  deg);
            else tmpnum1 = new ComplexNumber(exp_string1, prec, deg);
            res=tmpnum.calculate(tmpnum1,operator);
            exp_string1 = res.toString();
            if (!exp_string1.equals("oo")) {
                res.transform();
                exp_string2 = res.toString();
                tv2.setText(exp_string2);
                tv2.setText(getSpannedText(exp_string2));
            }
            tv1.setText(getSpannedText(exp_string1));
            if (exp_string1.equals("oo")) {
                exp_string1 = "0";
                exp_string2="0";
                tv2.setText("0");
                gl_state = 'a';
            } else gl_state = 'r';
            state = 'r';
        }

    }

    public CharSequence getSpannedText(String str) {
        int powerPosition = str.indexOf("e^");
        int powEndPosition = str.indexOf(")");
        if (powerPosition == -1) return str;
        else {
            String val = str.substring(0, powerPosition + 1);
            String pow = str.substring(powerPosition + 2, powEndPosition + 1);
            String rem = str.substring(powEndPosition + 1);
            return (fromHtml(val + "<small><sup>" + pow + "</sup></small>" + rem));
        }

    }

    private static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        else return Html.fromHtml(html);
    }

    public void dr_click(View view){
        Button btn;
        btn = findViewById(R.id.degrad_key);
        if(deg) {btn.setText("RAD");deg=false;}
        else {btn.setText("DEG");deg=true;}
        SharedPreferences.Editor editor=mSetting.edit();
        editor.putBoolean(APP_PREF_DEGRAD,deg);
        editor.apply();
    }

    public void prec_click(View view){

        showPopupMenu(view);
    }

    private void showPopupMenu(View view){

        PopupMenu popupMenu=new PopupMenu(this,view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                case R.id.it1:
                    prec=1;
                    return true;
                    case R.id.it2:
                        prec=2;
                        return true;
                    case R.id.it3:
                        prec=3;
                        return true;
                    case R.id.it4:
                        prec=4;
                        return true;
                    case R.id.it5:
                        prec=5;
                        return true;
                    default: return false;

                }

            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Button btne;
                btne = findViewById(R.id.prec_key);
                btne.setText(Integer.toString(prec));
                SharedPreferences.Editor editor=mSetting.edit();
                editor.putInt(APP_PREF_PREC,prec);
                editor.apply();
            }
        });
        popupMenu.show();

    }

   public void hlp_click(View view){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void dgr_click(View view){
        Intent intent = new Intent(MainActivity.this, Diagram.class);
        ComplexNumber tmp=new ComplexNumber(exp_string1,prec,deg);
        if(!tmp.getForm()) tmp.transform();

        intent.putExtra("im",(float)tmp.getIm());
        intent.putExtra("rez",(float)tmp.getRez());
        startActivity(intent);
    }
}