package me.szypko.codeweaver.processor;

import java.util.Map;

public class ExpressionEvaluator {

  private String expr;
  private int pos;
  private Map<String, Boolean> flags;

  public boolean evaluate(final String expression, final Map<String, Boolean> flags) {
    this.expr = expression.trim();
    this.pos = 0;
    this.flags = flags;
    return parseOr();
  }

  private boolean parseOr() {
    boolean result = parseAnd();
    while (pos < expr.length() && expr.startsWith("||", skipSpaces())) {
      pos = skipSpaces() + 2;
      result = parseAnd() || result;
    }
    return result;
  }

  private boolean parseAnd() {
    boolean result = parseNot();
    while (pos < expr.length() && expr.startsWith("&&", skipSpaces())) {
      pos = skipSpaces() + 2;
      result = parseNot() && result;
    }
    return result;
  }

  private boolean parseNot() {
    pos = skipSpaces();
    if (pos < expr.length() && expr.charAt(pos) == '!') {
      pos++;
      return !parsePrimary();
    }
    return parsePrimary();
  }

  private boolean parsePrimary() {
    pos = skipSpaces();
    if (pos < expr.length() && expr.charAt(pos) == '(') {
      pos++;
      final boolean result = parseOr();
      pos = skipSpaces();
      if (pos < expr.length() && expr.charAt(pos) == ')') pos++;
      return result;
    }
    return parseFlag();
  }

  private boolean parseFlag() {
    pos = skipSpaces();
    final int start = pos;
    while (pos < expr.length() && isIdentifierChar(expr.charAt(pos))) pos++;
    final String name = expr.substring(start, pos);
    return flags.getOrDefault(name, false);
  }

  private int skipSpaces() {
    int i = pos;
    while (i < expr.length() && expr.charAt(i) == ' ') i++;
    return i;
  }

  private boolean isIdentifierChar(final char c) {
    return Character.isLetterOrDigit(c) || c == '_';
  }
}
