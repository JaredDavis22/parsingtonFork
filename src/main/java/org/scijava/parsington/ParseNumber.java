package org.scijava.parsington;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Utility methods for parsing numeric literals from strings. These methods largely
 * conform to the Java specification's ideas of what constitutes a numeric literal.
 * Based on a regular expression implementation.
 * Initial source from Google Gemini on July 23, 2026 query
 *   "java fast method to identify integers, doubles, hex and octal numbers in a string"
 * and existing code in Literals
 *
 * @author Curtis Rueden, Jared Davis
 */

public class ParseNumber {

    static long ignoredExceptions = 0; // not thread safe, good enough for rough metric usage

    /**
     * Parses a numeric literal of any known type.
     * <p>
     * This parsing mechanism is intended to be as close as possible to the
     * numeric literals supported by the Java programming language itself.
     * </p>
     *
     * @param s The string from which the numeric literal should be parsed.
     * @return The parsed numeric value, of a type consistent with Java's support
     *         for numeric primitives&mdash;or for values outside the normal range
     *         of Java primitives, {@link BigInteger} or {@link BigDecimal} as
     *         appropriate. Returns null if a numeric literal is not detected.
     */
    public static Number parseAllNumbers(String s) {
        return parseAllNumbers(s, new Position());
    }

    /**
     * Parses a numeric literal of any known type.
     * <p>
     * This parsing mechanism is intended to be as close as possible to the
     * numeric literals supported by the Java programming language itself.
     * </p>
     *
     * @param s The string from which the numeric literal should be parsed.
     * @param pos The offset from which the literal should be parsed. If parsing
     *          is successful, the position will be advanced to the next index
     *          after the parsed literal.
     * @return The parsed numeric value, of a type consistent with Java's support
     *         for numeric primitives&mdash;or for values outside the normal range
     *         of Java primitives, {@link BigInteger} or {@link BigDecimal} as
     *         appropriate. Returns null if a numeric literal is not detected.
     */

    public static Number parseAllNumbers(String s, Position pos) {
        ParseNumberResults result = ParseNumber.identifyNumber(s, pos.get());
        if (result == null) return null;
        ParseNumber.processNumber(s, result);
        if (result.getNumber() != null) {
            pos.inc(result.getLength());
        }
        return result.getNumber();
    }


    // identify

    // return null if not a number
    private static ParseNumberResults identifyNumber(String s, int startingPosition) {
        int len = s.length();
        final char first = s.charAt(startingPosition);
        boolean hasSign=false;
        // quick fail if first char not in not 0-9 or a +-
        if (first < '0' || first > '9') {
            if (first == '-' || first == '+') {
                hasSign = true;
                if (len == 1) {
                    // fail on just a sign
                    return null;
                }
            } else {
                return null; // first is not [-+0-9]
            }
        }

        ParseNumberResults results = new ParseNumberResults();
        results.getBeginGroup()[1] = startingPosition;
        int start = startingPosition;
        if (hasSign) {
            results.setSignIndex(startingPosition);
            start++;
        }

        // Check for Hexadecimal prefix (0x or 0X)
        if (start + 2 < len && s.charAt(start) == '0' &&
                (s.charAt(start + 1) == 'x' || s.charAt(start + 1) == 'X')) {
            results.numberType = NumberType.HEXADECIMAL;
            extractHexNumber(s, start + 2, len, results);
            return results;
        }

        if (start + 2 < len && s.charAt(start) == '0' &&
                (s.charAt(start + 1) == 'b' || s.charAt(start + 1) == 'B')) {
            results.numberType = NumberType.BINARY;
            extractBinaryNumber(s, start + 2, len, results);
            return results;
        }

        // try decimal, internally checks for octal
        extractDecimalNumber(s, start, len, results);
        return results;
    }

