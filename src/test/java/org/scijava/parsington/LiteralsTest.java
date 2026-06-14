/*
 * #%L
 * Parsington: the SciJava mathematical expression parser.
 * %%
 * Copyright (C) 2015 - 2026 Parsington developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.parsington;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Literals}.
 *
 * @author Curtis Rueden
 */
public class LiteralsTest extends AbstractTest {

	@Test
	public void testParseBoolean() {
		Literals literals = new Literals();
		assertSame(Boolean.FALSE, literals.parseBoolean("false"));
		assertSame(Boolean.TRUE, literals.parseBoolean("true"));

		assertNull(literals.parseBoolean("zfalse"));
		assertNull(literals.parseBoolean("zfalsez"));
		assertNull(literals.parseBoolean("ztrue"));
		assertNull(literals.parseBoolean("ztruez"));

		final Position pos = new Position();
		pos.set(0);
		assertSame(Boolean.FALSE, literals.parseBoolean("false-", pos));
		assertEquals(5, pos.get());
		pos.set(0);
		assertSame(Boolean.TRUE, literals.parseBoolean("true-", pos));
		assertEquals(4, pos.get());
	}

	@Test
	public void testParseString() {
		Literals literals = new Literals();
		assertEquals("hello world", literals.parseString("'hello world'"));
		// Test escape sequences.
		assertEquals("a\b\t\n\f\r\"\\z", literals
			.parseString("\"a\\b\\t\\n\\f\\r\\\"\\\\z\""));
		assertEquals("\t\\\t\\\\\t", literals
			.parseString("\"\\t\\\\\\t\\\\\\\\\\t\""));
		// Test Unicode escape sequences.
		assertEquals("\u9654", literals.parseString("\"\u9654\""));
		assertEquals("xyz\u9654abc", literals.parseString("\"xyz\\u9654abc\""));
		// Test octal escape sequences.
		assertEquals("\0", literals.parseString("\"\\0\""));
		assertEquals("\00", literals.parseString("\"\\00\""));
		assertEquals("\000", literals.parseString("\"\\000\""));
		assertEquals("\12", literals.parseString("\"\\12\""));
		assertEquals("\123", literals.parseString("\"\\123\""));
		assertEquals("\377", literals.parseString("\"\\377\""));
		assertEquals("\1234", literals.parseString("\"\\1234\""));
		// Test position
		final Position pos = new Position();
		pos.set(2);
		assertEquals("cde", literals.parseString("ab'cde'fg", pos));
		assertEquals(7, pos.get());
	}

	@Test
	public void testParseStringInvalid() {
		// Test non-string tokens.
		Literals literals = new Literals();
		assertNull(literals.parseString(""));
		assertNull(literals.parseString("1234"));
		assertNull(literals.parseString("foo"));
		assertNull(literals.parseString("a'b'c"));
		// Test malformed string literals.
		try {
			literals.parseString("'");
			fail("IllegalArgumentException expected");
		}
		catch (final IllegalArgumentException exc) {
			assertEquals("Unclosed string literal at index 0", exc.getMessage());
		}
	}

	@Test
	public void testParseHexInteger() {
		Literals literals = new Literals();
		assertNumber(0x123, literals.parseHex("0x123"));
		// Test explicit long.
		assertNumber(0x123L, literals.parseHex("0x123L"));
		// Test implicit long.
		assertNumber(0x123456789abcdefL, literals.parseHex("0x123456789abcdef"));
		// Test BigInteger.
		final String big = "123456789abcdeffedcba987654321";
		final Number bigNum = literals.parseHex("0x" + big);
		assertNumber(new BigInteger(big, 16), bigNum);
	}

	@Test
	public void testParseHexNegativeInteger() {
		Literals literals = new Literals();
		assertNumber(-0x123, literals.parseHex("-0x123"));
		// Test explicit long.
		assertNumber(-0x123L, literals.parseHex("-0x123L"));
		// Test implicit long.
		assertNumber(-0x123456789abcdefL, literals.parseHex("-0x123456789abcdef"));
		// Test BigInteger.
		final String big = "123456789abcdeffedcba987654321";
		final Number bigNum = literals.parseHex("-0x" + big);
		assertNumber(new BigInteger("-" + big, 16), bigNum);
	}

