package com.xantrix.webapp.configserver;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(properties = {"sicurezza.userpwd=" + ApplicationTests.USER_PASSWORD, "sicurezza.adminpwd=MagicaBula_2018", "jasypt.encryptor.password=amalaPazzaInterAmala"})
@Slf4j
class ApplicationTests {

    /**
     * sicurezza.userpwd=BimBumBam_2018 => encrypted value=kSnZ/ISHShFEPeok00UCYUqFSUu3P+co
     * sicurezza.adminpwd=MagicaBula_2018 => encrypted value=1QhYQyk24MvDG9X/UetDKIChLEREECij
     */
    public static final String JASYPT_PASSWORD_TO_ENCRYPT = "amalaPazzaInterAmala";
    public static final String USER_PASSWORD = "BimBumBam_2018";
    public static final String ADMIN_PASSWORD = "MagicaBula_2018";
//    @Autowired
//    private BasicSecurityConfiguration securityConfiguration;
//
//    @Test
//    void contextLoads() {
//    }
//
//    @Test
//    void encryptPassword() {
//        String encryptedPassword = securityConfiguration.encrypt(JASYPT_PASSWORD_TO_ENCRYPT);
//        log.debug("password to encrypt {} = {}", JASYPT_PASSWORD_TO_ENCRYPT, encryptedPassword);
//        String decryptedPassword = securityConfiguration.decryptString(encryptedPassword);
//        log.debug("decrypted password: {}", decryptedPassword);
//        assertThat(JASYPT_PASSWORD_TO_ENCRYPT).isEqualTo(decryptedPassword);
//        log.debug("sicurezza.userpwd=" + USER_PASSWORD + " => encrypted value={}", securityConfiguration.encrypt(USER_PASSWORD));
//        log.debug("sicurezza.adminpwd=" + ADMIN_PASSWORD + " => encrypted value={}", securityConfiguration.encrypt(ADMIN_PASSWORD));
//    }

    @Test
    void encryptPasswordInternal() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(JASYPT_PASSWORD_TO_ENCRYPT);
        String encryptedPassword = encryptor.encrypt(JASYPT_PASSWORD_TO_ENCRYPT);
        log.debug("password to encrypt {} = {}", JASYPT_PASSWORD_TO_ENCRYPT, encryptedPassword);
        String decryptedPassword = encryptor.decrypt(encryptedPassword);
        log.debug("decrypted password: {}", decryptedPassword);
        assertThat(JASYPT_PASSWORD_TO_ENCRYPT).isEqualTo(decryptedPassword);
        log.debug("sicurezza.userpwd=" + USER_PASSWORD + " => encrypted value={}", encryptor.encrypt(USER_PASSWORD));
        log.debug("sicurezza.adminpwd=" + ADMIN_PASSWORD + " => encrypted value={}", encryptor.encrypt(ADMIN_PASSWORD));
    }
}
