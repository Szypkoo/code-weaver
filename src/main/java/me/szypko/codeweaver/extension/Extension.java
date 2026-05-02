package me.szypko.codeweaver.extension;

import java.util.HashMap;
import java.util.Map;

public class Extension {
  private final Map<String, Boolean> flags = new HashMap<>();

  public void flag(final String name, final boolean enabled) {
    flags.put(name, enabled);
  }

  public Map<String, Boolean> getFlags() {
    return flags;
  }
}