    // extract

/**
 * Attempts to parse a decimal literal (integer or otherwise; e.g., {@code 1234567890},
 * {@code 1234.0987} or {@code 1.2e34} or {@code 01234}). Parsing supports base 10 and base 8.
 *
 *
 * @param s The string from which the numeric literal should be parsed.
 * @param start The index of the string to start the parsing after the sign and 0[Xx] prefix.
 * @param end The index of the last char to parse.
 * @param results contains the parsing details calculated in this method.
 * @return true if a potential decimal literal is found, false otherwise
 *
 *
 * <p>Based on regular expression (but not exact) (([-+]?[0-9]+(\.[0-9]*)?([Ee][-+]?[0-9]+)?)([DdFfLl])?).*
 * group 1 = entire matching string. NB: re has .* postfix but this method does not include .* in this group.
 * group 2 is from start of string including optional groups 3 and 4
 * group 3 is \. then [0-9]*
 * group 4 is [Ee][-+]?[0-9]+
 * group 5 is [DdFfLl]?
 * group 6 is not in re. [-+]? at start of string
 * group 7 is not in re. Digits after sign, before group 3. re has [0-9]+ but changed to [0-9]* to match java numeric parsing
 * group 8 is not in re. Leads to group 3 or 4 or 5 or end
 *
 * <p>Octal encoding detection
 * If we have (no group 3) and (no group 4) then do a scan for an octal value in group 2
 *  Octal IFF
 *    group 7 starts with 0; does not contain 8 or 9; is longer than 1
 *    group 5 == empty or Ll
 */

    private static boolean extractDecimalNumber(String s, int start, int end, ParseNumberResults results) {
        char c;

        results.setSignGroup(6);

        boolean havedot = false;
        boolean haveSign = false;
        boolean haveFour = false;

        int group = 7;
        results.getBeginGroup()[group] = start;

        bigWhile:
        while (start <= end) {
            if (start == end) {
                results.getEndGroup()[group] = start;
                break;
            }
            c = s.charAt(start);
            switch (group) {
                case 7: // [0-9]+  digits after sign and before decimal point
                    if (isDecimalDigit(c)) {
                        start++;
                    } else {
                        // removed this to match java - it is now a little slower
                        /*
                        // this does not follow java - e.g. parsing +.2 is valid in java.
                        if (results.beginGroup[group] == start) {
                            // need at least 1 digit
                            results.numberType = NumberType.NOT_A_NUMBER;
                            break bigWhile;
                        }
                         */
                        results.getEndGroup()[group] = start;
                        group = 8;
                    }
                    break;

                case 8: // transition to group 3, 4, 5 or end
                    if (c == '.') {
                        if (havedot) { // only one . allowed
                            results.numberType = NumberType.NOT_A_NUMBER;
                            break bigWhile;
                        }
                        havedot = true;
                        group = 3;
                        results.getBeginGroup()[group] = start++;
                        break;
                    }
                    if (isE(c)) {
                        if (haveFour) { // one group 4 allowed
                            results.numberType = NumberType.NOT_A_NUMBER;
                            break bigWhile;
                        }
                        haveFour = true;
                        group = 4;
                        results.getBeginGroup()[group] = start++;
                        break;
                    }
                    if (isADoubleOrFloatSuffix(c) || isL(c)) {
                        group = 5;
                        results.getBeginGroup()[group] = start++;
                        results.getEndGroup()[group] = start;
                        results.numberType = isL(c) ? NumberType.INTEGER: NumberType.DOUBLE;
                        // break bigWhile fall through below
                    }
                    break bigWhile;
                case 3: // [0-9] 0..n times
                    if (isDecimalDigit(c)) {
                        start++;
                    } else {
                        results.getEndGroup()[group] = start;
                        group = 8;
                    }
                    break;
                case 4: //  just [-+]?[0-9]+ of group 4. [Ee] done in 8
                    if (isASign(c)) {
                        if (haveSign) { // only one sign allowed
                            results.numberType = NumberType.NOT_A_NUMBER;
                            break bigWhile;
                        }
                        haveSign = true;
                        start++;
                    } else if (isDecimalDigit(c)) {
                        start++;
                    } else {
                        results.getEndGroup()[group] = start;
                        group = 8; // back to 8 for group 5
                    }
                    break;
            } // switch group
        } // bigWhile
        results.getEndGroup()[1] = start;

        int sevenLength = results.getEndGroup()[7] - results.getBeginGroup()[7];
        int threeLength = results.getEndGroup()[3] - results.getBeginGroup()[3];

        if ((sevenLength == 0) && (threeLength < 2))
            results.numberType = NumberType.NOT_A_NUMBER; // need at least 1 digit in number after decimal

        if (results.getBeginGroup()[4] != -1) { // we got an E
            int minLength = (haveSign) ? 3 : 2;
            if (results.getEndGroup()[4] - results.getBeginGroup()[4] < minLength)
                results.numberType = NumberType.NOT_A_NUMBER; // need at least 1 digit in exponent
        }

        if (results.numberType == null) {
            if ((results.getBeginGroup()[4] != -1) || (threeLength > 0)) {
                results.numberType = NumberType.DOUBLE;
            } else {
                results.numberType = NumberType.INTEGER;
            }
        }

        if (results.numberType != NumberType.NOT_A_NUMBER) {
            // group 2 length = group 1 length - group 5 length
            results.getBeginGroup()[2] = results.getBeginGroup()[1];
            results.getEndGroup()[2] = results.getEndGroup()[1];
            if (results.getBeginGroup()[5] != -1) {
                results.getEndGroup()[2]--;
            }
        }

        // octal encoding ?
        if (results.numberType == NumberType.INTEGER) {
            if (s.charAt(results.getBeginGroup()[7]) == '0') {
                boolean octal = (sevenLength > 1);
                if (octal) {
                    for (int i = results.getBeginGroup()[7] + 1; i < results.getEndGroup()[7]; i++) {
                        if (!isOctalDigit(s.charAt(i))) {
                            octal = false;
                            break;
                        }
                    }
                }
                if (octal) {
                    if (results.getBeginGroup()[5] != -1) {
                        if (!isL(s.charAt(results.getBeginGroup()[5]))) {
                            octal = false;
                        }
                    }
                }
                if (octal) {
                    results.numberType = NumberType.OCTAL;
                }
            }
        }

        return (results.numberType != NumberType.NOT_A_NUMBER);
    }



