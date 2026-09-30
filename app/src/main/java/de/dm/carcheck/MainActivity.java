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

        private final RectF[] hit = new RectF[6];

        /*
         * =====================================================
         * ANIMATION
         * =====================================================
         */

        private float logoOffset = -500f;

        private final float[] buttonOffset =
                new float[6];

        private int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            /*
             * Hintergrundbild
             */
            background =
                    BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.home_background
                    );

            /*
             * Dein neues Auto-Check-Logo
             */
            logo =
                    BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.auto_check_logo
                    );

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
         * =====================================================
         * STARTANIMATION
         * =====================================================
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
             * Obere drei Buttons kommen von links.
             */
            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            /*
             * Untere drei Buttons kommen von rechts.
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
                            -500f,
                            0f
                    );

            logoAnimation.setDuration(1400);

            /*
             * Buttonanimation
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

                animation.setDuration(1000);

                /*
                 * Kleine Staffelung.
                 */
                animation.setStartDelay(
                        (i % 3) * 90
                );

                animations.add(animation);
            }

            AnimatorSet buttonSet =
                    new AnimatorSet();

            buttonSet.playTogether(
                    animations
            );

            AnimatorSet all =
                    new AnimatorSet();

            all.playTogether(
                    logoAnimation,
                    buttonSet
            );

            all.start();
        }

        /*
         * =====================================================
         * LOGO PROPERTY
         * =====================================================
         */

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getLogoOffset() {
            return logoOffset;
        }

        /*
         * =====================================================
         * BUTTON PROPERTIES
         * =====================================================
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
         * =====================================================
         * HAUPTZEICHNUNG
         * =====================================================
         */

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            /*
             * Hintergrund
             */
            drawBackground(
                    canvas,
                    width,
                    height
            );

            /*
             * =================================================
             * OBEREN BEREICH SAUBER ABDECKEN
             * =================================================
             *
             * Der originale Hintergrund enthält bereits
             * ein Auto-Check-Logo.
             *
             * Dieses wird komplett abgedeckt.
             *
             * Danach zeichnen wir unser eigenes Logo.
             */

            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setAlpha(255);

            canvas.drawRect(
                    0,
                    0,
                    width,
                    height * 0.345f,
                    paint
            );

            /*
             * =================================================
             * NEUES LOGO
             * =================================================
             */

            drawLogo(
                    canvas,
                    width,
                    height
            );

            /*
             * =================================================
             * UNTEREN ALTEN BUTTONBEREICH ABDECKEN
             * =================================================
             *
             * Die alten Buttons aus dem JPG verschwinden.
             *
             * Wir beginnen die schwarze Fläche erst kurz
             * vor unserem neuen Buttonbereich.
             */

            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setAlpha(252);

            canvas.drawRect(
                    0,
                    height * 0.705f,
                    width,
                    height,
                    paint
            );

            paint.setAlpha(255);

            /*
             * =================================================
             * NEUE BUTTONS
             * =================================================
             */

            drawButtons(
                    canvas,
                    width,
                    height
            );
        }

        /*
         * =====================================================
         * HINTERGRUND
         * =====================================================
         */

        private void drawBackground(
                Canvas canvas,
                int width,
                int height
        ) {

            if (background == null) {

                canvas.drawColor(
                        Color.BLACK
                );

                return;
            }

            source.set(
                    0,
                    0,
                    background.getWidth(),
                    background.getHeight()
            );

            /*
             * Hintergrund proportional aufziehen.
             */
            float scale =
                    Math.max(
                            (float) width /
                                    background.getWidth(),

                            (float) height /
                                    background.getHeight()
                    );

            float scaledWidth =
                    background.getWidth()
                            * scale;

            float scaledHeight =
                    background.getHeight()
                            * scale;

            float left =
                    (width - scaledWidth)
                            / 2f;

            float top =
                    (height - scaledHeight)
                            / 2f;

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
         * =====================================================
         * LOGO
         * =====================================================
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

            /*
             * Das Logo kommt von oben herunter.
             */
            canvas.translate(
                    0,
                    logoOffset
            );

            /*
             * Logo etwas kleiner als die komplette
             * Bildschirmbreite, damit es sauber wirkt.
             */
            float logoWidth =
                    width * 0.98f;

            float ratio =
                    (float) logo.getHeight()
                            / (float) logo.getWidth();

            float logoHeight =
                    logoWidth * ratio;

            /*
             * Logo darf ungefähr den oberen
             * Drittelbereich ausfüllen.
             */
            float maxHeight =
                    height * 0.31f;

            if (logoHeight > maxHeight) {

                logoHeight =
                        maxHeight;

                logoWidth =
                        logoHeight / ratio;
            }

            /*
             * Jetzt kommt das Logo bewusst etwas
             * weiter nach unten.
             */
            float logoTop =
                    height * 0.035f;

            float logoLeft =
                    (width - logoWidth)
                            / 2f;

            Rect logoSource =
                    new Rect(
                            0,
                            0,
                            logo.getWidth(),
                            logo.getHeight()
                    );

            RectF logoDestination =
                    new RectF(
                            logoLeft,
                            logoTop,
                            logoLeft + logoWidth,
                            logoTop + logoHeight
                    );

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
         * =====================================================
         * BUTTONS
         * =====================================================
         */

        private void drawButtons(
                Canvas canvas,
                int width,
                int height
        ) {

            /*
             * Sehr kleine Außenränder.
             */
            float side =
                    width * 0.018f;

            /*
             * Abstand zwischen den Buttons.
             */
            float gap =
                    width * 0.014f;

            /*
             * Drei gleich große Spalten.
             */
            float buttonWidth =
                    (
                            width
                            - side * 2f
                            - gap * 2f
                    ) / 3f;

            /*
             * Große Buttons.
             */
            float buttonHeight =
                    height * 0.105f;

            /*
             * =================================================
             * POSITION
             * =================================================
             *
             * Die komplette Buttongruppe wird etwas höher
             * gesetzt.
             */

            float firstY =
                    height * 0.715f;

            float secondY =
                    height * 0.835f;

            for (int i = 0; i < 6; i++) {

                int column =
                        i % 3;

                int row =
                        i / 3;

                float x =
                        side
                        + column *
                        (buttonWidth + gap)
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
         * =====================================================
         * BUTTON DESIGN
         * =====================================================
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
                    label.equals(
                            "MEIN AUTO"
                    );

            /*
             * Hintergrund
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
             * Schatten
             */
            paint.setShadowLayer(
                    12,
                    0,
                    5,
                    Color.argb(
                            220,
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
             * Rahmen
             */
            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    pressed
                            ? 5f
                            : 2.5f
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
             * Icon
             */
            drawIcon(
                    canvas,
                    x + width / 2f,
                    y + height * 0.34f,
                    width * 0.125f,
                    label
            );

            /*
             * Beschriftung
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
         * =====================================================
         * ICONS
         * =====================================================
         */

        private void drawIcon(
                Canvas canvas,
                float cx,
                float cy,
                float size,
                String label
        ) {

            paint.setShader(null);
            paint.setColor(Color.WHITE);

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    4.2f
            );

            paint.setStrokeCap(
                    Paint.Cap.ROUND
            );

            if (
                    label.equals(
                            "MEIN AUTO"
                    )
            ) {

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
                    label.equals(
                            "REPARATUREN"
                    )
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
                    label.equals(
                            "WARTUNGEN"
                    )
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
                    label.equals(
                            "KOSTEN"
                    )
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
                    label.equals(
                            "STATISTIK"
                    )
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

                /*
                 * Einstellungen / Zahnrad
                 */
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
                                    (float)
                                            Math.cos(angle)
                                    * size * 0.8f,

                            cy +
                                    (float)
                                            Math.sin(angle)
                                    * size * 0.8f,

                            cx +
                                    (float)
                                            Math.cos(angle)
                                    * size,

                            cy +
                                    (float)
                                            Math.sin(angle)
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
         * =====================================================
         * TOUCH
         * =====================================================
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
