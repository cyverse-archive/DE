* Created test project with the following maven archetype cmd:
```
mvn archetype:generate 
-DarchetypeGroupId=com.github.branflake2267.archetypes 
-DarchetypeRepository=https://oss.sonatype.org/content/repositories/snapshots 
-DarchetypeArtifactId=gxt-basic-3x-archetype 
-DarchetypeVersion=1.0.0-SNAPSHOT 
-DgroupId=org.iplantc.de.fileViewers 
-DartifactId=FileSetViewerTest 
-Dmodule=FileSetViewerTest
```
* Next, ran `gradle init` to auto-generate gradle files based off of existing _pom.xml_
