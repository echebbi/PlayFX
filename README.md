# PlayFX
PlayFX is a light framework for managing multiple scenes in JavaFX. More specifically, it provides an easy way to manage the different states of a JavaFX application and to pass from one to another. Moreover, it comes with an embedded [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection) system.

## A Work in Progress
As of now, the work is still in progress and the framework may change in the future.

## Presentation

The heart of the framework is the class [Play](https://github.com/KazeJiyu/playfx/blob/master/src/main/java/fr/kazejiyu/playfx/Play.java). Its usage is pretty close to the standard JavaFX's API ; here is a sample of a simple application :

```java
public class Launcher extends Application {
	
  @Override
  public void start(Stage primaryStage) throws IOException {	
    // Initialize the play
    Play play = new Play(primaryStage);

    // Prepare a new scene and call it "firstView". 
    // It is semantically equal to FXMLLoader.load
    play.prepare("firstView", MyController.class.getResource("my.fxml"));

    // Put the scene "firstView" on the stage. It is semantically equal to stage.setScene
    play.setScene("firstView");

    // Finally, let the show begin ! It is semantically equal to stage.show
    play.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
```

`PlayFX`'s main advantage is its ability to inject controllers. Values to inject can be specified via three different ways :
- Command line arguments,
- Custom factory method,
- Property file.

Further explanations can be found in the relevant [wiki's section](https://github.com/KazeJiyu/PlayFX/wiki/Dependency-Injection).

## Documentation

Usage, documentation and samples can be found on [PlayFX's wiki](https://github.com/KazeJiyu/PlayFX/wiki).

## Installation

As of now, `PlayFX` is not mature enough to be delivered through Maven. Hence, one has to build library's JAR manually in order to use it in a project.

### Generate JAR through Maven

__Important__: you must run Maven 3.x with a JRE 9 in order to launch the build.

- Clone the repository:

```
git clone https://github.com/KazeJiyu/PlayFX.git
```

- Launch maven build:

```
cd PlayFX
mvn clean package
```

- The JAR file should have been generated in `target` as `fr.kazejiyu.playfx.jar`

### Java 9 Support

`PlayFX` is Java 9-aware. Its Maven build generates multi-release jars that can be used indifferently from Java 8 and Java 9 applications.
