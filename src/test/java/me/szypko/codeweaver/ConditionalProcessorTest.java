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
    final List<String> input = List.of("// #if TEST_FLAG", "    function();", "// #endif");

    final List<String> result = processor.process(input, Map.of("TEST_FLAG", false));
    assertEquals("// #if TEST_FLAG", result.get(0));
    assertEquals("    // function();", result.get(1));
    assertEquals("// #endif", result.get(2));
  }

  @Test
  void flagTrueUncomment() {
    final List<String> input = List.of("// #if TEST_FLAG", "    // function();", "// #endif");

    final List<String> result = processor.process(input, Map.of("TEST_FLAG", true));
    assertEquals("// #if TEST_FLAG", result.get(0));
    assertEquals("    function();", result.get(1));
    assertEquals("// #endif", result.get(2));
  }
}
