# kotlin-brigadier

[![ItemHelper](https://img.shields.io/badge/kotlin_brigadier-1.1.2-blue.svg)]()
<br><br>
[![Java](https://img.shields.io/badge/Java-21-FF7700.svg?logo=openjdk)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-186FCC.svg?logo=kotlin)]()
[![PaperMC](https://img.shields.io/badge/PaperMC-1.20.6_↑-222222.svg)]()
[![Licence](https://img.shields.io/badge/GPL-3.0-d8624b.svg)]()


<br>
<br>


```kotlin
val manager = plugin.lifecycleManager
manager.registerEventHandler {
    register("command", "description", "alias1", "alias2") {
        then("option1") {
            require { player.isOp }
            
            then("world" to world()) {
                executes {
                    val world: World by it
                    player.sendMessage(world)
                }
            }
            
            executes {
                player.sendMessage("you is op")
            }
        }
        "option2" {
            requires {listOf(
                player.isOp,
                player.isFlying
            )}
            executes {
                player.sendMessage("option2")
            }
        }
        then("option3" to string()) {
            executes {
                val option3: String by it
                player.sendMessage(option3)
            }
        }
        
        executes {
            player.sendMessage("test command")
        }
    }
}
```

<br>
<br>

### Use API


## Maven
```xml
<repositories>
    <repository>
        <id>kr.blugon</id>
        <url>https://repo.blugon.kr/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>kr.blugon</groupId>
        <artifactId>kotlin-brigadier</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```


## Groovy
```gradle
repositories {
    maven { 'https://repo.blugon.kr/repository/maven-public/' }
}

dependencies {
    implementation 'kr.blugon:kotlin-brigadier:VERSION'
}
```

## Kotlin DSL
```gradle
repositories {
    maven("https://repo.blugon.kr/repository/maven-public/")
}

dependencies {
    implementation("kr.blugon:kotlin-brigadier:VERSION")
}
```
