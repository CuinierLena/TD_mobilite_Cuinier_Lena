package android.arduinos.dao;

/**
 * Created by usrlocal on 19/01/2015.
 */
public class GenericResponse extends AbstractResponse {
  // réponse de l'Arduino
  private ArduinoResponse response;
// getters et setters

  public ArduinoResponse getResponse() {
    return response;
  }

  public void setResponse(ArduinoResponse response) {
    this.response = response;
  }

  @Override
  public String toString()
  {
    return response.toString();
  }

}
