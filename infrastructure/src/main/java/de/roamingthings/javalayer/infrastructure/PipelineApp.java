package de.roamingthings.javalayer.infrastructure;

import software.amazon.awscdk.App;

import java.util.Map;
import java.util.TreeMap;

import static de.roamingthings.javalayer.infrastructure.PipelineStack.GITHUB_REPOSITORY;

public class PipelineApp {

    private static final Map<String, String> DEFAULT_TAGS = new TreeMap<>(Map.of(
            "owner:scope", "infrastructure",
            "repository", "https://" + GITHUB_REPOSITORY.fullRepositoryPath(),
            "application-id", "lambda-java-layer"
    ));

    public static void main(final String[] args) {
        App app = new App();

        new PipelineStack(app, "LambdaJavaLayerPipelineStack", PipelineStack.PipelineStackProps.builder()
                .stackName("LambdaJavaLayerPipelineStack")
                .fullRepositoryPath(GITHUB_REPOSITORY.fullRepositoryPath())
                .repositoryBranch(GITHUB_REPOSITORY.branch())
                .tags(Map.copyOf(DEFAULT_TAGS))
                .build());
        app.synth();
    }
}
