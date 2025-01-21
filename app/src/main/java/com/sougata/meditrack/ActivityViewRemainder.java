package com.sougata.meditrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ActivityViewRemainder extends AppCompatActivity {
    Database db;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_remainder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Medicine");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new Database(this);

        Intent intent = getIntent();
        int id2 = intent.getIntExtra("id", 0);
        id = id2;
        loadData(id2);

        ImageView deleteCurrentMed = findViewById(R.id.iv_view_medicine_delete_full);
        deleteCurrentMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteMedicine(id2);
                Toast.makeText(ActivityViewRemainder.this, "Medicine Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadData(int id) {
        Cursor cursor = db.getSpecificMedicine(id);
        String name;
        int icon, repeat;
        long endDateMilli;
        while (cursor.moveToNext()) {
            name = cursor.getString(1);
            icon = cursor.getInt(2);
            repeat = cursor.getInt(3);
            endDateMilli = cursor.getLong(5);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(endDateMilli);
            ImageView ivShape = findViewById(R.id.iv_view_medicine_shape);
            ivShape.setImageResource(icon);
            TextView tvName = findViewById(R.id.tv_view_medicine_name);
            tvName.setText(name);
            TextView tvRepeat = findViewById(R.id.tv_view_medicine_repeat);
            tvRepeat.setText(repeat == 0 ? "Remained only once" : "Remainder will be repeated every");
            TextView endDate = findViewById(R.id.tv_view_medicine_end_date);
            endDate.setText(HelperFunctions.calendarToDate(c));

            if (repeat == 1) {
                String repeatDays = cursor.getString(6);
                String s2 = getString(repeatDays);
                TextView tvWeekDays = findViewById(R.id.tv_add_medicine_repeat_days);
                tvWeekDays.setText(s2);
            } else {
                HorizontalScrollView hsv = findViewById(R.id.sv_view_medicine_container);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0
                );
                hsv.setLayoutParams(layoutParams);
            }
        }
        Cursor c = db.getTimesOfMedicine(id);
        LinearLayout container = findViewById(R.id.view_medicine_times_container);
        container.removeAllViews();
        int len = 0;
        while (c.moveToNext()) {
            len++;
            container.addView(generateDoseTimeView(HelperFunctions.timeToString(c.getLong(2)), c.getLong(0), c.getString(3)));
        }
        if (len == 0) {
            db.deleteMedicine(id);
            finish();
        }
    }

    private static String getString(String repeatDays) {
        String[] rd = repeatDays.split(" ");
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (rd[i].equals("1")) {
                s.append(weekDays[i]).append(", ");
            }
        }
        String s2 = s.toString().trim();
        if (s2.charAt(s2.length() - 1) == ',') {
            s2 = s2.substring(0, s2.length() - 1);
        }
        return s2;
    }

    private View generateDoseTimeView(String time, long idxId, String alarmId) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, HelperFunctions.dpToPx(this, 5));
        cardView.setLayoutParams(layoutParams);
        cardView.setCardBackgroundColor(this.getResources().getColor(android.R.color.white));
        cardView.setRadius(HelperFunctions.dpToPx(this, 5)); // Corner radius 5dp
        cardView.setCardElevation(HelperFunctions.dpToPx(this, 5));

        LinearLayout linearLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10)
        );

        LinearLayout linearLayout2 = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout2.setLayoutParams(layoutParams);
        linearLayout2.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);
        textView.setText(time);
        textView.setTextColor(this.getResources().getColor(R.color.black));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        TextView textView2 = new TextView(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView2.setLayoutParams(layoutParams);
        textView2.setText(HelperFunctions.isExpired(time));

        linearLayout2.addView(textView);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2);

        LinearLayout linearLayout3 = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout3.setLayoutParams(layoutParams);
        linearLayout3.setPadding(
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10),
                HelperFunctions.dpToPx(this, 10)
        );
        linearLayout3.setGravity(android.view.Gravity.END);

        ImageView imageView = new ImageView(this);
        layoutParams = new LinearLayout.LayoutParams(
                HelperFunctions.dpToPx(this, 25),
                HelperFunctions.dpToPx(this, 25)
        );
        layoutParams.setMargins(HelperFunctions.dpToPx(this, 30), 0, 0, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.trash_solid);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDelete(idxId, alarmId);
            }
        });

        linearLayout3.addView(imageView);
        linearLayout.addView(linearLayout3);
        cardView.addView(linearLayout);

        return cardView;
    }

    private void onClickDelete(long idxId, String alarmId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteSingleAlarm(idxId);
                String[] alarmIds = alarmId.split(" ");
                for (String i : alarmIds) {
                    AlarmScheduler.cancelAlarm(ActivityViewRemainder.this, i);
                }
                loadData(id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}