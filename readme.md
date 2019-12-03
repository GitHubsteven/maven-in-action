#### what for
Example for the maven explore. To solve the following problems
##### add the snapshot repository as the repository
 don't work when add the repository to the pom.xml config file. Have no idea why this setting
 does not work.

##### deploy the sibling but dependent module

##### deploy the child module
example:
```
/**
    1. deploy will include the package and install
    2. -pl:  assign the target child module
    3. -am  include the neccessary dependencies of the target module including the third party and the sibling modules
*/
D:\spring_boot_project\ecerp-wms-parent>mvn clean deploy -pl :ecerp-wms-service-core-api -am -U -DskipTests=true
```

The parent project pom setting will work when the child module conduct the maven commands. As the above stating, if you child module
**ecerp-wms-service-core-api** depends on some sibling modules and the third party jars, you have to add the option -am to the
command under the parent project.

In this project, if you want to deploy the child module maven-utils, input and conduct the following at the terminal, you will deploy it to the nexus successfully.
```
mvn clean deploy -pl :maven-utils -am -U -Dmaven.test.skip=true
```


