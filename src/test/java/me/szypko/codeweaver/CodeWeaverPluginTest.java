package me.szypko.codeweaver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.testkit.runner.BuildResult;
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

  @Test
  void extensionFlagsAreReadable() throws IOException {
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'me.szypko.codeweaver'
            }
            codeWeaver {
                flag 'DEV_MODE', true
            }
            tasks.register('printFlags') {
                doLast {
                    def flags = project.extensions.getByType(me.szypko.codeweaver.extension.Extension).getFlags()
                    println "FLAG:" + flags
                }
            }
        """);

    final BuildResult result =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("printFlags")
            .withPluginClasspath()
            .build();

    assertTrue(result.getOutput().contains("FLAG:[DEV_MODE:true]"));
  }
}
