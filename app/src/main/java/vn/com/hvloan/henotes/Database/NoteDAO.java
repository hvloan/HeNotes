package vn.com.hvloan.henotes.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import vn.com.hvloan.henotes.Models.Note;
import static androidx.room.OnConflictStrategy.REPLACE;

import java.util.List;

@Dao
public interface NoteDAO {

    @Insert(onConflict = REPLACE)
    void insert(Note note);

    @Query("SELECT * FROM tbl_notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Query("UPDATE tbl_notes SET title = :title, content = :content, color = :color WHERE id = :id")
    void update(int id, String title, String content, String color);

    @Delete
    void delete(Note note);

    @Query("UPDATE tbl_notes SET pinned = :pinned WHERE id = :id")
    void updatePinNote(int id, boolean pinned);

    @Query("UPDATE tbl_notes SET password = :password, hide = :hide WHERE id = :id")
    void updateLockNote(int id, String password, boolean hide);
}
