package de.roamingthings.javalayer.infrastructure;

import software.amazon.awscdk.Stage;
import software.amazon.awscdk.StageProps;
import software.constructs.Construct;

public class DeploymentStage extends Stage {
    public DeploymentStage(Construct scope, String id, StageProps props) {
        super(scope, id, props);

        new LambdaJavaLayerStack(this, "LambdaJavaLayerStack");
    }
}
