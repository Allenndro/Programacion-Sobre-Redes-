package comerciotcp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

final class Cifrado {
    private static final String AES = "AES/CBC/PKCS5Padding";
    private static final String HMAC = "HmacSHA256";
    private static final int IV = 16;
    private static final int FIRMA = 32;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final SecretKeySpec claveAes;
    private final SecretKeySpec claveFirma;

    Cifrado(char[] frase) throws GeneralSecurityException {
        if (frase.length < 12) {
            throw new IllegalArgumentException("La frase del canal debe tener al menos 12 caracteres.");
        }
        byte[] sal = "ComercioTCP-v1-AES-CBC".getBytes(StandardCharsets.UTF_8);
        PBEKeySpec datos = new PBEKeySpec(frase, sal, 210_000, 512);
        try {
            byte[] claves = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(datos).getEncoded();
            claveAes = new SecretKeySpec(claves, 0, 32, "AES");
            claveFirma = new SecretKeySpec(claves, 32, 32, HMAC);
            Arrays.fill(claves, (byte) 0);
        } finally {
            datos.clearPassword();
        }
    }

    byte[] cifrar(String mensaje, byte direccion, long secuencia, byte[] sesion) throws GeneralSecurityException {
        byte[] iv = new byte[IV];
        ALEATORIO.nextBytes(iv);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, claveAes, new IvParameterSpec(iv));
        byte[] cifrado = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));
        byte[] datos = ByteBuffer.allocate(IV + cifrado.length).put(iv).put(cifrado).array();
        return ByteBuffer.allocate(datos.length + FIRMA)
                .put(datos).put(firmar(datos, direccion, secuencia, sesion)).array();
    }

    String descifrar(byte[] trama, byte direccion, long secuencia, byte[] sesion) throws GeneralSecurityException {
        int sinFirma = trama.length - FIRMA;
        if (sinFirma < IV * 2 || sinFirma % IV != 0) {
            throw new GeneralSecurityException("Trama cifrada inválida.");
        }
        byte[] datos = Arrays.copyOf(trama, sinFirma);
        byte[] firma = Arrays.copyOfRange(trama, sinFirma, trama.length);
        if (!MessageDigest.isEqual(firma, firmar(datos, direccion, secuencia, sesion))) {
            throw new GeneralSecurityException("La clave del canal no coincide o la trama fue alterada.");
        }
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, claveAes, new IvParameterSpec(datos, 0, IV));
        return new String(cipher.doFinal(datos, IV, datos.length - IV), StandardCharsets.UTF_8);
    }

    private byte[] firmar(byte[] datos, byte direccion, long secuencia, byte[] sesion)
            throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC);
        mac.init(claveFirma);
        mac.update(direccion);
        mac.update(ByteBuffer.allocate(Long.BYTES).putLong(secuencia).array());
        mac.update(sesion);
        return mac.doFinal(datos);
    }

    static byte[] nuevoNonce() {
        byte[] nonce = new byte[32];
        ALEATORIO.nextBytes(nonce);
        return nonce;
    }
}
