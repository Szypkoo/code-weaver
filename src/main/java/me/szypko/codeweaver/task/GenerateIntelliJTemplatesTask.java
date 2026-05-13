package me.szypko.codeweaver.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class GenerateIntelliJTemplatesTask extends DefaultTask {

    @Input
    public abstract MapProperty<String, Boolean> getFlags();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @TaskAction
    public void generate() throws IOException {
        final Map<String, Boolean> flags = getFlags().get();
        final Path outputDir = getOutputDir().get().getAsFile().toPath();
        final Path templatesDir = outputDir.resolve("liveTemplates");
        Files.createDirectories(templatesDir);

        final String flagEnum = flags.keySet().stream()
                .map(f -> "\"" + f + "\"")
                .collect(Collectors.joining(","));

        final String content = """
            <templateSet group="CodeWeaver">
              <template name="#if" value="// #if $FLAG$" description="CodeWeaver conditional" toReformat="false" toShortenFQNames="true">
                <variable name="FLAG" expression="enum(%s)" defaultValue="" alwaysStopAt="true" />
                <context>
                  <option name="JAVA_COMMENT" value="true" />
                </context>
              </template>
              <template name="#endif" value="// #endif" description="CodeWeaver end conditional" toReformat="false" toShortenFQNames="true">
                <context>
                  <option name="JAVA_COMMENT" value="true" />
                </context>
              </template>
            </templateSet>
            """.formatted(flagEnum);

        Files.writeString(templatesDir.resolve("CodeWeaver.xml"), content, StandardCharsets.UTF_8);
    }
}
