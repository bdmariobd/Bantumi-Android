package es.upm.miw.bantumi.integration;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDAO {
    @Query("SELECT games.*, MAX(games.PlayerScore,games.CPUScore) as BetterScore FROM games ORDER BY BetterScore DESC ")
    LiveData<List<Game>> getAllGames();

    @Insert
    void insert(Game game);

    @Delete
    void delete(Game game);

    @Query("DELETE FROM games")
    void deleteAll();

}
