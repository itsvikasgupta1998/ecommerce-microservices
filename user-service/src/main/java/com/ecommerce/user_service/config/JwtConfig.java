package com.ecommerce.user_service.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
public class JwtConfig {

    @Bean
    public KeyPair rsaKeyPair() {

        try {
            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);

            return keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to generate RSA key pair",
                    exception
            );
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair keyPair) {

        RSAKey rsaKey = new RSAKey.Builder(
                (java.security.interfaces.RSAPublicKey) keyPair.getPublic()
        )
                .privateKey(
                        (java.security.interfaces.RSAPrivateKey)
                                keyPair.getPrivate()
                )
                .keyID("ecommerce-user-service-key")
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        ImmutableJWKSet<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(jwkSet);

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {

        return NimbusJwtDecoder
                .withPublicKey(
                        (java.security.interfaces.RSAPublicKey)
                                keyPair.getPublic()
                )
                .build();
    }
}