package com.codingzero.saam.common;

import com.codingzero.utilities.error.BusinessError;

import java.util.regex.Pattern;

public class PasswordPolicy {

    private static final Pattern BASE_PATTERN = Pattern.compile("[a-zA-Z0-9\\p{Punct}]+");
    private static final Pattern CAPITAL_PATTERN = Pattern.compile("^[A-Z]+.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[\\p{Punct}]+.*");
    private static final int DEFAULT_MAX_LENGTH = 25;

    private int minLength;
    private int maxLength;
    private boolean isNeedCapital;
    private boolean isNeedSpecialChar;

    public PasswordPolicy(int minLength, int maxLength, boolean isNeedCapital, boolean isNeedSpecialChar) {
        checkForLength(minLength, maxLength);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.isNeedCapital = isNeedCapital;
        this.isNeedSpecialChar = isNeedSpecialChar;
    }

    private void checkForLength(int minLength, int maxLength) {
        if (minLength >= maxLength) {
            throw new IllegalArgumentException(
                    "Min length is need to be smaller than max length, " + minLength + ", " + maxLength);
        }
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isNeedCapital() {
        return isNeedCapital;
    }

    public boolean isNeedSpecialChar() {
        return isNeedSpecialChar;
    }

    public void check(String password) {
        if (null == password) {
            throw new IllegalArgumentException("Password is missing");
        }
        if (!isValidLength(password)) {
            throw BusinessError.raise(Errors.ILLEGAL_PASSWORD_FORMAT)
                    .message("Length need to be longer than "
                            + getMinLength()
                            + " characters and shorter than "
                            + getMaxLength()
                            + " characters.")
                    .details("minLength", getMinLength())
                    .details("maxLength", getMaxLength())
                    .build();
        }
        if (!isValidPattern(password)) {
            String msg = null;
            if (isNeedCapital() && isNeedSpecialChar()) {
                msg = "Password need to be capitalized and include at least special character.";
            } else if (isNeedCapital() && !isNeedSpecialChar()) {
                msg = "Password need to be capitalized.";
            } else if (isNeedCapital() && !isNeedSpecialChar()) {
                msg = "Password need include at least special character.";
            }
            throw BusinessError.raise(Errors.ILLEGAL_PASSWORD_FORMAT)
                    .message(msg)
                    .build();
        }
    }

    private boolean isValidPattern(String password) {
        checkForNullPassword(password);
        if (isNeedCapital) {
            if (!CAPITAL_PATTERN.matcher(password).matches()) {
                return false;
            }
        }
        if (isNeedSpecialChar) {
            if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
                return false;
            }
        }
        if (!BASE_PATTERN.matcher(password).matches()) {
            return false;
        }
        return true;
    }

    private boolean isValidLength(String password) {
        checkForNullPassword(password);
        return (password.length() >= minLength && password.length() <= maxLength);
    }

    private void checkForNullPassword(String password) {
        if(null == password) {
            throw new NullPointerException("Password cannot be null.");
        }
    }

    /**
     * Alphabets, digits, and punctuation -- !"#$%&'()\*+,-./:;<=>?@[\]^_`{|}~
     *
     * @return PasswordPolicy
     */
    public static PasswordPolicy easyPolicy(int minLength) {
        return new PasswordPolicy(minLength, DEFAULT_MAX_LENGTH, false, false);
    }

    /**
     * All rules inheritent from easy policy plus the first letter must be upper case.
     *
     * @param minLength
     * @return PasswordPolicy
     */
    public static PasswordPolicy regularPolicy(int minLength) {
        return new PasswordPolicy(minLength, DEFAULT_MAX_LENGTH, true, false);
    }

    /**
     * All rules inheritent from easy policy plus must include one of punctuation char
     *
     * @param minLength
     * @return PasswordPolicy
     */
    public static PasswordPolicy strictPolicy(int minLength) {
        return new PasswordPolicy(minLength, DEFAULT_MAX_LENGTH, true, true);
    }

}
