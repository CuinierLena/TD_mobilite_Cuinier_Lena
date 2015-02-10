package android.arduinos.dao;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.List;
@EBean(scope = EBean.Scope.Singleton)
public class Dao implements IDao {
  // client du service REST
  @RestService
  RestClient restClient;
  @Override
  public ArduinosResponse getArduinos() {
    return restClient.getArduinos();
  }
  @Override
  public GenericResponse setBlink(String id,int pin,int speed, int time) {
    return restClient.setBlink(id, pin, speed, time);
  }
  @Override
  public GenericResponse readPin(String id,int pin, char mode) // mode='a' analogique 'b' binaire
  {
    return restClient.readPin(id, pin, mode);
  }
  @Override
  public GenericResponse writePin(String id,int pin,char mode, int val)
  {
    return restClient.writePin(id, pin, mode,val);
  }
  @Override
  public CommandsResponse sendCommands(List<ArduinoCommand> commands, String id)
  {
    return restClient.sendCommands(commands,id);
  }
  @Override
  public void setUrlServiceRest(String urlServiceRest) {
// on fixe l'URL du service REST
    restClient.setRootUrl(urlServiceRest);
  }
  @Override
  public void setTimeout(int timeout) {
// on fixe le timeout des requêtes du client REST
    HttpComponentsClientHttpRequestFactory factory = new  HttpComponentsClientHttpRequestFactory();
    factory.setReadTimeout(timeout);
    factory.setConnectTimeout(timeout);
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    restClient.setRestTemplate(restTemplate);
  }
}