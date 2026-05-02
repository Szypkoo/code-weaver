package me.szypko.codeweaver.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionalProcessor {
  public List<String> process(final List<String> lines, final Map<String, Boolean> flags) {
    final List<String> result = new ArrayList<>();
    boolean insideBlock = false, blockEnabled = false;
    for (final String line : lines) {
      final String trimmed = line.trim();

      if (trimmed.startsWith("// #if ")) {
        final String flagName = trimmed.substring("// #if ".length()).trim();
        blockEnabled = flags.getOrDefault(flagName, false);
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
        result.add(blockEnabled ? uncomment(line) : comment(line));
        continue;
      }

      result.add(line);
    }

    return result;
  }

  private String uncomment(final String line) {
    final int slashIndex = line.indexOf("//");
    if (slashIndex == -1) {
      return line;
    }

    String afterSlash = line.substring(slashIndex + 2);
    if (afterSlash.startsWith(" ")) {
      afterSlash = afterSlash.substring(1);
    }

    return line.substring(0, slashIndex) + afterSlash;
  }

  private String comment(final String line) {
    int indent = 0;
    while (indent < line.length() && line.charAt(indent) == ' ') {
      indent++;
    }

    final String content = line.substring(indent);
    if (content.startsWith("//")) {
      return line;
    }

    return line.substring(0, indent) + "// " + line.substring(indent);
  }
}
