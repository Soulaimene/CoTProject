package com.lifeguardian.lifeguardian.utils;



import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Argon2Utility {

    private final static Config config = ConfigProvider.getConfig();
    private final static int saltLength = config.getValue("argon2.saltLength", Integer.class);
    private final static int hashLength = config.getValue("argon2.hashLength", Integer.class);
    private final static Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, saltLength, hashLength);
    private final static int iterations = config.getValue("argon2.iterations", Integer.class);
    private final static int memory = config.getValue("argon2.memory", Integer.class);
    private final static int threadNumber = config.getValue("argon2.threadNumber", Integer.class);

    public static boolean check(String dbHash, char[] clientHash) {
        try {
            return argon2.verify(dbHash, clientHash);
        } finally {
            argon2.wipeArray(clientHash);
        }
    }

    public static String hash(char[] clientHash){
        try{
            return argon2.hash(iterations, memory, threadNumber, clientHash);
        } finally {
            argon2.wipeArray(clientHash);
        }
    }

}