    /**
     * Parses a binary literal (e.g., {@code 0b010101000011}).
     *
     * @param s The string from which the numeric literal should be parsed.
     * @param start The index of the string to start the parsing after the sign and 0[Bb] prefix.
     * @param end The index of the last char to parse.
     * @param results contains the parsing details calculated in this method.
     * @return true if a potential binary literal is found, false otherwise
     *
     * <p>based on Regular Expression (([-+]?)0[Bb]([01]+)([Ll]?)).*
     * group 1 = entire matching string. NB: re has .* postfix but this method does not include .* in this group.
     * group 2 [-+]?  This is processed by the caller and placed into ParseNumberResults ndxSign.
     * group none 0[Bb] must exist. This is processed by the caller.
     * group 3 [01]+
     * group 4 [lL]?
     */

    private static boolean extractBinaryNumber(String s, int start, int end, ParseNumberResults results) {
        char c;
        results.setSignGroup(2);

        int group = 3;
        results.getBeginGroup()[group] = start;

        bigWhile:
        while (start <= end) {
            if (start == end) {
                results.getEndGroup()[group] = start;
                break;
            }
            c = s.charAt(start);
            switch (group) {
                case 3: // [01]+
                    if (isBinaryDigit(c)) {
                        start++;
                    } else {
                        results.getEndGroup()[group] = start;
                        group = 4;
                    }
                    break;
                case 4: // [Ll]?
                    if (isL(c)) {
                        results.getBeginGroup()[group] = start;
                        results.getEndGroup()[group] = ++start;
                    }
                    // ok - we are done
                    break bigWhile;
            } // switch group
        } // while bigWhile
        results.getEndGroup()[1] = start;
        // need at least 1 digit in group 3
        if (results.getEndGroup()[3] - results.getBeginGroup()[3] == 0)
            results.numberType = NumberType.NOT_A_NUMBER;
        return (results.numberType == NumberType.BINARY);
    }

