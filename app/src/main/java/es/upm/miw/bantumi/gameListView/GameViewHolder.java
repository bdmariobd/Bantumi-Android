package es.upm.miw.bantumi.gameListView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.integration.Game;
import es.upm.miw.bantumi.utils.DateUtils;

public class GameViewHolder extends RecyclerView.ViewHolder {

    TextView gameScore, gameDate;
    Game game;

    Button deleteButton;

    public GameViewHolder(@NonNull View itemView) {
        super(itemView);
        gameScore = itemView.findViewById(R.id.tvScoreTitleItem);
        gameDate = itemView.findViewById(R.id.tvDateScoreItem);
        deleteButton = itemView.findViewById(R.id.btDeleteOneItem);
    }

    public void bind(Game game) {
        String scoreData = game.getNickname() + " " + game.getPlayerScore() + " - " + game.getCpuScore() + " " + this.itemView.getResources().getString(R.string.cpu);
        gameScore.setText(scoreData);
        String date = new DateUtils().dateToString(game.getDate());
        gameDate.setText(date);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }
}