	@Test
	public void testParseHexFloat() {
		Literals literals = new Literals();
		assertNumber(0xfedcba.98765432P-10f, //
			literals.parseHex("0xfedcba.98765432P-10f"));
		assertNumber(0x1.fffffffffffffP+1023, //
			literals.parseHex("0x1.fffffffffffffP+1023"));
		assertNumber(0xff.fP0F, literals.parseHex("0xff.fP0F"));
		assertNumber(0x1P+1023, literals.parseHex("0x1P+1023"));
		assertNumber(0xfP102, literals.parseHex("0xfP102"));
		assertNumber(0xfP-102, literals.parseHex("0xfP-102"));
		assertNumber(0xffP-102, literals.parseHex("0xffP-102"));
		assertNumber(0x123.456P1, literals.parseHex("0x123.456P1"));
		assertNumber(0x123.456P0f, literals.parseHex("0x123.456P0f"));
		assertNumber(0x123.456P0d, literals.parseHex("0x123.456P0d"));
		assertNumber(0x123.456P-10d, literals.parseHex("0x123.456P-10d"));
		assertNumber(0x123.456P+009, literals.parseHex("0x123.456P+009"));
	}

	@Test
	public void testParseHexNegativeFloat() {
		Literals literals = new Literals();
		assertNumber(-0xfedcba.98765432P-10f, //
			literals.parseHex("-0xfedcba.98765432P-10f"));
		assertNumber(-0x1.fffffffffffffP+1023, //
			literals.parseHex("-0x1.fffffffffffffP+1023"));
		assertNumber(-0xff.fP0F, literals.parseHex("-0xff.fP0F"));
		assertNumber(-0x1P+1023, literals.parseHex("-0x1P+1023"));
		assertNumber(-0xfP102, literals.parseHex("-0xfP102"));
		assertNumber(-0xfP-102, literals.parseHex("-0xfP-102"));
		assertNumber(-0xffP-102, literals.parseHex("-0xffP-102"));
		assertNumber(-0x123.456P1, literals.parseHex("-0x123.456P1"));
		assertNumber(-0x123.456P0f, literals.parseHex("-0x123.456P0f"));
		assertNumber(-0x123.456P0d, literals.parseHex("-0x123.456P0d"));
		assertNumber(-0x123.456P-10d, literals.parseHex("-0x123.456P-10d"));
		assertNumber(-0x123.456P+009, literals.parseHex("-0x123.456P+009"));
	}

	@Test
	public void testParseBinary() {
		Literals literals = new Literals();
		// NB: "0b..." syntax is only supported starting with Java 7.
		assertNumber(33, literals.parseBinary("0b100001"));
		// Test explicit long.
		assertNumber(33L, literals.parseBinary("0b100001L"));
		// Test implicit long.
		assertNumber(194588677707L, literals.parseBinary(
			"0b10110101001110011000111001011001001011"));
		// Test BigInteger.
		final String big =
			"10110011100011110000111110000011111100000011111110000000"
				+ "111111110000000011111111100000000011111111110000000000";
		final Number bigNum = literals.parseBinary("0b" + big);
		assertNumber(new BigInteger(big, 2), bigNum);
	}

	@Test
	public void testParseBinaryNegative() {
		Literals literals = new Literals();
		// NB: "0b..." syntax is only supported starting with Java 7.
		assertNumber(-33, literals.parseBinary("-0b100001"));
		// Test explicit long.
		assertNumber(-33L, literals.parseBinary("-0b100001L"));
		// Test implicit long.
		assertNumber(-194588677707L, literals.parseBinary(
			"-0b10110101001110011000111001011001001011"));
		// Test BigInteger.
		final String big =
			"10110011100011110000111110000011111100000011111110000000"
				+ "111111110000000011111111100000000011111111110000000000";
		final Number bigNum = literals.parseBinary("-0b" + big);
		assertNumber(new BigInteger("-" + big, 2), bigNum);
	}

	@Test
	public void testParseOctal() {
		Literals literals = new Literals();
		assertNumber(01234567, literals.parseOctal("01234567"));
		// Test explicit long.
		assertNumber(01234567L, literals.parseOctal("01234567L"));
		// Test implicit long.
		assertNumber(012345677654321L, literals.parseOctal("012345677654321"));
		// Test BigInteger.
		final String big = "1234567765432112345677654321";
		final Number bigNum = literals.parseOctal("0" + big);
		assertNumber(new BigInteger(big, 8), bigNum);
	}

