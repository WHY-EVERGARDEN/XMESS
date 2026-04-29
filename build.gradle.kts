plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1" 
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "vi.vault"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}