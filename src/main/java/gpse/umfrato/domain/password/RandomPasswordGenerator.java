package gpse.umfrato.domain.password;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Random password generator class which uses SecureRandom java api.
 */
public class RandomPasswordGenerator {

    private static final int ASCII_BEGIN = 33;
    private static final int ASCII_END = 127;
    private static final int PWD_LENGTH = 15;
    private final SecureRandom randomNr = new SecureRandom();

    // a list of ascii characters from 33 to 126.
    private final List<Character> asciiVal;

    public RandomPasswordGenerator() {

        asciiVal = new ArrayList<>();
        for (int i = ASCII_BEGIN; i < ASCII_END; i++) {
            asciiVal.add((char) i);
        }
    }

    /**
     * This method generates a password of length 15 (changeable) with Secure Random api.
     *
     * @return returns a char array password (best practise) with length 15.
     */
    public char[] generatePwd() {
        // Password length of 15 characters
        char[] password = new char[PWD_LENGTH];
        for (int i = 0; i < PWD_LENGTH; i++) {
            password[i] = asciiVal.get(randomNr.nextInt(asciiVal.size()));
        }
        return password;
    }

}
