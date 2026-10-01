package de.dm.carcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubmenuActivity extends Activity {

    private static final int MAX_CARS = 5;
    private static final int PICK_IMAGE = 1001;

    private SharedPreferences prefs;

    private final List<Vehicle> vehicles = new ArrayList<>();

    private int selectedCar = 0;

    private ImageView carImage;
    private TextView carCounter;

    private EditText brandField;
    private EditText typeField;
    private EditText engineField;
    private EditText yearField;
    private EditText kmField;

    private Button previousButton;
    private Button nextButton;

    private String currentTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.BLACK);

        currentTitle = getIntent().getStringExtra("title");

        if (currentTitle == null) {
            currentTitle = "";
        }

        prefs = getSharedPreferences(
                "car_check_data",
                MODE_PRIVATE
        );

        loadVehicles();

        if (currentTitle.equalsIgnoreCase("MEIN AUTO")) {
            showVehicleManager();
        } else {
            showPlaceholder();
        }
    }

    // ============================================================
    // MEIN AUTO
    // ============================================================

    private void showVehicleManager() {

        final FrameLayout screen = new FrameLayout(this);

        screen.setBackgroundColor(
                Color.rgb(3, 5, 8)
        );

        /*
         * Alles wird später anhand der echten Bildschirmgröße
         * positioniert. Dadurch gibt es keine Überlappungen mehr.
         */

        carImage = new ImageView(this);

        carImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        carImage.setBackgroundColor(
                Color.rgb(14, 17, 22)
        );

        screen.addView(carImage);

        carImage.setOnClickListener(
                v -> chooseCarImage()
        );

        // ========================================================
        // TITEL
        // ========================================================

        TextView title = new TextView(this);

        title.setText("MEIN AUTO");

        title.setTextColor(Color.WHITE);

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        title.setGravity(Gravity.CENTER);

        title.setIncludeFontPadding(true);

        screen.addView(title);

        // ========================================================
        // ZÄHLER
        // ========================================================

        carCounter = new TextView(this);

        carCounter.setTextColor(
                Color.rgb(175, 180, 190)
        );

        carCounter.setGravity(Gravity.CENTER);

        screen.addView(carCounter);

        // ========================================================
        // FOTO-HINWEIS
        // ========================================================

        TextView imageHint = new TextView(this);

        imageHint.setText(
                "📷   Fahrzeugfoto hinzufügen / ändern"
        );

        imageHint.setTextColor(
                Color.rgb(210, 215, 225)
        );

        imageHint.setGravity(Gravity.CENTER);

        screen.addView(imageHint);

        imageHint.setOnClickListener(
                v -> chooseCarImage()
        );

        // ========================================================
        // EINGABEFELDER
        // ========================================================

        brandField = createField("Marke");
        typeField = createField("Typ / Modell");
        engineField = createField("Motor");
        yearField = createField("Baujahr");
        kmField = createField("Kilometerstand");

        screen.addView(brandField);
        screen.addView(typeField);
        screen.addView(engineField);
        screen.addView(yearField);
        screen.addView(kmField);

        // ========================================================
        // NAVIGATION
        // ========================================================

        previousButton =
                createDarkButton("‹  ZURÜCK");

        nextButton =
                createDarkButton("WEITER  ›");

        screen.addView(previousButton);
        screen.addView(nextButton);

        previousButton.setOnClickListener(
                v -> {

                    saveCurrentVehicle();

                    if (selectedCar > 0) {
                        selectedCar--;
                        loadSelectedVehicle();
                    }
                }
        );

        nextButton.setOnClickListener(
                v -> {

                    saveCurrentVehicle();

                    if (selectedCar < MAX_CARS - 1) {
                        selectedCar++;
                        loadSelectedVehicle();
                    }
                }
        );

        // ========================================================
        // SPEICHERN
        // ========================================================

        Button saveButton =
                createRedButton(
                        "FAHRZEUG SPEICHERN"
                );

        screen.addView(saveButton);

        saveButton.setOnClickListener(
                v -> {

                    saveCurrentVehicle();
                    saveVehicles();

                    Toast.makeText(
                            this,
                            "Fahrzeug gespeichert",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        // ========================================================
        // LÖSCHEN
        // ========================================================

        Button deleteButton =
                createDarkButton(
                        "FAHRZEUG LÖSCHEN"
                );

        screen.addView(deleteButton);

        deleteButton.setOnClickListener(
                v -> deleteCurrentVehicle()
        );

        // ========================================================
        // SCREEN SETZEN
        // ========================================================

        setContentView(screen);

        /*
         * WICHTIG:
         * Erst nachdem Android die echte Bildschirmgröße kennt,
         * werden alle Elemente sauber verteilt.
         */

        screen.post(() -> {

            arrangeScreen(
                    screen,
                    title,
                    imageHint,
                    saveButton,
                    deleteButton
            );

            loadSelectedVehicle();
        });
    }

    // ============================================================
    // PROFESSIONELLE BILDSCHIRMAUFTEILUNG
    // ============================================================

    private void arrangeScreen(
            FrameLayout screen,
            TextView title,
            TextView imageHint,
            Button saveButton,
            Button deleteButton
    ) {

        int width = screen.getWidth();
        int height = screen.getHeight();

        /*
         * Wir verwenden die echte Bildschirmhöhe.
         *
         * Dadurch funktioniert die Darstellung unabhängig davon,
         * ob das Gerät 720, 1080 oder eine andere Auflösung hat.
         */

        int side = Math.max(
                12,
                (int)(width * 0.018f)
        );

        int usableWidth =
                width - side * 2;

        // --------------------------------------------------------
        // Gesamtbereich
        // --------------------------------------------------------

        int blockHeight =
                Math.min(
                        (int)(height * 0.88f),
                        1180
                );

        int top =
                (height - blockHeight) / 2;

        /*
         * Falls der Bildschirm sehr klein ist,
         * bleibt trotzdem ein kleiner Rand.
         */

        if (top < 20) {
            top = 20;
        }

        // --------------------------------------------------------
        // Größen
        // --------------------------------------------------------

        int titleHeight =
                Math.max(
                        58,
                        (int)(height * 0.055f)
                );

        int counterHeight =
                Math.max(
                        28,
                        (int)(height * 0.030f)
                );

        int photoHeight =
                Math.max(
                        240,
                        (int)(height * 0.205f)
                );

        int hintHeight =
                Math.max(
                        38,
                        (int)(height * 0.038f)
                );

        int fieldHeight =
                Math.max(
                        48,
                        (int)(height * 0.043f)
                );

        int fieldGap =
                Math.max(
                        5,
                        (int)(height * 0.006f)
                );

        int navigationHeight =
                Math.max(
                        52,
                        (int)(height * 0.050f)
                );

        int saveHeight =
                Math.max(
                        52,
                        (int)(height * 0.050f)
                );

        int deleteHeight =
                Math.max(
                        45,
                        (int)(height * 0.043f)
                );

        int gapSmall =
                Math.max(
                        5,
                        (int)(height * 0.007f)
                );

        int gapMedium =
                Math.max(
                        10,
                        (int)(height * 0.012f)
                );

        // --------------------------------------------------------
        // Position
        // --------------------------------------------------------

        int y = top;

        // ========================================================
        // TITEL
        // ========================================================

        place(
                title,
                side,
                y,
                usableWidth,
                titleHeight
        );

        title
