package com.sougata.meditrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addMedicine;
    RecyclerView homeRemainderList;
    List<HomeMedicineListItem> homeMedicineListItems;
    Database db;
    TextView showDate;
    Calendar selectedDate;

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private final String[] requiredPermissions = {
            "android.permission.WAKE_LOCK",
            "android.permission.VIBRATE",
            "android.permission.POST_NOTIFICATIONS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addMedicine = findViewById(R.id.fab_add_medicine);
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                startActivity(intent);
            }
        });

        homeRemainderList = findViewById(R.id.rv_remainders);
        homeRemainderList.setLayoutManager(new LinearLayoutManager(this));

        db = new Database(MainActivity.this);
        showDate = findViewById(R.id.tv_view_selected_date);
        Calendar calendar = Calendar.getInstance();
        showDate.setText(HelperFunctions.calendarToDate(calendar));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        selectedDate = calendar;
        LinearLayout ll_dateContainer = findViewById(R.id.ll_home_date_container);
        ll_dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        c.set(year, month, day, 0, 0, 0);
                        selectedDate = c;
                        String d = HelperFunctions.pad(day) + "/" + HelperFunctions.pad(month + 1) + "/" + year;
                        showDate.setText(d);
                        loadAllData();
                    }

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });
        loadAllData();
    }

    protected void onResume() {
        super.onResume();
        loadAllData();
    }

    protected void onRestart() {
        super.onRestart();
        loadAllData();
    }

    private void loadAllData() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception ignored) {
        }
        homeMedicineListItems = new ArrayList<>();
        Cursor cursor = db.getAllMedicines();
        TextView tv = findViewById(R.id.tv_show_no_medicine);
        LinearLayout.LayoutParams layoutParams, rvLayoutParams;
        if (cursor.getCount() == 0) {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rvLayoutParams = new LinearLayout.LayoutParams(0, 0);
            layoutParams.setMargins(0, 10, 0, 10);
            layoutParams.gravity = Gravity.CENTER;
            tv.setLayoutParams(layoutParams);
            homeRemainderList.setLayoutParams(rvLayoutParams);
            return;
        }
        while (cursor.moveToNext()) {
            Calendar endDate = Calendar.getInstance(),
                    createdAt = Calendar.getInstance();
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int icon = cursor.getInt(2);
            int repeat = cursor.getInt(3);
            long creationDate = cursor.getLong(4);
            createdAt.setTimeInMillis(creationDate);
            long end = cursor.getLong(5);
            endDate.setTimeInMillis(end);
            endDate.add(Calendar.DATE, 1);
            String[] repeatedDays = cursor.getString(6).split(" ");
            int day = selectedDate.get(Calendar.DAY_OF_WEEK) - 1;
            if (repeatedDays[day].equals("1") && selectedDate.before(endDate) && createdAt.before(selectedDate)) {
                int dose = 0;
                Cursor c = db.getTimesOfMedicine(id);
                while (c.moveToNext()) {
                    dose++;
                }
                homeMedicineListItems.add(new HomeMedicineListItem(icon, dose, name, id, repeat));
                homeRemainderList.setAdapter(new HomeRecyclerViewAdapter(getApplicationContext(), homeMedicineListItems, (position) -> {
                    openViewMedicineActivity(homeMedicineListItems.get(position).getId());
                }));
            }
            if (homeMedicineListItems.isEmpty()) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rvLayoutParams = new LinearLayout.LayoutParams(0, 0);
            } else {
                layoutParams = new LinearLayout.LayoutParams(0, 0);
                rvLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
            }
            layoutParams.setMargins(0, 10, 0, 10);
            layoutParams.gravity = Gravity.CENTER;

            tv.setLayoutParams(layoutParams);
            homeRemainderList.setLayoutParams(rvLayoutParams);
        }
    }

    private void openViewMedicineActivity(int id) {
        Intent intent = new Intent(MainActivity.this, ActivityViewRemainder.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private boolean allPermissionsGranted() {
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}