package com.example;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    private static final String CLAVE_SECRETA = "1234567890123456";
    private static final String ALGORITMO = "AES/ECB/PKCS5Padding";
    private static final CryptoUtil instancia = new CryptoUtil();

    private CryptoUtil() {}

    public static CryptoUtil getInstance() {
        return instancia;
    }

    public String encriptar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec key = new SecretKeySpec(CLAVE_SECRETA.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encriptado = cipher.doFinal(texto.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encriptado);
    }

    public String desencriptar(String textoEncriptado) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        SecretKeySpec key = new SecretKeySpec(CLAVE_SECRETA.getBytes("UTF-8"), "AES");

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodificado = Base64.getDecoder().decode(textoEncriptado.trim().replace("\n", "").replace("\r", ""));
        byte[] desencriptado = cipher.doFinal(decodificado);

        return new String(desencriptado, "UTF-8");
    }
}
