package de.roamingthings.javalayer.infrastructure;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.LayerVersion;
import software.amazon.awscdk.services.lambda.LayerVersionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

import java.util.List;

import static software.amazon.awscdk.RemovalPolicy.RETAIN;

public class LambdaJavaLayerStack extends Stack {
    public LambdaJavaLayerStack(Construct scope, String id) {
        super(scope, id);

        var java17Layer = new LayerVersion(this, "Java17Layer", LayerVersionProps.builder()
                .layerVersionName("Java17Layer")
                .description("Java 17 runtime for Lambda Functions")
                .compatibleRuntimes(List.of(Runtime.PROVIDED_AL2))
                .code(Code.fromAsset("../build/distribution/java17layer.zip"))
                .compatibleArchitectures(List.of(Architecture.X86_64))
                .removalPolicy(RETAIN)
                .build());

        var java19Layer = new LayerVersion(this, "Java19Layer", LayerVersionProps.builder()
                .layerVersionName("Java19Layer")
                .description("Java 19 runtime for Lambda Functions")
                .compatibleRuntimes(List.of(Runtime.PROVIDED_AL2))
                .code(Code.fromAsset("../build/distribution/java19layer.zip"))
                .compatibleArchitectures(List.of(Architecture.X86_64))
                .removalPolicy(RETAIN)
                .build());

        StringParameter.Builder.create(this, "Java17LayerArnParameter")
                .parameterName("Java17RuntimeLayerArn")
                .description("ARN of the Java 17 layer version")
                .stringValue(java17Layer.getLayerVersionArn())
                .build();

        StringParameter.Builder.create(this, "Java19LayerArnParameter")
                .parameterName("Java19RuntimeLayerArn")
                .description("ARN of the Java 19 layer version")
                .stringValue(java19Layer.getLayerVersionArn())
                .build();
    }
}
