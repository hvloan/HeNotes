package vn.com.hvloan.henotes.Events;

import androidx.cardview.widget.CardView;

import vn.com.hvloan.henotes.Models.Note;

public interface NoteClickListener {
    void onClick(Note note);
    void onLongClick(Note note, CardView cardView);
}
