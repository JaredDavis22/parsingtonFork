/*
 * #%L
 * Parsington: the SciJava mathematical expression parser.
 * %%
 * Copyright (C) 2015 - 2026 Board of Regents of the University of
 * Wisconsin-Madison.
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

package org.scijava.parsington.eval;

import org.scijava.parsington.Function;
import org.scijava.parsington.Operator;
import org.scijava.parsington.Operators;
import org.scijava.parsington.Tokens;
import org.scijava.parsington.Variable;

/**
 * Interface for expression evaluators which support the {@link Operators
 * standard operators}.
 *
 * @author Curtis Rueden
 */
public interface StandardEvaluator extends Evaluator {

	// -- function --

	/**
	 * Applies the {@link Function} operator.
	 * 
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object function(Object a, Object b);

	// -- dot --

	/**
	 * Applies the {@link Operators#DOT} operator.
	 * 
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object dot(Object a, Object b);

	// -- groups --

	/**
	 * Applies the {@link Operators#PARENS} operator.
	 * 
	 * @param args The arguments.
	 * @return The result of the operation.
	 */
	Object parens(Object... args);

	/**
	 * Applies the {@link Operators#BRACKETS} operator.
	 * 
	 * @param args The arguments.
	 * @return The result of the operation.
	 */
	Object brackets(Object... args);

	/**
	 * Applies the {@link Operators#BRACES} operator.
	 * 
	 * @param args The arguments.
	 * @return The result of the operation.
	 */
	Object braces(Object... args);

	// -- transpose, power --

	/**
	 * Applies the {@link Operators#TRANSPOSE} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object transpose(Object a);

	/**
	 * Applies the {@link Operators#DOT_TRANSPOSE} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object dotTranspose(Object a);

	/**
	 * Applies the {@link Operators#POW} operator.
	 * 
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object pow(Object a, Object b);

	/**
	 * Applies the {@link Operators#DOT_POW} operator.
	 * 
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object dotPow(Object a, Object b);

	// -- postfix --

	/**
	 * Applies the {@link Operators#POST_INC} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	default Object postInc(final Object a) {
		final Variable v = var(a);
		final Object value = value(v);
		if (value == null) return null;
		set(v, add(a, 1));
		return value;
	}

	/**
	 * Applies the {@link Operators#POST_DEC} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	default Object postDec(final Object a) {
		final Variable v = var(a);
		final Object value = value(v);
		if (value == null) return null;
		set(v, sub(a, 1));
		return value;
	}

	// -- unary --

	/**
	 * Applies the {@link Operators#PRE_INC} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	default Object preInc(final Object a) {
		final Variable v = var(a);
		final Object result = add(a, 1);
		set(v, result);
		return result;
	}

	/**
	 * Applies the {@link Operators#PRE_DEC} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	default Object preDec(final Object a) {
		final Variable v = var(a);
		final Object result = sub(a, 1);
		set(v, result);
		return result;
	}

	/**
	 * Applies the {@link Operators#POS} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object pos(Object a);

	/**
	 * Applies the {@link Operators#NEG} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object neg(Object a);

	/**
	 * Applies the {@link Operators#COMPLEMENT} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object complement(Object a);

	/**
	 * Applies the {@link Operators#NOT} operator.
	 * 
	 * @param a The argument.
	 * @return The result of the operation.
	 */
	Object not(Object a);

	// -- multiplicative --

