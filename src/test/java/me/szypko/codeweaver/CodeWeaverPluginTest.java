package me.szypko.codeweaver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CodeWeaverPluginTest {
  @TempDir File projectDir;

  @Test
  void pluginApplies() throws IOException {
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'me.szypko.codeweaver'
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("tasks")
        .withPluginClasspath()
        .build();
  }
}
