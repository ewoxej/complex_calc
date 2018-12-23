package test.ewoxej.com.testapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class Diagram extends AppCompatActivity {
float Rez,Im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
        Intent intent = getIntent();
        Im = intent.getFloatExtra("im",0);
        Rez = intent.getFloatExtra("rez",0);
    }

    class DrawView extends View {
Paint p;

        public DrawView(Context context) {
            super(context);
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            int xmiddle=(canvas.getWidth()/2);
            int ymiddel=(canvas.getHeight()/2)+17;
            p.setColor(Color.BLACK);
            p.setStrokeWidth(1);
            for(int i=0;i<canvas.getWidth();i+=30)
                canvas.drawLine(i,0,i,canvas.getHeight(),p);
            for(int i=0;i<canvas.getHeight();i+=30)
                canvas.drawLine(0,i,canvas.getWidth(),i,p);
            p.setStrokeWidth(3);
            canvas.drawLine(xmiddle,0,xmiddle,canvas.getHeight(),p);
            canvas.drawLine(0,ymiddel,canvas.getWidth(),ymiddel,p);
            p.setColor(Color.RED);
            //canvas.drawText(Float.toString(Rez),50,50,100,100,p);
            double power=Math.sqrt((Im*Im)+(Rez*Rez));
            int depth=1;
            float scale=30;
            if(power!=0) {
                if(Math.abs(power)>=1) {
                    depth = (int) Math.log10(power) + 1;
                    while (depth != 1) {
                        scale /= 10;
                        depth--;
                    }
                }
                else{
                    depth=(int) Math.log10(power) -1;
                    depth=Math.abs(depth);
                    while(depth>0){
                        scale*=10;
                        depth--;
                    }
                }
            }
            canvas.drawLine(xmiddle,ymiddel,(xmiddle+(Rez*scale)),(ymiddel-(Im*scale)),p);
            p.setTextSize(35);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2);
            scale=(30/scale);
            canvas.drawText(Double.toString((double)Math.round(scale*1000d)/1000d),10,60,p);
            ComplexNumber tmp=new ComplexNumber(Rez,Im,true,5,true);
            canvas.drawText(tmp.toString(),10,100,p);
            tmp.transform();
            canvas.drawText(tmp.toString(),10,140,p);
        }

    }
}
