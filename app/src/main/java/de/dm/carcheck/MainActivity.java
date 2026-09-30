package de.dm.carcheck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends Activity {

    AutoCheckView view;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        view = new AutoCheckView();
        setContentView(view);
    }

    class AutoCheckView extends View {

        Bitmap bg;
        Bitmap gear;

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        Rect src = new Rect();
        RectF dst = new RectF();

        String[] labels = {
                "MEIN AUTO",
                "REPARATUREN",
                "WARTUNGEN",
                "KOSTEN",
                "STATISTIK",
                "EINSTELLUNGEN"
        };

        String[] subTitles = {
                "Fahrzeugdaten",
                "Reparaturhistorie",
                "Wartungsplan",
                "Kostenübersicht",
                "Auswertungen",
                "App-Einstellungen"
        };

        RectF[] hit = new RectF[6];

        float headerOffset = -650f;
        float[] buttonOffset = new float[6];

        int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            bg = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background
            );

            gear = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.gear_logo
            );

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            for (int i = 0; i < 6; i++) {
                buttonOffset[i] = 0;
            }

            post(this::startIntro);
        }

        void startIntro() {

            float w = getWidth();

            if (w <= 0) {
                postDelayed(this::startIntro, 50);
                return;
            }

            /*
             * Obere Reihe:
             * kommt von LINKS
             */
            for (int i = 0; i < 3; i++) {
                buttonOffset[i] = -w;
            }

            /*
             * Untere Reihe:
             * kommt von RECHTS
             */
            for (int i = 3; i < 6; i++) {
                buttonOffset[i] = w;
            }

            ObjectAnimator header = ObjectAnimator.ofFloat(
                    this,
                    "headerOffset",
                    -650f,
                    0f
            );

            header.setDuration(1800);

            AnimatorSet buttons = new AnimatorSet();

            ArrayList<Animator> list = new ArrayList<>();

            for (int i = 0; i < 6; i++) {

                final int idx = i;

                ObjectAnimator a = ObjectAnimator.ofFloat(
                        this,
                        "buttonOffset" + idx,
                        buttonOffset[i],
                        0f
                );

                a.setDuration(1500);

                /*
                 * Leichte Verzögerung zwischen den Buttons
                 */
                a.setStartDelay(i % 3 * 120);

                list.add(a);
            }

            buttons.playTogether(list);

            AnimatorSet all = new AnimatorSet();

            all.playTogether(header, buttons);

            all.start();
        }

        public void setHeaderOffset(float value) {
            headerOffset = value;
            invalidate();
        }

        public float getHeaderOffset() {
            return headerOffset;
        }

        public void setButtonOffset0(float value) {
            buttonOffset[0] = value;
            invalidate();
        }

        public float getButtonOffset0() {
            return buttonOffset[0];
        }

        public void setButtonOffset1(float value) {
            buttonOffset[1] = value;
            invalidate();
        }

        public float getButtonOffset1() {
            return buttonOffset[1];
        }

        public void setButtonOffset2(float value) {
            buttonOffset[2] = value;
            invalidate();
        }

        public float getButtonOffset2() {
            return buttonOffset[2];
        }

        public void setButtonOffset3(float value) {
            buttonOffset[3] = value;
            invalidate();
        }

        public float getButtonOffset3() {
            return buttonOffset[3];
        }

        public void setButtonOffset4(float value) {
            buttonOffset[4] = value;
            invalidate();
        }

        public float getButtonOffset4() {
            return buttonOffset[4];
        }

        public void setButtonOffset5(float value) {
            buttonOffset[5] = value;
            invalidate();
        }

        public float getButtonOffset5() {
            return buttonOffset[5];
        }

        @Override
        protected void onDraw(Canvas c) {

            super.onDraw(c);

            int w = getWidth();
            int h = getHeight();

            /*
             * Hintergrundbild
             */
            src.set(
                    0,
                    0,
                    bg.getWidth(),
                    bg.getHeight()
            );

            float scale = Math.max(
                    (float) w / bg.getWidth(),
                    (float) h / bg.getHeight()
            );

            float bw = bg.getWidth() * scale;
            float bh = bg.getHeight() * scale;

            dst.set(
                    (w - bw) / 2f,
                    (h - bh) / 2f,
                    (w + bw) / 2f,
                    (h + bh) / 2f
            );

            c.drawBitmap(
                    bg,
                    src,
                    dst,
                    p
            );

            /*
             * Alte Elemente aus dem Hintergrund
             * werden abgedeckt.
             */
            coverOldBackground(c, w, h);

            /*
             * Neues Logo
             */
            drawHeader(c, w);

            /*
             * Neue Buttons
             */
            drawButtons(c, w, h);
        }

        void coverOldBackground(Canvas c, int w, int h) {

            /*
             * Oben:
             * altes eingebautes Logo abdecken.
             *
             * Der Bereich bleibt dunkel, damit das
             * Werkstattbild darunter optisch erhalten bleibt.
             */
            LinearGradient topGradient =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            h * 0.30f,
                            new int[]{
                                    Color.rgb(5, 5, 8),
                                    Color.rgb(7, 8, 11),
                                    Color.argb(190, 7, 8, 11)
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            p.setShader(topGradient);

            c.drawRect(
                    0,
                    0,
                    w,
                    h * 0.31f,
                    p
            );

            p.setShader(null);

            /*
             * Unten:
             * alte eingebettete Buttons komplett verdecken.
             */
            LinearGradient bottomGradient =
                    new LinearGradient(
                            0,
                            h * 0.65f,
                            0,
                            h,
                            new int[]{
                                    Color.argb(30, 0, 0, 0),
                                    Color.rgb(4, 5, 8),
                                    Color.rgb(3, 4, 6)
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            p.setShader(bottomGradient);

            c.drawRect(
                    0,
                    h * 0.64f,
                    w,
                    h,
                    p
            );

            p.setShader(null);
        }

        void drawHeader(Canvas c, int w) {

            c.save();

            c.translate(0, headerOffset);

            float cx = w / 2f;

            /*
             * Großes Zahnrad
             */
            float gearSize = Math.min(
                    w * 0.36f,
                    280f
            );

            RectF gearRect = new RectF(
                    cx - gearSize / 2f,
                    8,
                    cx + gearSize / 2f,
                    8 + gearSize
            );

            p.setAlpha(255);

            c.drawBitmap(
                    gear,
                    null,
                    gearRect,
                    p
            );

            /*
             * AUTO CHECK
             */
            p.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );

            p.setTextAlign(Paint.Align.CENTER);

            p.setTextSize(
                    Math.min(
                            w * 0.135f,
                            92f
                    )
            );

            p.setShadowLayer(
                    20,
                    0,
                    0,
                    Color.RED
            );

            /*
             * AUTO
             */
            p.setColor(Color.WHITE);

            c.drawText(
                    "AUTO",
                    cx - 45,
                    205,
                    p
            );

            /*
             * CHECK
             */
            p.setColor(
                    Color.rgb(245, 20, 20)
            );

            c.drawText(
                    "CHECK",
                    cx + 92,
                    205,
                    p
            );

            p.clearShadowLayer();

            /*
             * Slogan
             */
            p.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.NORMAL
                    )
            );

            p.setTextSize(
                    Math.min(
                            w * 0.035f,
                            28f
                    )
            );

            p.setLetterSpacing(0.16f);

            p.setColor(Color.WHITE);

            c.drawText(
                    "VERLIERE NICHT DIE ÜBERSICHT",
                    cx,
                    245,
                    p
            );

            p.setLetterSpacing(0);

            c.restore();
        }

        void drawButtons(Canvas c, int w, int h) {

            /*
             * Fast komplette Bildschirmbreite.
             */
            float side = w * 0.025f;

            float gap = w * 0.018f;

            float bw =
                    (w - 2 * side - 2 * gap) / 3f;

            /*
             * Zwei große Reihen im unteren Bereich.
             */
            float bh = Math.min(
                    h * 0.125f,
                    190f
            );

            float y1 = h * 0.695f;

            float y2 =
                    y1 +
                    bh +
                    h * 0.022f;

            for (int i = 0; i < 6; i++) {

                int col = i % 3;
                int row = i / 3;

                float x =
                        side +
                        col * (bw + gap) +
                        buttonOffset[i];

                float y =
                        row == 0
                                ? y1
                                : y2;

                hit[i] = new RectF(
                        x,
                        y,
                        x + bw,
                        y + bh
                );

                drawButton(
                        c,
                        x,
                        y,
                        bw,
                        bh,
                        labels[i],
                        i == pressedIndex
                );
            }
        }

        void drawButton(
                Canvas c,
                float x,
                float y,
                float w,
                float h,
                String label,
                boolean isPressed
        ) {

            boolean isMainButton =
                    label.equals("MEIN AUTO");

            /*
             * Hintergrund
             */
            p.setStyle(Paint.Style.FILL);

            if (isMainButton) {

                p.setColor(
                        Color.argb(
                                240,
                                135,
                                5,
                                12
                        )
                );

            } else {

                p.setColor(
                        Color.argb(
                                238,
                                8,
                                10,
                                14
                        )
                );
            }

            p.setShadowLayer(
                    18,
                    0,
                    7,
                    Color.BLACK
            );

            c.drawRoundRect(
                    new RectF(
                            x,
                            y,
                            x + w,
                            y + h
                    ),
                    20,
                    20,
                    p
            );

            p.clearShadowLayer();

            /*
             * Rahmen
             */
            p.setStyle(Paint.Style.STROKE);

            p.setStrokeWidth(
                    isPressed ? 5 : 3
            );

            if (isMainButton) {

                p.setColor(
                        Color.rgb(
                                255,
                                35,
                                35
                        )
                );

            } else {

                p.setColor(
                        Color.rgb(
                                105,
                                108,
                                116
                        )
                );
            }

            c.drawRoundRect(
                    new RectF(
                            x + 2,
                            y + 2,
                            x + w - 2,
                            y + h - 2
                    ),
                    20,
                    20,
                    p
            );

            p.setStyle(Paint.Style.FILL);

            /*
             * Druck-Effekt
             */
            if (isPressed) {

                p.setColor(
                        Color.argb(
                                70,
                                255,
                                0,
                                0
                        )
                );

                c.drawRoundRect(
                        new RectF(
                                x,
                                y,
                                x + w,
                                y + h
                        ),
                        20,
                        20,
                        p
                );
            }

            /*
             * Icon
             */
            drawIcon(
                    c,
                    x + w / 2f,
                    y + h * 0.35f,
                    w * 0.105f,
                    label
            );

            /*
             * Text
             */
            p.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );

            p.setTextAlign(Paint.Align.CENTER);

            p.setTextSize(
                    Math.min(
                            w * 0.082f,
                            27f
                    )
            );

            p.setColor(Color.WHITE);

            c.drawText(
                    label,
                    x + w / 2f,
                    y + h * 0.80f,
                    p
            );
        }

        void drawIcon(
                Canvas c,
                float cx,
                float cy,
                float s,
                String label
        ) {

            p.setColor(Color.WHITE);

            p.setStyle(
                    Paint.Style.STROKE
            );

            p.setStrokeWidth(4.5f);

            p.setStrokeCap(
                    Paint.Cap.ROUND
            );

            if (label.equals("MEIN AUTO")) {

                c.drawRoundRect(
                        new RectF(
                                cx - s,
                                cy - s * .45f,
                                cx + s,
                                cy + s * .45f
                        ),
                        s * .18f,
                        s * .18f,
                        p
                );

                c.drawCircle(
                        cx - s * .55f,
                        cy + s * .45f,
                        s * .16f,
                        p
                );

                c.drawCircle(
                        cx + s * .55f,
                        cy + s * .45f,
                        s * .16f,
                        p
                );

            } else if (label.equals("REPARATUREN")) {

                c.drawLine(
                        cx - s,
                        cy + s * .65f,
                        cx + s,
                        cy - s * .65f,
                        p
                );

                c.drawCircle(
                        cx - s * .55f,
                        cy + s * .45f,
                        s * .18f,
                        p
                );

                c.drawLine(
                        cx - s * .65f,
                        cy - s * .35f,
                        cx - s * .2f,
                        cy - s * .8f,
                        p
                );

            } else if (label.equals("WARTUNGEN")) {

                c.drawRect(
                        new RectF(
                                cx - s * .75f,
                                cy - s * .8f,
                                cx + s * .75f,
                                cy + s * .8f
                        ),
                        p
                );

                c.drawLine(
                        cx - s * .45f,
                        cy - s * .2f,
                        cx + s * .45f,
                        cy - s * .2f,
                        p
                );

                c.drawLine(
                        cx - s * .45f,
                        cy + s * .2f,
                        cx + s * .45f,
                        cy + s * .2f,
                        p
                );

            } else if (label.equals("KOSTEN")) {

                c.drawRect(
                        new RectF(
                                cx - s * .65f,
                                cy - s * .8f,
                                cx + s * .65f,
                                cy + s * .8f
                        ),
                        p
                );

                c.drawLine(
                        cx - s * .35f,
                        cy - s * .25f,
                        cx + s * .35f,
                        cy - s * .25f,
                        p
                );

                c.drawLine(
                        cx - s * .35f,
                        cy + s * .1f,
                        cx + s * .35f,
                        cy + s * .1f,
                        p
                );

            } else if (label.equals("STATISTIK")) {

                c.drawLine(
                        cx - s * .7f,
                        cy + s * .7f,
                        cx - s * .7f,
                        cy + s * .15f,
                        p
                );

                c.drawLine(
                        cx,
                        cy + s * .7f,
                        cx,
                        cy - s * .35f,
                        p
                );

                c.drawLine(
                        cx + s * .7f,
                        cy + s * .7f,
                        cx + s * .7f,
                        cy - s * .75f,
                        p
                );

            } else {

                c.drawCircle(
                        cx,
                        cy,
                        s * .72f,
                        p
                );

                c.drawCircle(
                        cx,
                        cy,
                        s * .25f,
                        p
                );

                for (int i = 0; i < 8; i++) {

                    double a =
                            i * Math.PI / 4;

                    c.drawLine(
                            cx + (float) Math.cos(a) * s * .8f,
                            cy + (float) Math.sin(a) * s * .8f,
                            cx + (float) Math.cos(a) * s,
                            cy + (float) Math.sin(a) * s,
                            p
                    );
                }
            }

            p.setStyle(
                    Paint.Style.FILL
            );
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            if (e.getAction() == MotionEvent.ACTION_DOWN) {

                pressedIndex = -1;

                for (int i = 0; i < 6; i++) {

                    if (
                            hit[i] != null &&
                            hit[i].contains(
                                    e.getX(),
                                    e.getY()
                            ) &&
                            Math.abs(buttonOffset[i])
                                    < getWidth() / 2
                    ) {

                        pressedIndex = i;
                    }
                }

                invalidate();

                return true;
            }

            if (e.getAction() == MotionEvent.ACTION_UP) {

                int idx = pressedIndex;

                pressedIndex = -1;

                invalidate();

                if (
                        idx >= 0 &&
                        hit[idx] != null &&
                        hit[idx].contains(
                                e.getX(),
                                e.getY()
                        )
                ) {

                    Intent in =
                            new Intent(
                                    MainActivity.this,
                                    SubmenuActivity.class
                            );

                    in.putExtra(
                            "title",
                            labels[idx]
                    );

                    in.putExtra(
                            "subtitle",
                            subTitles[idx]
                    );

                    startActivity(in);
                }

                return true;
            }

            return true;
        }
    }
}
