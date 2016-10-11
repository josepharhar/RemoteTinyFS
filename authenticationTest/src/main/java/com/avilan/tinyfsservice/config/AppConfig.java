package com.avilan.tinyfsservice.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.avilan.tinyfsservice.credentials.ClientCredentialsProvider;
import com.avilan.tinyfsservice.credentials.CredentialsObfuscator;

@Configuration
public class AppConfig {

    /**
     * This key are in no way secure, and merely to serve as a proof of concept.
     * This scheme will use a much more secure scheme, where a key is not statically used from code.
     */
    private static final String DES_KEY_RAW = "a9/Vbr+oosQ=";
    private static final byte[] DES_KEY_DECODED = Base64.getDecoder().decode(DES_KEY_RAW);

    @Bean
    SecretKey secretKey() throws Exception {
        return new SecretKeySpec(DES_KEY_DECODED, 0, DES_KEY_DECODED.length, "DES");
    }

    @Bean
    CredentialsObfuscator obfuscator(
            final SecretKey secretKey) throws Exception {
        return new CredentialsObfuscator(secretKey);
    }

    @Bean
    ClientCredentialsProvider clientCredentialsProvider() {
        return new ClientCredentialsProvider();
    }
}
