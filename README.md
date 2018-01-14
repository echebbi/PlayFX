# PlayFX
![build status](https://travis-ci.org/KazeJiyu/PlayFX.svg?branch=master)

PlayFX is a light framework for managing multiple scenes in JavaFX. More specifically, it provides an easy way to manage the different states of a JavaFX application and to pass from one to another. In addition, it comes with an embedded [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection) system.

## Quick start

### Launch the application 

The code required to launch a JavaFX application with `PlayFX` is pretty close to the standard JavaFX's API. 

The heart of the framework is the [Play](https://github.com/KazeJiyu/playfx/blob/master/src/main/java/fr/kazejiyu/playfx/Play.java) class, which represents a set of `Scenes`. Its use is demonstrated in the following code snippet :

```java
public class Launcher extends Application {
	
  @Override
  public void start(Stage primaryStage) throws IOException {	
    // Initialize the play
    Play play = new Play(primaryStage);

    // Prepare a new scene and call it "welcome". 
    // It is semantically equal to FXMLLoader.load
    play.prepare("welcome", WelcomeController.class.getResource("welcome.fxml"));

    // Put the scene "welcome" on the stage. It is semantically equal to stage.setScene
    play.setScene("welcome");

    // Finally, let the show begin ! It is semantically equal to stage.show
    play.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
```

### Change current Scene

Once a scene has been `prepared`, the `setScene` method can be used to show it:

```java
// into WelcomeController
play.prepare("users", UsersController.class.getResource("users.fxml"));
play.setScene("users");
```

This method has optional parameters that can be specified to trigger an animated transition
when opening the new scene.

### Inject dependencies

#### Provide values to inject

`PlayFX`'s main advantage is its ability to inject controllers. Values to inject can be specified via three different ways :
- command line arguments,
- custom factory method,
- property file.

Detailed explanations can be found in the [relevant wiki's section](https://github.com/KazeJiyu/PlayFX/wiki/Dependency-Injection).

#### Ask for values

A controller can ask for a value by annotating a field with [`@Inject`](https://github.com/KazeJiyu/PlayFX/blob/master/src/main/java/fr/kazejiyu/playfx/injection/Inject.java) : 

```java
public class LoginController implements Initializable {
	
    @Inject private String usr;                      // will inject the value "usr"
    @Inject private String pwd;                      // will inject the value "pwd"
    @Inject(name="app.title") private String title;  // will inject the value "app.title"
	
    // rest of the class
}
```

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

`PlayFX` is Java 9-aware. Its Maven build generates multi-release JARs that can be used indifferently from Java 8 and Java 9 applications.
