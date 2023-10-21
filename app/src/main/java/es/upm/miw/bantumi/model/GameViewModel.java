package es.upm.miw.bantumi.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.upm.miw.bantumi.integration.Game;
import es.upm.miw.bantumi.integration.GameRepository;

public class GameViewModel extends AndroidViewModel {
    private final GameRepository gameRepository;

    private final LiveData<List<Game>> allGames;


    public GameViewModel(Application application) {
        super(application);
        this.gameRepository = new GameRepository(application);
        this.allGames = gameRepository.getAllGames();
    }

    public void insert(Game game) {
        gameRepository.insert(game);
    }

    public void delete(Game game) {
        gameRepository.delete(game);
    }

    public void deleteAll() {
        gameRepository.deleteAll();
    }


    public LiveData<List<Game>> getAllGames() {
        return this.allGames;
    }
}
