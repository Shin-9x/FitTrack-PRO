package it.fartingbrains.fitness.config;

import it.fartingbrains.fitness.common.constant.AuthConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = AuthConstants.RSA_PREFIX)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
