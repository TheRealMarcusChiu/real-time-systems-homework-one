- download project
- all Java Code files in /src/main/java folder

#### Prerequisites
- java 1.8 (version 8)

#### Using mvnw
- all <code>./mvnw</code> commands should be executed at project home directory

#### install third-party packages (run once)
- <code>./mvnw clean package</code>

#### compile code (run once - or whenever code base changes)
- <code>./mvnw compile</code>

#### run compiled application
- <code>./mvnw exec:java -Dexec.mainClass="Application" -Dexec.args="table-1.txt"</code>
- reads file from /src/main/resources folder
  