package android.arduinos.fragments;
import android.arduinos.R;
import android.arduinos.activity.MainActivity;
import android.arduinos.dao.GenericResponse;
import android.arduinos.entities.CheckedArduino;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;
import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.pinwrite)
public class PinWriteFragment extends MyFragment {
  // interface visuelle
  @ViewById(R.id.ListViewArduinos)
  ListView listArduinos;

  @ViewById(R.id.list_pin)
  Spinner list_pin;

  @ViewById(R.id.erreur_pin)
  TextView erreur_pin;

  @ViewById(R.id.RadioGroup)
  RadioGroup RadioGroup;

  @ViewById(R.id.radio_analogique)
  RadioButton radio_analogique;

  @ViewById(R.id.radio_binaire)
  RadioButton radio_binaire;

  @ViewById(R.id.switch_binaire_val)
  Switch switch_binaire_val;

  @ViewById(R.id.frame_val_to_write)
  LinearLayout  frame_val_to_write;

  @ViewById(R.id.analogique_val)
  TextView analogique_val;

  @ViewById(R.id.seekBar_analogique_val)
  SeekBar seekBar_analogique_val;

  @ViewById(R.id.btn_Executer)
  Button btn_Executer;

  @ViewById(R.id.btn_annuler)//pas inplémenté
    Button btn_annuler;

  @ViewById(R.id.ListCommandsResponse)
  ListView ListCommandsResponse;

  // l'activité
  private MainActivity activité;

  // les valeurs saisies
  private int val = 0;
  private char mode = 'b';
  private int idpin=8;

  @AfterViews
  public void initFragment() {
// on note l'activité
    activité = (MainActivity) getActivity();

// visibilité boutons
    btn_Executer.setVisibility(View.VISIBLE);
    btn_annuler.setVisibility(View.INVISIBLE);

    //init pin list
    ArrayList<String> pin_num = new ArrayList<String>();
    for(int i=0; i<15;i++) pin_num.add(Integer.toString(i));
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activité,android.R.layout.simple_spinner_item,pin_num);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    list_pin.setAdapter(dataAdapter);
  }
  public void onRefresh() {
    if (activité != null) {
// on rafraîchit les Arduinos
      List<CheckedArduino> arduinos = activité.getCheckedArduinos();
      if (arduinos != null) {
        listArduinos.setAdapter(new ListArduinosAdapter(activité, R.layout.listarduinos_item, arduinos, true));
      }
    }
  }
  @Click(R.id.btn_Executer)
  protected void doRafraichir() {
    // on vérifie les saisies
    if (!pageValid()) {
      return;
    }
// on demande la liste des Arduinos
    //nbInfosAttendues = 1;
    writePinInBackground();
// on commence l'attente
    beginWaiting();
  }
  @Background(id = "writePin", delay = MainActivity.PAUSE)
  void writePinInBackground() {
    GenericResponse response = null;
    List<CheckedArduino> arduinos = activité.getCheckedArduinos();
    ArrayList<GenericResponse> responsesList = new ArrayList<GenericResponse>();
    try {
      for(CheckedArduino a : arduinos) {
        if ( a.isChecked() )
        {
          response = activité.writePin(a.getId(), idpin, mode,val);
          responsesList.add(response);
        }
      }
    } catch (Exception e) {
      response = new GenericResponse();
      response.setErreur(2);
      response.setMessages(getMessagesFromException(e));
      responsesList.add(response);
    }
    showResponse(responsesList);
  }
  @UiThread
  void showResponse(List<GenericResponse> response) {
    ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
    // ListCommandsResponse.setAdapter( listAdapter );
    for(GenericResponse r : response)
    {
      // on teste la nature de l'information reçue
      int erreur = r.getErreur();
      if (erreur == 0) {
// on récupère le message
        listAdapter.add(r.toString());
      }
// erreur ?
      if (erreur != 0) {
// on affiche les msg d'erreur
        //showMessages(activité, r.getMessages()); //pop up
        List<String> errors =  r.getMessages();
        for (String e : errors)
        {
          listAdapter.add(e);
        }
      }
    }
    ListCommandsResponse.setAdapter(listAdapter);
    cancelWaiting();
  }
  @Click(R.id.btn_annuler)
  void doAnnuler() {
// on annule l'attente
    cancelWaiting();
  }
  protected void cancelWaiting() {
// le bouton [Exécuter] remplace le bouton [Annuler]
    btn_annuler.setVisibility(View.INVISIBLE);
    btn_Executer.setVisibility(View.VISIBLE);
// on enlève le sablier
    activité.setProgressBarIndeterminateVisibility(false);
  }
  private void beginWaiting() {
// le bouton [Annuler] remplace le bouton [Exécuter]
    btn_Executer.setVisibility(View.INVISIBLE);
    btn_annuler.setVisibility(View.VISIBLE);
// on met le sablier
    activité.setProgressBarIndeterminateVisibility(true);
  }

  @SeekBarProgressChange(R.id.seekBar_analogique_val)
  protected  void doRafraichirAnalogiqueVal()
  {
    Integer progress = seekBar_analogique_val.getProgress();
    analogique_val.setText(progress.toString());
  }
  @Click(R.id.radio_binaire)
  protected void doRafraichirModeB() {
    switch_binaire_val.setVisibility(View.VISIBLE);
    frame_val_to_write.setVisibility(View.INVISIBLE);

  }
  @Click(R.id.radio_analogique)
  protected void doRafraichirModeA() {
    switch_binaire_val.setVisibility(View.INVISIBLE);
    frame_val_to_write.setVisibility(View.VISIBLE);
  }

  private boolean pageValid() {
    List<String> errors = new ArrayList<String>();
    int error = 0;

    if(radio_binaire.isChecked() && !radio_analogique.isChecked()) mode = 'b';
    else if(!radio_binaire.isChecked() && radio_analogique.isChecked()) mode = 'a';
    else errors.add("selectionnez un mode de lecture");

    try {
      if(mode=='a')
      {
        val=Integer.valueOf(analogique_val.getText().toString());
      }
      else //mode==b ou erreure déjà prise en compte
      {
        if(switch_binaire_val.isChecked()) val=1;
        else val=0;
      }

      idpin = Integer.valueOf(list_pin.getSelectedItem().toString());
    }
    catch (Exception ex)
    {
      errors.add(ex.getMessage());
    }

    try {
      idpin = Integer.valueOf(list_pin.getSelectedItem().toString());

      if(idpin<1||idpin>13)
      {
        erreur_pin.setText("Le numero de pin doit être compris entre [1;13]");
        error++;
      }
      else erreur_pin.setText("");
    }
    catch (Exception ex)
    {
      erreur_pin.setText(ex.getMessage());
      error++;
    }

    List<CheckedArduino> arduinos = activité.getCheckedArduinos();
    List<CheckedArduino> arduinosCkecked = new ArrayList<CheckedArduino>();
    for(CheckedArduino a : arduinos) {
      if ( a.isChecked() )
      {
        arduinosCkecked.add(a);
      }
    }
    if(arduinosCkecked.isEmpty()) errors.add("aucun arduino selectioné");

    if(errors.isEmpty()&&error==0) return true;
    else if(!errors.isEmpty())
    {
      showMessages(activité, errors);
      return false;
    }
    else
    {
      return false;
    }
  }
}