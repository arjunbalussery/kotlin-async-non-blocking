import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property
import org.springframework.boot.gradle.tasks.bundling.BootJar


plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
	id("com.google.cloud.tools.jib") version "3.4.0" // not used.
	id("org.flywaydb.flyway") version "9.22.1" // Major version 10 has some issues with postgres migration
	id("nu.studer.jooq") version "8.2" // for jooq generation
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	//spring boot starter
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jooq:3.2.1")
	implementation ("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-webflux")




	//reactive database
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:3.2.1")
	implementation("io.r2dbc:r2dbc-spi:1.0.0.RELEASE")
	implementation("io.r2dbc:r2dbc-pool:1.0.1.RELEASE")
	implementation("org.postgresql:r2dbc-postgresql:1.0.2.RELEASE")

	//database-for flyway migration and jooq stub generation
	implementation("org.postgresql:postgresql:42.7.1")

	//kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0-Beta1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0-Beta1")

	//coroutine
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.8.0-RC")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0-RC")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	// Add jOOQ dependencies
	implementation("org.jooq:jooq")
	implementation("org.jooq:jooq-meta")
	implementation("org.jooq:jooq-codegen")

	//caffeine
	implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

	//flyway
	implementation("org.flywaydb:flyway-core:10.3.0")
	implementation("org.flywaydb:flyway-database-postgresql:10.3.0")



	//log4j
	implementation("org.apache.logging.log4j:log4j:3.0.0-alpha1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:junit-jupiter:1.18.3") // Use the latest version
	testImplementation("org.testcontainers:postgresql:1.18.3")

}

//tasks
tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//jooq generation using postgres db
dependencies {
	jooqGenerator("org.postgresql:postgresql:42.5.1")
}

/* jooq and flyway setup */
val dbUrl = "jdbc:postgresql://localhost:5432/postgres"
val dbUser = "postgres"
val dbPassword = "pass123"
val dbDriver = "org.postgresql.Driver"

flyway {
	driver = dbDriver
	url = dbUrl
	user = dbUser
	password = dbPassword
	schemas = arrayOf("public")
	baselineOnMigrate = true
	table = "flyway_schema_history"
	locations = arrayOf("filesystem:$projectDir/src/main/resources/db/migration")
}

jooq {
	version.set("3.18.7")  // the default (can be omitted)
	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // the default (can be omitted)
	configurations {
		create("main") {  // name of the jOOQ configuration
			generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)

			jooqConfiguration.apply {
				logging = Logging.WARN
				jdbc.apply {
					driver = dbDriver
					url = dbUrl
					user = dbUser
					password = dbPassword
					properties.add(Property().apply {
						key = "ssl"
						value = "false"
					})
				}
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
						forcedTypes.addAll(listOf(
							ForcedType().apply {
								name = "varchar"
								includeExpression = ".*"
								includeTypes = "JSONB?"
							},
							ForcedType().apply {
								name = "varchar"
								includeExpression = ".*"
								includeTypes = "INET"
							}
						))
					}
					generate.apply {
						isDeprecated = false
						isRecords = true
						isImmutablePojos = true
						isFluentSetters = true
					}
					target.apply {
						packageName = "com.example.generated"
						directory = "src/main/jooq"  // default (can be omitted)
					}
					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
				}
			}
		}
	}

}

