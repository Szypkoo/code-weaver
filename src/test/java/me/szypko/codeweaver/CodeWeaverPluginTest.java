package me.szypko.codeweaver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CodeWeaverPluginTest {
  @TempDir File projectDir;

  @Test
  void pluginRegistersTaskAndProcessesFiles() throws IOException {
    final Path srcDir = projectDir.toPath().resolve("src/main/java");

    Files.createDirectories(srcDir);
    Files.writeString(
        srcDir.resolve("Foo.java"),
        """
            // #if TEST_FLAG
            function();
            // #endif
        """);

    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'java'
                id 'me.szypko.codeweaver'
            }
            codeWeaver {
                flag 'TEST_FLAG', false
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("processConditionalsMainSources")
        .withPluginClasspath()
        .build();

    final String result = Files.readString(srcDir.resolve("Foo.java"));
    Assertions.assertTrue(result.contains("// function();"));
  }
}