	/**
	 * Applies the {@link Operators#MUL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object mul(Object a, Object b);

	/**
	 * Applies the {@link Operators#DIV} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object div(Object a, Object b);

	/**
	 * Applies the {@link Operators#MOD} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object mod(Object a, Object b);

	/**
	 * Applies the {@link Operators#RIGHT_DIV} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object rightDiv(Object a, Object b);

	/**
	 * Applies the {@link Operators#DOT_MUL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object dotMul(Object a, Object b);

	/**
	 * Applies the {@link Operators#DOT_DIV} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object dotDiv(Object a, Object b);

	/**
	 * Applies the {@link Operators#DOT_RIGHT_DIV} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object dotRightDiv(Object a, Object b);

	// -- additive --

	/**
	 * Applies the {@link Operators#ADD} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object add(Object a, Object b);

	/**
	 * Applies the {@link Operators#SUB} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object sub(Object a, Object b);

	// -- shift --

	/**
	 * Applies the {@link Operators#LEFT_SHIFT} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object leftShift(Object a, Object b);

	/**
	 * Applies the {@link Operators#RIGHT_SHIFT} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object rightShift(Object a, Object b);

	/**
	 * Applies the {@link Operators#UNSIGNED_RIGHT_SHIFT} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object unsignedRightShift(Object a, Object b);

	// -- relational --

	/**
	 * Applies the {@link Operators#LESS_THAN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object lessThan(Object a, Object b);

	/**
	 * Applies the {@link Operators#GREATER_THAN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object greaterThan(Object a, Object b);

	/**
	 * Applies the {@link Operators#LESS_THAN_OR_EQUAL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object lessThanOrEqual(Object a, Object b);

	/**
	 * Applies the {@link Operators#GREATER_THAN_OR_EQUAL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object greaterThanOrEqual(Object a, Object b);

	/**
	 * Applies the {@link Operators#INSTANCEOF} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object instanceOf(Object a, Object b);

	// -- equality --

	/**
	 * Applies the {@link Operators#EQUAL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object equal(Object a, Object b);

	/**
	 * Applies the {@link Operators#NOT_EQUAL} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object notEqual(Object a, Object b);

	// -- bitwise --

	/**
	 * Applies the {@link Operators#BITWISE_AND} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object bitwiseAnd(Object a, Object b);

	/**
	 * Applies the {@link Operators#BITWISE_OR} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object bitwiseOr(Object a, Object b);

	// -- logical --

	/**
	 * Applies the {@link Operators#LOGICAL_AND} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object logicalAnd(Object a, Object b);

	/**
	 * Applies the {@link Operators#LOGICAL_OR} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object logicalOr(Object a, Object b);

	// -- ternary --

	/**
	 * Applies the {@link Operators#QUESTION} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object question(Object a, Object b);

	/**
	 * Applies the {@link Operators#COLON} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	Object colon(Object a, Object b);

	// -- assignment --

	/**
	 * Applies the {@link Operators#ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object assign(final Object a, final Object b) {
		final Variable v = var(a);
		set(v, value(b));
		return v;
	}

	/**
	 * Applies the {@link Operators#POW_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object powAssign(final Object a, final Object b) {
		return assign(a, pow(a, b));
	}

	/**
	 * Applies the {@link Operators#DOT_POW_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object dotPowAssign(final Object a, final Object b) {
		return assign(a, dotPow(a, b));
	}

	/**
	 * Applies the {@link Operators#MUL_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object mulAssign(final Object a, final Object b) {
		return assign(a, mul(a, b));
	}

	/**
	 * Applies the {@link Operators#DIV_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object divAssign(final Object a, final Object b) {
		return assign(a, div(a, b));
	}

	/**
	 * Applies the {@link Operators#MOD_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object modAssign(final Object a, final Object b) {
		return assign(a, mod(a, b));
	}

	/**
	 * Applies the {@link Operators#RIGHT_DIV_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object rightDivAssign(final Object a, final Object b) {
		return assign(a, rightDiv(a, b));
	}

	/**
	 * Applies the {@link Operators#DOT_DIV_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object dotDivAssign(final Object a, final Object b) {
		return assign(a, dotDiv(a, b));
	}

	/**
	 * Applies the {@link Operators#DOT_RIGHT_DIV_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object dotRightDivAssign(final Object a, final Object b) {
		return assign(a, dotRightDiv(a, b));
	}

	/**
	 * Applies the {@link Operators#ADD_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object addAssign(final Object a, final Object b) {
		return assign(a, add(a, b));
	}

	/**
	 * Applies the {@link Operators#SUB_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object subAssign(final Object a, final Object b) {
		return assign(a, sub(a, b));
	}

	/**
	 * Applies the {@link Operators#AND_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object andAssign(final Object a, final Object b) {
		return assign(a, bitwiseAnd(a, b));
	}

	/**
	 * Applies the {@link Operators#OR_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object orAssign(final Object a, final Object b) {
		return assign(a, bitwiseOr(a, b));
	}

	/**
	 * Applies the {@link Operators#LEFT_SHIFT_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object leftShiftAssign(final Object a, final Object b) {
		return assign(a, leftShift(a, b));
	}

	/**
	 * Applies the {@link Operators#RIGHT_SHIFT_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object rightShiftAssign(final Object a, final Object b) {
		return assign(a, rightShift(a, b));
	}

	/**
	 * Applies the {@link Operators#UNSIGNED_RIGHT_SHIFT_ASSIGN} operator.
	 *
	 * @param a The first argument.
	 * @param b The second argument.
	 * @return The result of the operation.
	 */
	default Object unsignedRightShiftAssign(final Object a, final Object b) {
		return assign(a, unsignedRightShift(a, b));
	}

