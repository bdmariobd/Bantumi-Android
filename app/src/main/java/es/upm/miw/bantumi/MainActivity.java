package es.upm.miw.bantumi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;

import es.upm.miw.bantumi.dialogs.FinalAlertDialog;
import es.upm.miw.bantumi.dialogs.RestoreGameDialog;
import es.upm.miw.bantumi.gameListView.BestResultsActivity;
import es.upm.miw.bantumi.integration.Game;
import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.GameViewModel;
import es.upm.miw.bantumi.utils.DateUtils;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    public JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    Integer numInicialSemillas;
    Boolean gameHasTimer;
    private GameViewModel gameVM;

    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = this.findViewById(R.id.tvChronometer);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = this.readNumInicialSemillas();
        gameHasTimer = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("displayTimer", false);
        String player1Name = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("nickName", "Jugador 1");
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        tvJugador1.setText(player1Name);

        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        gameVM = new ViewModelProvider(this).get(GameViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        crearObservadores();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        if(gameHasTimer){
            this.findViewById(R.id.mcChronometer).setVisibility(View.VISIBLE);
            bantumiVM.getGameStarted().observe(  // Inicio de juego
                    this,
                    started -> {
                        if (started && chronometer != null) {
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                        }
                    }
            );
        }
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        MaterialCardView mvJugador1 = findViewById(R.id.mvPlayer1);
        MaterialCardView mvJugador2 = findViewById(R.id.mvPlayer2);
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainerHighest, typedValue, true);
        int colorSurfaceContainerHighest = typedValue.data;
        switch (turnoActual) {
            case turnoJ1:
                mvJugador1.setCardBackgroundColor(colorSurfaceContainerHighest);
                mvJugador2.setCardBackgroundColor(Color.TRANSPARENT);
                break;
            case turnoJ2:
                mvJugador1.setCardBackgroundColor(Color.TRANSPARENT);
                mvJugador2.setCardBackgroundColor(colorSurfaceContainerHighest);
                break;
            default:
                mvJugador1.setCardBackgroundColor(Color.TRANSPARENT);
                mvJugador1.setCardBackgroundColor(Color.TRANSPARENT);
                break;
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos   posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcReiniciarPartida:

                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.txtReiniciarPartida)
                        .setMessage(R.string.txtReiniciarPartidaAlerta)
                        .setPositiveButton(R.string.txtReiniciarPartida, (dialog, which) -> {
                            if(gameHasTimer) chronometer.setBase(SystemClock.elapsedRealtime());
                            this.numInicialSemillas = this.readNumInicialSemillas();
                            juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
                            this.recreate();
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.txtJuegoReiniciado),
                                    Snackbar.LENGTH_LONG
                            ).show();
                        })
                        .setNegativeButton(R.string.cancelar, null)
                        .show();
                return true;
            case R.id.opcGuardarPartida:
                String gameSerialized = juegoBantumi.serializa();
                Log.i(LOG_TAG, "onOptionsItemSelected() -> " + gameSerialized);
                this.saveGameToFile(gameSerialized);
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtFicheroGuardado),
                        Snackbar.LENGTH_LONG
                ).show();
                return true;
            case R.id.opcRecuperarPartida:
                RestoreGameDialog restoreGameDialog = new RestoreGameDialog();
                restoreGameDialog.show(getSupportFragmentManager(), "RESTORE_DIALOG");
                return true;
            case R.id.opcMejoresResultados:
                startActivity(new Intent(this, BestResultsActivity.class));
                return true;
            case R.id.opcAjustes:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.opcAcercaDe:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    public String readFile(String name) throws IOException {
        BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(name)));
        StringBuilder fileContent = new StringBuilder();
        String linea = fin.readLine();
        while (linea != null) {
            fileContent.append(linea).append("\n");
            linea = fin.readLine();
        }
        fin.close();
        return fileContent.toString();
    }

    private void saveGameToFile(String gameSerialized) {
        String fileName = new DateUtils().dateToUnixTimestamp(new Date()) + ".txt";
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(gameSerialized.getBytes());
            fos.close();
        } catch (IOException e) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.txtFicheroFalloAlGuardar),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria [7..12]
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
        }
        Snackbar.make(
                        findViewById(android.R.id.content),
                        texto,
                        Snackbar.LENGTH_LONG
                )
                .show();

        // guardar puntuación
        String nickname = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("nickName", "Player");
        Game game = new Game(nickname, juegoBantumi.getSemillas(6), juegoBantumi.getSemillas(13));
        this.gameVM.insert(game);
        // terminar
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    public void restartChronometer(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    public int readNumInicialSemillas() {
        int numInicialSemillas = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("initialSeedNumber", String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas))));
        if (numInicialSemillas < 1) {
            numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        }
        if (this.numInicialSemillas==null || this.numInicialSemillas != numInicialSemillas) {
            this.numInicialSemillas = numInicialSemillas;
        }
        return this.numInicialSemillas;
    }
}