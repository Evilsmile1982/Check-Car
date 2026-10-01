package de.dm.carcheck;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

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
    private Button saveButton;
    private Button deleteButton;

    private String currentTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTitle = getIntent().getStringExtra("title");
        if (currentTitle == null) {
            currentTitle = "";
        }

        prefs = getSharedPreferences("car_check_data", MODE_PRIVATE);

        loadVehicles();

        if (currentTitle.equals("MEIN AUTO")) {
            showVehicleManager();
        } else {
            showPlaceholder();
        }
    }

    // ============================================================
    // FAHRZEUGVERWALTUNG
    // ============================================================

    private void showVehicleManager() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 24, 28, 20);
        root.setBackgroundColor(Color.rgb(5, 7, 10));

        // --------------------------------------------------------
        // Überschrift
        // --------------------------------------------------------

        TextView title = new TextView(this);
        title.setText("MEIN AUTO");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                65
        ));

        // --------------------------------------------------------
        // Fahrzeugzähler
        // --------------------------------------------------------

        carCounter = new TextView(this);
        carCounter.setTextColor(Color.LTGRAY);
        carCounter.setTextSize(17);
        carCounter.setGravity(Gravity.CENTER);
        root.addView(carCounter, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                45
        ));

        // --------------------------------------------------------
        // Fahrzeugbild
        // --------------------------------------------------------

        carImage = new ImageView(this);
        carImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        carImage.setBackgroundColor(Color.rgb(12, 14, 18));

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        300
                );

        imageParams.setMargins(0, 8, 0, 12);

        root.addView(carImage, imageParams);

        carImage.setOnClickListener(v -> chooseCarImage());

        // Hinweis
        TextView imageHint = new TextView(this);
        imageHint.setText("📷  Fahrzeugfoto hinzufügen / ändern");
        imageHint.setTextColor(Color.LTGRAY);
        imageHint.setTextSize(15);
        imageHint.setGravity(Gravity.CENTER);
        root.addView(imageHint, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                38
        ));

        // --------------------------------------------------------
        // Eingabefelder
        // --------------------------------------------------------

        brandField = createField("Marke");
        typeField = createField("Typ / Modell");
        engineField = createField("Motor");
        yearField = createField("Baujahr");
        kmField = createField("Kilometerstand");

        root.addView(brandField);
        root.addView(typeField);
        root.addView(engineField);
        root.addView(yearField);
        root.addView(kmField);

        // --------------------------------------------------------
        // Navigation Fahrzeuge
        // --------------------------------------------------------

        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setGravity(Gravity.CENTER);
        navigation.setPadding(0, 15, 0, 8);

        previousButton = createDarkButton("‹ ZURÜCK");
        nextButton = createDarkButton("WEITER ›");

        navigation.addView(previousButton, new LinearLayout.LayoutParams(
                0, 58, 1
        ));

        navigation.addView(nextButton, new LinearLayout.LayoutParams(
                0, 58, 1
        ));

        root.addView(navigation);

        previousButton.setOnClickListener(v -> {
            saveCurrentVehicle();

            if (selectedCar > 0) {
                selectedCar--;
                loadSelectedVehicle();
            }
        });

        nextButton.setOnClickListener(v -> {
            saveCurrentVehicle();

            if (selectedCar < MAX_CARS - 1) {
                selectedCar++;
                loadSelectedVehicle();
            }
        });

        // --------------------------------------------------------
        // Speichern
        // --------------------------------------------------------

        saveButton = createRedButton("FAHRZEUG SPEICHERN");

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        62
                );

        saveParams.setMargins(0, 8, 0, 8);

        root.addView(saveButton, saveParams);

        saveButton.setOnClickListener(v -> {
            saveCurrentVehicle();
            saveVehicles();

            android.widget.Toast.makeText(
                    this,
                    "Fahrzeug gespeichert",
                    android.widget.Toast.LENGTH_SHORT
            ).show();

            updateNavigation();
        });

        // --------------------------------------------------------
        // Fahrzeug löschen
        // --------------------------------------------------------

        deleteButton = createDarkButton("FAHRZEUG LÖSCHEN");

        root.addView(deleteButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
        ));

        deleteButton.setOnClickListener(v -> deleteCurrentVehicle());

        setContentView(root);

        loadSelectedVehicle();
    }

    // ============================================================
    // EINGABEFELD
    // ============================================================

    private EditText createField(String hint) {

        EditText field = new EditText(this);

        field.setHint(hint);
        field.setHintTextColor(Color.rgb(145, 150, 160));
        field.setTextColor(Color.WHITE);
        field.setTextSize(17);
        field.setSingleLine(true);
        field.setPadding(20, 0, 20, 0);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(15, 18, 23));
        background.setCornerRadius(18);
        background.setStroke(2, Color.rgb(70, 75, 85));

        field.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        58
                );

        params.setMargins(0, 5, 0, 5);

        field.setLayoutParams(params);

        return field;
    }

    // ============================================================
    // BUTTONS
    // ============================================================

    private Button createDarkButton(String text) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setAllCaps(false);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(15, 18, 23));
        background.setCornerRadius(18);
        background.setStroke(2, Color.rgb(80, 85, 95));

        button.setBackground(background);

        return button;
    }

    private Button createRedButton(String text) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setAllCaps(false);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(145, 5, 12));
        background.setCornerRadius(18);
        background.setStroke(3, Color.rgb(230, 20, 30));

        button.setBackground(background);

        return button;
    }

    // ============================================================
    // FOTO AUSWÄHLEN
    // ============================================================

    private void chooseCarImage() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri uri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
            }

            Vehicle vehicle = getCurrentVehicle();
            vehicle.photo = uri.toString();

            loadPhoto(vehicle);

            saveVehicles();
        }
    }

    // ============================================================
    // FAHRZEUG LADEN
    // ============================================================

    private void loadSelectedVehicle() {

        Vehicle vehicle = getCurrentVehicle();

        brandField.setText(vehicle.brand);
        typeField.setText(vehicle.type);
        engineField.setText(vehicle.engine);
        yearField.setText(vehicle.year);
        kmField.setText(vehicle.kilometers);

        loadPhoto(vehicle);

        updateNavigation();
    }

    private void loadPhoto(Vehicle vehicle) {

        if (vehicle.photo == null || vehicle.photo.isEmpty()) {

            carImage.setImageResource(android.R.drawable.ic_menu_camera);

            carImage.setColorFilter(
                    Color.rgb(100, 105, 115),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );

            return;
        }

        try {

            carImage.clearColorFilter();

            Uri uri = Uri.parse(vehicle.photo);

            InputStream input =
                    getContentResolver().openInputStream(uri);

            Bitmap bitmap =
                    BitmapFactory.decodeStream(input);

            if (input != null) {
                input.close();
            }

            carImage.setImageBitmap(bitmap);

        } catch (Exception e) {

            carImage.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        }
    }

    // ============================================================
    // FAHRZEUG SPEICHERN
    // ============================================================

    private void saveCurrentVehicle() {

        Vehicle vehicle = getCurrentVehicle();

        vehicle.brand =
                brandField.getText().toString().trim();

        vehicle.type =
                typeField.getText().toString().trim();

        vehicle.engine =
                engineField.getText().toString().trim();

        vehicle.year =
                yearField.getText().toString().trim();

        vehicle.kilometers =
                kmField.getText().toString().trim();

        if (!vehicle.isEmpty()) {
            vehicle.exists = true;
        }
    }

    private Vehicle getCurrentVehicle() {

        while (vehicles.size() <= selectedCar) {
            vehicles.add(new Vehicle());
        }

        return vehicles.get(selectedCar);
    }

    // ============================================================
    // NAVIGATION
    // ============================================================

    private void updateNavigation() {

        carCounter.setText(
                "FAHRZEUG " +
                        (selectedCar + 1) +
                        " / " +
                        MAX_CARS
        );

        previousButton.setEnabled(selectedCar > 0);

        previousButton.setAlpha(
                selectedCar > 0 ? 1.0f : 0.35f
        );

        nextButton.setEnabled(selectedCar < MAX_CARS - 1);

        nextButton.setAlpha(
                selectedCar < MAX_CARS - 1 ? 1.0f : 0.35f
        );
    }

    // ============================================================
    // FAHRZEUG LÖSCHEN
    // ============================================================

    private void deleteCurrentVehicle() {

        new android.app.AlertDialog.Builder(this)
                .setTitle("Fahrzeug löschen?")
                .setMessage(
                        "Das aktuell ausgewählte Fahrzeug " +
                        "wird gelöscht."
                )
                .setNegativeButton("ABBRECHEN", null)
                .setPositiveButton(
                        "LÖSCHEN",
                        (dialog, which) -> {

                            Vehicle vehicle =
                                    getCurrentVehicle();

                            vehicle.clear();

                            saveVehicles();
                            loadSelectedVehicle();

                            android.widget.Toast.makeText(
                                    this,
                                    "Fahrzeug gelöscht",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // ============================================================
    // DATEN SPEICHERN
    // ============================================================

    private void saveVehicles() {

        try {

            JSONArray array = new JSONArray();

            for (Vehicle vehicle : vehicles) {

                JSONObject object = new JSONObject();

                object.put("exists", vehicle.exists);
                object.put("brand", vehicle.brand);
                object.put("type", vehicle.type);
                object.put("engine", vehicle.engine);
                object.put("year", vehicle.year);
                object.put("kilometers", vehicle.kilometers);
                object.put("photo", vehicle.photo);

                array.put(object);
            }

            prefs.edit()
                    .putString(
                            "vehicles",
                            array.toString()
                    )
                    .apply();

        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // DATEN LADEN
    // ============================================================

    private void loadVehicles() {

        vehicles.clear();

        String data =
                prefs.getString("vehicles", "");

        if (data.isEmpty()) {

            for (int i = 0; i < MAX_CARS; i++) {
                vehicles.add(new Vehicle());
            }

            return;
        }

        try {

            JSONArray array =
                    new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {

                JSONObject object =
                        array.getJSONObject(i);

                Vehicle vehicle =
                        new Vehicle();

                vehicle.exists =
                        object.optBoolean(
                                "exists",
                                false
                        );

                vehicle.brand =
                        object.optString(
                                "brand",
                                ""
                        );

                vehicle.type =
                        object.optString(
                                "type",
                                ""
                        );

                vehicle.engine =
                        object.optString(
                                "engine",
                                ""
                        );

                vehicle.year =
                        object.optString(
                                "year",
                                ""
                        );

                vehicle.kilometers =
                        object.optString(
                                "kilometers",
                                ""
                        );

                vehicle.photo =
                        object.optString(
                                "photo",
                                ""
                        );

                vehicles.add(vehicle);
            }

        } catch (Exception ignored) {
        }

        while (vehicles.size() < MAX_CARS) {
            vehicles.add(new Vehicle());
        }

        if (vehicles.size() > MAX_CARS) {

            while (vehicles.size() > MAX_CARS) {
                vehicles.remove(vehicles.size() - 1);
            }
        }
    }

    // ============================================================
    // EINFACHE PLACEHOLDER FÜR DIE ANDEREN BEREICHE
    // ============================================================

    private void showPlaceholder() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setGravity(Gravity.CENTER);

        root.setPadding(30, 30, 30, 30);

        root.setBackgroundColor(
                Color.rgb(5, 7, 10)
        );

        TextView title = new TextView(this);

        title.setText(currentTitle);

        title.setTextColor(Color.WHITE);

        title.setTextSize(30);

        title.setGravity(Gravity.CENTER);

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        root.addView(title);

        TextView info = new TextView(this);

        info.setText(
                "Dieser Bereich wird als Nächstes aufgebaut."
        );

        info.setTextColor(Color.LTGRAY);

        info.setTextSize(17);

        info.setGravity(Gravity.CENTER);

        root.addView(info);

        setContentView(root);
    }

    // ============================================================
    // FAHRZEUG-KLASSE
    // ============================================================

    private static class Vehicle {

        boolean exists = false;

        String brand = "";
        String type = "";
        String engine = "";
        String year = "";
        String kilometers = "";
        String photo = "";

        boolean isEmpty() {

            return brand.isEmpty()
                    && type.isEmpty()
                    && engine.isEmpty()
                    && year.isEmpty()
                    && kilometers.isEmpty()
                    && photo.isEmpty();
        }

        void clear() {

            exists = false;

            brand = "";
            type = "";
            engine = "";
            year = "";
            kilometers = "";
            photo = "";
        }
    }
}
