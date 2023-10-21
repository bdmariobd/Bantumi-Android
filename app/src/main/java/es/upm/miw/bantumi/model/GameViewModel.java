package es.upm.miw.bantumi.model;

import androidx.lifecycle.ViewModel;

import es.upm.miw.bantumi.integration.Game;
import es.upm.miw.bantumi.integration.GameRepository;

public class GameViewModel extends ViewModel {
    private final GameRepository gameRepository;

    public GameViewModel() {
        this.gameRepository = new GameRepository();
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
}
