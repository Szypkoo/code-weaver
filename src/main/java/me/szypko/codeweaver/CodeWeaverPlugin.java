package me.szypko.codeweaver;

import me.szypko.codeweaver.extension.Extension;
import me.szypko.codeweaver.task.ProcessConditionalsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

public class CodeWeaverPlugin implements Plugin<Project> {
  @Override
  public void apply(Project target) {
    final Extension extension = target.getExtensions().create("codeWeaver", Extension.class);

    target
        .getPlugins()
        .withId(
            "java",
            ignored -> {
              final SourceSetContainer sourceSets =
                  target.getExtensions().getByType(SourceSetContainer.class);

              sourceSets.configureEach(
                  sourceSet -> {
                    final String taskName =
                        "processConditionals" + capitalize(sourceSet.getName()) + "Sources";

                    target
                        .getTasks()
                        .register(
                            taskName,
                            ProcessConditionalsTask.class,
                            task -> {
                              task.getSourceDirs()
                                  .from(sourceSet.getAllJava().getSourceDirectories());
                              task.getFlags().set(extension.getFlags());
                            });

                    target
                        .getTasks()
                        .named(
                            sourceSet.getCompileJavaTaskName(),
                            compileJava -> compileJava.dependsOn(taskName));
                  });
            });
  }

  /* I know, I should put this in some kind of util instead of keeping it there. */
  private static String capitalize(final String string) {
    if (string.isEmpty()) {
      return string;
    }

    return Character.toUpperCase(string.charAt(0)) + string.substring(1);
  }
}
