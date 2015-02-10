package android.arduinos.dao;

import java.util.Map;

/**
 * Created by usrlocal on 09/01/2015.
 */
public class ArduinoCommand {
  // data
  private String id;
  private String ac;
  private Map<String, Object> pa;
  // constructeurs
  public ArduinoCommand() {
  }
  public ArduinoCommand(String id, String ac, Map<String, Object> pa) {
    this.id = id;
    this.ac = ac;
    this.pa = pa;
  }
// getters et setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAc() {
    return ac;
  }

  public void setAc(String ac) {
    this.ac = ac;
  }

  public Map<String, Object> getPa() {
    return pa;
  }

  public void setPa(Map<String, Object> pa) {
    this.pa = pa;
  }
}
