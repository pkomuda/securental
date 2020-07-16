package pl.lodz.p.it.securental.security;

import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Getter
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private final String combination;
    private final String characters;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.combination = request.getParameter("combination");
        this.characters = request.getParameter("characters");
    }
}
