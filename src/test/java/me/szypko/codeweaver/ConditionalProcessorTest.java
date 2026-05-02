package me.szypko.codeweaver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import me.szypko.codeweaver.processor.ConditionalProcessor;
import org.junit.jupiter.api.Test;

class ConditionalProcessorTest {
  private final ConditionalProcessor processor = new ConditionalProcessor();

  @Test
  void flagFalseComment() {
    List<String> input = List.of("    // #if TEST_FLAG", "        function();", "    // #endif");

    List<String> result = processor.process(input, Map.of("TEST_FLAG", false));
    assertEquals("    // #if TEST_FLAG", result.get(0));
    assertEquals("    // function();", result.get(1));
    assertEquals("    // #endif", result.get(2));
  }

  @Test
  void flagTrueUncomment() {
    List<String> input = List.of("    // #if TEST_FLAG", "    // function();", "    // #endif");

    List<String> result = processor.process(input, Map.of("TEST_FLAG", true));
    assertEquals("    // #if TEST_FLAG", result.get(0));
    assertEquals("    function();", result.get(1));
    assertEquals("    // #endif", result.get(2));
  }

  @Test
  void linesOutsideBlockAreUntouched() {
    final List<String> input =
        List.of("package foo;", "// #if TEST_FLAG", "    someCode();", "// #endif", "otherCode();");

    final List<String> result = processor.process(input, Map.of("TEST_FLAG", false));

    assertEquals("package foo;", result.get(0));
    assertEquals("otherCode();", result.get(4));
  }

  @Test
  void unknownFlag_treatedAsFalse() {
    List<String> input = List.of("// #if UNKNOWN_FLAG", "    someCode();", "// #endif");
    List<String> result = processor.process(input, Map.of());

    assertEquals("// someCode();", result.get(1));
  }

  @Test
  void alreadyCommented_flagFalse_staysCommented() {
    final List<String> input = List.of("// #if TEST_FLAG", "    // someCode();", "// #endif");
    final List<String> result = processor.process(input, Map.of("TEST_FLAG", false));

    assertEquals("    // someCode();", result.get(1));
  }
}
