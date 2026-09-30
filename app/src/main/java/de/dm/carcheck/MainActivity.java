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

        private Bitmap background;
        private Bitmap logo;

        private final Paint paint =
                new Paint(
                        Paint.ANTI_ALIAS_FLAG |
                        Paint.FILTER_BITMAP_FLAG |
                        Paint.DITHER_FLAG
                );

        private final Rect source = new Rect();
        private final RectF destination = new RectF();

        private final String[] labels = {
                "MEIN AUTO",
                "REPARATUREN",
                "WARTUNGEN",
                "KOSTEN",
                "STATISTIK",
                "EINSTELLUNGEN"
        };

        private final String[] subtitles = {
                "Fahrzeugdaten",
                "Reparaturhistorie",
                "Wartungsplan",
                "Kostenübersicht",
                "Auswertungen",
                "App-Einstellungen"
        };

        private final RectF[] hit =
                new RectF[6];

        /*
         * =========================================
         * ANIMATION
         * =========================================
         */

        private float logoOffset = -700f;

        private final float[] buttonOffset =
                new float[6];

        private int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            background =
                    BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.home_background
                    );

            logo =
                    BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.auto_check_logo
                    );

            /*
             * Hochwertige Bitmap-Darstellung.
             */
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setAntiAlias(true);

            setLayerType(
                    View.LAYER_TYPE_SOFTWARE,
                    null
            );

            postDelayed(
                    this::startIntro,
                    250
            );
        }

        /*
         * =========================================
         * STARTANIMATION
         * =========================================
         */

        private void startIntro() {

            float width = getWidth();

            if (width <= 0) {

                postDelayed(
                        this::startIntro,
                        80
                );

                return;
            }

            /*
             * Obere Reihe kommt von links.
             */
            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            /*
             * Untere Reihe kommt von rechts.
             */
            buttonOffset[3] = width;
            buttonOffset[4] = width;
            buttonOffset[5] = width;

            /*
             * Logo kommt von oben.
             */
            ObjectAnimator logoAnimation =
                    ObjectAnimator.ofFloat(
                            this,
                            "logoOffset",
                            -700f,
                            0f
                    );

            logoAnimation.setDuration(1450);

            /*
             * Button-Animationen.
             */
            ArrayList<Animator> list =
                    new ArrayList<>();

            for (int i = 0; i < 6; i++) {

                ObjectAnimator animation =
                        ObjectAnimator.ofFloat(
                                this,
                                "buttonOffset" + i,
                                buttonOffset[i],
                                0f
                        );

                animation.setDuration(1050);

                /*
                 * Leichte Staffelung.
                 */
                animation.setStartDelay(
                        (i % 3) * 90
                );

                list.add(animation);
            }

            AnimatorSet buttons =
                    new AnimatorSet();

            buttons.playTogether(list);

            AnimatorSet complete =
                    new AnimatorSet();

            complete.playTogether(
                    logoAnimation,
                    buttons
            );

            complete.start();
        }

        /*
         * =========================================
         * LOGO PROPERTY
         * =========================================
         */

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getLogoOffset() {
            return logoOffset;
        }

        /*
         * =========================================
         * BUTTON PROPERTIES
         * =========================================
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
         * =========================================
         * HAUPTZEICHNUNG
         * =========================================
         */

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            /*
             * =====================================
             * HINTERGRUND
             * =====================================
             */

            drawBackground(
                    canvas,
                    width,
                    height
            );

            /*
             * =====================================
             * ALTEN LOGO-BEREICH ABDECKEN
             * =====================================
             *
             * Wichtig:
             * Nicht bis zum Auto schwarz machen.
             */

            LinearGradient topFade =
                    new LinearGradient(
                            0,
                            0,
                            0,
                            height * 0.40f,

                            new int[]{
                                    Color.rgb(0, 0, 0),
                                    Color.rgb(0, 0, 0),
                                    Color.argb(
                                            230,
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
                                    0.48f,
                                    0.76f,
                                    1f
                            },

                            Shader.TileMode.CLAMP
                    );

            paint.setShader(topFade);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawRect(
                    0,
                    0,
                    width,
                    height * 0.40f,
                    paint
            );

            paint.setShader(null);

            /*
             * =====================================
             * NEUES LOGO
             * =====================================
             */

            drawLogo(
                    canvas,
                    width,
                    height
            );

            /*
             * =====================================
             * UNTEREN ALTEN BUTTONBEREICH
             * KOMPLETT SCHWARZ
             * =====================================
             *
             * Etwas höher als vorher, damit wirklich
             * keine alten Hintergrund-Buttons mehr
             * sichtbar sind.
             */

            paint.setColor(Color.BLACK);
            paint.setAlpha(250);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawRect(
                    0,
                    height * 0.655f,
                    width,
                    height,
                    paint
            );

            paint.setAlpha(255);

            /*
             * =====================================
             * NEUE BUTTONS
             * =====================================
             */

            drawButtons(
                    canvas,
                    width,
                    height
            );
        }

        /*
         * =========================================
         * HINTERGRUND
         * =========================================
         */

        private void drawBackground(
                Canvas canvas,
                int width,
                int height
        ) {

            if (background == null) {
                canvas.drawColor(Color.BLACK);
                return;
            }

            source.set(
                    0,
                    0,
                    background.getWidth(),
                    background.getHeight()
            );

            /*
             * Cover-Scaling.
             *
             * Das Seitenverhältnis bleibt erhalten.
             */
            float scale =
                    Math.max(
                            (float) width
                                    / background.getWidth(),

                            (float) height
                                    / background.getHeight()
                    );

            float scaledWidth =
                    background.getWidth()
                            * scale;

            float scaledHeight =
                    background.getHeight()
                            * scale;

            float left =
                    (width - scaledWidth) / 2f;

            float top =
                    (height - scaledHeight) / 2f;

            destination.set(
                    left,
                    top,
                    left + scaledWidth,
                    top + scaledHeight
            );

            paint.setShader(null);
            paint.setAlpha(255);
            paint.setStyle(Paint.Style.FILL);

            /*
             * Hochwertiges Bitmap-Rendering.
             */
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setAntiAlias(true);

            canvas.drawBitmap(
                    background,
                    source,
                    destination,
                    paint
            );
        }

        /*
         * =========================================
         * LOGO
         * =========================================
         */

        private void drawLogo(
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
             * Logo fast über die komplette Breite.
             */
            float logoWidth =
                    width * 1.04f;

            float ratio =
                    (float) logo.getHeight()
                            / (float) logo.getWidth();

            float logoHeight =
                    logoWidth * ratio;

            /*
             * Maximale Höhe des oberen Bereichs.
             */
            float maxHeight =
                    height * 0.365f;

            if (logoHeight > maxHeight) {

                logoHeight =
                        maxHeight;

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

            /*
             * Hochwertige Darstellung.
             */
            paint.setShader(null);
            paint.setAlpha(255);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setAntiAlias(true);

            canvas.drawBitmap(
                    logo,
                    logoSource,
                    logoDestination,
                    paint
            );

            canvas.restore();
        }

        /*
         * =========================================
         * BUTTONS
         * =========================================
         */

        private void drawButtons(
                Canvas canvas,
                int width,
                int height
        ) {

            /*
             * Sehr kleine Seitenränder.
             */
            float side =
                    width * 0.018f;

            /*
             * Gleichmäßiger Spaltenabstand.
             */
            float gap =
                    width * 0.014f;

            /*
             * Maximale Breite.
             */
            float buttonWidth =
                    (
                            width
                            - side * 2f
                            - gap * 2f
                    ) / 3f;

            /*
             * Buttons etwas kompakter in der Höhe,
             * damit unten genug Sicherheitsabstand
             * zur Android-Leiste bleibt.
             */
            float buttonHeight =
                    height * 0.112f;

            /*
             * WICHTIG:
             *
             * Die komplette Button-Gruppe wird
             * gegenüber der vorherigen Version
             * deutlich nach oben verschoben.
             */

            float firstY =
                    height * 0.685f;

            float secondY =
                    height * 0.815f;

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
         * =========================================
         * BUTTON DESIGN
         * =========================================
         */

        private void drawButton(
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
             * Button-Hintergrund.
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
                                6,
                                8,
                                12
                        )
                );
            }

            /*
             * Professioneller Schatten.
             */
            paint.setShadowLayer(
                    12,
                    0,
                    5,
                    Color.argb(
                            210,
                            0,
                            0,
                            0
                    )
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
                    pressed ? 5f : 2.5f
            );

            if (red) {

                paint.setColor(
                        Color.rgb(
                                255,
                                35,
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
                            x + 1.5f,
                            y + 1.5f,
                            x + width - 1.5f,
                            y + height - 1.5f
                    ),
                    18,
                    18,
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
                    y + height * 0.34f,
                    width * 0.125f,
                    label
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
                            width * 0.078f,
                            27f
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
         * =========================================
         * ICONS
         * =========================================
         */

        private void drawIcon(
                Canvas canvas,
                float cx,
                float cy,
                float size,
                String label
        ) {

            paint.setShader(null);

            paint.setColor(
                    Color.WHITE
            );

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    4.2f
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

            } else if (
                    label.equals("REPARATUREN")
            ) {

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

            } else if (
                    label.equals("WARTUNGEN")
            ) {

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

            } else if (
                    label.equals("KOSTEN")
            ) {

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

            } else if (
                    label.equals("STATISTIK")
            ) {

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
         * =========================================
         * TOUCH
         * =========================================
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
                            Math.abs(
                                    buttonOffset[i]
                            ) < getWidth() / 2f
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
