package de.dm.carcheck;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;

public class SubmenuActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40,40,40,40);
        root.setBackgroundColor(Color.rgb(7,9,12));

        TextView title=new TextView(this);
        title.setText(getIntent().getStringExtra("title"));
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView sub=new TextView(this);
        sub.setText(getIntent().getStringExtra("subtitle"));
        sub.setTextColor(Color.LTGRAY);
        sub.setTextSize(18);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0,20,0,50);

        Button back=new Button(this);
        back.setText("ZURÜCK");
        back.setOnClickListener(v -> finish());

        root.addView(title);
        root.addView(sub);
        root.addView(back, new LinearLayout.LayoutParams(-2,-2));
        setContentView(root);
    }
}
