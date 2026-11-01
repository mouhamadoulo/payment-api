package com.molo.payment_api.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Nous utilisons ici les composants Nimbus intégrés à Spring Security :
 *
 * - `JwtEncoder` : crée et signe un JWT.
 * - `JwtDecoder` : décode et vérifie un JWT.
 * - `NimbusJwtEncoder` : implémentation de l'encodage.
 * - `NimbusJwtDecoder` : implémentation de la validation.
 *
 * Spring Security sait utiliser ces composants pour traiter les Bearer Tokens
 */

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret}") String secret) {

        byte[] decodedKey = Base64.getDecoder().decode(secret);

        if (decodedKey.length < 32) {
            throw new IllegalArgumentException(
                    "La clé JWT doit contenir au minimum 256 bits"
            );
        }

        return new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {

        JWK jwk = new OctetSequenceKey.Builder(jwtSecretKey)
                .keyID("payment-api-key")
                .build();

        var jwkSource = new ImmutableJWKSet<SecurityContext>(
                new JWKSet(jwk)
        );

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {

        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
