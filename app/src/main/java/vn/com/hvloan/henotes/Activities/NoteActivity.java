package vn.com.hvloan.henotes.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

import vn.com.hvloan.henotes.Models.Note;
import vn.com.hvloan.henotes.R;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NoteActivity extends AppCompatActivity {

    private Toolbar toolbarNote;
    private TextInputEditText txtTitle;
    private TextInputEditText txtContent;
    private AppCompatButton btnSave;
    private AppCompatButton btnChangeColor;
    private TextView txtDateCreate;
    private Note note;

    private boolean isOldNote = false;

    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initComponent();
        setupToolbarNote();
        actionButton();
    }

    private void actionButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleNote = txtTitle.getText().toString();
                String contentNote = txtContent.getText().toString();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date dateTime = Calendar.getInstance().getTime();

                if(titleNote.isEmpty()) {
                    Toast.makeText(NoteActivity.this, "Please input title your note!", Toast.LENGTH_LONG).show();
                }
                if(contentNote.isEmpty()) {
                    Toast.makeText(NoteActivity.this, "Please input content your note!", Toast.LENGTH_LONG).show();
                }

                if(!isOldNote){
                    note = new Note();
                }
                note.setTitle(titleNote);
                note.setContent(contentNote);
                note.setColor(String.valueOf(defaultColor));
                note.setDate(simpleDateFormat.format(dateTime));

                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenColorPickerDialog();
            }
        });
    }

    private void OpenColorPickerDialog() {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(NoteActivity.this, defaultColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {
                defaultColor = color;
                btnChangeColor.setBackgroundColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Toast.makeText(NoteActivity.this, "Color Picker Closed", Toast.LENGTH_SHORT).show();
            }
        });
        ambilWarnaDialog.show();

    }

    private void setupToolbarNote() {
        setSupportActionBar(toolbarNote);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initComponent() {
        toolbarNote = findViewById(R.id.toolbar_notes);
        txtTitle = findViewById(R.id.txt_title);
        txtContent = findViewById(R.id.txt_content);
        btnSave = findViewById(R.id.btn_save);
        btnChangeColor = findViewById(R.id.btn_change_color);
        txtDateCreate = findViewById(R.id.txt_date);
        defaultColor = ContextCompat.getColor(NoteActivity.this, R.color.orange);
        btnChangeColor.setBackgroundColor(defaultColor);

        txtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (txtContent.hasFocus()) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            }
        });

        note = new Note();
        try {
            note = (Note) getIntent().getSerializableExtra("old_note");
            txtTitle.setText(note.getTitle());
            txtContent.setText(note.getContent());
            btnChangeColor.setBackgroundColor(Integer.parseInt(note.getColor()));
            txtDateCreate.setText(note.getDate());
            txtDateCreate.setVisibility(View.VISIBLE);
            isOldNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}