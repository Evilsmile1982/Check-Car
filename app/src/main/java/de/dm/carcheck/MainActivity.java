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

    private AutoCheckView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        view = new AutoCheckView();
        setContentView(view);
    }

    class AutoCheckView extends View {

        Bitmap background;
        Bitmap logo;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Rect source = new Rect();
        RectF destination = new RectF();

        String[] labels = {
                "MEIN AUTO",
                "REPARATUREN",
                "WARTUNGEN",
                "KOSTEN",
                "STATISTIK",
                "EINSTELLUNGEN"
        };

        String[] subtitles = {
                "Fahrzeugdaten",
                "Reparaturhistorie",
                "Wartungsplan",
                "Kostenübersicht",
                "Auswertungen",
                "App-Einstellungen"
        };

        RectF[] hit = new RectF[6];

        /*
         * ==========================================
         * ANIMATION
         * ==========================================
         */

        float logoOffset = -600f;

        float[] buttonOffset = new float[6];

        int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            background = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background
            );

            logo = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.auto_check_logo
            );

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            postDelayed(
                    this::startIntro,
                    200
            );
        }

        /*
         * ==========================================
         * STARTANIMATION
         * ==========================================
         */

        void startIntro() {

            float width = getWidth();

            if (width <= 0) {
                postDelayed(
                        this::startIntro,
                        80
                );
                return;
            }

            /*
             * OBERE 3 BUTTONS
             * kommen von links.
             */
            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            /*
             * UNTERE 3 BUTTONS
             * kommen von rechts.
             */
            buttonOffset[3] = width;
            buttonOffset[4] = width;
            buttonOffset[5] = width;

            /*
             * LOGO KOMMT VON OBEN.
             */
            ObjectAnimator logoAnimation =
                    ObjectAnimator.ofFloat(
                            this,
                            "logoOffset",
                            -600f,
                            0f
                    );

            logoAnimation.setDuration(1600);

            /*
             * BUTTON ANIMATIONEN.
             */
            ArrayList<Animator> animations =
                    new ArrayList<>();

            for (int i = 0; i < 6; i++) {

                ObjectAnimator animation =
                        ObjectAnimator.ofFloat(
                                this,
                                "buttonOffset" + i,
                                buttonOffset[i],
                                0f
                        );

                animation.setDuration(1100);

                /*
                 * Kleine Staffelung.
                 */
                animation.setStartDelay(
                        (i % 3) * 100
                );

                animations.add(animation);
            }

            AnimatorSet buttons =
                    new AnimatorSet();

            buttons.playTogether(animations);

            /*
             * Logo und Buttons gleichzeitig.
             */
            AnimatorSet all =
                    new AnimatorSet();

            all.playTogether(
                    logoAnimation,
                    buttons
            );

            all.start();
        }

        /*
         * ==========================================
         * LOGO PROPERTY
         * ==========================================
         */

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getLogoOffset() {
            return logoOffset;
        }

        /*
         * ==========================================
         * BUTTON PROPERTIES
         * ==========================================
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
         * ==========================================
         * HAUPTZEICHNUNG
         * ==========================================
         */

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            /*
             * ======================================
             * 1. HINTERGRUND
             * ======================================
             *
             * Das komplette Bild wird bildschirmfüllend
             * und mittig dargestellt.
             */

            source.set(
                    0,
                    0,
                    background.getWidth(),
                    background.getHeight()
            );

            float scale = Math.max(
                    (float) width / background.getWidth(),
                    (float) height / background.getHeight()
            );

            float bgWidth =
                    background.getWidth() * scale;

            float bgHeight =
                    background.getHeight() * scale;

            destination.set(
                    (width - bgWidth) / 2f,
                    (height - bgHeight) / 2f,
                    (width + bgWidth) / 2f,
                    (height + bgHeight) / 2f
            );

            paint.setAlpha(255);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawBitmap(
                    background,
                    source,
                    destination,
                    paint
            );

            /*
             * ======================================
             * 2. ALTEN LOGO-BEREICH ÜBERDECKEN
             * ======================================
             *
             * Nicht mehr bis zum Auto schwarz machen.
             *
             * Nur der Bereich des alten Logos wird
             * dunkel überblendet.
             */

            LinearGradient topGradient =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            height * 0.34f,
                            new int[]{
                                    Color.rgb(0, 0, 0),
                                    Color.rgb(0, 0, 0),
                                    Color.argb(
                                            225,
                                            0,
                                            0,
                                            0
                                    ),
                                    Color.argb(
                                            0,
                                            0,
                                            0,
                                            0
                                    )
                            },
                            new float[]{
                                    0f,
                                    0.55f,
                                    0.82f,
                                    1f
                            },
                            Shader.TileMode.CLAMP
                    );

            paint.setShader(topGradient);

            canvas.drawRect(
                    0,
                    0,
                    width,
                    height * 0.38f,
                    paint
            );

            paint.setShader(null);

            /*
             * ======================================
             * 3. NEUES LOGO
             * ======================================
             */

            drawLogo(
                    canvas,
                    width,
                    height
            );

            /*
             * ======================================
             * 4. UNTEREN BEREICH SCHWARZ MACHEN
             * ======================================
             *
             * Dadurch verschwinden die alten
             * Hintergrund-Buttons vollständig.
             */

            paint.setShader(null);
            paint.setColor(Color.BLACK);
            paint.setAlpha(245);

            canvas.drawRect(
                    0,
                    height * 0.675f,
                    width,
                    height,
                    paint
            );

            paint.setAlpha(255);

            /*
             * ======================================
             * 5. NEUE BUTTONS
             * ======================================
             */

            drawButtons(
                    canvas,
                    width,
                    height
            );
        }

        /*
         * ==========================================
         * LOGO
         * ==========================================
         */

        void drawLogo(
                Canvas canvas,
                int width,
                int height
        ) {

            if (logo == null) {
                return;
            }

            canvas.save();

            canvas.translate(
                    0,
                    logoOffset
            );

            /*
             * Das Logo wird bewusst größer als
             * vorher dargestellt.
             */
            float logoWidth =
                    width * 1.10f;

            float ratio =
                    (float) logo.getHeight()
                            / (float) logo.getWidth();

            float logoHeight =
                    logoWidth * ratio;

            /*
             * Maximal bis ungefähr 34 %
             * der Bildschirmhöhe.
             */
            float maximumHeight =
                    height * 0.34f;

            if (logoHeight > maximumHeight) {

                logoHeight =
                        maximumHeight;

                logoWidth =
                        logoHeight / ratio;
            }

            float left =
                    (width - logoWidth) / 2f;

            Rect logoSource =
                    new Rect(
                            0,
                            0,
                            logo.getWidth(),
                            logo.getHeight()
                    );

            RectF logoDestination =
                    new RectF(
                            left,
                            0,
                            left + logoWidth,
                            logoHeight
                    );

            paint.setAlpha(255);

            canvas.drawBitmap(
                    logo,
                    logoSource,
                    logoDestination,
                    paint
            );

            canvas.restore();
        }

        /*
         * ==========================================
         * BUTTONS
         * ==========================================
         */

        void drawButtons(
                Canvas canvas,
                int width,
                int height
        ) {

            /*
             * Sehr kleine Seitenabstände.
             */
            float side =
                    width * 0.018f;

            /*
             * Abstand zwischen den 3 Spalten.
             */
            float gap =
                    width * 0.014f;

            /*
             * Größere Buttons.
             */
            float buttonWidth =
                    (
                            width
                            - side * 2f
                            - gap * 2f
                    ) / 3f;

            /*
             * Zwei große Reihen.
             */
            float buttonHeight =
                    height * 0.125f;

            /*
             * Bereich beginnt direkt
             * unterhalb des Autos.
             */
            float firstY =
                    height * 0.695f;

            float secondY =
                    height * 0.835f;

            for (int i = 0; i < 6; i++) {

                int column =
                        i % 3;

                int row =
                        i / 3;

                float x =
                        side
                        + column
                        * (buttonWidth + gap)
                        + buttonOffset[i];

                float y =
                        row == 0
                                ? firstY
                                : secondY;

                hit[i] =
                        new RectF(
                                x,
                                y,
                                x + buttonWidth,
                                y + buttonHeight
                        );

                drawButton(
                        canvas,
                        x,
                        y,
                        buttonWidth,
                        buttonHeight,
                        labels[i],
                        i == pressedIndex
                );
            }
        }

        /*
         * ==========================================
         * BUTTON
         * ==========================================
         */

        void drawButton(
                Canvas canvas,
                float x,
                float y,
                float width,
                float height,
                String label,
                boolean pressed
        ) {

            boolean red =
                    label.equals("MEIN AUTO");

            /*
             * Buttonfläche.
             */
            paint.setShader(null);
            paint.setStyle(
                    Paint.Style.FILL
            );

            if (red) {

                paint.setColor(
                        Color.rgb(
                                150,
                                5,
                                12
                        )
                );

            } else {

                paint.setColor(
                        Color.rgb(
                                5,
                                7,
                                11
                        )
                );
            }

            paint.setShadowLayer(
                    14,
                    0,
                    6,
                    Color.BLACK
            );

            canvas.drawRoundRect(
                    new RectF(
                            x,
                            y,
                            x + width,
                            y + height
                    ),
                    18,
                    18,
                    paint
            );

            paint.clearShadowLayer();

            /*
             * Rahmen.
             */
            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    pressed ? 5f : 3f
            );

            if (red) {

                paint.setColor(
                        Color.rgb(
                                255,
                                40,
                                40
                        )
                );

            } else {

                paint.setColor(
                        Color.rgb(
                                105,
                                110,
                                120
                        )
                );
            }

            canvas.drawRoundRect(
                    new RectF(
                            x + 2,
                            y + 2,
                            x + width - 2,
                            y + height - 2
                    ),
                    18,
                    18,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            /*
             * Icon größer.
             */
            drawIcon(
                    canvas,
                    x + width / 2f,
                    y + height * 0.34f,
                    width * 0.13f,
                    label
            );

            /*
             * Beschriftung größer.
             */
            paint.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            paint.setTextSize(
                    Math.min(
                            width * 0.088f,
                            30f
                    )
            );

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    label,
                    x + width / 2f,
                    y + height * 0.82f,
                    paint
            );
        }

        /*
         * ==========================================
         * ICONS
         * ==========================================
         */

        void drawIcon(
                Canvas canvas,
                float cx,
                float cy,
                float size,
                String label
        ) {

            paint.setColor(
                    Color.WHITE
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    4.5f
            );

            paint.setStrokeCap(
                    Paint.Cap.ROUND
            );

            if (label.equals("MEIN AUTO")) {

                canvas.drawRoundRect(
                        new RectF(
                                cx - size,
                                cy - size * 0.45f,
                                cx + size,
                                cy + size * 0.45f
                        ),
                        size * 0.18f,
                        size * 0.18f,
                        paint
                );

                canvas.drawCircle(
                        cx - size * 0.55f,
                        cy + size * 0.45f,
                        size * 0.16f,
                        paint
                );

                canvas.drawCircle(
                        cx + size * 0.55f,
                        cy + size * 0.45f,
                        size * 0.16f,
                        paint
                );

            } else if (label.equals("REPARATUREN")) {

                canvas.drawLine(
                        cx - size,
                        cy + size * 0.65f,
                        cx + size,
                        cy - size * 0.65f,
                        paint
                );

                canvas.drawCircle(
                        cx - size * 0.55f,
                        cy + size * 0.45f,
                        size * 0.18f,
                        paint
                );

            } else if (label.equals("WARTUNGEN")) {

                canvas.drawRect(
                        new RectF(
                                cx - size * 0.75f,
                                cy - size * 0.8f,
                                cx + size * 0.75f,
                                cy + size * 0.8f
                        ),
                        paint
                );

                canvas.drawLine(
                        cx - size * 0.45f,
                        cy - size * 0.2f,
                        cx + size * 0.45f,
                        cy - size * 0.2f,
                        paint
                );

                canvas.drawLine(
                        cx - size * 0.45f,
                        cy + size * 0.2f,
                        cx + size * 0.45f,
                        cy + size * 0.2f,
                        paint
                );

            } else if (label.equals("KOSTEN")) {

                canvas.drawRect(
                        new RectF(
                                cx - size * 0.65f,
                                cy - size * 0.8f,
                                cx + size * 0.65f,
                                cy + size * 0.8f
                        ),
                        paint
                );

                canvas.drawLine(
                        cx - size * 0.35f,
                        cy - size * 0.25f,
                        cx + size * 0.35f,
                        cy - size * 0.25f,
                        paint
                );

                canvas.drawLine(
                        cx - size * 0.35f,
                        cy + size * 0.1f,
                        cx + size * 0.35f,
                        cy + size * 0.1f,
                        paint
                );

            } else if (label.equals("STATISTIK")) {

                canvas.drawLine(
                        cx - size * 0.7f,
                        cy + size * 0.7f,
                        cx - size * 0.7f,
                        cy + size * 0.15f,
                        paint
                );

                canvas.drawLine(
                        cx,
                        cy + size * 0.7f,
                        cx,
                        cy - size * 0.35f,
                        paint
                );

                canvas.drawLine(
                        cx + size * 0.7f,
                        cy + size * 0.7f,
                        cx + size * 0.7f,
                        cy - size * 0.75f,
                        paint
                );

            } else {

                canvas.drawCircle(
                        cx,
                        cy,
                        size * 0.72f,
                        paint
                );

                canvas.drawCircle(
                        cx,
                        cy,
                        size * 0.25f,
                        paint
                );

                for (int i = 0; i < 8; i++) {

                    double angle =
                            i * Math.PI / 4;

                    canvas.drawLine(
                            cx +
                                    (float) Math.cos(angle)
                                    * size * 0.8f,

                            cy +
                                    (float) Math.sin(angle)
                                    * size * 0.8f,

                            cx +
                                    (float) Math.cos(angle)
                                    * size,

                            cy +
                                    (float) Math.sin(angle)
                                    * size,

                            paint
                    );
                }
            }

            paint.setStyle(
                    Paint.Style.FILL
            );
        }

        /*
         * ==========================================
         * TOUCH
         * ==========================================
         */

        @Override
        public boolean onTouchEvent(
                MotionEvent event
        ) {

            if (
                    event.getAction()
                            == MotionEvent.ACTION_DOWN
            ) {

                pressedIndex = -1;

                for (int i = 0; i < 6; i++) {

                    if (
                            hit[i] != null
                            &&
                            hit[i].contains(
                                    event.getX(),
                                    event.getY()
                            )
                            &&
                            Math.abs(buttonOffset[i])
                                    < getWidth() / 2f
                    ) {

                        pressedIndex = i;
                    }
                }

                invalidate();

                return true;
            }

            if (
                    event.getAction()
                            == MotionEvent.ACTION_UP
            ) {

                int selected =
                        pressedIndex;

                pressedIndex = -1;

                invalidate();

                if (
                        selected >= 0
                        &&
                        hit[selected] != null
                        &&
                        hit[selected].contains(
                                event.getX(),
                                event.getY()
                        )
                ) {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    SubmenuActivity.class
                            );

                    intent.putExtra(
                            "title",
                            labels[selected]
                    );

                    intent.putExtra(
                            "subtitle",
                            subtitles[selected]
                    );

                    startActivity(intent);
                }

                return true;
            }

            return true;
        }
    }
}
