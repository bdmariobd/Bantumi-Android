package es.upm.miw.bantumi.integration;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Game.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GameRoomDatabase extends RoomDatabase {
    private static GameRoomDatabase INSTANCE;

    public static GameRoomDatabase getDatabase() {
        return INSTANCE;
    }

    public abstract GameDAO gameDao();


}
