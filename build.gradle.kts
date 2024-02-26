plugins {
    id("java")
}

group = "net.bridgesplash"
version = "1.0.0"

repositories {
    mavenCentral()

    maven("https://jitpack.io")

}

dependencies {
    implementation("dev.hollowcube:polar:1.7.1")
    implementation("net.minestom:minestom-snapshots:7320437640")
}

tasks{

    jar{
        manifest {
            attributes["Main-Class"] = "net.bridgesplash.polarconverter.PolarConverter"
            attributes["Multi-Release"] = true
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}