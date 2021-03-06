package pt.ua.tqs104_rentua_restapi.util;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */
@Dependent @Default
public class SimpleKeyGenerator implements KeyGenerator {

    // ======================================
    // =          Business methods          =
    // ======================================

    @Override
    public Key generateKey() {
        String keyString = "simplekey";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        return key;
    }
}