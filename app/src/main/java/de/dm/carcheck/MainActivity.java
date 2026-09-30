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

        /*
         * Logo fährt von oben herein.
         */
        float headerOffset = -430f;

        /*
         * Button-Animationen.
         */
        float[] buttonOffset = new float[6];

        int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            bg = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background
            );

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            for (int i = 0; i < 6; i++) {
                buttonOffset[i] = 0;
            }

            post(this::startIntro);
        }

        /*
         * ------------------------------------------------
         * STARTANIMATION
         * ------------------------------------------------
         */
        void startIntro() {

            float w = getWidth();

            if (w <= 0) {
                postDelayed(
                        this::startIntro,
                        50
                );
                return;
            }

            /*
             * Obere Reihe kommt von LINKS.
             */
            for (int i = 0; i < 3; i++) {
                buttonOffset[i] = -w;
            }

            /*
             * Untere Reihe kommt von RECHTS.
             */
            for (int i = 3; i < 6; i++) {
                buttonOffset[i] = w;
            }

            /*
             * LOGO:
             * komplett von oben herein.
             */
            ObjectAnimator header =
                    ObjectAnimator.ofFloat(
                            this,
                            "headerOffset",
                            -430f,
                            0f
                    );

            header.setDuration(1800);

            /*
             * BUTTONS.
             */
            AnimatorSet buttons =
                    new AnimatorSet();

            ArrayList<Animator> list =
                    new ArrayList<>();

            for (int i = 0; i < 6; i++) {

                final int idx = i;

                ObjectAnimator a =
                        ObjectAnimator.ofFloat(
                                this,
                                "buttonOffset" + idx,
                                buttonOffset[i],
                                0f
                        );

                a.setDuration(1500);

                /*
                 * Leichte Staffelung.
                 */
                a.setStartDelay(
                        (i % 3) * 120
                );

                list.add(a);
            }

            buttons.playTogether(list);

            /*
             * Logo und Buttons gleichzeitig.
             */
            AnimatorSet all =
                    new AnimatorSet();

            all.playTogether(
                    header,
                    buttons
            );

            all.start();
        }

        /*
         * ------------------------------------------------
         * ANIMATION PROPERTY LOGO
         * ------------------------------------------------
         */
        public void setHeaderOffset(float value) {
            headerOffset = value;
            invalidate();
        }

        public float getHeaderOffset() {
            return headerOffset;
        }

        /*
         * ------------------------------------------------
         * ANIMATION PROPERTY BUTTONS
         * ------------------------------------------------
         */

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

        /*
         * ------------------------------------------------
         * ZEICHNEN
         * ------------------------------------------------
         */
        @Override
        protected void onDraw(Canvas c) {

            super.onDraw(c);

            int w = getWidth();
            int h = getHeight();

            /*
             * Hintergrundbild auf Bildschirmgröße skalieren.
             */
            src.set(
                    0,
                    0,
                    bg.getWidth(),
                    bg.getHeight()
            );

            float scale =
                    Math.max(
                            (float) w / bg.getWidth(),
                            (float) h / bg.getHeight()
                    );

            float bw =
                    bg.getWidth() * scale;

            float bh =
                    bg.getHeight() * scale;

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
             * Altes Logo und alte Buttons verdecken.
             */
            coverOldElements(
                    c,
                    w,
                    h
            );

            /*
             * Neues Logo.
             */
            drawHeader(
                    c,
                    w
            );

            /*
             * Neue Buttons.
             */
            drawButtons(
                    c,
                    w,
                    h
            );
        }

        /*
         * ------------------------------------------------
         * ALTE ELEMENTE ABDECKEN
         * ------------------------------------------------
         */
        void coverOldElements(
                Canvas c,
                int w,
                int h
        ) {

            /*
             * Oberen Bereich abdunkeln.
             *
             * Das alte Logo befindet sich dort.
             */
            LinearGradient top =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            h * 0.33f,
                            new int[]{
                                    Color.rgb(
                                            2,
                                            3,
                                            5
                                    ),
                                    Color.rgb(
                                            4,
                                            5,
                                            7
                                    ),
                                    Color.argb(
                                            230,
                                            4,
                                            5,
                                            7
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            p.setShader(top);

            c.drawRect(
                    0,
                    0,
                    w,
                    h * 0.33f,
                    p
            );

            p.setShader(null);

            /*
             * Unteren Bereich abdunkeln.
             *
             * Dadurch verschwinden die alten
             * eingebauten Buttons aus dem JPG.
             */
            LinearGradient bottom =
                    new LinearGradient(
                            0,
                            h * 0.64f,
                            0,
                            h,
                            new int[]{
                                    Color.argb(
                                            20,
                                            0,
                                            0,
                                            0
                                    ),
                                    Color.rgb(
                                            4,
                                            5,
                                            8
                                    ),
                                    Color.rgb(
                                            2,
                                            3,
                                            5
                                    )
                            },
                            null,
                            Shader.TileMode.CLAMP
                    );

            p.setShader(bottom);

            c.drawRect(
                    0,
                    h * 0.64f,
                    w,
                    h,
                    p
            );

            p.setShader(null);
        }

        /*
         * ------------------------------------------------
         * LOGO
         * ------------------------------------------------
         *
         * Hier wird KEIN neues AUTO-CHECK-Logo gezeichnet.
         *
         * Stattdessen wird der echte Logo-Bereich
         * aus home_background.jpg ausgeschnitten.
         *
         * Dadurch entspricht die Optik deinem Bild 2.
         */
        void drawHeader(
                Canvas c,
                int w
        ) {

            c.save();

            /*
             * Logo fährt von oben herein.
             */
            c.translate(
                    0,
                    headerOffset
            );

            /*
             * Breite des Logos.
             */
            float logoWidth =
                    w * 0.98f;

            /*
             * Nicht übermäßig groß werden.
             */
            if (logoWidth > 900f) {
                logoWidth = 900f;
            }

            /*
             * Der obere Bereich des Originalbildes
             * ist 848 x 430 Pixel.
             */
            float sourceWidth =
                    848f;

            float sourceHeight =
                    430f;

            /*
             * Seitenverhältnis beibehalten.
             */
            float logoHeight =
                    logoWidth *
                    sourceHeight /
                    sourceWidth;

            float left =
                    (w - logoWidth) / 2f;

            /*
             * Logo-Quelle.
             *
             * Das komplette obere Logo aus dem
             * Originalbild.
             */
            Rect logoSource =
                    new Rect(
                            0,
                            0,
                            Math.min(
                                    848,
                                    bg.getWidth()
                            ),
                            Math.min(
                                    430,
                                    bg.getHeight()
                            )
                    );

            /*
             * Zielbereich.
             */
            RectF logoDestination =
                    new RectF(
                            left,
                            0,
                            left + logoWidth,
                            logoHeight
                    );

            p.setAlpha(255);

            /*
             * Das Original-Logo zeichnen.
             */
            c.drawBitmap(
                    bg,
                    logoSource,
                    logoDestination,
                    p
            );

            p.setAlpha(255);

            c.restore();
        }

        /*
         * ------------------------------------------------
         * BUTTONS
         * ------------------------------------------------
         */
        void drawButtons(
                Canvas c,
                int w,
                int h
        ) {

            /*
             * Sehr kleiner Rand.
             */
            float side =
                    w * 0.025f;

            /*
             * Abstand zwischen den Kästchen.
             */
            float gap =
                    w * 0.018f;

            /*
             * Drei gleich große Spalten.
             */
            float bw =
                    (
                            w
                            - 2f * side
                            - 2f * gap
                    ) / 3f;

            /*
             * Höhe der Kästchen.
             */
            float bh =
                    Math.min(
                            h * 0.125f,
                            190f
                    );

            /*
             * Erste Reihe.
             */
            float y1 =
                    h * 0.695f;

            /*
             * Zweite Reihe.
             */
            float y2 =
                    y1
                    + bh
                    + h * 0.022f;

            for (int i = 0; i < 6; i++) {

                int col =
                        i % 3;

                int row =
                        i / 3;

                float x =
                        side
                        + col *
                        (bw + gap)
                        + buttonOffset[i];

                float y =
                        row == 0
                                ? y1
                                : y2;

                hit[i] =
                        new RectF(
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

        /*
         * ------------------------------------------------
         * EINZELNER BUTTON
         * ------------------------------------------------
         */
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
                    label.equals(
                            "MEIN AUTO"
                    );

            /*
             * Button-Hintergrund.
             */
            p.setStyle(
                    Paint.Style.FILL
            );

            if (isMainButton) {

                /*
                 * MEIN AUTO rot.
                 */
                p.setColor(
                        Color.argb(
                                240,
                                135,
                                5,
                                12
                        )
                );

            } else {

                /*
                 * Andere Buttons dunkel.
                 */
                p.setColor(
                        Color.argb(
                                238,
                                8,
                                10,
                                14
                        )
                );
            }

            /*
             * Schatten.
             */
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
             * Rahmen.
             */
            p.setStyle(
                    Paint.Style.STROKE
            );

            p.setStrokeWidth(
                    isPressed
                            ? 5f
                            : 3f
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

            p.setStyle(
                    Paint.Style.FILL
            );

            /*
             * Druckeffekt.
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
             * Icon.
             */
            drawIcon(
                    c,
                    x + w / 2f,
                    y + h * 0.35f,
                    w * 0.105f,
                    label
            );

            /*
             * Text.
             */
            p.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );

            p.setTextAlign(
                    Paint.Align.CENTER
            );

            p.setTextSize(
                    Math.min(
                            w * 0.082f,
                            27f
                    )
            );

            p.setColor(
                    Color.WHITE
            );

            c.drawText(
                    label,
                    x + w / 2f,
                    y + h * 0.80f,
                    p
            );
        }

        /*
         * ------------------------------------------------
         * BUTTON-ICONS
         * ------------------------------------------------
         */
        void drawIcon(
                Canvas c,
                float cx,
                float cy,
                float s,
                String label
        ) {

            p.setColor(
                    Color.WHITE
            );

            p.setStyle(
                    Paint.Style.STROKE
            );

            p.setStrokeWidth(
                    4.5f
            );

            p.setStrokeCap(
                    Paint.Cap.ROUND
            );

            /*
             * MEIN AUTO
             */
            if (label.equals(
                    "MEIN AUTO"
            )) {

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

            /*
             * REPARATUREN
             */
            } else if (label.equals(
                    "REPARATUREN"
            )) {

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

            /*
             * WARTUNGEN
             */
            } else if (label.equals(
                    "WARTUNGEN"
            )) {

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

            /*
             * KOSTEN
             */
            } else if (label.equals(
                    "KOSTEN"
            )) {

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

            /*
             * STATISTIK
             */
            } else if (label.equals(
                    "STATISTIK"
            )) {

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

            /*
             * EINSTELLUNGEN
             */
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
                            cx +
                                    (float)
                                            Math.cos(a)
                                    * s * .8f,
                            cy +
                                    (float)
                                            Math.sin(a)
                                    * s * .8f,
                            cx +
                                    (float)
                                            Math.cos(a)
                                    * s,
                            cy +
                                    (float)
                                            Math.sin(a)
                                    * s,
                            p
                    );
                }
            }

            p.setStyle(
                    Paint.Style.FILL
            );
        }

        /*
         * ------------------------------------------------
         * TOUCH
         * ------------------------------------------------
         */
        @Override
        public boolean onTouchEvent(
                MotionEvent e
        ) {

            /*
             * Finger gedrückt.
             */
            if (
                    e.getAction()
                            == MotionEvent.ACTION_DOWN
            ) {

                pressedIndex = -1;

                for (int i = 0; i < 6; i++) {

                    if (
                            hit[i] != null
                            &&
                            hit[i].contains(
                                    e.getX(),
                                    e.getY()
                            )
                            &&
                            Math.abs(
                                    buttonOffset[i]
                            )
                                    < getWidth() / 2f
                    ) {

                        pressedIndex = i;
                    }
                }

                invalidate();

                return true;
            }

            /*
             * Finger losgelassen.
             */
            if (
                    e.getAction()
                            == MotionEvent.ACTION_UP
            ) {

                int idx =
                        pressedIndex;

                pressedIndex = -1;

                invalidate();

                if (
                        idx >= 0
                        &&
                        hit[idx] != null
                        &&
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
