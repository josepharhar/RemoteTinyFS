package com.tinyfs.credentials;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.google.protobuf.ByteString;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.model.ServiceModel.ClientRegistrationRequest;
import com.tinyfs.model.ServiceModel.WriteRequest;

public class CredentialsObfuscatorTest {

  private static final String DES_KEY_RAW = "a9/Vbr+oosQ=";
  private static final byte[] DES_KEY_DECODED = Base64.getDecoder().decode(DES_KEY_RAW);

  @Test
  public void test() throws Exception {
    CredentialsObfuscator obfuscator =
      new CredentialsObfuscator(new SecretKeySpec(DES_KEY_DECODED, 0, DES_KEY_DECODED.length, "DES"));
    for (byte letter : ClientRegistrationRequest.newBuilder()
        .setToken(ByteString.copyFrom(
            obfuscator.obfuscateClientCredentials(ClientCredentials.newBuilder()
                .setClientId("avilan-client")
                .build()))
        )
        .addFsNames("dumb")
        .build().toByteArray()) {
      System.out.print(letter + ", ");
    }
    System.out.println(0);

    for (byte letter : WriteRequest.newBuilder()
          .setSessionId("0110")
          .setMessage(ByteString.copyFromUtf8("ASDF_ASDF"))
        .build().toByteArray()) {
      System.out.print(letter + ", ");
    }
    System.out.println(0);
  }
}
