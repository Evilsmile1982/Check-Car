package de.dm.carcheck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import java.util.*;

public class MainActivity extends Activity {
    AutoCheckView view;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new AutoCheckView();
        setContentView(view);
    }

    class AutoCheckView extends View {
        Bitmap bg, gear;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect src = new Rect();
        RectF dst = new RectF();
        String[] labels = {"MEIN AUTO","REPARATUREN","WARTUNGEN","KOSTEN","STATISTIK","EINSTELLUNGEN"};
        String[] subTitles = {"Fahrzeugdaten","Reparaturhistorie","Wartungsplan","Kostenübersicht","Auswertungen","App-Einstellungen"};
        RectF[] hit = new RectF[6];
        float headerOffset = -500f;
        float[] buttonOffset = new float[6];
        boolean pressed = false;
        int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);
            bg = BitmapFactory.decodeResource(getResources(), R.drawable.home_background);
            gear = BitmapFactory.decodeResource(getResources(), R.drawable.gear_logo);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            for (int i=0;i<6;i++) buttonOffset[i] = (i<3 ? getResources().getDisplayMetrics().widthPixels : -getResources().getDisplayMetrics().widthPixels);
            post(this::startIntro);
        }

        void startIntro() {
            float w = getWidth();
            if (w <= 0) { postDelayed(this::startIntro, 50); return; }
            for (int i=0;i<3;i++) buttonOffset[i] = w;
            for (int i=3;i<6;i++) buttonOffset[i] = -w;

            ObjectAnimator h = ObjectAnimator.ofFloat(this, "headerOffset", -520f, 0f);
            h.setDuration(2500);

            AnimatorSet buttons = new AnimatorSet();
            ArrayList<Animator> list = new ArrayList<>();
            for (int i=0;i<6;i++) {
                final int idx=i;
                ObjectAnimator a = ObjectAnimator.ofFloat(this, "buttonOffset"+idx,
                        buttonOffset[i], 0f);
                a.setDuration(2500);
                list.add(a);
            }
            buttons.playTogether(list);
            AnimatorSet all = new AnimatorSet();
            all.playTogether(h, buttons);
            all.start();
        }

        public void setHeaderOffset(float v) { headerOffset=v; invalidate(); }
        public float getHeaderOffset(){return headerOffset;}
        public void setButtonOffset0(float v){buttonOffset[0]=v;invalidate();}
        public void setButtonOffset1(float v){buttonOffset[1]=v;invalidate();}
        public void setButtonOffset2(float v){buttonOffset[2]=v;invalidate();}
        public void setButtonOffset3(float v){buttonOffset[3]=v;invalidate();}
        public void setButtonOffset4(float v){buttonOffset[4]=v;invalidate();}
        public void setButtonOffset5(float v){buttonOffset[5]=v;invalidate();}

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            int w=getWidth(), h=getHeight();
            src.set(0,0,bg.getWidth(),bg.getHeight());
            float scale=Math.max((float)w/bg.getWidth(), (float)h/bg.getHeight());
            float bw=bg.getWidth()*scale, bh=bg.getHeight()*scale;
            dst.set((w-bw)/2f,(h-bh)/2f,(w+bw)/2f,(h+bh)/2f);
            c.drawBitmap(bg,src,dst,p);

            drawHeader(c,w);
            drawButtons(c,w,h);
        }

        void drawHeader(Canvas c,int w) {
            c.save();
            c.translate(0,headerOffset);
            float cx=w/2f;
            float gearSize=Math.min(w*0.25f,210f);
            RectF gd=new RectF(cx-gearSize/2,28,cx+gearSize/2,28+gearSize);
            c.drawBitmap(gear,null,gd,p);

            p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(Math.min(w*0.115f,86f));
            p.setShadowLayer(18,0,0,Color.RED);
            p.setColor(Color.WHITE);
            c.drawText("AUTO",cx-4,p.getTextSize()*0+185,p);
            p.setColor(Color.rgb(245,20,20));
            c.drawText("CHECK",cx+130,185,p);
            p.clearShadowLayer();

            p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
            p.setTextSize(Math.min(w*0.037f,29f));
            p.setLetterSpacing(.18f);
            p.setColor(Color.WHITE);
            c.drawText("VERLIERE NICHT DIE ÜBERSICHT",cx,225,p);
            p.setLetterSpacing(0);
            c.restore();
        }

        void drawButtons(Canvas c,int w,int h) {
            float gap=w*0.025f, side=w*0.055f;
            float bw=(w-2*side-2*gap)/3f;
            float bh=Math.min(h*0.105f,210f);
            float y1=h-bh*2- h*0.055f;
            float y2=h-bh- h*0.025f;
            for(int i=0;i<6;i++){
                int col=i%3, row=i/3;
                float x=side+col*(bw+gap)+buttonOffset[i];
                float y=row==0?y1:y2;
                hit[i]=new RectF(x,y,x+bw,y+bh);
                drawButton(c,x,y,bw,bh,labels[i],i==pressedIndex);
            }
        }

        void drawButton(Canvas c,float x,float y,float w,float h,String label,boolean isPressed){
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.argb(220,8,10,14));
            p.setShadowLayer(18,0,6,Color.BLACK);
            c.drawRoundRect(new RectF(x,y,x+w,y+h),24,24,p);
            p.clearShadowLayer();

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(isPressed?5:3);
            p.setColor(isPressed?Color.rgb(255,40,40):Color.rgb(95,100,108));
            c.drawRoundRect(new RectF(x+2,y+2,x+w-2,y+h-2),24,24,p);
            p.setStyle(Paint.Style.FILL);

            if (isPressed) {
                p.setColor(Color.argb(55,255,0,0));
                c.drawRoundRect(new RectF(x,y,x+w,y+h),24,24,p);
            }

            drawIcon(c,x+w/2,y+h*.38f,w*.12f,label);
            p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(Math.min(w*.085f,31f));
            p.setColor(Color.WHITE);
            c.drawText(label,x+w/2,y+h*.79f,p);
        }

        void drawIcon(Canvas c,float cx,float cy,float s,String label){
            p.setColor(Color.WHITE); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); p.setStrokeCap(Paint.Cap.ROUND);
            if(label.equals("MEIN AUTO")){
                c.drawRoundRect(new RectF(cx-s,cy-s*.45f,cx+s,cy+s*.45f),s*.18f,s*.18f,p);
                c.drawCircle(cx-s*.55f,cy+s*.45f,s*.16f,p); c.drawCircle(cx+s*.55f,cy+s*.45f,s*.16f,p);
            } else if(label.equals("REPARATUREN")){
                c.drawLine(cx-s,cy+s*.65f,cx+s,cy-s*.65f,p); c.drawCircle(cx-s*.55f,cy+s*.45f,s*.18f,p);
                c.drawLine(cx-s*.65f,cy-s*.35f,cx-s*.2f,cy-s*.8f,p);
            } else if(label.equals("WARTUNGEN")){
                c.drawRect(new RectF(cx-s*.75f,cy-s*.8f,cx+s*.75f,cy+s*.8f),p);
                c.drawLine(cx-s*.45f,cy-s*.2f,cx+s*.45f,cy-s*.2f,p);
                c.drawLine(cx-s*.45f,cy+s*.2f,cx+s*.45f,cy+s*.2f,p);
            } else if(label.equals("KOSTEN")){
                c.drawRect(new RectF(cx-s*.65f,cy-s*.8f,cx+s*.65f,cy+s*.8f),p);
                c.drawLine(cx-s*.35f,cy-s*.25f,cx+s*.35f,cy-s*.25f,p);
                c.drawLine(cx-s*.35f,cy+s*.1f,cx+s*.35f,cy+s*.1f,p);
            } else if(label.equals("STATISTIK")){
                c.drawLine(cx-s*.7f,cy+s*.7f,cx-s*.7f,cy+s*.15f,p);
                c.drawLine(cx,cy+s*.7f,cx,cy-s*.35f,p);
                c.drawLine(cx+s*.7f,cy+s*.7f,cx+s*.7f,cy-s*.75f,p);
            } else {
                c.drawCircle(cx,cy,s*.72f,p);
                c.drawCircle(cx,cy,s*.25f,p);
                for(int i=0;i<8;i++){ double a=i*Math.PI/4; c.drawLine(cx+(float)Math.cos(a)*s*.8f,cy+(float)Math.sin(a)*s*.8f,cx+(float)Math.cos(a)*s,cy+(float)Math.sin(a)*s,p); }
            }
            p.setStyle(Paint.Style.FILL);
        }

        @Override public boolean onTouchEvent(android.view.MotionEvent e) {
            if(e.getAction()==MotionEvent.ACTION_DOWN){
                pressedIndex=-1;
                for(int i=0;i<6;i++) if(hit[i]!=null && hit[i].contains(e.getX(),e.getY()) && buttonOffset[i] > -getWidth()/2 && buttonOffset[i] < getWidth()/2) pressedIndex=i;
                invalidate(); return true;
            }
            if(e.getAction()==MotionEvent.ACTION_UP){
                int idx=pressedIndex; pressedIndex=-1; invalidate();
                if(idx>=0 && hit[idx]!=null && hit[idx].contains(e.getX(),e.getY())){
                    Intent in=new Intent(MainActivity.this,SubmenuActivity.class);
                    in.putExtra("title",labels[idx]); in.putExtra("subtitle",subTitles[idx]);
                    startActivity(in);
                }
                return true;
            }
            return true;
        }
    }
}
