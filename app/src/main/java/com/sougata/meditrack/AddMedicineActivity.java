package com.sougata.meditrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Objects;
import java.util.UUID;

public class AddMedicineActivity extends AppCompatActivity {
    EditText name;
    TextView showEndDate;
    Button saveData, addTime;
    Spinner shapes;
    LinearLayout container;
    RadioGroup radioGroup;
    ImageView endDateSelector;
    CheckBox[] days = new CheckBox[7];
    ArrayList<Integer> daysInt = new ArrayList<>();
    ArrayList<String> alarmIds = new ArrayList<>();
    ArrayList<Long> alarmIdx = new ArrayList<>();
    ArrayList<AddMedicineTimeContent> contents = new ArrayList<>();
    LinearLayout containerLayout;
    Database db;
    int shape, editDataId;
    int repeat = 0;
    long endDate;
    boolean isEdit;

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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Medicine");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.et_medicine_name);
        shapes = findViewById(R.id.sp_shapes);
        days[0] = findViewById(R.id.cb_sunday);
        days[1] = findViewById(R.id.cb_monday);
        days[2] = findViewById(R.id.cb_tuesday);
        days[3] = findViewById(R.id.cb_wednesday);
        days[4] = findViewById(R.id.cb_thursday);
        days[5] = findViewById(R.id.cb_friday);
        days[6] = findViewById(R.id.cb_saturday);

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


