package de.dm.carcheck;

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

        RectF[] buttons = new RectF[6];

        /*
         * Logo startet komplett oberhalb
         * des Bildschirms.
         */
        float logoY = -500f;

        /*
         * Button-Animationen.
         */
        float[] buttonX = new float[6];

        int pressedButton = -1;

        AutoCheckView() {
            super(MainActivity.this);

            background = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background
            );

            /*
             * DAS ECHTE LOGO AUS DEINEM BILD 2
             */
            logo = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.auto_check_logo
            );

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            postDelayed(
                    this::startAnimation,
                    250
            );
        }

        /*
         * ==========================================
         * STARTANIMATION
         * ==========================================
         */
        void startAnimation() {

            float width = getWidth();

            if (width <= 0) {
                postDelayed(
                        this::startAnimation,
                        100
                );
                return;
            }

            /*
             * OBERE REIHE:
             * kommt von links.
             */
            buttonX[0] = -width;
            buttonX[1] = -width;
            buttonX[2] = -width;

            /*
             * UNTERE REIHE:
             * kommt von rechts.
             */
            buttonX[3] = width;
            buttonX[4] = width;
            buttonX[5] = width;

            /*
             * LOGO FÄHRT VON OBEN EIN.
             */
            ObjectAnimator logoAnimation =
                    ObjectAnimator.ofFloat(
                            this,
                            "logoY",
                            -500f,
                            0f
                    );

            logoAnimation.setDuration(1800);

            /*
             * BUTTONS.
             */
            ArrayList<android.animation.Animator> list =
                    new ArrayList<>();

            for (int i = 0; i < 6; i++) {

                ObjectAnimator animation =
                        ObjectAnimator.ofFloat(
                                this,
                                "buttonX" + i,
                                buttonX[i],
                                0f
                        );

                animation.setDuration(1300);

                animation.setStartDelay(
                        (i % 3) * 120
                );

                list.add(animation);
            }

            AnimatorSet buttonSet =
                    new AnimatorSet();

            buttonSet.playTogether(list);

            /*
             * Logo + Buttons gemeinsam.
             */
            AnimatorSet all =
                    new AnimatorSet();

            all.playTogether(
                    logoAnimation,
                    buttonSet
            );

            all.start();
        }

        /*
         * ==========================================
         * LOGO
         * ==========================================
         */
        public void setLogoY(float value) {
            logoY = value;
            invalidate();
        }

        public float getLogoY() {
            return logoY;
        }

        /*
         * ==========================================
         * BUTTON PROPERTIES
         * ==========================================
         */
        public void setButtonX0(float value) {
            buttonX[0] = value;
            invalidate();
        }

        public float getButtonX0() {
            return buttonX[0];
        }

        public void setButtonX1(float value) {
            buttonX[1] = value;
            invalidate();
        }

        public float getButtonX1() {
            return buttonX[1];
        }

        public void setButtonX2(float value) {
            buttonX[2] = value;
            invalidate();
        }

        public float getButtonX2() {
            return buttonX[2];
        }

        public void setButtonX3(float value) {
            buttonX[3] = value;
            invalidate();
        }

        public float getButtonX3() {
            return buttonX[3];
        }

        public void setButtonX4(float value) {
            buttonX[4] = value;
            invalidate();
        }

        public float getButtonX4() {
            return buttonX[4];
        }

        public void setButtonX5(float value) {
            buttonX[5] = value;
            invalidate();
        }

        public float getButtonX5() {
            return buttonX[5];
        }

        /*
         * ==========================================
         * ZEICHNEN
         * ==========================================
         */
        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            /*
             * Hintergrundbild.
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

            canvas.drawBitmap(
                    background,
                    source,
                    destination,
                    paint
            );

            /*
             * Alten Logo-Bereich komplett
             * abdecken.
             */
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(null);
            paint.setColor(Color.rgb(1, 2, 4));

            canvas.drawRect(
                    0,
                    0,
                    width,
                    height * 0.39f,
                    paint
            );

            /*
             * Echtes Logo zeichnen.
             */
            drawLogo(
                    canvas,
                    width,
                    height
            );

            /*
             * Buttons.
             */
            drawButtons(
                    canvas,
                    width,
                    height
            );
        }

        /*
         * ==========================================
         * LOGO ZEICHNEN
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
                    logoY
            );

            /*
             * Logo fast über die gesamte
             * Bildschirmbreite.
             */
            float logoWidth =
                    width * 0.99f;

            float ratio =
                    (float) logo.getHeight()
                            / (float) logo.getWidth();

            float logoHeight =
                    logoWidth * ratio;

            /*
             * Nicht zu klein machen.
             */
            if (logoHeight > height * 0.39f) {

                logoHeight =
                        height * 0.39f;

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

            float side =
                    width * 0.025f;

            float gap =
                    width * 0.018f;

            float buttonWidth =
                    (
                            width
                            - side * 2
                            - gap * 2
                    ) / 3f;

            float buttonHeight =
                    Math.min(
                            height * 0.125f,
                            190f
                    );

            /*
             * Erste Reihe.
             */
            float firstY =
                    height * 0.695f;

            /*
             * Zweite Reihe.
             */
            float secondY =
                    firstY
                    + buttonHeight
                    + height * 0.022f;

            for (int i = 0; i < 6; i++) {

                int column =
                        i % 3;

                int row =
                        i / 3;

                float x =
                        side
                        + column
                        * (buttonWidth + gap)
                        + buttonX[i];

                float y =
                        row == 0
                                ? firstY
                                : secondY;

                buttons[i] =
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
                        i == pressedButton
                );
            }
        }

        /*
         * ==========================================
         * EIN BUTTON
         * ==========================================
         */
        void drawButton(
                Canvas canvas,
                float x,
                float y,
                float width,
                float height,
                String text,
                boolean pressed
        ) {

            boolean red =
                    text.equals("MEIN AUTO");

            /*
             * Button-Hintergrund.
             */
            paint.setStyle(
                    Paint.Style.FILL
            );

            if (red) {

                paint.setColor(
                        Color.rgb(
                                145,
                                5,
                                12
                        )
                );

            } else {

                paint.setColor(
                        Color.rgb(
                                7,
                                9,
                                13
                        )
                );
            }

            paint.setShadowLayer(
                    16,
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
                    20,
                    20,
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
                                100,
                                105,
                                115
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
                    20,
                    20,
                    paint
            );

            paint.setStyle(
                    Paint.Style.FILL
            );

            /*
             * Icon.
             */
            drawIcon(
                    canvas,
                    x + width / 2f,
                    y + height * 0.36f,
                    width * 0.105f,
                    text
            );

            /*
             * Text.
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
                            width * 0.082f,
                            27f
                    )
            );

            paint.setColor(
                    Color.WHITE
            );

            canvas.drawText(
                    text,
                    x + width / 2f,
                    y + height * 0.80f,
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
                String text
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

            if (text.equals("MEIN AUTO")) {

                canvas.drawRoundRect(
                        new RectF(
                                cx - size,
                                cy - size * .45f,
                                cx + size,
                                cy + size * .45f
                        ),
                        size * .18f,
                        size * .18f,
                        paint
                );

                canvas.drawCircle(
                        cx - size * .55f,
                        cy + size * .45f,
                        size * .16f,
                        paint
                );

                canvas.drawCircle(
                        cx + size * .55f,
                        cy + size * .45f,
                        size * .16f,
                        paint
                );

            } else if (text.equals("REPARATUREN")) {

                canvas.drawLine(
                        cx - size,
                        cy + size * .65f,
                        cx + size,
                        cy - size * .65f,
                        paint
                );

                canvas.drawCircle(
                        cx - size * .55f,
                        cy + size * .45f,
                        size * .18f,
                        paint
                );

            } else if (text.equals("WARTUNGEN")) {

                canvas.drawRect(
                        new RectF(
                                cx - size * .75f,
                                cy - size * .8f,
                                cx + size * .75f,
                                cy + size * .8f
                        ),
                        paint
                );

                canvas.drawLine(
                        cx - size * .45f,
                        cy - size * .2f,
                        cx + size * .45f,
                        cy - size * .2f,
                        paint
                );

                canvas.drawLine(
                        cx - size * .45f,
                        cy + size * .2f,
                        cx + size * .45f,
                        cy + size * .2f,
                        paint
                );

            } else if (text.equals("KOSTEN")) {

                canvas.drawRect(
                        new RectF(
                                cx - size * .65f,
                                cy - size * .8f,
                                cx + size * .65f,
                                cy + size * .8f
                        ),
                        paint
                );

                canvas.drawLine(
                        cx - size * .35f,
                        cy - size * .25f,
                        cx + size * .35f,
                        cy - size * .25f,
                        paint
                );

            } else if (text.equals("STATISTIK")) {

                canvas.drawLine(
                        cx - size * .7f,
                        cy + size * .7f,
                        cx - size * .7f,
                        cy + size * .15f,
                        paint
                );

                canvas.drawLine(
                        cx,
                        cy + size * .7f,
                        cx,
                        cy - size * .35f,
                        paint
                );

                canvas.drawLine(
                        cx + size * .7f,
                        cy + size * .7f,
                        cx + size * .7f,
                        cy - size * .75f,
                        paint
                );

            } else {

                canvas.drawCircle(
                        cx,
                        cy,
                        size * .72f,
                        paint
                );

                canvas.drawCircle(
                        cx,
                        cy,
                        size * .25f,
                        paint
                );

                for (int i = 0; i < 8; i++) {

                    double angle =
                            i * Math.PI / 4;

                    canvas.drawLine(
                            cx + (float)Math.cos(angle)
                                    * size * .8f,
                            cy + (float)Math.sin(angle)
                                    * size * .8f,
                            cx + (float)Math.cos(angle)
                                    * size,
                            cy + (float)Math.sin(angle)
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

                pressedButton = -1;

                for (int i = 0; i < 6; i++) {

                    if (
                            buttons[i] != null
                            &&
                            buttons[i].contains(
                                    event.getX(),
                                    event.getY()
                            )
                            &&
                            Math.abs(buttonX[i])
                                    < getWidth() / 2f
                    ) {

                        pressedButton = i;
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
                        pressedButton;

                pressedButton = -1;

                invalidate();

                if (
                        selected >= 0
                        &&
                        buttons[selected] != null
                        &&
                        buttons[selected].contains(
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