    /**
     * Parses a hexadecimal literal. Both hexadecimal integer (e.g.,
     * {@code 0xfedcba9876543210}) and hexadecimal floating point (e.g.,
     * {@code 0xfedcba.98765432p10f}) are supported.
     *
     * @param s The string from which the numeric literal should be parsed.
     * @param start The index of the string to start the parsing after the sign and 0[Xx] prefix.
     * @param end The index of the last char to parse.
     * @param results contains the parsing details calculated in this method.
     * @return true if a potential hexadecimal literal is found, false otherwise
     *
     *
     * <p>based on Regular Expression (([-+]?)0[Xx]([0-9a-fA-F]+)([Ll]|(\\.[0-9a-fA-F]*)?[Pp]([-+]?)([0-9]+)([DdFf]|)|)).*
     * group 1 = entire matching string. NB: re has .* postfix but this method does not include .* in this group.
     * group 2 [-+]? This is processed by the caller and placed into ParseNumberResults ndxSign.
     * group none 0[Xx]  This is processed by the caller.
     * group 3 [0-9a-fA-F]+ repeat.
     * group 4 includes groups 5, 6, 7 and 8 or [lL]?
     * group 5 (\\.[0-9a-fA-F]*)? Entire group is optional
     * group 6 requires a [Pp] prefix which is not part of the group. [-+]?
     * group 7 [0-9]+
     * group 8 [DdFf]?
     * group 9, not in re, is [Pp] which is required before groups 6, 7.
     */

    private static boolean extractHexNumber(String s, int start, int end, ParseNumberResults results) {
        char c;

        results.setSignGroup(2);

        int group = 3;
        results.getBeginGroup()[3] = start;
        bigWhile:
        while (start <= end) {
            if (start == end) {
                results.getEndGroup()[group] = start;
                break;
            }
            c = s.charAt(start); // start may not change when group changes
            switch (group) {
                case 3: // [0-9a-fA-F]+
                    if (isHexDigit(c)) {
                        start++;
                    } else {
                        results.getEndGroup()[group] = start;
                        group = 4;
                        results.getBeginGroup()[group] = start;
                    }
                    break;
                case 4: // [Ll]? group end is adjusted after switch to include groups 5-8.
                    if (isL(c)) {
                        results.getEndGroup()[group] = ++start;
                        break bigWhile; // end of re. No need to continue parsing chars
                    }
                    if (c == '.') {
                        group = 5;
                        results.getBeginGroup()[group] = start++;
                        break;
                    }
                    if (isP(c)) {
                        start++;
                        group = 9;
                        break;
                    }
                    // we are done, there is no group 4
                    results.getBeginGroup()[group] = -1;
                    break bigWhile;
                case 5: // \.[0-9a-fA-F]* 0..n
                    if (isHexDigit(c)) {
                        start++;
                    } else {
                        results.getEndGroup()[group] = start;
                        if (isP(c)) {
                            group = 9;
                            results.getBeginGroup()[group] = start;
                            results.getEndGroup()[group] = ++start;
                        }
                    }
                    break;
                case 9: // start -1 == p or P
                    if (isASign(c)) {
                        group = 6;
                        results.getBeginGroup()[group] = start;
                        results.getEndGroup()[group] = ++start;
                    }
                    group = 7;
                    break;
                case 7: // [0-9]+ enforce at least 1 char in this group
                    if (isDecimalDigit(c)) {
                        if (results.getBeginGroup()[group] == -1) results.getBeginGroup()[group] = start;
                        start++;
                    } else {
                        if (results.getBeginGroup()[group] == -1) {
                            // need at least 1 digit to be valid
                            results.numberType = NumberType.NOT_A_NUMBER;
                            break bigWhile;
                        } else {
                            results.getEndGroup()[group] = start;
                            group = 8;
                        }
                    }
                    break;
                case 8: // [DdFf]?
                    if (isADoubleOrFloatSuffix(c)) {
                        results.getBeginGroup()[8] = start;
                        results.getEndGroup()[8] = ++start;
                    }
                    break bigWhile;
            } // switch  group
        } // while bigWhile

        results.getEndGroup()[1] = start;
        results.getEndGroup()[4] = Math.max(results.getEndGroup()[4], Math.max(Math.max(results.getEndGroup()[5], results.getEndGroup()[6]), Math.max(results.getEndGroup()[7], results.getEndGroup()[8])));
        // P (in group 9) requires at least one entry in group 7
        if ((results.getBeginGroup()[9] != -1) && (results.getBeginGroup()[7] == -1))
            results.numberType = NumberType.NOT_A_NUMBER;
        // group 3 must have at least 1 digit
        if (results.getEndGroup()[3] - results.getBeginGroup()[3] == 0)
            results.numberType = NumberType.NOT_A_NUMBER;
        return (results.numberType == NumberType.HEXADECIMAL);
    }