	/**
	 * Performs an operation.
	 *
	 * @param op The operator to execute.
	 * @param args The arguments to pass.
	 * @return The result of the operation.
	 */
	default Object execute(final Operator op, final Object... args) {
		final Object a = args.length > 0 ? args[0] : null;
		final Object b = args.length > 1 ? args[1] : null;

		// Let the case logic begin!
		if (op instanceof Function) return function(a, b);
		if (Tokens.isMatchingGroup(op, Operators.PARENS)) return parens(args);
		if (Tokens.isMatchingGroup(op, Operators.BRACKETS)) return brackets(args);
		if (Tokens.isMatchingGroup(op, Operators.BRACES)) return braces(args);

		switch (op.getToken()+op.getArity()+op.getAssociativity()) {
			case "*2LEFT":
				return mul(a, b);
			case "/2LEFT":
				return div(a, b);
			case "%2LEFT":
				return mod(a, b);
			case "+2LEFT":
				return add(a, b);
			case "-2LEFT":
				return sub(a, b);
			case "\\2LEFT":
				return rightDiv(a, b);
			case "^2RIGHT":
				return pow(a, b);
			case "=2RIGHT":
				return assign(a, b);
			case "==2LEFT":
				return equal(a, b);
			case "!=2LEFT":
				return notEqual(a, b);
			case "<2LEFT":
				return lessThan(a, b);
			case ">2LEFT":
				return greaterThan(a, b);
			case "<=2LEFT":
				return lessThanOrEqual(a, b);
			case ">=2LEFT":
				return greaterThanOrEqual(a, b);
			case "+1RIGHT":
				return pos(a);
			case "-1RIGHT":
				return neg(a);
			case "~1RIGHT":
				return complement(a);
			case "!1RIGHT":
				return not(a);
			case "*=2RIGHT":
				return mulAssign(a, b);
			case "/=2RIGHT":
				return divAssign(a, b);
			case "<<2LEFT":
				return leftShift(a, b);
			case ">>2LEFT":
				return rightShift(a, b);
			case ">>>2LEFT":
				return unsignedRightShift(a, b);
			case "&2LEFT":
				return bitwiseAnd(a, b);
			case "|2LEFT":
				return bitwiseOr(a, b);
			case "&&2LEFT":
				return logicalAnd(a, b);
			case "||2LEFT":
				return logicalOr(a, b);
			case "^=2RIGHT":
				return powAssign(a, b);
			case "%=2RIGHT":
				return modAssign(a, b);
			case "\\=2RIGHT":
				return rightDivAssign(a, b);
			case "+=2RIGHT":
				return addAssign(a, b);
			case "-=2RIGHT":
				return subAssign(a, b);
			case "&=2RIGHT":
				return andAssign(a, b);
			case "|=2RIGHT":
				return orAssign(a, b);
			case "<<=RIGHT":
				return leftShiftAssign(a, b);
			case ">>=RIGHT":
				return rightShiftAssign(a, b);
			case ">>>=RIGHT":
				return unsignedRightShiftAssign(a, b);
			case "++1LEFT":
				return postInc(a);
			case "--1LEFT":
				return postDec(a);
			case "++1RIGHT":
				return preInc(a);
			case "--1RIGHT":
				return preDec(a);
			case ".2LEFT":
				return dot(a, b);
			case "'1LEFT":
				return transpose(a);
			case ".'1LEFT":
				return dotTranspose(a);
			case ".^2RIGHT":
				return dotPow(a, b);
			case "./=2RIGHT'":
				return dotDivAssign(a, b);
			case ".\\=2RIGHT'":
				return dotRightDivAssign(a, b);
			case ".*2LEFT":
				return dotMul(a, b);
			case "./=2LEFT":
				return dotDiv(a, b);
			case ".\\=2LEFT":
				return dotRightDiv(a, b);
			case ".^=2RIGHT":
				return dotPowAssign(a, b);
			case "instanceof2LEFT":
				return instanceOf(a, b);
			case "?2LEFT":
				return question(a, b);
			case ":2LEFT":
				return colon(a, b);
			default:
				throw new UnsupportedOperationException(op.toString() + " is not supported with arity "+ op.getArity() + " and association "+ op.getAssociativity());
		}
	}
}
