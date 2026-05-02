package me.szypko.codeweaver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CodeWeaverPluginTest {
  @TempDir File projectDir;

  @Test
  void taskProcessesConditionalBlocks() throws IOException {
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
                id 'me.szypko.codeweaver'
            }
            codeWeaver {
                flag 'TEST_FLAG', false
            }
            tasks.register('processConditionals', me.szypko.codeweaver.task.ProcessConditionalsTask) {
                sourceDirs.from(file('src/main/java'))
                flags = project.extensions.getByType(me.szypko.codeweaver.extension.Extension).getFlags()
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("processConditionals")
        .withPluginClasspath()
        .build();

    String result = Files.readString(srcDir.resolve("Foo.java"));
    assertTrue(result.contains("// function();"));
  }

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