    // process extracted results

    private static void processNumber(String in, ParseNumberResults results) {
        switch (results.numberType) {
            case INTEGER:
            case DOUBLE:
                results.setNumber(processDecimal(in, results));
                break;
            case HEXADECIMAL:
                results.setNumber(processHex(in, results));
                break;
            case BINARY:
                results.setNumber(processBinary(in, results));
                break;
            case OCTAL:
                results.setNumber(processOctal(in, results));
                break;
        }
    }

    private static Number processHex(final String s, ParseNumberResults results) {
        final String sign = results.getGroup(s, 2); // + or - or ""
        final String integer = results.getGroup(s, 3); // hex digits before decimal point
        final String suffix = results.getGroup(s, 4);  // L or floating point expression
        final boolean forceLong = "L".equalsIgnoreCase(String.valueOf(suffix));

        final Number result;
        if (forceLong || suffix.isEmpty()) {
            // Integer notation.
            final String number = sign + integer;
            result = parseIntegerToNumber(number, forceLong, 16);
        } else {
            // Floating point notation.
            final String token = results.getGroup(s, 1);       // entire matched literal
            final String expSuffix = results.getGroup(s, 8);   // f or d or nothing
            final boolean forceFloat = "F".equalsIgnoreCase(expSuffix);
            final boolean forceDouble = "D".equalsIgnoreCase(expSuffix);
            // NB: The BigDecimal code does not understand floating point
            // hex strings, so the following invocation will never produce
            // a larger-than-double-precision floating point BigDecimal.
            // It's a convenient way to support float and double precision,
            // but for BigDecimal support, we would need to process the
            // matched groups above, converting hex to base 10 first.
            result = parseDecimalToNumber(token, forceFloat, forceDouble);
        }
        return result;//verifyResult(result, m, pos);
    }

    private static Number processBinary(String s, ParseNumberResults results) {
        final String sign = results.getGroup(s, 2);
        final String number = sign + results.getGroup(s, 3);

        final boolean forceLong = !results.getGroup(s, 4).isEmpty();
        return parseIntegerToNumber(number, forceLong, 2);
    }

    // NB: uses decimal regular expression group numbers
    private static Number processOctal(String s, ParseNumberResults results) {
        final String number = results.getGroup(s, 2);
        final String expSuffix = results.getGroup(s, 5);   // Ll nothing
        final boolean forceLong = "L".equalsIgnoreCase(expSuffix);
        return parseIntegerToNumber(number, forceLong, 8);
    }

