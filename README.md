# Carbon Libraries 
powered maven repos by github page hosting

----

## Carbon Validation

|  items    |   |   |   |   |
|-----------|---|---|---|---|
| build     |[![wercker status](https://app.wercker.com/status/ee54ade2bfebafa23d061afcccfa03de/s/master "wercker status")](https://app.wercker.com/project/byKey/ee54ade2bfebafa23d061afcccfa03de)||||
| coverage  |[![Coverage Status](https://coveralls.io/repos/github/ShotaOd/maven/badge.svg)](https://coveralls.io/github/ShotaOd/maven)|[![codecov](https://codecov.io/gh/ShotaOd/maven/branch/master/graph/badge.svg)](https://codecov.io/gh/ShotaOd/maven)||||
| quality   |[![CodeFactor](https://www.codefactor.io/repository/github/shotaod/maven/badge)](https://www.codefactor.io/repository/github/shotaod/maven)|[![Codacy Badge](https://api.codacy.com/project/badge/Grade/96c99761f89f4d9688bbfadabe565620)](https://www.codacy.com/app/shota.oda-github/maven?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ShotaOd/maven&amp;utm_campaign=Badge_Grade)|[![codebeat badge](https://codebeat.co/badges/a15d35d7-017b-4089-aa4a-32f125067f61)](https://codebeat.co/projects/github-com-shotaod-maven-master)|[![Maintainability](https://api.codeclimate.com/v1/badges/eaab7b6dab58aeac15f5/maintainability)](https://codeclimate.com/github/ShotaOd/maven/maintainability)|
| sonarqube |[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=ShotaOd_maven&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=ShotaOd_maven&metric=Maintainability)|[![Lines](https://sonarcloud.io/api/project_badges/measure?project=ShotaOd_maven&metric=ncloc)](https://sonarcloud.io/component_measures?id=ShotaOd_maven&metric=ncloc)|[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ShotaOd_maven&metric=bugs)](https://sonarcloud.io/project/issues?id=ShotaOd_maven&resolved=false&types=BUG)||

### quick start

#### Gradle
build.gradle
```groovy
ext {
    ver = ext {
        carbon = ext {
            validaton = '0.3.1'
        }
    }
}
repository {
    maven {
        url: 'https://shotaod.github.io/maven/release'
    }
}
dependency {
    implementation "org.carbon.libs:carbon-validation:$ver.carbon.validation" 
}
```

#### Maven
pom.xml
```xml
<project>
    <properties>
        <carbonValidationVersion>0.3.1</carbonValidationVersion>
    </properties>

    <repositories>
        <repository>
            <id>carbon</id>
            <url>https://shotaod.github.io/maven/release</url>
        </repository>
    </repositories>
    
    <dependency>
        <groupId>org.carbon.libs</groupId>
        <artifactId>carbon-validation</artifactId>
        <version>${carbonValidationVersion}</version>
    </dependency>
</project>
```
