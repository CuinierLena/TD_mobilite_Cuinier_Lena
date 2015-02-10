package android.arduinos.fragments;
import android.app.AlertDialog;
import android.arduinos.activity.MainActivity;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public abstract class MyFragment extends Fragment implements OnRefreshListener {
  protected void showMessages(MainActivity activité, List<String> messages) {
// on construit le texte à afficher
    StringBuilder texte = new StringBuilder();
    for (String message : messages) {
      texte.append(String.format("%s\n", message));
    }
// on l'affiche
    new AlertDialog.Builder(activité).setTitle("De erreurs se sont produites").setMessage(texte).setNeutralButton("Fermer", null).show();
  }
  protected List<String> getMessagesFromException(Exception e) {
// liste des messages d'une exception
    Throwable th = e;
    List<String> messages = new ArrayList<String>();
    while (th != null) {
      messages.add(th.getMessage());
      th = th.getCause();
    }
    return messages;
  }
}