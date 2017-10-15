# PlayFX
PlayFX is a light framework for managing multiple scenes in JavaFX. More specifically, it provides an easy way to manage the different states of a JavaFX application and to pass from one to another. Moreover, it comes with an embedded [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection) system.

## A Work in Progress
As of now, the work is still in progress and the framework is not quite stable.

## PlayFX's API

### `Play` class

The heart of the framework is the [Play](https://github.com/KazeJiyu/playfx/blob/master/src/main/java/fr/kazejiyu/playfx/Play.java) class. Following the theater's world analogy, a play is played on **one stage** and is made of **several scenes**. As such, a `Play` instance brings the facilities to manage your different scenes.

Here is how a simple application would look like with PlayFX :

```java
public class Launcher extends Application {
	
  @Override
  public void start(Stage primaryStage) throws IOException {	
    // Initialize the play
    Play play = new Play(primaryStage);

    // Prepare a new scene and call it "firstView". It is semantically equal to FXMLLoader.load
    play.prepare("firstView", MyController.class.getResource("my.fxml"));

    // Put the scene "firstView" on the stage. It is semantically equal to stage.setScene
    play.setScene("firstView");

    // Finally, let the show begin ! It is semantically equal to stage.show
    play.start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
```

### `Act` interface

`Act` is an optional interface that can be implemented by the controllers in order to get a new chance to initialize UI components. Indeed, the standard method `initialize` is called _before_ the creation of the `Scene` associated with the controller. That could lead to some issues, like the uncapability of a given `Node` to request the focus.

To overcome this difficulty, `Act` defines a only a method : 

```java
public interface Act {

  public void prepare(Play play, Scene scene);

}
```

If a controller loaded by PlayFX implements `Act`, `prepare` is called once the associated `Scene` is created. The controller can hence set up itself and get a reference to the `Play` instance. However, since `prepare` is called by `Play.directAct`, please note that the scene is **not** yet shown on the screen. As a result, some methods, including :
 - `getWidth` / `getHeight`,
 - `getWindow` 
 do not return an exploitable output.
 
 ## Dependency Injection
 
 ### `Dependencies` class
 
 On top of the injection of the UI components already provided by FXML, PlayFX brings the possibility to inject custom fields.
 
 First, the services to use have to be specified to PlayFX _via_ a `Dependencies` instance. This class presents two methods that make able to tell PlayFX to check the fields to inject against either their name or their type. For instance :
 
 ```java
 public class Launcher extends Application {
	
  @Override
  public void start(Stage primaryStage) throws IOException {	
    // Initialize the dependencies 
    Dependencies dependencies = new Dependencies();
    
    // Create an instance that represents the application's context of execution
    Context context = new Context();
    // Only the fields named "context" and of the right type will be injected
    dependencies.registerName("context", context);
    // All the fields of type Context will be injected, whatever their name
    dependencies.registerType(context.getClass(), context);    
    
    Play play = new Play(primaryStage, dependencies);  // create the play with custom dependencies
    play.prepare("firstView", MyController.class.getResource("my.fxml"));  // load the scene
    play.setScene("firstView"); // set the scene on the stage

    play.start(); // show the scene
  }

  public static void main(String[] args) {
    launch(args);
  }
}
 ```
 
 Secondly, controllers must tell PlayFX which fields are opened to injection. This is merely done by annotating the relevant fields with the `Inject` annotation :
 
 ```java
    @Inject Context context;  // will be injected with the object instanciated above
  ```
 
 ### `config.properties` file
 
 #### Description
 
 Primitive values can also be specified from configuration files and be available for injections.
 
It is possible to create one configuration file per controller in order to cleary separate unrelated data. Such files **must** be named `config.properties` and being located in the same directory as the corresponding controller. Otherwise, PlayFX won't identify them and therefore won't be able to inject their content.

#### Formatting

Configuration files must be formatted as pairs `key=value` like shown below :

```conf
user=User1
width=400
height=600
```
