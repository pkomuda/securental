package pl.lodz.p.it.securental;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String name = "org.hibernate.dialect.PostgreSQL10Dialect";
        System.out.println(name.substring(name.lastIndexOf(".") + 1));
    }
}
