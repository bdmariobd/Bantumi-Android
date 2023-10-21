package es.upm.miw.bantumi.integration;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GameRepository {
    private final GameDAO gameDAO;
    private final LiveData<List<Game>> allGames;

    public GameRepository(Context context) {
        GameRoomDatabase db = GameRoomDatabase.getDatabase(context);
        this.gameDAO = db.gameDao();
        this.allGames = gameDAO.getAllGames();
    }

    public LiveData<List<Game>> getAllGames() {
        return allGames;
    }

    public void insert(Game game) {
        gameDAO.insert(game);
    }

    public void delete(Game game) {
        gameDAO.delete(game);
    }

    public void deleteAll() {
        gameDAO.deleteAll();
    }
}
