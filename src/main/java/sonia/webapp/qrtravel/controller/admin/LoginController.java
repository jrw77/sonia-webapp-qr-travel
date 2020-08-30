package sonia.webapp.qrtravel.controller.admin;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import sonia.webapp.qrtravel.QrTravelToken;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;
import sonia.webapp.qrtravel.controller.HomeController;
import sonia.webapp.qrtravel.form.LoginForm;

/**
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@Controller
public class LoginController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    HomeController.class.getName());

  @GetMapping("/admin/login")
  public String httpGetLoginPage(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, LoginForm loginForm)
  {
    LOGGER.debug("Login GET Request");
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return "login";
  }

  @PostMapping("/admin/login")
  public String httpPostloginPage(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, @Valid LoginForm loginForm,
    BindingResult bindingResult)
  {
    LOGGER.debug("Login POST Request");
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    if (bindingResult.hasErrors())
    {
      LOGGER.error("bind result has errors");
      List<FieldError> fel = bindingResult.getFieldErrors();
      for (FieldError fe : fel)
      {
        LOGGER.trace(fe.toString());
      }
    }
    else
    {
      LOGGER.trace( "user id = {}", loginForm.getUserId());
      LOGGER.trace( "password = {}", loginForm.getPassword());
    }

    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return "login";
  }
}