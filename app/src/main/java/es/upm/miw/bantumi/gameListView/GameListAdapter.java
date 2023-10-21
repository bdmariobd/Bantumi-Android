package es.upm.miw.bantumi.gameListView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.integration.Game;

public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {

    List<Game> mGameList;

    public GameListAdapter() {
        this.mGameList = new ArrayList<Game>();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gamescore_item, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.bind(mGameList.get(position));
        holder.getDeleteButton().setOnClickListener(v -> {
            Game game = holder.getGame();
            mGameList.remove(game);
            notifyDataSetChanged();
            Snackbar.make(
                    v,
                    R.string.txtPartidaFuerBorrada,
                    Snackbar.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public int getItemCount() {
        return mGameList.size();
    }

    public void setmGameList(List<Game> games) {
        this.mGameList = games;
        notifyDataSetChanged();
    }

}
