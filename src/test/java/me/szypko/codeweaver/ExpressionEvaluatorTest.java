package me.szypko.codeweaver;

import me.szypko.codeweaver.processor.ExpressionEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ExpressionEvaluatorTest {
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Test
    void singleFlagTrue() {
        Assertions.assertTrue(evaluator.evaluate("A", Map.of("A", true)));
    }

    @Test
    void singleFlagFalse() {
        Assertions.assertFalse(evaluator.evaluate("A", Map.of("A", false)));
    }

    @Test
    void negation() {
        Assertions.assertTrue(evaluator.evaluate("!A", Map.of("A", false)));
        Assertions.assertFalse(evaluator.evaluate("!A", Map.of("A", true)));
    }

    @Test
    void and() {
        Assertions.assertTrue(evaluator.evaluate("A && B", Map.of("A", true, "B", true)));
        Assertions.assertFalse(evaluator.evaluate("A && B", Map.of("A", true, "B", false)));
    }

    @Test
    void or() {
        Assertions.assertTrue(evaluator.evaluate("A || B", Map.of("A", false, "B", true)));
        Assertions.assertFalse(evaluator.evaluate("A || B", Map.of("A", false, "B", false)));
    }

    @Test
    void andBeforeOr() {
        Assertions.assertTrue(evaluator.evaluate("A && B || C", Map.of("A", false, "B", true, "C", true)));
        Assertions.assertFalse(evaluator.evaluate("A && B || C", Map.of("A", false, "B", true, "C", false)));
    }

    @Test
    void unknownFlagTreatedAsFalse() {
        Assertions.assertFalse(evaluator.evaluate("UNKNOWN", Map.of()));
    }
}
