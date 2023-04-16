package de.roamingthings.javalayer.infrastructure;

import lombok.Data;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.IStackSynthesizer;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Stage;
import software.amazon.awscdk.StageProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodeBuildStepProps;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineProps;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.pipelines.ConnectionSourceOptions;
import software.amazon.awscdk.pipelines.Step;
import software.amazon.awscdk.services.codebuild.BuildEnvironment;
import software.amazon.awscdk.services.codebuild.BuildEnvironmentVariable;
import software.amazon.awscdk.services.codebuild.ComputeType;
import software.amazon.awscdk.services.codebuild.LinuxBuildImage;
import software.amazon.awscdk.services.codestarconnections.CfnConnection;
import software.amazon.awscdk.services.codestarconnections.CfnConnectionProps;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static software.amazon.awscdk.services.codebuild.BuildEnvironmentVariableType.PLAINTEXT;

public class PipelineStack extends Stack {

    public record GitHubRepository(
            String owner,
            String repository,
            String branch) {

        public String fullRepositoryPath() {
            return owner + "/" + repository;
        }
    }

    public static final GitHubRepository GITHUB_REPOSITORY = new GitHubRepository(
            "roamingthings",
            "lambda-java-layer",
            "main"
    );

    public static final boolean SELF_MUTATION = true;

    public PipelineStack(Construct scope, String id, PipelineStackProps props) {
        super(scope, id, props);

        var deployStage = createDeployStage();

        var pipeline = createCodePipeline(props);
        pipeline.addStage(deployStage);

        pipeline.buildPipeline();
    }

    private Stage createDeployStage() {
        return new DeploymentStage(this, "TestAccountStage", StageProps.builder()
                .stageName("Deploy")
                .build());
    }

    private CodePipeline createCodePipeline(PipelineStackProps props) {

        var githubConnection = new CfnConnection(this, "LambdaJavaLayerConnection", CfnConnectionProps.builder()
                .connectionName("LambdaJavaLayerConnection")
                .providerType("GitHub")
                .build());

        return new CodePipeline(this, "LambdaJavaLayerPipeline", CodePipelineProps.builder()
                .selfMutation(SELF_MUTATION)
                .crossAccountKeys(true)
                .synth(createSynthStep(props, githubConnection))
                .build());
    }

    private Step createSynthStep(PipelineStackProps props, CfnConnection githubConnection) {
        return new CodeBuildStep("LambdaJavaLayerSynth", CodeBuildStepProps.builder()
                .input(CodePipelineSource.connection(
                        props.fullRepositoryPath,
                        props.repositoryBranch,
                        ConnectionSourceOptions.builder()
                                .connectionArn(githubConnection.getAttrConnectionArn())
                                .build()))
                .primaryOutputDirectory("infrastructure/cdk.out")
                .commands(List.of(
                        "./gradlew --console=plain -PjdkVersion=17 clean :buildLayer",
                        "./gradlew --console=plain -PjdkVersion=19 :buildLayer",
                        "./gradlew --console=plain :infrastructure:synth"
                ))
                .buildEnvironment(BuildEnvironment.builder()
                        .buildImage(LinuxBuildImage.STANDARD_6_0)
                        .computeType(ComputeType.MEDIUM)
                        .privileged(true)
                        .environmentVariables(new TreeMap<>(Map.of(
                                "CI", BuildEnvironmentVariable.builder()
                                        .type(PLAINTEXT).value("true")
                                        .build()
                        )))
                        .build())
                .build());
    }

    @Data
    @lombok.Builder
    public static class PipelineStackProps implements StackProps {
        private Boolean analyticsReporting;
        private String description;
        private Environment env;
        private String stackName;
        private IStackSynthesizer synthesizer;
        private Map<String, String> tags;
        private Boolean terminationProtection;
        private String tokenForInstallingPackagesSecretArn;
        private String dockerHubTokenSecretArn;
        private String fullRepositoryPath;
        private String repositoryBranch;
    }
}
