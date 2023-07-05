plugins {
    java
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip.placeholderapi:PlaceholderAPI:2.11.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(20))
}
