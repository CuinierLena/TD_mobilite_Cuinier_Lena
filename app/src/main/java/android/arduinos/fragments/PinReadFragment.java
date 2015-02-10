package android.arduinos.fragments;
import android.arduinos.R;
import android.arduinos.dao.GenericResponse;
import android.arduinos.entities.CheckedArduino;
import android.support.v4.app.Fragment;
import android.arduinos.activity.MainActivity;
import android.view.View;
import android.widget.*;
import org.androidannotations.annotations.*;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.pinread)
public class PinReadFragment extends MyFragment {
  // interface visuelle
  @ViewById(R.id.ListViewArduinos)
  ListView listArduinos;

  @ViewById(R.id.list_pin)
  Spinner list_pin;

  @ViewById(R.id.erreur_pin)
  TextView erreur_pin;

  @ViewById(R.id.radio_analogique)
  RadioButton radio_analogique;

  @ViewById(R.id.radio_binaire)
  RadioButton radio_binaire;

  @ViewById(R.id.btn_Executer)
  Button btn_Executer;

  @ViewById(R.id.btn_annuler)
  Button btn_annuler;

  @ViewById(R.id.ListCommandsResponse)
  ListView ListCommandsResponse;

  // l'activité
  private MainActivity activité;

  // les valeurs saisies
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
    readPinInBackground();
// on commence l'attente
    beginWaiting();
  }
  @Background(id = "readPin", delay = MainActivity.PAUSE)
  void readPinInBackground() {
    GenericResponse response = null;
    List<CheckedArduino> arduinos = activité.getCheckedArduinos();
    ArrayList<GenericResponse> responsesList = new ArrayList<GenericResponse>();
    try {
      for(CheckedArduino a : arduinos) {
        if ( a.isChecked() )
        {
          response = activité.readPin(a.getId(), idpin, mode);
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
  private boolean pageValid() {
    List<String> errors = new ArrayList<String>();
    int error = 0;

    try {
      idpin = Integer.valueOf(list_pin.getSelectedItem().toString());
    }
    catch (Exception ex)
    {
      erreur_pin.setText("Le numero de pin doit être un nombre");
    }

    if(radio_binaire.isChecked() && !radio_analogique.isChecked())
    {
      mode = 'b';
      if(idpin<1||idpin>14)
      {
        erreur_pin.setText("En mode analogique, le numero de pin doit être compris entre [1;13]");
        error++;
      }
      else erreur_pin.setText("");
    }
    else if(!radio_binaire.isChecked() && radio_analogique.isChecked())
    {
      mode = 'a';
      if(idpin<0||idpin>5)
      {
        erreur_pin.setText("En mode analogique, le numero de pin doit être compris entre [0;5]");
        error++;
      }
      else erreur_pin.setText("");
    }
    else errors.add("selectionnez un mode de lecture"); //cas impossible

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