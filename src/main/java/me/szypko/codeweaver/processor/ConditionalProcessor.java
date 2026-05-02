package me.szypko.codeweaver.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionalProcessor {
  private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

  public List<String> process(final List<String> lines, final Map<String, Boolean> flags) {
    final List<String> result = new ArrayList<>();
    boolean insideBlock = false, blockEnabled = false;
    String blockIndent = "";

    for (final String line : lines) {
      final String trimmed = line.trim();

      if (trimmed.startsWith("// #if ")) {
        final String flagName = trimmed.substring("// #if ".length()).trim();
        blockEnabled = evaluator.evaluate(flagName, flags);
        blockIndent = leadingSpaces(line);
        insideBlock = true;
        result.add(line);
        continue;
      }

      if (trimmed.equals("// #endif")) {
        insideBlock = false;
        result.add(line);
        continue;
      }

      if (insideBlock) {
        result.add(blockEnabled ? uncomment(line, blockIndent) : comment(line, blockIndent));
        continue;
      }

      result.add(line);
    }

    return result;
  }

  private String leadingSpaces(String line) {
    int i = 0;
    while (i < line.length() && line.charAt(i) == ' ') {
      i++;
    }

    return line.substring(0, i);
  }

  private String comment(String line, String blockIndent) {
    if (line.isBlank()) {
      return line;
    }

    final String trimmed = line.trim();
    if (trimmed.startsWith("//")) {
      return line;
    }

    return blockIndent + "// " + trimmed;
  }

  private String uncomment(String line, String blockIndent) {
    if (line.isBlank()) {
      return line;
    }

    final String trimmed = line.trim();
    if (!trimmed.startsWith("//")) {
      return line;
    }

    String afterSlash = trimmed.substring(2);
    if (afterSlash.startsWith(" ")) {
      afterSlash = afterSlash.substring(1);
    }

    return blockIndent + afterSlash;
  }
}
