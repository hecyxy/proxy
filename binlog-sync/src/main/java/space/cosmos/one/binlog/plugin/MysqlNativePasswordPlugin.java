package space.cosmos.one.binlog.plugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MysqlNativePasswordPlugin implements AuthenticationPlugin {
    public String getPluginName() {
        return "mysql_native_password";
    }

    public byte[] process(byte[] password, byte[] seedAsBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] passwordHashStage1 = md.digest(password);
            md.reset();
            byte[] passwordHashStage2 = md.digest(passwordHashStage1);
            md.reset();
            md.update(seedAsBytes);
            md.update(passwordHashStage2);
            byte[] toBeXord = md.digest();
            int numToXor = toBeXord.length;

            for (int i = 0; i < numToXor; i++)
                toBeXord[i] = (byte) (toBeXord[i] ^ passwordHashStage1[i]);
            return toBeXord;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA-1 Provider");
        }

    }
}
