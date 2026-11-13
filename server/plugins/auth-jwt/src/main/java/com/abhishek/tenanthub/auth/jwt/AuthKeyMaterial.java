package com.abhishek.tenanthub.auth.jwt;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Resolves the JWT signing secret and the RSA key pair used to encrypt login credentials.
 * <p>
 * Explicitly configured values win ({@code jwt.token.secret}, {@code jwt.token.rsa-private-key},
 * {@code jwt.token.rsa-public-key}). Otherwise the material is read from {@code jwt.token.keys-dir},
 * and generated there on first start, so every deployment gets its own keys and nothing secret
 * is committed to source control.
 *
 * @author Abhishek Raj
 */
@Slf4j
public final class AuthKeyMaterial {

    static final String SECRET_FILE = "jwt_secret";
    static final String PRIVATE_KEY_FILE = "rsa_private_key.pem";
    static final String PUBLIC_KEY_FILE = "rsa_public_key.pem";
    private static final int RSA_BITS = 2048;

    private AuthKeyMaterial() {
    }

    /**
     * HS256 signing secret for access tokens.
     */
    public static synchronized byte[] jwtSecret(JwtTokenProperties props) {
        if (StringUtils.isNotBlank(props.getSecret())) {
            return props.getSecret().getBytes(StandardCharsets.UTF_8);
        }
        var file = keysDir(props).resolve(SECRET_FILE);
        try {
            if (Files.notExists(file)) {
                var bytes = new byte[64];
                new SecureRandom().nextBytes(bytes);
                writePrivate(file, Base64.getEncoder().encodeToString(bytes));
                log.warn("Generated a new JWT signing secret at {}. Keep this file private and back it up.", file.toAbsolutePath());
            }
            return Files.readString(file, StandardCharsets.UTF_8).trim().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read or create JWT secret " + file.toAbsolutePath(), e);
        }
    }

    /**
     * RSA key pair used by the login form to encrypt credentials in transit.
     */
    public static synchronized RSA rsa(ResourceLoader loader, JwtTokenProperties props) {
        try {
            String privatePem;
            String publicPem;
            if (StringUtils.isNoneBlank(props.getRsaPrivateKey(), props.getRsaPublicKey())) {
                privatePem = loader.getResource(props.getRsaPrivateKey()).getContentAsString(StandardCharsets.UTF_8);
                publicPem = loader.getResource(props.getRsaPublicKey()).getContentAsString(StandardCharsets.UTF_8);
            } else {
                var dir = keysDir(props);
                var privateFile = dir.resolve(PRIVATE_KEY_FILE);
                var publicFile = dir.resolve(PUBLIC_KEY_FILE);
                boolean hasPrivate = Files.exists(privateFile);
                boolean hasPublic = Files.exists(publicFile);
                if (hasPrivate != hasPublic) {
                    throw new IllegalStateException("Only one of " + privateFile.toAbsolutePath() + " and "
                            + publicFile.toAbsolutePath() + " exists. Restore the missing file or delete both to regenerate.");
                }
                if (!hasPrivate) {
                    generateRsa(privateFile, publicFile);
                }
                privatePem = Files.readString(privateFile, StandardCharsets.UTF_8);
                publicPem = Files.readString(publicFile, StandardCharsets.UTF_8);
            }
            return new RSA(stripPem(privatePem), stripPem(publicPem));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load RSA key pair", e);
        }
    }

    private static void generateRsa(Path privateFile, Path publicFile) throws IOException {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(RSA_BITS, new SecureRandom());
            var pair = generator.generateKeyPair();
            writePrivate(privateFile, toPem("PRIVATE KEY", pair.getPrivate().getEncoded()));
            Files.writeString(publicFile, toPem("PUBLIC KEY", pair.getPublic().getEncoded()), StandardCharsets.UTF_8);
            log.warn("Generated a new {}-bit RSA key pair in {}. Keep the private key private and back it up.",
                    RSA_BITS, privateFile.getParent().toAbsolutePath());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA is not available in this JVM", e);
        }
    }

    private static Path keysDir(JwtTokenProperties props) {
        var dir = Path.of(StringUtils.defaultIfBlank(props.getKeysDir(), "./keys"));
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create keys directory " + dir.toAbsolutePath(), e);
        }
        return dir;
    }

    /**
     * Writes a file readable only by its owner where the file system supports POSIX permissions.
     */
    private static void writePrivate(Path file, String content) throws IOException {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            Files.deleteIfExists(file);
            Files.createFile(file, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        }
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    private static String toPem(String type, byte[] der) {
        var body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(der);
        return "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----\n";
    }

    private static String stripPem(String pem) {
        return pem.replaceAll("-----(BEGIN|END) [A-Z ]+-----", "").replaceAll("\\s", "");
    }
}
