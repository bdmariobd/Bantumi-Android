package es.upm.miw.bantumi.integration;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GameDAO {
    @Query("SELECT * FROM games ")
    public Game[] getAllGames();

    @Insert
    public void insert(Game game);

    @Delete
    public void delete(Game game);

}