    private static Number processDecimal(String s, ParseNumberResults results) {
        final String numberStr = results.getGroup(s, 2);
        final String force = results.getGroup(s, 5);
        final boolean forceLong = "l".equalsIgnoreCase(force);
        final boolean forceFloat = "f".equalsIgnoreCase(force);
        final boolean forceDouble = "d".equalsIgnoreCase(force);
        Number result = null;
        if (results.numberType == NumberType.INTEGER) {
            // No decimal point and no exponent part. So this *might* be an integer!
            result = parseIntegerToNumber(numberStr, forceLong, 10);
        }
        if (result == null && !forceLong) {
            result = parseDecimalToNumber(numberStr, forceFloat, forceDouble);
        }
        return result;
    }


    // parse resulting Strings and flags to Number

    /**
     * Parses an Integer literal (e.g., {@code 1234}).
     * Attempts to minimize swallowed Exceptions by parsing as a long first.
     *
     * @param number The string from which the numeric literal should be parsed.
     * @param forceLong boolean flag to require a long Number be returned
     * @param base radix for conversion
     * @return The parsed numeric value&mdash;an {@link Integer} if sufficiently
     *         small, or a {@link Long} if needed or if the {@code L} suffix is
     *         given; or a {@link BigInteger} if the value is too large even for
     *         {@code long} or null.
     */

    private static Number parseIntegerToNumber(final String number,
                                               final boolean forceLong, final int base) {

        // parse to long, convert to int if in range and not forced to a long
        long result ;
        try {
            result = Long.parseLong(number, base);
            if (forceLong || result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
                return result;
            }
            return (int) result; // NB: range check above to avoid silent loss
        }  catch (NumberFormatException e) {
            // will not fit in long or bad text
            ignoredExceptions++; // not thread safe, ok for rough metric
            if (!forceLong) {
                try {
                    return new BigInteger(number, base);
                } catch (final NumberFormatException exc) {
                    ignoredExceptions++;
                }
            }
        }
        return null;
    }


    /**
     * Parses a Decimal literal (e.g., {@code 1234.1}).
     *
     * @param number The string from which the numeric literal should be parsed.
     * @param forceFloat boolean flag to require a float Number be returned
     * @param forceDouble boolean flag to require a double Number be returned
     * @return The parsed numeric value&mdash;an {@link Double}, {@link Float} or {@link BigDecimal} or null
     */
    private static Number parseDecimalToNumber(final String number,
                                               final boolean forceFloat, final boolean forceDouble) {
        if (forceFloat) {
            // Try to fit it into a float.
            try {
                return Float.parseFloat(number);
            } catch (final NumberFormatException exc) {
                // NB: No action needed.
                ignoredExceptions++;
            }
        } else {
            // Try to fit it into a double.
            try {
                return Double.parseDouble(number);
            } catch (final NumberFormatException exc) {
                // NB: No action needed.
                ignoredExceptions++;
            }
        }

        if (!forceDouble && !forceFloat) {
            // Try to treat it as a BigDecimal.
            try {
                return new BigDecimal(number);
            } catch (final NumberFormatException exc) {
                // NB: No action needed.
                ignoredExceptions++;
            }
        }

        return null;
    }

    // Helpers

    private static boolean isHexDigit(char c) {
        return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
    }

    private static boolean isOctalDigit(char c) {
        return (c >= '0' && c <= '7');
    }

    private static boolean isL(char c) {
        return (c == 'L' || c == 'l');
    }

    private static boolean isP(char c) {
        return (c == 'P' || c == 'p');
    }

    private static boolean isE(char c) {
        return (c == 'E' || c == 'e');
    }

    private static boolean isDecimalDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private static boolean isBinaryDigit(char c) {
        return (c == '0' || c == '1');
    }

    private static boolean isASign(char c) {
        return (c == '+' || c == '-');
    }

    private static boolean isADoubleOrFloatSuffix(char c) {
        return (c == 'D' || c == 'd' || c == 'F' || c == 'f');
    }

    public enum NumberType {
        NOT_A_NUMBER,
        INTEGER, // or LONG or BIGINT
        DOUBLE, // or FLOAT
        HEXADECIMAL,
        OCTAL, // INTEGER or LONG
        BINARY, // INTEGER, DOUBLE, FLOAT
    }


}
