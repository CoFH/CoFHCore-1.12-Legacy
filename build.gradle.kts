plugins {
    id("fpgradle-minecraft") version ("0.6.1")
}

group = "cofh"

minecraft_fp {
    mod {
        modid = "CoFHCore"
        name = "CoFH Core"
        rootPkg = "$group"
    }

    api {
        packages = listOf("api", "lib")
    }

    core {
        coreModClass = "asm.LoadingPlugin"
        accessTransformerFile = "CoFH_at.cfg"
    }

    tokens {
        tokenClass = "Tags"
    }
}

repositories {
    mega {
        content {
            includeGroup("codechicken")
        }
    }
}

dependencies {
    implementation("codechicken:codechickencore-mc1.7.10:1.4.0-mega:dev")
    implementation("codechicken:notenoughitems-mc1.7.10:2.3.1-mega:dev")
}