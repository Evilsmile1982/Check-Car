package de.dm.carcheck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

        private final Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG
                        | Paint.FILTER_BITMAP_FLAG
                        | Paint.DITHER_FLAG
        );

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

        private final float[] buttonOffset = new float[6];

        private float logoOffset = -700f;

        private int pressedIndex = -1;

        AutoCheckView() {
            super(MainActivity.this);

            background = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background
            );

            /*
             * DAS VON DIR HOCHGELADENE LOGO
             */
            logo = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.auto_check_logo
            );

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

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
             * Logo fährt von oben herunter.
             */
            ObjectAnimator logoAnimation =
                    ObjectAnimator.ofFloat(
                            this,
                            "logoOffset",
                            -700f,
                            0f
                    );

            logoAnimation.setDuration(1200);

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

                animation.setDuration(850);

                animation.setStartDelay(
                        (i % 3) * 80
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
         * LOGO
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
         * BUTTON OFFSETS
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
             * KOMPLETTER HINTERGRUND SCHWARZ
             */
            canvas.drawColor(Color.BLACK);

            /*
             * 1. LOGO
             */
            drawLogo(
                    canvas,
                    width,
                    height
            );

            /*
             * 2. NUR DER SAUBERE AUTO-BEREICH
             *
             * WICHTIG:
             * Der Bereich mit den alten Buttons
             * wird gar nicht erst verwendet.
             */
            drawCarImage(
                    canvas,
                    width,
                    height
            );

            /*
             * 3. SCHWARZER BEREICH
             *
             * Dieser Bereich beginnt VOR den
             * alten Hintergrund-Buttons.
             */
            drawCleanBlackArea(
                    canvas,
                    width,
                    height
            );

            /*
             * 4. NEUE BUTTONS
             */
            drawButtons(
                    canvas,
                    width,
                    height
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

            canvas.translate(
                    0,
                    logoOffset
            );

            /*
             * Logo etwas weiter unten.
             */
            float top =
                    height * 0.018f;

            /*
             * Sehr großes Logo.
             */
            float logoWidth =
                    width * 0.965f;

            float ratio =
                    (float) logo.getHeight()
                            / (float) logo.getWidth();

            float logoHeight =
                    logoWidth * ratio;

            /*
             * Begrenzung damit es nicht
             * mit dem Auto kollidiert.
             */
            float maxHeight =
                    height * 0.245f;

            if (logoHeight > maxHeight) {

                logoHeight =
                        maxHeight;

                logoWidth =
                        logoHeight / ratio;
            }

            float left =
                    (width - logoWidth) / 2f;

            Rect source =
                    new Rect(
                            0,
                            0,
                            logo.getWidth(),
                            logo.getHeight()
                    );

            RectF destination =
                    new RectF(
                            left,
                            top,
                            left + logoWidth,
                            top + logoHeight
                    );

            paint.setAlpha(255);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawBitmap(
                    logo,
                    source,
                    destination,
                    paint
            );

            canvas.restore();
        }

        /*
         * =====================================================
         * AUTO-BILD
         * =====================================================
         */

        private void drawCarImage(
                Canvas canvas,
                int width,
                int height
        ) {

            if (background == null) {
                return;
            }

            int imageWidth =
                    background.getWidth();

            int imageHeight =
                    background.getHeight();

            /*
             * OBERER BEREICH DES JPGs WIRD NICHT
             * VERWENDET, weil dort das alte Logo sitzt.
             */
            int sourceTop =
                    (int) (
                            imageHeight * 0.285f
                    );

            /*
             * SEHR WICHTIG:
             *
             * Wir schneiden hier VOR den
             * alten Buttons ab.
             *
             * Die alten Buttons werden somit
             * überhaupt nicht mehr in das
             * neue Bild übernommen.
             */
            int sourceBottom =
                    (int) (
                            imageHeight * 0.620f
                    );

            Rect source =
                    new Rect(
                            0,
                            sourceTop,
                            imageWidth,
                            sourceBottom
                    );

            /*
             * Auto beginnt unter dem Logo.
             */
            float destinationTop =
                    height * 0.255f;

            /*
             * Auto endet deutlich VOR
             * unseren neuen Buttons.
             */
            float destinationBottom =
                    height * 0.635f;

            RectF destination =
                    new RectF(
                            0,
                            destinationTop,
                            width,
                            destinationBottom
                    );

            paint.setAlpha(255);
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
         * SCHWARZER BEREICH
         * =====================================================
         *
         * AB HIER GIBT ES ABSOLUT KEIN BILD MEHR.
         *
         * Damit können die alten Buttons aus
         * dem JPG nicht mehr erscheinen.
         */

        private void drawCleanBlackArea(
                Canvas canvas,
                int width,
                int height
        ) {

            float top =
                    height * 0.635f;

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.BLACK
            );

            paint.setAlpha(255);

            canvas.drawRect(
                    0,
                    top,
                    width,
                    height,
                    paint
            );
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

            float side =
                    width * 0.018f;

            float gap =
                    width * 0.014f;

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
             * Erste Reihe.
             */
            float firstY =
                    height * 0.690f;

            /*
             * Zweite Reihe.
             */
            float secondY =
                    height * 0.810f;

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

            paint.setStyle(
                    Paint.Style.FILL
            );

            if (red) {

                paint.setColor(
                        Color.rgb(
                                155,
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
                    14,
                    0,
                    5,
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
             * Rahmen
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
                                45,
                                45
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
             * Text
             */
            paint.setTypeface(
                    android.graphics.Typeface.create(
                            "sans-serif",
                            android.graphics.Typeface.BOLD
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
                            cx + (float) Math.cos(angle)
                                    * size * 0.8f,

                            cy + (float) Math.sin(angle)
                                    * size * 0.8f,

                            cx + (float) Math.cos(angle)
                                    * size,

                            cy + (float) Math.sin(angle)
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
