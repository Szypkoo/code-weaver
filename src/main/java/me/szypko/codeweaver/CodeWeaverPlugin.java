package me.szypko.codeweaver;

import me.szypko.codeweaver.extension.Extension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CodeWeaverPlugin implements Plugin<Project> {
  @Override
  public void apply(Project target) {
    target.getExtensions().create("codeWeaver", Extension.class);
  }
}
