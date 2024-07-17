package tech.mappie;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension;
import org.jetbrains.kotlin.maven.PluginOption;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

//@Component(role = KotlinMavenPluginExtension.class, hint = "mappie-compiler-plugin")
//public class MappieMavenPlugin implements KotlinMavenPluginExtension {
//    @Override
//    public boolean isApplicable(@NotNull MavenProject project, @NotNull MojoExecution execution) {
//        return true;
//    }
//
//    @Override
//    public String getCompilerPluginId() {
//        try (var resource = getClass().getClassLoader().getResourceAsStream("version.properties")) {
//            var properties = new Properties();
//            properties.load(resource);
//            var version = properties.getProperty("version");
//            return "tech.mappie:mappie-compiler-plugin:" + version;
//        } catch (IOException e) {
//            throw new IllegalStateException("Failed to load version of Mappie.");
//        }
//    }
//
//    @NotNull
//    @Override
//    public List<PluginOption> getPluginOptions(@NotNull MavenProject project, @NotNull MojoExecution execution) {
//        return List.of();
//    }
//}
