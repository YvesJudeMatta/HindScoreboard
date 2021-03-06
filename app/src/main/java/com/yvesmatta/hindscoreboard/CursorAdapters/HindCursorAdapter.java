package com.yvesmatta.hindscoreboard.cursoradapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yvesmatta.hindscoreboard.DBOpenHelper;
import com.yvesmatta.hindscoreboard.R;
import com.yvesmatta.hindscoreboard.models.Player;
import com.yvesmatta.hindscoreboard.providers.GameWinnerProvider;
import com.yvesmatta.hindscoreboard.providers.PlayerProvider;
import com.yvesmatta.hindscoreboard.utils.MyUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HindCursorAdapter extends CursorAdapter {

    // Constructor
    public HindCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // return the inflated view
        return LayoutInflater.from(context).inflate(R.layout.hind_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // ArrayList of winning players
        ArrayList<Integer> winnerIds = new ArrayList<>();

        // Format for the date
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

        // Initialize a date
        Date date = new Date(cursor.getLong(cursor.getColumnIndex(DBOpenHelper.CREATED_AT)));

        // Grab all the players that won
        int gameId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.GAME_ID));
        String gameWinnerFilter = DBOpenHelper.GAME_FOREIGN_ID + "=" + gameId;
        Cursor gameWinnerCursor = context.getContentResolver().query(GameWinnerProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, gameWinnerFilter, null, null);

        // If data is found
        if (gameWinnerCursor != null) {
            // Grab all the rows
            while (gameWinnerCursor.moveToNext()) {
                // Add the player ids to the ArrayList
                winnerIds.add(gameWinnerCursor.getInt(gameWinnerCursor.getColumnIndex(DBOpenHelper.PLAYER_FOREIGN_ID)));
            }

            // Close the cursor
            gameWinnerCursor.close();
        }


        // ArrayList to store winning players
        ArrayList<Player> winningPlayers = new ArrayList<>();

        // For every Id in the winning id ArrayList
        for (int p = 0; p < winnerIds.size(); p++) {
            // Grab all the player that won
            String winningPlayerFilter = DBOpenHelper.PLAYER_ID + "=" + winnerIds.get(p);
            Cursor winningPlayerCursor = context.getContentResolver().query(PlayerProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, winningPlayerFilter, null, null);

            // If data is found
            if (winningPlayerCursor != null) {
                // Add the player to the winning players ArrayList
                winningPlayerCursor.moveToNext();
                String name = winningPlayerCursor.getString(winningPlayerCursor.getColumnIndex(DBOpenHelper.PLAYER_NAME));
                Player player = new Player(name);
                player.setId(winningPlayerCursor.getInt(winningPlayerCursor.getColumnIndex(DBOpenHelper.PLAYER_ID)));
                winningPlayers.add(player);

                // Close the cursor
                winningPlayerCursor.close();
            }
        }

        // Try to parse the formatted date
        try {
            date = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Find the TextView
        TextView txtWinner = (TextView) view.findViewById(R.id.txtWinner);

        // Build the winning players message with a method from the MyUtilities class
        int round = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.GAME_ROUNDS_PLAYED));
        boolean isCompleted = false;
        int completed = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.GAME_COMPLETED));
        if (completed == 1) {
            isCompleted = true;
        }
        String winningPlayersMessage = MyUtilities.buildWinningPlayersMessage(winningPlayers, round, isCompleted);

        // Set the winning players message
        txtWinner.setText(winningPlayersMessage);

        // Find the TextView
        TextView txtDateWon = (TextView) view.findViewById(R.id.txtDateWon);

        // Set the formatted date
        String formattedDate = "Date: " + dateFormat.format(date);
        txtDateWon.setText(formattedDate);
    }
}
