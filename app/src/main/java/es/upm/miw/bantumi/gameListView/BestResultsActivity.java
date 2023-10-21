package es.upm.miw.bantumi.gameListView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.model.GameViewModel;

public class BestResultsActivity extends AppCompatActivity {

    GameViewModel gameViewModel;
    GameListAdapter gameListAdapter;

    RecyclerView lvGameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highest_scores_activity);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameListAdapter = new GameListAdapter();
        lvGameList = findViewById(R.id.rvScores);
        lvGameList.setAdapter(gameListAdapter);
        lvGameList.setLayoutManager(new LinearLayoutManager(this));

        gameViewModel.getAllGames().observe(this, games -> {
            gameListAdapter.setmGameList(games);
            gameListAdapter.notifyDataSetChanged();
        });
    }

    public void deleteAllGames(View view) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.txtDialogoBorrarPartidasTitulo)
                .setMessage(R.string.txtDialogoBorrarPartidasPregunta)
                .setPositiveButton(
                        getString(R.string.borrar),
                        (dialog, which) -> {
                            gameViewModel.deleteAll();
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    R.string.txtPartidasFueronBorradas,
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }
                )
                .setNegativeButton(
                        getString(R.string.cancelar),
                        (dialog, which) -> dialog.cancel()
                );
        builder.create().show();
        
    }
}
