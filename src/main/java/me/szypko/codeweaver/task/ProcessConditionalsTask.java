package me.szypko.codeweaver.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import me.szypko.codeweaver.processor.ConditionalProcessor;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;

public abstract class ProcessConditionalsTask extends DefaultTask {

  @InputFiles
  public abstract ConfigurableFileCollection getSourceDirs();

  @Input
  public abstract MapProperty<String, Boolean> getFlags();

  @TaskAction
  public void process() throws IOException {
    final ConditionalProcessor processor = new ConditionalProcessor();

    for (var srcDir : getSourceDirs()) {
      if (!srcDir.exists()) {
        continue;
      }

      try (var walk = Files.walk(srcDir.toPath())) {
        walk.filter(p -> p.toString().endsWith(".java"))
            .forEach(
                file -> {
                  try {
                    final List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8),
                        processed = processor.process(lines, getFlags().get());

                    Files.write(file, processed, StandardCharsets.UTF_8);
                  } catch (IOException e) {
                    throw new RuntimeException("Conditional processor failed on " + file, e);
                  }
                });
      }
    }
  }
}
