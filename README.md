# lambda-java17-layer
AWS Lambda layer to enable Java 17 support

## Usage

- Create a function using the PROVIDED_AL2 runtime.
- Add this layer to your function.

## Java 17 

A custom JRE is created to reduce final file size. Lambda has a 250MB unzipped file size limit.

[Dockerfile](Dockerfile) describes how the JRE is built

## JVM Settings

The following JVM settings are added by default.

```
--add-opens java.base/java.util=ALL-UNNAMED 
-XX:+TieredCompilation -XX:TieredStopAtLevel=1 
-Xshare:on
```

Further suggestions welcomed

## Java Class Path

```
aws-lambda-java-runtime-interface-client-1.1.0.jar
aws-lambda-java-core-1.2.1.jar
aws-lambda-java-serialization-1.0.0.jar
$LAMBDA_TASK_ROOT
$LAMBDA_TASK_ROOT/*
$LAMBDA_TASK_ROOT/lib/*
```
