package android.arduinos.dao;

import java.util.List;
/**
 * Created by usrlocal on 09/01/2015.
 */
public interface IDao {
  // liste des arduinos
  public ArduinosResponse getArduinos();
  // URL du service web
  public void setUrlServiceRest(String url);
  // timeout du service web
  public void setTimeout(int timeout);

  //blink
  public GenericResponse setBlink(String idArduino,int pin,int speed, int time);
  //pinRead
  public GenericResponse readPin(String id,int pin, char mode); // mode='a' analogique 'b' binaire
  //pinWrite
  public GenericResponse writePin(String id,int pin,char mode, int val);
  // envoi de commandes jSON
  public CommandsResponse sendCommands(List<ArduinoCommand> commands, String idArduino);
}