	@Test
	public void testParseOctalNegative() {
		Literals literals = new Literals();
		assertNumber(-01234567, literals.parseOctal("-01234567"));
		// Test explicit long.
		assertNumber(-01234567L, literals.parseOctal("-01234567L"));
		// Test implicit long.
		assertNumber(-012345677654321L, literals.parseOctal("-012345677654321"));
		// Test BigInteger.
		final String big = "1234567765432112345677654321";
		final Number bigNum = literals.parseOctal("-0" + big);
		assertNumber(new BigInteger("-" + big, 8), bigNum);
	}

	@Test
	public void testParseDecimal() {
		Literals literals = new Literals();
		assertNumber(123456789, literals.parseDecimal("123456789"));
		// Test explicit long.
		assertNumber(123456789L, literals.parseDecimal("123456789L"));
		// Test implicit long.
		assertNumber(123456787654321L, literals.parseDecimal("123456787654321"));
		// Test BigInteger.
		final String bigI = "1234567898765432123456789";
		final Number bigInt = literals.parseDecimal(bigI);
		assertNumber(new BigInteger(bigI), bigInt);
		// Test explicit float.
		assertNumber(1f, literals.parseDecimal("1f"));
		// Test explicit double.
		assertNumber(1d, literals.parseDecimal("1d"));
		// Test implicit double.
		assertNumber(1.0, literals.parseDecimal("1.0"));
		assertNumber(1., literals.parseDecimal("1."));
		// Test scientific notation.
		assertNumber(1e2, literals.parseDecimal("1e2"));
		assertNumber(1.2e3, literals.parseDecimal("1.2e3"));
		assertNumber(4.5e-6, literals.parseDecimal("4.5e-6"));
		assertNumber(1.2e3f, literals.parseDecimal("1.2e3f"));
		assertNumber(4.5e-6f, literals.parseDecimal("4.5e-6f"));
	}

	@Test
	public void testParseDecimalNegative() {
		Literals literals = new Literals();
		assertNumber(-123456789, literals.parseDecimal("-123456789"));
		// Test explicit long.
		assertNumber(-123456789L, literals.parseDecimal("-123456789L"));
		// Test implicit long.
		assertNumber(-123456787654321L, literals.parseDecimal("-123456787654321"));
		// Test BigInteger.
		final String bigI = "-1234567898765432123456789";
		final Number bigInt = literals.parseDecimal(bigI);
		assertNumber(new BigInteger(bigI), bigInt);
		// Test explicit float.
		assertNumber(-1f, literals.parseDecimal("-1f"));
		// Test explicit double.
		assertNumber(-1d, literals.parseDecimal("-1d"));
		// Test implicit double.
		assertNumber(-1.0, literals.parseDecimal("-1.0"));
		assertNumber(-1., literals.parseDecimal("-1."));
		// Test scientific notation.
		assertNumber(-1e2, literals.parseDecimal("-1e2"));
		assertNumber(-1.2e3, literals.parseDecimal("-1.2e3"));
		assertNumber(-4.5e-6, literals.parseDecimal("-4.5e-6"));
		assertNumber(-1.2e3f, literals.parseDecimal("-1.2e3f"));
		assertNumber(-4.5e-6f, literals.parseDecimal("-4.5e-6f"));
	}

	@Test
	public void testParseNumber() {
		Literals literals = new Literals();
		final Position pos = new Position();

		assertNumber(0, literals.parseNumber("0", pos));
		assertEquals(1, pos.get());

		pos.set(1);
		assertNumber(5.7, literals.parseNumber("a5.7a", pos));
		assertEquals(4, pos.get());

		pos.set(2);
		assertNumber(-11, literals.parseNumber("bb-11bb", pos));
		assertEquals(5, pos.get());

		pos.set(3);
		assertNumber(0x123L, literals.parseNumber("ccc0x123Lccc", pos));
		assertEquals(9, pos.get());
	}

	@Test
	public void testParseLiteral() {
		Literals literals = new Literals();
		assertSame(Boolean.FALSE, literals.parseLiteral("false"));
		assertSame(Boolean.TRUE, literals.parseLiteral("true"));

		assertEquals("fubar", literals.parseLiteral("'fubar'"));

		assertNumber(0, literals.parseLiteral("0"));
	}

}
