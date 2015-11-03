package org.iplantc.de.server.auth;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;

/**
 * Utility methods for dealing with PEM encoded key files. This class may not be instantiated.
 *
 * @author dennis
 */
public class PemKeyUtils {

    // Prevent instantiation.
    private PemKeyUtils() {}

    /**
     * Loads a private key from a PEM encoded file. The password may be null if the key is not
     * encrypted.
     *
     * @param path the path to the file.
     * @param password the password used to decrypt the file.
     * @return the private key.
     * @throws IOException if the file can't be read.
     * @throws GeneralSecurityException if the key can't be parsed or decrypted.
     * @throws NullPointerException if the key is encrypted and the password is null.
     */
    public static PrivateKey loadPrivateKey(String path, String password)
            throws IOException, GeneralSecurityException {

        PEMParser pemParser = new PEMParser(new FileReader(path));
        Object keyObject = pemParser.readObject();

        // Extract the private key info.
        PrivateKeyInfo privateKeyInfo;
        if (keyObject instanceof PEMKeyPair) {
            privateKeyInfo = ((PEMKeyPair) keyObject).getPrivateKeyInfo();
        } else if (keyObject instanceof PEMEncryptedKeyPair){
            if (password == null) {
                String msg = "No password provided for encrypted key: " + path;
                throw new NullPointerException(msg);
            }
            privateKeyInfo = decryptKey((PEMEncryptedKeyPair) keyObject, password);
        } else {
            String msg = "Unsupported key type: " + keyObject.getClass().getName();
            throw new IllegalArgumentException(msg);
        }

        // Return the key.
        return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
    }

    private static PrivateKeyInfo decryptKey(PEMEncryptedKeyPair encryptedKeyPair, String password)
            throws IOException, GeneralSecurityException {

        // Make sure that the Bouncy Castle provider is registered.
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // Get the decryptor provider.
        PEMDecryptorProvider decryptorProvider = new JcePEMDecryptorProviderBuilder()
                .setProvider("BC").build(password.toCharArray());

        // Decrypt the key.
        return encryptedKeyPair.decryptKeyPair(decryptorProvider).getPrivateKeyInfo();
    }
}
