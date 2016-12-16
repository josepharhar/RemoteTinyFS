package com.tinyfs.credentials;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.exception.CredentialsException;
import com.tinyfs.exception.InvalidCredentialsException;

@Component
public class CredentialsObfuscator {

  private final Cipher encryptCipher;
  private final Cipher decryptCipher;

  @Inject
  public CredentialsObfuscator(final SecretKey credentialsKey)
      throws CredentialsException {
    try {
      this.encryptCipher = Cipher.getInstance(credentialsKey.getAlgorithm());
      this.decryptCipher = Cipher.getInstance(credentialsKey.getAlgorithm());

      encryptCipher.init(Cipher.ENCRYPT_MODE, credentialsKey);
      decryptCipher.init(Cipher.DECRYPT_MODE, credentialsKey);

    } catch (NoSuchAlgorithmException|NoSuchPaddingException|InvalidKeyException e) {
      throw new CredentialsException(e);
    }
  }

  /**
   * @param The protobuf credentials to generate an array of bytes.
   * @return An obfuscated array of bytes signed using the service's secret key.
   */
  public byte[] obfuscateClientCredentials(
      final ClientCredentials clientCredentials) {
    return obfuscateProtobufCredentials(
      () -> clientCredentials.toByteArray());
  }

  /**
   * @param The obfuscated protobuf credentials generated by obfuscateAccessCredentials.
   * @return The AccessCredentials that byte array represents.
   */
  public ClientCredentials deobfuscateClientCredentials(
      final byte[] obfuscatedCredentials) {
    return deobfuscateProtobufCredentials(
      obfuscatedCredentials,
      (credentials) -> ClientCredentials.parseFrom(credentials));
  } 

  private byte[] obfuscateProtobufCredentials(
      final ByteArrayProvider byteArrayProvider) {
    byte[] cipherText = null;

    try {
      cipherText = toBase64(signByteArray(byteArrayProvider.get()));
    } catch (Exception e) {
      Throwables.propagate(e);
    }

    return cipherText;
  }

  private <E> E deobfuscateProtobufCredentials(
      final byte[] obfuscatedCredentials,
      final ByteArrayParser<E> parseFunction) {
    byte[] base64 = null;
    try {
      base64 = Base64.getDecoder().decode(obfuscatedCredentials);
    } catch (IllegalArgumentException e) {
      // Client sent non-base64 token string
      throw new InvalidCredentialsException("Client gave non-Base64 token", e);
    }

    byte[] decryptedBase64 = null;
    try {
      decryptedBase64 = decryptCipher.doFinal(base64);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      // Client sent unregistered token?
      throw new InvalidCredentialsException("Client gave bad token", e);
    }

    try {
      return parseFunction.parseFrom(decryptedBase64);
    } catch (Exception e) {
      // ???
      throw new RuntimeException(e);
    }
  }

  private byte[] toBase64(final byte[] plainText) throws Exception {
    return Base64.getEncoder().encode(plainText);
  }

  private byte[] fromBase64(final byte[] plainText) throws Exception {
    return Base64.getDecoder().decode(plainText);
  }

  private byte[] signByteArray(final byte[] plainText) throws Exception {
    return encryptCipher.doFinal(plainText);
  }

  private byte[] decodeByteArray(final byte[] cipherText) throws Exception {
    return decryptCipher.doFinal(cipherText);
  }

  @FunctionalInterface
  private interface ByteArrayProvider {
    public byte[] get() throws Exception;
  }

  @FunctionalInterface
  private interface ByteArrayParser<E> {
    public E parseFrom(final byte[] byteArray) throws Exception;
  }
}