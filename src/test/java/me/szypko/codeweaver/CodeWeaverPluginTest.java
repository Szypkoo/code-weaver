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
  void taskProcessesTestSources() throws IOException {
    Path testSrcDir = projectDir.toPath().resolve("src/test/java");
    Files.createDirectories(testSrcDir);
    Files.writeString(
        testSrcDir.resolve("FooTest.java"),
        """
            public class FooTest {
                // #if TEST_FLAG
                void debug() {}
                // #endif
            }
        """);

    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'java'
                id 'io.github.szypkoo.codeweaver'
            }
            codeWeaver {
                flag 'TEST_FLAG', false
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("processConditionalsTestSources")
        .withPluginClasspath()
        .build();

    String result = Files.readString(testSrcDir.resolve("FooTest.java"));
    Assertions.assertTrue(result.contains("// void debug() {}"));
  }

  @Test
  void taskIsIdempotent() throws IOException {
    Path srcDir = projectDir.toPath().resolve("src/main/java");
    Files.createDirectories(srcDir);
    Files.writeString(
        srcDir.resolve("Foo.java"),
        """
            public class Foo {
                // #if TEST_FLAG
                void hello() {}
                // #endif
            }
        """);

    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'java'
                id 'io.github.szypkoo.codeweaver'
            }
            codeWeaver {
                flag 'TEST_FLAG', false
            }
        """);

    GradleRunner runner =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("processConditionalsMainSources")
            .withPluginClasspath();

    runner.build();
    String afterFirst = Files.readString(srcDir.resolve("Foo.java"));

    runner.build();
    String afterSecond = Files.readString(srcDir.resolve("Foo.java"));

    Assertions.assertEquals(afterFirst, afterSecond);
  }

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
                id 'io.github.szypkoo.codeweaver'
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

  @Test
  void taskRunsBeforeCompileJava() throws IOException {
    final Path srcDir = projectDir.toPath().resolve("src/main/java");
    Files.createDirectories(srcDir);
    Files.writeString(
        srcDir.resolve("Foo.java"),
        """
            public class Foo {
                // #if TEST_FLAG
                // void hello() {}
                // #endif
            }
        """);

    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'java'
                id 'io.github.szypkoo.codeweaver'
            }
            codeWeaver {
                flag 'TEST_FLAG', true
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("compileJava")
        .withPluginClasspath()
        .build();

    final String result = Files.readString(srcDir.resolve("Foo.java"));
    Assertions.assertTrue(result.contains("void hello() {}"));
  }

  @Test
  void generatesIntelliJTemplates() throws IOException {
    Files.writeString(projectDir.toPath().resolve("settings.gradle"), "");
    Files.writeString(
        projectDir.toPath().resolve("build.gradle"),
        """
            plugins {
                id 'java'
                id 'io.github.szypkoo.codeweaver'
            }
            codeWeaver {
                flag 'DEBUG_MODE', true
                flag 'FEATURE_X', false
            }
        """);

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("generateCodeWeaverIntelliJTemplates")
        .withPluginClasspath()
        .build();

    Path templateFile = projectDir.toPath().resolve(".idea/liveTemplates/CodeWeaver.xml");
    Assertions.assertTrue(Files.exists(templateFile));

    String content = Files.readString(templateFile);
    Assertions.assertTrue(content.contains("DEBUG_MODE"));
    Assertions.assertTrue(content.contains("FEATURE_X"));
    Assertions.assertTrue(content.contains("enum(\"DEBUG_MODE\",\"FEATURE_X\")") || 
                          content.contains("enum(\"FEATURE_X\",\"DEBUG_MODE\")"));
  }
}
