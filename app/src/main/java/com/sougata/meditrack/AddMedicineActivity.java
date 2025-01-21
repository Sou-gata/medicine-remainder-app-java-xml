package com.sougata.meditrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {
    private EditText name;
    TextView showEndDate;
    private int shape;
    int repeat = 0;
    ArrayList<Long> times = new ArrayList<>();
    ArrayList<String> uniqueAlarmId = new ArrayList<>();
    private LinearLayout containerLayout;
    Database db;
    int h, m;
    long endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_medicine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Medicine");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.et_medicine_name);
        Spinner shapes = findViewById(R.id.sp_shapes);
        shapes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shape = ShapeDropDownMenu.getShapeList().get(position).getImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ShapesAdapter shapesAdapter = new ShapesAdapter(AddMedicineActivity.this, ShapeDropDownMenu.getShapeList());
        shapes.setAdapter(shapesAdapter);


        Button addTime = findViewById(R.id.btn_add_time);
        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeText();
            }
        });

        containerLayout = findViewById(R.id.tv_times_container);
        Button saveData = findViewById(R.id.btn_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRemainder();
            }
        });
        db = new Database(AddMedicineActivity.this);

        LinearLayout container = findViewById(R.id.ll_add_end_container);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, HelperFunctions.dpToPx(this, 10), 0, HelperFunctions.dpToPx(this, 10));
        container.setLayoutParams(layoutParams);

//        RadioGroup radioGroup = findViewById(R.id.rg_add_repeat);
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            RadioButton selectedRadioButton = findViewById(checkedId);
//            if (selectedRadioButton.getText().toString().equals("Repeated")) {
//                repeat = 1;
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//                layoutParams.setMargins(0, HelperFunctions.dpToPx(this, 10), 0, HelperFunctions.dpToPx(this, 10));
//                container.setLayoutParams(layoutParams);
//            } else {
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        0
//                );
//                container.setLayoutParams(layoutParams);
//            }
//        });

        Calendar c = Calendar.getInstance();
        endDate = c.getTimeInMillis();
        String d = HelperFunctions.pad(c.get(Calendar.DATE)) + "/" + HelperFunctions.pad(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        showEndDate = findViewById(R.id.tv_add_end_date);
        showEndDate.setText(d);

        ImageView endDateSelector = findViewById(R.id.iv_add_change_date);
        endDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddMedicineActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        c.set(year, month, day, 0, 0, 0);
                        endDate = c.getTimeInMillis();
                        String d = HelperFunctions.pad(day) + "/" + HelperFunctions.pad(month + 1) + "/" + year;
                        showEndDate.setText(d);
                    }

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void addTimeText() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                TextView tv = new TextView(AddMedicineActivity.this);
                String time = HelperFunctions.get12hTime(hour, minute);
                Calendar c = Calendar.getInstance();
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hour, minute, 0);
                long t1 = c.getTimeInMillis();
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
                long t2 = c.getTimeInMillis();
                long t = t1 - t2;
                if (times.contains(t)) {
                    return;
                }
                times.add(t);
                tv.setText(time);
                tv.setPadding(30, 30, 30, 30);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 0, 5, 0);
                tv.setLayoutParams(params);
                tv.setTextColor(getColor(R.color.black));
                tv.setTextSize(16);
                tv.setBackgroundResource(R.drawable.bg_edit_text);
                containerLayout.addView(tv);
            }
        }, 0, 0, false);
        timePickerDialog.show();
    }

    public void saveRemainder() {
        StringBuilder repeatDays = new StringBuilder();
        CheckBox[] days = new CheckBox[7];
        days[0] = findViewById(R.id.cb_sunday);
        days[1] = findViewById(R.id.cb_monday);
        days[2] = findViewById(R.id.cb_tuesday);
        days[3] = findViewById(R.id.cb_wednesday);
        days[4] = findViewById(R.id.cb_thursday);
        days[5] = findViewById(R.id.cb_friday);
        days[6] = findViewById(R.id.cb_saturday);
        ArrayList<Integer> dys = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            CheckBox day = days[i];
            if (day.isChecked()) {
                repeatDays.append("1 ");
                dys.add(i + 1);
            } else {
                repeatDays.append("0 ");
            }
        }
        String repeatDaysStr = repeatDays.toString().trim();

        String txt = name.getText().toString();
        if (!txt.isEmpty() && !times.isEmpty()) {
//            String ids = setAlarms(times, repeat, dys);
            String ids = setAlarms(times, 1, dys);
            String[] alarmIds = ids.split(",");
            for (int i = 0; i < alarmIds.length; i++) {
                alarmIds[i] = alarmIds[i].trim();
            }
            Calendar creationDate = Calendar.getInstance();
            creationDate.set(creationDate.get(Calendar.YEAR), creationDate.get(Calendar.MONTH), creationDate.get(Calendar.DATE), 0, 0, 0);
//            db.insertData(name.getText().toString(), shape, repeat, times, creationDate.getTimeInMillis(), endDate, repeatDaysStr, alarmIds);
            db.insertData(name.getText().toString(), shape, 1, times, creationDate.getTimeInMillis(), endDate, repeatDaysStr, alarmIds);
            finish();
        } else {
            Toast.makeText(this, "Please fill al the details", Toast.LENGTH_SHORT).show();
        }
    }

    private String setAlarms(ArrayList<Long> times, int repeat, ArrayList<Integer> days) {
        Calendar calendar = Calendar.getInstance();
        StringBuilder ids = new StringBuilder();
        for (long time : times) {
            Calendar c = HelperFunctions.timeMillisToHM(time);
            if (repeat == 1) {
                for (int day : days) {
                    c.set(Calendar.MILLISECOND, 0);
                    c.set(Calendar.DAY_OF_WEEK, day);
                    if (c.before(Calendar.getInstance())) {
                        c.add(Calendar.WEEK_OF_YEAR, 1);
                    }
                    long delay = c.getTimeInMillis() - calendar.getTimeInMillis();
                    String uniqueWorkerName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + String.valueOf(c.getTimeInMillis());
                    ids.append(uniqueWorkerName).append(" ");
                    AlarmScheduler.schedulePeriodicAlarm(this, uniqueWorkerName, delay);
                }
            } else {
                // TODO: fix this part
                c.set(Calendar.MILLISECOND, 0);
                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, 1);
                }
                long delay = c.getTimeInMillis() - calendar.getTimeInMillis();
                String uniqueWorkerName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + String.valueOf(c.getTimeInMillis());
                ids.append(uniqueWorkerName);
                AlarmScheduler.scheduleOneTimeAlarm(this, uniqueWorkerName, delay);
            }
            ids.append(",");
        }
        return ids.toString();
    }

}