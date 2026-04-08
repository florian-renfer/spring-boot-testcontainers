plugins {
	java
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.boot.dependency.management)
}

group = "com.github.florian-renfer"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
  compileOnly("org.projectlombok:lombok")

  annotationProcessor("org.projectlombok:lombok")

  implementation(libs.bundles.spring.boot.starter)
  implementation("org.mariadb.jdbc:mariadb-java-client")
  implementation("org.liquibase:liquibase-core")

	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

  testImplementation(libs.bundles.spring.boot.test)
  testImplementation(libs.bundles.testcontainers)
  testImplementation("org.testcontainers:junit-jupiter")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
