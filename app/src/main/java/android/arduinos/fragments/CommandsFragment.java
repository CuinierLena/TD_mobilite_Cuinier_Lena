package android.arduinos.fragments;
import android.arduinos.R;
import android.arduinos.activity.MainActivity;
import android.arduinos.dao.ArduinoCommand;
import android.arduinos.dao.ArduinoResponse;
import android.arduinos.dao.CommandsResponse;
import android.arduinos.entities.CheckedArduino;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;
import org.androidannotations.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

@EFragment(R.layout.commands)
public class CommandsFragment extends MyFragment {

  // interface visuelle
  @ViewById(R.id.ListViewArduinos)
  ListView listArduinos;

  @ViewById(R.id.editText_json_comande)
  EditText editText_json_comande;

  @ViewById(R.id.erreur_json_comande)
  TextView erreur_json_comande;

  @ViewById(R.id.btn_Executer)
  Button btn_Executer;

  @ViewById(R.id.btn_annuler)
  Button btn_annuler;

  @ViewById(R.id.ListCommandsResponse)
  ListView ListCommandsResponse;

  // les valeurs saisies
  List<ArduinoCommand> Commandes;

  // l'activité
  private MainActivity activité;
  @AfterViews
  public void initFragment() {
// on note l'activité
    activité = (MainActivity) getActivity();
    // visibilité boutons
    btn_Executer.setVisibility(View.VISIBLE);
    btn_annuler.setVisibility(View.INVISIBLE);

  }
  public void onRefresh() {
    if (activité != null) {
// on rafraîchit les Arduinos
      List<CheckedArduino> arduinos = activité.getCheckedArduinos();
      if (arduinos != null) {
        listArduinos.setAdapter(new ListArduinosAdapter(activité,
          R.layout.listarduinos_item, arduinos, true));
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
    sendCommandsInBackground();
// on commence l'attente
    beginWaiting();
  }
  @Background(id = "sendCommands", delay = MainActivity.PAUSE)
  void sendCommandsInBackground() {
    CommandsResponse response = null;
    List<CheckedArduino> arduinos = activité.getCheckedArduinos();
    ArrayList<CommandsResponse> responsesList = new ArrayList<CommandsResponse>();
    try {
      for(CheckedArduino a : arduinos) {
        if ( a.isChecked() )
        {
          response = activité.sendCommands(Commandes, a.getId());
          responsesList.add(response);
        }
      }
    } catch (Exception e) {
      response = new CommandsResponse();
      response.setErreur(2);
      response.setMessages(getMessagesFromException(e));
      responsesList.add(response);
    }
    showResponse(responsesList);
  }
  @UiThread
  void showResponse(List<CommandsResponse> response) {
    ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
    // ListCommandsResponse.setAdapter( listAdapter );
    for(CommandsResponse r_arduino : response)
    {
      // on teste la nature de l'information reçue
      int erreur_arduino = r_arduino.getErreur();
      if (erreur_arduino == 0) {
        for (ArduinoResponse r : r_arduino.getResponses()) {
          // on teste la nature de l'information reçue
          Integer erreur = Integer.valueOf(r.getErreur());
          if (erreur == 0) {
            // on récupère le message
            listAdapter.add(r.toString());
            ListCommandsResponse.setAdapter(listAdapter);
          }
          // erreur ?
          if (erreur != 0) {
            List<String> erreurs = new ArrayList<String>();
            erreurs.add(r.getErreur());
            // on affiche les msg d'erreur
            showMessages(activité, erreurs); //pop up
          }
        }
      }
      if (erreur_arduino != 0) {
        // on affiche les msg d'erreur
        showMessages(activité, r_arduino.getMessages()); //pop up
      }
    }
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

    Commandes = new ArrayList<ArduinoCommand>();
    try {

      /*JSONArray comandes_JSON = new JSONArray("["+editText_json_comande.getText().toString()+"]");
      final int n = comandes_JSON.length();
      for (int i = 0; i < n; ++i) {
        JSONObject comande_JSON = comandes_JSON.getJSONObject(i);*/
      //récupérer la commande entrée par l'utilisateur
      JSONObject comande_JSON= new JSONObject(editText_json_comande.getText().toString());
      //récupéréer les valeur de id, ac, pa
      String id = comande_JSON.getString("id");
      String ac = comande_JSON.getString("ac");
      String pa_String = comande_JSON.getString("pa");
      //parcourir le contenu de pa
      Map pa = new HashMap();
      JSONObject params = new JSONObject(pa_String);
      for (Iterator iterator = params.keys(); iterator.hasNext(); ) {
        Object cle = iterator.next(); //récupère la clé
        Object val = params.get(String.valueOf(cle)); //récupère la valeure
        // rempli la map
        pa.put(cle.toString(), val.toString());//Integer.valueOf(
      }
      Commandes.add(new ArduinoCommand(id, ac, pa));
      //}
    } catch (JSONException e) {
      erreur_json_comande.setText("commande JSON non reconu : "+e.getMessage());
      error++;
    }

    //
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