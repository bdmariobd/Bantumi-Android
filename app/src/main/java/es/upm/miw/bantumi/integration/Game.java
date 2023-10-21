package es.upm.miw.bantumi.integration;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "games")
public class Game {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int uuid;

    @NonNull
    @ColumnInfo(name = "Nickname")
    private String nickname;

    @NonNull
    @ColumnInfo(name = "Date")
    private Date date;

    @NonNull
    @ColumnInfo(name = "PlayerScore")
    private int playerScore;

    @NonNull
    @ColumnInfo(name = "CPUScore")
    private int cpuScore;

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@NonNull String nickname) {
        this.nickname = nickname;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getCpuScore() {
        return cpuScore;
    }

    public void setCpuScore(int cpuScore) {
        this.cpuScore = cpuScore;
    }

    public Game(@NonNull String nickname, int playerScore, int cpuScore) {
        this.nickname = nickname;
        this.playerScore = playerScore;
        this.cpuScore = cpuScore;
        this.date = new Date();
    }
}
