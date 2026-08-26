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

/**
 * Identifies which of the {@link Operators} constants a given {@link
 * Operator} is, allowing consumers (e.g. an evaluator) to dispatch on an
 * {@code enum} {@code switch} rather than a chain of reference-equality
 * checks or a separate lookup table.
 * <p>
 * Every {@link Operators} constant except {@link Operators#PARENS}, {@link
 * Operators#BRACKETS} and {@link Operators#BRACES} has a corresponding
 * value here; those three groups are matched structurally via {@link
 * Group#matches} instead, since group tokens are not singletons.
 * An {@link Operator} constructed via the public constructor&mdash;e.g. a
 * custom operator belonging to a non-standard grammar&mdash;has a null
 * {@link Operator#getKind() kind}, since it is not one of the standard
 * operators.
 * </p>
 *
 */
public enum OperatorKind {
	DOT,
	TRANSPOSE,
	DOT_TRANSPOSE,
	POW,
	DOT_POW,
	POST_INC,
	POST_DEC,
	PRE_INC,
	PRE_DEC,
	POS,
	NEG,
	COMPLEMENT,
	NOT,
	MUL,
	DIV,
	MOD,
	RIGHT_DIV,
	DOT_MUL,
	DOT_DIV,
	DOT_RIGHT_DIV,
	ADD,
	SUB,
	LEFT_SHIFT,
	RIGHT_SHIFT,
	UNSIGNED_RIGHT_SHIFT,
	LESS_THAN,
	GREATER_THAN,
	LESS_THAN_OR_EQUAL,
	GREATER_THAN_OR_EQUAL,
	INSTANCEOF,
	EQUAL,
	NOT_EQUAL,
	BITWISE_AND,
	BITWISE_OR,
	LOGICAL_AND,
	LOGICAL_OR,
	QUESTION,
	COLON,
	ASSIGN,
	POW_ASSIGN,
	DOT_POW_ASSIGN,
	MUL_ASSIGN,
	DIV_ASSIGN,
	MOD_ASSIGN,
	RIGHT_DIV_ASSIGN,
	DOT_DIV_ASSIGN,
	DOT_RIGHT_DIV_ASSIGN,
	ADD_ASSIGN,
	SUB_ASSIGN,
	AND_ASSIGN,
	OR_ASSIGN,
	LEFT_SHIFT_ASSIGN,
	RIGHT_SHIFT_ASSIGN,
	UNSIGNED_RIGHT_SHIFT_ASSIGN
}
