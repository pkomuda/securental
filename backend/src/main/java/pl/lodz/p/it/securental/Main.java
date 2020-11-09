package pl.lodz.p.it.securental;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static pl.lodz.p.it.securental.security.CustomUserDetailsService.getUserRoles;

public class Main {

    public static void main(String[] args) {
        List<String> groups = List.of("ADMIN", "EMPLOYEE");
        for (SimpleGrantedAuthority authority : getUserRoles(groups)) {
            System.out.println(authority);
        }
    }
}
