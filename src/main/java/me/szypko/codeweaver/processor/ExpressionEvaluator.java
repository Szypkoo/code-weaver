package me.szypko.codeweaver.processor;

import java.util.Map;

public class ExpressionEvaluator {
    public boolean evaluate(final String expression, final Map<String, Boolean> flags) {
        return parseOr(expression.trim(), flags);
    }

    private boolean parseOr(final String expr, final Map<String, Boolean> flags) {
        final String[] parts = expr.split("\\|\\|");

        for (final String part : parts) {
            if (parseAnd(part.trim(), flags)) {
                return true;
            }
        }

        return false;
    }

    private boolean parseAnd(final String expr, final Map<String, Boolean> flags) {
        final String[] parts = expr.split("&&");

        for (final String part : parts) {
            if (!parseNot(part.trim(), flags)) {
                return false;
            }
        }

        return true;
    }

    private boolean parseNot(final String expr, final Map<String, Boolean> flags) {
        if (expr.startsWith("!")) {
            return !flags.getOrDefault(expr.substring(1).trim(), false);
        }

        return flags.getOrDefault(expr, false);
    }
}
