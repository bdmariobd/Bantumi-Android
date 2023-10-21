package es.upm.miw.bantumi.dialogs;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.upm.miw.bantumi.MainActivity;
import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.utils.DateUtils;

public class RestoreGameDialog extends DialogFragment {

    String[] files;

    int selected = -1;


    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        String[] fileList = main.fileList();
        List<String> filteredFiles = new ArrayList<>();
        DateUtils dateUtils = new DateUtils();
        for (String file : fileList) {
            if (file.endsWith(".txt")) {
                filteredFiles.add(file);
            }
        }
        files = filteredFiles.toArray(new String[0]);

        List<String> filesParsed = new ArrayList<>();
        for (String file : files) {
            try {
                filesParsed.add(dateUtils.unixTimestampToString(file.replace(".txt", "")));
            } catch (Exception e) {
                Log.e("ERROR", "Error al parsear la fecha");
            }
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(main);
        builder
                .setTitle(R.string.txtDialogoRestaurarTitulo)
                .setSingleChoiceItems(
                        filesParsed.toArray(new String[0]),
                        -1,
                        (dialog, which) -> {
                            selected = which;
                        }
                )
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> {
                            if (selected == -1) {
                                Snackbar.make(
                                        main.findViewById(android.R.id.content),
                                        getString(R.string.txtFicheroNoSeleccionado),
                                        Snackbar.LENGTH_LONG
                                ).show();
                                return;
                            }
                            try {
                                String data = main.readFile(files[selected]);
                                main.juegoBantumi.deserializa(data);
                                Snackbar.make(
                                        main.findViewById(android.R.id.content),
                                        getString(R.string.txtFicheroRecuperado),
                                        Snackbar.LENGTH_LONG
                                ).show();
                            } catch (IOException e) {
                                Snackbar.make(
                                        main.findViewById(android.R.id.content),
                                        getString(R.string.txtFicheroFalloAlRecuperar),
                                        Snackbar.LENGTH_LONG
                                ).show();
                            }
                        }
                )
                .setNegativeButton(
                        getString(android.R.string.cancel),
                        (dialog, which) -> {
                            dialog.cancel();
                        }
                );

        return builder.create();
    }

}
