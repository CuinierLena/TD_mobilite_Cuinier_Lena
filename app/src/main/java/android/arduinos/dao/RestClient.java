package android.arduinos.dao;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.List;
@Rest(converters = {MappingJacksonHttpMessageConverter.class})
public interface RestClient extends RestClientRootUrl, RestClientSupport {
  // liste des Arduinos
  @Get("/arduinos")
  public ArduinosResponse getArduinos();
  // RestTemplate
  public void setRestTemplate(RestTemplate restTemplate);

  // envoi de commandes jSON
  @Post("/arduinos/commands/{idArduino}")
  public CommandsResponse sendCommands(List<ArduinoCommand> commands, String idArduino);

  //blink
  @Get("/arduinos/blink/1/{idArduino}/{pin}/{speed}/{nb}/")
  public GenericResponse setBlink(String idArduino,int pin,int speed, int nb);


  //pinRead
  @Get("/arduinos/pinRead/1/{idArduino}/{pin}/{mode}/")
  public GenericResponse readPin(String idArduino,int pin, char mode); // mode='a' analogique 'b' binaire


  //pinWrite
  @Get("/arduinos/pinWrite/1/{idArduino}/{pin}/{mode}/{val}/")
  public GenericResponse writePin(String idArduino,int pin,char mode, int val);

  /*
http://localhost:8080/arduinos/blink/1/192.168.2.2/8/100/20/
http://localhost:8080/arduinos/pinRead/1/192.168.2.2/0/a/
http://localhost:8080/arduinos/pinRead/1/192.168.2.2/5/b/
http://localhost:8080/arduinos/pinWrite/1/192.168.2.2/8/b/1/
http://localhost:8080/arduinos/pinWrite/1/192.168.2.2/4/a/100/
*/
}