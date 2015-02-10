package android.arduinos.dao;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by usrlocal on 19/01/2015.
 */
public class ArduinoResponse implements Serializable {
  private String json;
  private String id;
  private String erreur;
  private Map<String, Object> etat;
// getters et setters

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getErreur() {
    return erreur;
  }

  public void setErreur(String erreur) {
    this.erreur = erreur;
  }

  public Map<String, Object> getEtat() {
    return etat;
  }

  public void setEtat(Map<String, Object> etat) {
    this.etat = etat;
  }

  @Override
  public String toString()
  {
    return "{\"json\":\""+json+";\"id\":\""+id+"\";\"erreur\":\"\""+erreur+"\";\"etat\":\"\""+etat+"\"}";
  }
}