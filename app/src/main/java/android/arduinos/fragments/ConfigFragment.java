package android.arduinos.fragments;
import android.app.AlertDialog;
import android.arduinos.R;
import android.arduinos.dao.ArduinoCommand;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;
import android.arduinos.activity.MainActivity;
import android.arduinos.dao.ArduinosResponse;
import android.arduinos.entities.Arduino;
import android.arduinos.entities.CheckedArduino;
import org.androidannotations.annotations.*;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

@EFragment(R.layout.config)
public class ConfigFragment extends MyFragment {
  // les éléments de l'interface visuelle
  @ViewById(R.id.btn_Rafraichir)
  Button btnRafraichir;
  @ViewById(R.id.btn_Annuler)
  Button btnAnnuler;
  @ViewById(R.id.edt_UrlServiceRest)
  EditText edtUrlServiceRest;
  @ViewById(R.id.txt_MsgErreurIpPort)
  TextView txtMsgErreurUrlServiceRest;
  @ViewById(R.id.ListViewArduinos)
  ListView listArduinos;
  // l'activité
  private MainActivity activité;
  // les valeurs saisies
  private String urlServiceRest;
  // le nombre d'informations reçues
  private int nbInfosAttendues;

  @AfterViews
  public void initFragment() {
// on note l'activité
    activité = (MainActivity) getActivity();
    // visibilité boutons
    btnRafraichir.setVisibility(View.VISIBLE);
    btnAnnuler.setVisibility(View.INVISIBLE);
// le msg d'erreur éventuel
    txtMsgErreurUrlServiceRest.setText("");
  }
  @Click(R.id.btn_Rafraichir)
  protected void doRafraichir() {
// on efface la liste actuelle des Arduinos
    clearArduinos();
// on vérifie les saisies
    if (!pageValid()) {
      return;
    }
    // on mémorise la saisie
    activité.setUrlServiceRest(urlServiceRest);
// on demande la liste des Arduinos
    nbInfosAttendues = 1;
    getArduinosInBackground();
// on commence l'attente
    beginWaiting();
  }
  @Background(id = "getArduinos", delay = MainActivity.PAUSE)
  void getArduinosInBackground() {
    ArduinosResponse response = null;
    try {
      response = activité.getArduinos();
    } catch (Exception e) {
      response = new ArduinosResponse();
      response.setErreur(2);
      response.setMessages(getMessagesFromException(e));
    }
    showResponse(response);
  }
  // affichage réponse
  @UiThread
  void showResponse(ArduinosResponse response) {
// on teste la nature de l'information reçue
    int erreur = response.getErreur();
    if (erreur == 0) {
// on récupère la liste d'Arduinos
      List<Arduino> arduinos = response.getArduinos();
// on en fait une liste de [CheckedArduino]
      List<CheckedArduino> checkedArduinos = new ArrayList<CheckedArduino>();
      for (Arduino arduino : arduinos) {
        checkedArduinos.add(new CheckedArduino(arduino, false));
      }
// on affiche les Arduinos
      showArduinos(checkedArduinos);
// on affiche tous les onglets
      activité.showTabs(new Boolean[]{true, true, true, true, true});
      // on mémorise les Arduinos dans l'activité
      activité.setCheckedArduinos(checkedArduinos);
    }
// erreur ?
    if (erreur != 0) {
// on affiche les msg d'erreur
      showMessages(activité, response.getMessages());
// on n'affiche que l'onglet [Config]
      activité.showTabs(new Boolean[]{true, false, false, false, false});
    }
// attente finie ?
    nbInfosAttendues--;
    if (nbInfosAttendues == 0) {
      cancelWaiting();
    }
  }
  // raz liste des Arduinos
  private void clearArduinos() {
// on affiche une liste vide
    ListArduinosAdapter adapter = new ListArduinosAdapter(getActivity(), R.layout.listarduinos_item, new ArrayList<CheckedArduino>(), false);
    listArduinos.setAdapter(adapter);
  }

  @Click(R.id.btn_Annuler)
  void doAnnuler() {
// on annule l'attente
    cancelWaiting();
  }
  // vérification des saisies
  private boolean pageValid() {
// on récupère l'Ip et le port du serveur
    urlServiceRest = String.format("http://%s", edtUrlServiceRest.getText().toString().trim());
// on vérifie sa validité
    try {
      URI uri = new URI(urlServiceRest);
      String host = uri.getHost();
      int port = uri.getPort();
      if (host == null || port == -1) {
        throw new Exception();
      }
    } catch (Exception ex) {
// affichage msg d'erreur
      txtMsgErreurUrlServiceRest.setText(getResources().getString(R.string.txt_MsgErreurUrlServiceRest));
// retour à l'UI
      return false;
    }
// c'est bon
    txtMsgErreurUrlServiceRest.setText("");
    return true;
  }
  protected void cancelWaiting() {
// le bouton [Exécuter] remplace le bouton [Annuler]
    btnAnnuler.setVisibility(View.INVISIBLE);
    btnRafraichir.setVisibility(View.VISIBLE);
// on enlève le sablier
    activité.setProgressBarIndeterminateVisibility(false);
// plus de tâches à attendre
    nbInfosAttendues = 0;
  }
  private void beginWaiting() {
// le bouton [Annuler] remplace le bouton [Exécuter]
    btnRafraichir.setVisibility(View.INVISIBLE);
    btnAnnuler.setVisibility(View.VISIBLE);
// on met le sablier
    activité.setProgressBarIndeterminateVisibility(true);
  }
  private void showArduinos(List<CheckedArduino> checkedArduinos) {
// on affiche les Arduinos
    ListArduinosAdapter adapter = new ListArduinosAdapter(getActivity(), R.layout.listarduinos_item, checkedArduinos, false);
    listArduinos.setAdapter(adapter);
  }

  public void onRefresh() {
    if (activité != null) {
// on rafraîchit les Arduinos
      List<CheckedArduino> arduinos = activité.getCheckedArduinos();
      if (arduinos != null) {
        listArduinos.setAdapter(new ListArduinosAdapter(activité,
          R.layout.listarduinos_item, arduinos, false));
      }
    }
  }
}