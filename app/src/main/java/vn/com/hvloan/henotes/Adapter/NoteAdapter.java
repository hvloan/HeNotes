package vn.com.hvloan.henotes.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.com.hvloan.henotes.Events.NoteClickListener;
import vn.com.hvloan.henotes.Models.Note;
import vn.com.hvloan.henotes.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    Context context;
    List<Note> listNotes;
    NoteClickListener noteClickListener;

    public NoteAdapter(Context context, List<Note> listNotes, NoteClickListener noteClickListener) {
        this.context = context;
        this.listNotes = listNotes;
        this.noteClickListener = noteClickListener;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        if (listNotes.get(position).isHide()) {
            holder.textTitle.setText("");
            holder.textContent.setText("");
            holder.textDate.setText("");
        } else {
            holder.textTitle.setText(listNotes.get(position).getTitle());
            holder.textTitle.setSelected(true);

            holder.textContent.setText(listNotes.get(position).getContent());

            holder.textDate.setText(listNotes.get(position).getDate());
            holder.textDate.setSelected(true);

            if (listNotes.get(position).isPinned()) {
                holder.viewPinned.setVisibility(View.VISIBLE);
            } else {
                holder.viewPinned.setVisibility(View.INVISIBLE);
            }
        }

        holder.cardItemNote.setCardBackgroundColor(Integer.parseInt(listNotes.get(position).getColor()));

        holder.cardItemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onClick(listNotes.get(holder.getAdapterPosition())); //try use .get(position) late
            }
        });

        holder.cardItemNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                noteClickListener.onLongClick(listNotes.get(holder.getAdapterPosition()), holder.cardItemNote);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardItemNote;
        private final TextView textTitle;
        private final TextView textContent;
        private final View viewPinned;
        private final TextView textDate;

        public ViewHolder(View view) {
            super(view);
            this.cardItemNote = view.findViewById(R.id.card_item_note);
            this.textTitle = view.findViewById(R.id.tv_title);
            this.textContent = view.findViewById(R.id.tv_content);
            this.viewPinned = view.findViewById(R.id.view_pinned);
            this.textDate = view.findViewById(R.id.tv_date);
        }

        public TextView getTextTitle() {
            return textTitle;
        }

        public TextView getTextContent() {
            return textContent;
        }

        public View getViewPinned() {
            return viewPinned;
        }

        public TextView getTextDate() {
            return textDate;
        }

        public CardView getCardItemNote() {
            return cardItemNote;
        }
    }
}


