package es.upm.miw.bantumi.integration;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import es.upm.miw.bantumi.BantumiApp;

@Database(entities = {Game.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GameRoomDatabase extends RoomDatabase {
    private static GameRoomDatabase INSTANCE;

    public static GameRoomDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context,
                    GameRoomDatabase.class,
                    "games")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract GameDAO gameDao();
}
