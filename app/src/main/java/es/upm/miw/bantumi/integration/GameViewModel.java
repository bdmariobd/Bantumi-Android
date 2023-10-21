package es.upm.miw.bantumi.integration;

import androidx.lifecycle.ViewModel;

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


}
