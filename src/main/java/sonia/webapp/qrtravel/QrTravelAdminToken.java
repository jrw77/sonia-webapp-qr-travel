package sonia.webapp.qrtravel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class QrTravelAdminToken
{
  public final static String QR_TRAVEL_ADMIN_TOKEN = "QrTravelAdminToken";

  public final static String UNKNOWN_ADMIN_TOKEN = "unknown";

  private final static Config CONFIG = Config.getInstance();

  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    QrTravelAdminToken.class.getName());

  private QrTravelAdminToken()
  {
    uuid = UUID.randomUUID().toString();
  }

  public void addToHttpServletResponse(HttpServletResponse response)
  {
    response.addCookie(toCookie());
    response.addHeader("Cache-Control",
      "no-cache, no-store, max-age=0, must-revalidate");
    response.addHeader("Pragma", "no-cache");
  }

  public static QrTravelAdminToken fromCookieValue(String value)
  {
    QrTravelAdminToken token = null;

    if (!UNKNOWN_ADMIN_TOKEN.equalsIgnoreCase(value))
    {
      ObjectMapper objectMapper = new ObjectMapper();
      try
      {
        value = AdminCipher.getInstance().decrypt(value);
        token = objectMapper.readValue(value, QrTravelAdminToken.class);
      }
      catch (JsonProcessingException ex)
      {
        LOGGER.error("getting admin cookie ", ex);
      }
    }

    if (token == null)
    {
      token = new QrTravelAdminToken();
    }

    return token;
  }

  public Cookie toCookie()
  {
    Cookie cookie = null;
    ObjectMapper objectMapper = new ObjectMapper();

    try
    {
      this.lastAccess = System.currentTimeMillis();
      String value = objectMapper.writeValueAsString(this);
      value = AdminCipher.getInstance().encrypt(value);
      cookie = new Cookie(QR_TRAVEL_ADMIN_TOKEN, value);
      cookie.setPath("/");
      cookie.setMaxAge(CONFIG.getAdminTokenTimeout());
      cookie.setHttpOnly(true);
    }
    catch (JsonProcessingException ex)
    {
      LOGGER.error("creating admin cookie ", ex);
    }

    return cookie;
  }

  @Getter
  @Setter
  @JsonProperty("ca")
  private boolean cookieAccepted;

  @Getter
  @Setter
  @JsonProperty("ad")
  private boolean loginSuccessful;

  @Getter
  @JsonProperty("ts")
  private long lastAccess;

  @Setter
  @Getter
  @JsonProperty("id")
  @ToString.Exclude
  private String uid;

  @Getter
  @JsonProperty("uu")
  private final String uuid;
}
