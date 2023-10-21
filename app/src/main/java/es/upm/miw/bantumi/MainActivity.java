package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import es.upm.miw.bantumi.gameListView.BestResultsActivity;
import es.upm.miw.bantumi.dialogs.FinalAlertDialog;
import es.upm.miw.bantumi.integration.Game;
import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String MI_FICHERO = "game.txt";
    protected final String LOG_TAG = "MiW";
    public JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    int numInicialSemillas;
    private GameViewModel gameVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas =
                getResources().getInteger(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("numInicialSemillas", R.integer.intNumInicialSemillas));
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        gameVM = new ViewModelProvider(this).get(GameViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        crearObservadores();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
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
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(R.color.md_theme_light_secondary));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(R.color.md_theme_light_secondary));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
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
                juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtJuegoReiniciado),
                        Snackbar.LENGTH_LONG
                ).show();
                return true;
            case R.id.opcGuardarPartida:
                String gameSerialized = juegoBantumi.serializa();
                Log.i(LOG_TAG, "onOptionsItemSelected() -> " + gameSerialized);
                this.saveGameToFile(gameSerialized);
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtFicheroGuardado),
                        Snackbar.LENGTH_LONG
                ).setAction(
                        getString(R.string.txtDeshacer),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveGameToFile("");
                            }
                        }
                ).show();
                return true;
            case R.id.opcRecuperarPartida:
                try {
                    String game = this.readFile();
                    this.juegoBantumi.deserializa(game);
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.txtFicheroRecuperado),
                            Snackbar.LENGTH_LONG
                    ).show();
                } catch (IOException e) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.txtFicheroFalloAlRecuperar),
                            Snackbar.LENGTH_LONG
                    ).show();
                }
                return true;
            case R.id.opcMejoresResultados:
                startActivity(new Intent(this, BestResultsActivity.class));
                return true;
            case R.id.opcAjustes:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            // @TODO!!! resto opciones

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    private String readFile() throws IOException {
        BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("game.txt")));
        StringBuilder fileContent = new StringBuilder();
        String linea = fin.readLine();
        while (linea != null) {
            fileContent.append(linea).append("\n");
            linea = fin.readLine();
        }
        fin.close();
        Log.d("Ficheros", "File read! Content: " + fileContent);
        return fileContent.toString();
    }

    private void saveGameToFile(String gameSerialized) {
        try {
            FileOutputStream fos = openFileOutput(MI_FICHERO, Context.MODE_PRIVATE);
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
}