# Java Lambda Runtimes Version 17+

This project creates AWS Lambda Layers with a Java 17 (or newer Version) based on Corretto to allow Lambda Functions to
be written using Java 17 or newer.

## Prerequisites

* [Java 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) (Corretto recommended)
* [AWS CDK](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html)
* [Node.js](https://nodejs.org/en/download/) as required by the AWS CDK
* [SDKMAN!](https://sdkman.io/) is recommended to install and manage Java versions (optional)

## Quickstart

The pipeline that builds and deploys the runtimes automatically has to be deployed once.
This is done by manually deploy the included CDK stack that includes a pipeline. The following command assumes that
you have already authenticated to the AWS account and assumed the correct role.

```sh
./gradlew clean
./gradlew -PjdkVersion=17 buildLayer # On non-x86_64 machines this may take very long
./gradlew -PjdkVersion=19 buildLayer # On non-x86_64 machines this may take very long
./gradlew :infrastructure:deploy # Deploy the pipeline  
```

After deploying the pipeline you have to once approve the Codestar connection to the GitHub repository.

## Build the Layer Locally

To build the layer locally you need to have Docker installed. Then you can run the following command:

```shell
./gradlew -PjdkVersion=17 clean buildLayer
```

This will build the layer for Java 17. To build the layer for Java 19 replace `17` with `19`.

## Using the Custom Java Runtime

Whenever a new version of the Java runtime is deployed, the ARN of the layer is stored in a SSM parameter named
`Java<version>RuntimeLayerArn`.

To use the custom Java runtime in your application add the following code to your CDK stack:

1. Import the current layer version of the Java runtime layer for the account

```Java
private ILayerVersion importJavaRuntimeLayer(){
        var javaRuntimeLayerArn=StringParameter.valueForStringParameter(this,"Java17RuntimeLayerArn");
        return LayerVersion.fromLayerVersionArn(this,"import-JavaLayer",javaRuntimeLayerArn);
        }
```

To use the Java 19 runtime replace `Java17RuntimeLayerArn` with `Java19RuntimeLayerArn`

2. Use this layer as runtime for you function:

```Java
var java17Layer=importJavaRuntimeLayer();
        Function.Builder.create(this,"Function")
        .runtime(Runtime.PROVIDED_AL2)
        .architecture(Architecture.X86_64)
        // ...
        .layers(List.of(java17Layer))
        .build();
```

## How it Works: Custom Java Runtimes for AWS Lambda

This project uses [AWS Lambda Layers](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html) to provide
a stripped down distribution of Java 17 and 19 [Amazon Corretto JDK](https://aws.amazon.com/corretto/).

The layer can be shared between all Lambda functions in the account. One important restriction to notice is that the
size of all layers and the lambda code itself must not exceed 250MB.

To respect these quotas it's not possible to use the full JDK distribution. Instead this project contains a build script
that will create a stripped down JRE of the JDK using `jlink`. If you are interested in how to do that take a closer
look at the [`Dockerfile`](Dockerfile).

In addition to the JRE the layer, that is distributed as a ZIP file, contains a [`bootstrap`](bootstrap) script that
starts the Java runtime and includes the AWS Lambda runtime implementation along with two other dependencies in the
classpath and sets the classpath to `$LAMBDA_TASK_ROOT`, `$LAMBDA_TASK_ROOT/*`, `$LAMBDA_TASK_ROOT/lib/*` and
`/opt/java/lib/*`. Please note that Layers are extracted to `/opt/` by default. Therefore any other layer that contains
dependencies in the `java/lib` folder of the ZIP archive will become available in the `/opt/java/lib` folder which is
picked-up bey the Lambda runtime.

## Architecture of this Project

The infrastructure to build the custom Java runtimes consist of

* a CodePipeline that will build and deploy both runtime distributions in the current AWS account.
* scripts that will build the distribution archives:
  * `prepare-layer` - download all dependencies of the AWS Lambda runtime and copy the bootstrap file to
    `build/layer/<version>`
  * `build-jre.sh` - a script that uses Docker and the provided `Dockerfile` to build the stripped down distribution of
    the JDK
