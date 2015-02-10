package android.arduinos.fragments;

import android.app.Activity;
import android.arduinos.R;
import android.arduinos.entities.CheckedArduino;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class ListArduinosAdapter extends ArrayAdapter<CheckedArduino> {
  // le tableau des arduinos
  private List<CheckedArduino> arduinos;
  // le contexte d'exécution
  private Context context;
  // l'id du layout d'affichage d'une ligne de la liste des arduinos
  private int layoutResourceId;
  // la ligne comporte ou non un checkbox
  private Boolean selectable;
  // constructeur
  public ListArduinosAdapter(Context context, int layoutResourceId, List<CheckedArduino> arduinos, Boolean selectable) {
// parent
    super(context, layoutResourceId, arduinos);
// on mémorise les infos
    this.arduinos = arduinos;
    this.context = context;
    this.layoutResourceId = layoutResourceId;
    this.selectable = selectable;
  }
  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
// l'arduino courant
    final CheckedArduino arduino = arduinos.get(position);
// déjà vue ?
// on crée la ligne courante
    View row = ((Activity) context).getLayoutInflater().inflate(layoutResourceId, parent,
      false);
// on récupère les références sur les [TextView]
    TextView txtArduinoId = (TextView) row.findViewById(R.id.txt_arduino_id);
    TextView txtArduinoDesc = (TextView) row.findViewById(R.id.txt_arduino_description);
// on remplit la ligne
    txtArduinoId.setText(arduino.getId());
    txtArduinoDesc.setText(arduino.getDescription());
// la CheckBox n'est pas toujours visible
    CheckBox ck = (CheckBox) row.findViewById(R.id.checkBoxArduino);
    ck.setVisibility(selectable ? View.VISIBLE : View.INVISIBLE);
    if (selectable) {
// on lui affecte sa valeur
      ck.setChecked(arduino.isChecked());
// on gère le clic
      ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          arduino.setChecked(isChecked);
        }
      });
    }
// on rend la ligne
    return row;
      }
  }