        addTime = findViewById(R.id.btn_add_time);
        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeText();
            }
        });

        containerLayout = findViewById(R.id.tv_times_container);
        saveData = findViewById(R.id.btn_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRemainder();
            }
        });
        db = new Database(AddMedicineActivity.this);

        container = findViewById(R.id.ll_add_end_container);

        radioGroup = findViewById(R.id.rg_add_repeat);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton.getText().toString().equals("Repeated")) {
                repeat = 1;
                setRepeatLayout();
            } else {
                setOnceLayout();
            }
        });

        Calendar c = Calendar.getInstance();
        endDate = c.getTimeInMillis();
        String d = HelperFunctions.pad(c.get(Calendar.DATE)) + "/" + HelperFunctions.pad(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        showEndDate = findViewById(R.id.tv_add_end_date);
        showEndDate.setText(d);

        endDateSelector = findViewById(R.id.iv_add_change_date);
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
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("edit", false);
        if (isEdit) {
            editDataId = intent.getIntExtra("id", 0);
            loadEditData();
        }
    }

    private void setRepeatLayout() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, HelperFunctions.dpToPx(this, 10), 0, HelperFunctions.dpToPx(this, 10));
        container.setLayoutParams(layoutParams);
    }

    private void setOnceLayout() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
        );
        container.setLayoutParams(layoutParams);
    }

    private void loadEditData() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Medicine");
        saveData.setText("Edit");
        Cursor cursor = db.getSpecificMedicine(editDataId);
        Cursor c2 = db.getTimesOfMedicine(editDataId);
        while (cursor.moveToNext()) {
            boolean isRepeated = cursor.getInt(3) == 1;
            name.setText(cursor.getString(1));
            shapes.setSelection(HelperFunctions.idToSelection(cursor.getInt(2)));
            radioGroup.check(isRepeated ? R.id.rb_repeated : R.id.rb_once);
            endDate = cursor.getLong(5);
            if (isRepeated) {
                String repeatDays = cursor.getString(6);
                long endDateMilli = cursor.getLong(5);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(endDateMilli);
                String d = HelperFunctions.calendarToDate(c);
                showEndDate.setText(d);
                String[] dys = repeatDays.split(" ");
                for (int i = 0; i < 7; i++) {
                    if (dys[i].equals("1")) {
                        days[i].setChecked(true);
                    }
                }
                setRepeatLayout();
            } else {
                setOnceLayout();
            }
        }
        cursor.close();
        while (c2.moveToNext()) {
            long idx = c2.getLong(0);
            long time = c2.getLong(2);
            String alarmId = c2.getString(3);
            alarmIdx.add(idx);
            alarmIds.add(alarmId);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.add(Calendar.MILLISECOND, (int) time);
            AddMedicineTimeContent content = new AddMedicineTimeContent(time, UUID.randomUUID().toString(), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            contents.add(content);
            containerLayout.addView(getTimeView(content));
        }
        c2.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private LinearLayout getTimeView(AddMedicineTimeContent content) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.bg_edit_text);
        int margin = (int) (5 * this.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.setMargins(margin, margin, margin, margin);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        int padding = (int) (10 * this.getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, 0, padding);
        textView.setText(HelperFunctions.get12hTime(content.h, content.m));
        textView.setTextSize(16);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                (int) (35 * getResources().getDisplayMetrics().density),
                (int) (35 * getResources().getDisplayMetrics().density)
        );
        imageView.setLayoutParams(imageParams);
        imageView.setContentDescription(getString(R.string.delete));
        int imagePadding = (int) (5 * getResources().getDisplayMetrics().density);
        imageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
        imageView.setImageResource(R.drawable.ic_cross);
        imageView.setColorFilter(getResources().getColor(R.color.danger, getTheme()));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contents.removeIf(a -> a.id.equals(content.id));
                containerLayout.removeAllViews();
                for (AddMedicineTimeContent con : contents) {
                    containerLayout.addView(getTimeView(con));
                }
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(imageView);

        return linearLayout;
    }

    public void addTimeText() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MILLISECOND, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.HOUR_OF_DAY, hour);
                long t1 = c.getTimeInMillis();
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.HOUR_OF_DAY, 0);
                long t2 = c.getTimeInMillis();
                long t = t1 - t2;
                for (AddMedicineTimeContent con : contents) {
                    if (con.time == t) {
                        Toast.makeText(AddMedicineActivity.this, "Time already added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                AddMedicineTimeContent content = new AddMedicineTimeContent(t, UUID.randomUUID().toString(), hour, minute);
                contents.add(content);
                LinearLayout ll = getTimeView(content);
                containerLayout.addView(ll);
            }
        }, 0, 0, false);
        timePickerDialog.show();
    }

    public void saveRemainder() {
        if (isEdit) {
            cancelPreviousAlarms();
        }

        StringBuilder repeatDays = new StringBuilder();
        if (repeat == 1) {
            for (int i = 0; i < 7; i++) {
                CheckBox day = days[i];
                if (day.isChecked()) {
                    repeatDays.append("1 ");
                    daysInt.add(i + 1);
                } else {
                    repeatDays.append("0 ");
                }
            }
        } else {
            int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            daysInt.add(d);
            for (int i = 1; i <= 7; i++) {
                if (i != d) {
                    repeatDays.append("0 ");
                } else {
                    repeatDays.append("1 ");
                }
            }
        }
        String repeatDaysStr = repeatDays.toString().trim();

        String txt = name.getText().toString();
        if (!txt.isEmpty() && !contents.isEmpty()) {
            String ids = setAlarms(repeat, daysInt, txt, shape);
            String[] alarmIds = ids.split(",");
            for (int i = 0; i < alarmIds.length; i++) {
                alarmIds[i] = alarmIds[i].trim();
            }
            Calendar creationDate = Calendar.getInstance(), temp = Calendar.getInstance();
            creationDate.setTimeInMillis(0);
            creationDate.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DATE), 0, 0, 0);

            if (isEdit) {
                db.editMedicine(editDataId, txt, shape, repeat, contents, creationDate.getTimeInMillis(), endDate, repeatDaysStr, alarmIds);
                Toast.makeText(this, getResources().getText(R.string.medici_edited), Toast.LENGTH_SHORT).show();
            } else {
                db.insertData(txt, shape, repeat, contents, creationDate.getTimeInMillis(), endDate, repeatDaysStr, alarmIds);
                Toast.makeText(this, getResources().getText(R.string.medicine_added), Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(this, getResources().getText(R.string.please_fill_al_the_details), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelPreviousAlarms() {
        for (String alarmId : alarmIds) {
            String[] alIds = alarmId.split(" ");
            for (String alId : alIds) {
                AlarmScheduler.cancelAlarm(getApplicationContext(), alId);
            }
        }
    }

    private String setAlarms(int repeat, ArrayList<Integer> days, String name, int icon) {
        StringBuilder ids = new StringBuilder();
        for (AddMedicineTimeContent content : contents) {
            if (repeat == 1) {
                for (int day : days) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.MILLISECOND, 0);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.add(Calendar.MILLISECOND, (int) content.time);
                    int diff = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - day;
                    if (diff != 0) {
                        c.add(Calendar.DATE, -diff);
                    }
                    if (c.before(Calendar.getInstance())) {
                        c.add(Calendar.DATE, 7);
                    }
                    long delay = c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    String uniqueWorkerName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + c.getTimeInMillis();
                    ids.append(uniqueWorkerName).append(" ");
                    AlarmScheduler.schedulePeriodicAlarm(this, uniqueWorkerName, delay, name, icon);
                }
            } else {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MILLISECOND, 0);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                c.add(Calendar.MILLISECOND, (int) content.time);
                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, 1);
                }
                long delay = c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                String uniqueWorkerName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + c.getTimeInMillis();
                ids.append(uniqueWorkerName);
                AlarmScheduler.scheduleOneTimeAlarm(this, uniqueWorkerName, delay, name, shape);
            }
            ids.append(",");
        }
        return ids.toString();
    }

}