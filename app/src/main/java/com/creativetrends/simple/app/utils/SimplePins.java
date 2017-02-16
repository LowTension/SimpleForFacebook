package com.creativetrends.simple.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.activities.MainActivity;

import java.util.ArrayList;

/** Created by Creative Trends Apps on 10/19/2016.*/

public class SimplePins extends Adapter<SimplePins.ViewHolderBookmark> {
    @SuppressLint("StaticFieldLeak")
    private static SimplePins adapter;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Pin> listBookmarks = new ArrayList<>();
    private onBookmarkSelected onBookmarkSelected;

    class ViewHolderBookmark extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Pin bookmark;
        private RelativeLayout bookmarkHolder;
        private ImageView delete;
        private TextView title;

        ViewHolderBookmark(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.pin_title);
            delete = (ImageView) itemView.findViewById(R.id.remove_pin);
            bookmarkHolder = (RelativeLayout) itemView.findViewById(R.id.bookmark_holder);
        }

        void bind(Pin bookmark) {
            this.bookmark = bookmark;
            title.setText(bookmark.getTitle());
            bookmarkHolder.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bookmark_holder:
                    onBookmarkSelected.loadBookmark(bookmark.getTitle(), bookmark.getUrl());
                    MainActivity.drawerLayout.closeDrawers();
                    break;
                case R.id.remove_pin:
                    AlertDialog.Builder removeFavorite = new AlertDialog.Builder(context);
                    removeFavorite.setTitle("Remove Pin?");
                    removeFavorite.setMessage("Are you sure you would like to remove " + bookmark.getTitle() + " from your pins? This action cannot be undone.");
                    removeFavorite.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listBookmarks.remove(bookmark);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, context.getResources().getString(R.string.removed )+ " " + bookmark.getTitle() + " " + context.getResources().getString(R.string.from_favs), Toast.LENGTH_LONG).show();

                        }
                    });
                    removeFavorite.setNegativeButton(R.string.cancel, null);
                    removeFavorite.show();
                    break;
                default:
                    break;
            }
        }

        public boolean onLongClick(View v) {
            AlertDialog.Builder removeFavorite = new AlertDialog.Builder(context);
            removeFavorite.setTitle("Remove Favorite");
            removeFavorite.setMessage("Are you sure you would like to remove " + bookmark.getTitle() + " from your favorites? This action cannot be undone.");
            removeFavorite.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    listBookmarks.remove(bookmark);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, context.getResources().getString(R.string.removed )+ " " + bookmark.getTitle() + " " + context.getResources().getString(R.string.from_favs), Toast.LENGTH_LONG).show();

                }
            });
            removeFavorite.setNegativeButton(R.string.cancel, null);
            removeFavorite.show();
            return true;
        }
    }

    public interface onBookmarkSelected {
        void loadBookmark(String str, String str2);
    }

    public SimplePins(Context context, ArrayList<Pin> listBookmarks, onBookmarkSelected onBookmarkSelected) {
        this.context = context;
        this.listBookmarks = listBookmarks;
        this.onBookmarkSelected = onBookmarkSelected;
        layoutInflater = LayoutInflater.from(context);
        adapter = this;
    }

    public void addItem(Pin bookmark) {
        listBookmarks.add(bookmark);
        notifyDataSetChanged();
    }

    public ArrayList<Pin> getListBookmarks() {
        return listBookmarks;

    }

    public ViewHolderBookmark onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBookmark(layoutInflater.inflate(R.layout.fragment_pins, parent, false));
    }

    public void onBindViewHolder(ViewHolderBookmark holder, int position) {
        holder.bind(listBookmarks.get(position));
    }

    public int getItemCount() {
        return this.listBookmarks.size();

    }
}