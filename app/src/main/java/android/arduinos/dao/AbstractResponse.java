package android.arduinos.dao;

import java.util.List;

/**
 * Created by usrlocal on 09/01/2015.
 */
public class AbstractResponse {
  // erreur
  private int erreur;
  // message d'erreur
  private List<String> messages;
// getters et setters

  public int getErreur() {
    return erreur;
  }

  public void setErreur(int erreur) {
    this.erreur = erreur;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }
}
