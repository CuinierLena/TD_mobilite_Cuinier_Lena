package android.arduinos.entities;
public class CheckedArduino extends Arduino {
  private static final long serialVersionUID = 1L;
  // un Arduino peut être sélectionné
  private boolean isChecked;
  // constructeur
  public CheckedArduino(Arduino arduino, boolean isChecked) {
// parent
    super(arduino.getId(), arduino.getDescription(), arduino.getMac(), arduino.getIp(), arduino.getPort());
// local
    this.isChecked = isChecked;
  }
  // getters et setters
  public boolean isChecked() {
    return isChecked;
  }
  public void setChecked(boolean isChecked) {
    this.isChecked = isChecked;
  }
}