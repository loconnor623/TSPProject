// Main class for the Traveling Salesman Problem Visualization Tool
// CSC 242-01
// Liam O'Connor, Luis Silva, Tarinderjit Singh
// December 7, 2021

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

public class Main extends Application {

    public static int windowWidth = 1000; // Pixels for the width of the window
    public static int windowHeight = 800; // Pixels for the height of the window
    Stage window;
    Scene algorithmSelection;
    Scene visualization;

    // Default values for the text fields
    final int DEFAULT_NUM_NODES = 12,
              DEFAULT_POP_SIZE = 100;

    // Made so that the Keyframe will not continue to run the whole algorithm every time it is executed
    public boolean nearestPathFinished = false;
    public boolean greedyPathFinished = false;
    public boolean manualPathStarted = false;
    public int manualPathDistance = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;

        // index 0 genetic, index 1 nearest neighbor, index 2 greedy, index 3 manual
        ArrayList<Boolean> buttonSelected = new ArrayList<Boolean>();
        buttonSelected.add(false);
        buttonSelected.add(false);
        buttonSelected.add(false);
        buttonSelected.add(false);

        Group selectionRoot = new Group();
        algorithmSelection = new Scene(selectionRoot, Color.LIGHTGRAY);

        int numberOfNodes = DEFAULT_NUM_NODES;
        int populationSize = DEFAULT_POP_SIZE;

        ArrayList<Node> nodes = new ArrayList<Node>(); //Declaring and generating nodes
        generateNodes(nodes, numberOfNodes);

        Group visualizationRoot = new Group();
        visualization = new Scene(visualizationRoot, Color.BLACK);

        GeneticAlgorithm geneticAlgo = new GeneticAlgorithm(populationSize, nodes);
        TSPNearestNeighbour nearestAlgo = new TSPNearestNeighbour(nodes);
        GreedyAlgorithm greedyAlgo = new GreedyAlgorithm(nodes);
        ManualPath manualPath = new ManualPath(nodes);

        ArrayList<Line> lines = new ArrayList<>();
        ArrayList<Line> relativeLines = new ArrayList<>();
        ArrayList<Circle> circles = new ArrayList<>();
        ArrayList<Node> manualPathOrder = new ArrayList<>();
        ArrayList<Label> circleLabels = new ArrayList<>();

        List<Double> bestDistance = new ArrayList<Double>();
        bestDistance.add(Double.POSITIVE_INFINITY);

        // Printing nodes as circles
        for(int i = 0; i < numberOfNodes; i++) {

            Circle circle = new Circle(nodes.get(i).getX(), nodes.get(i).getY(), 8);
            circle.setFill(Color.WHITE);
            circles.add(circle);
            visualizationRoot.getChildren().add(circles.get(i));
        }


                              /* **** SETTING UP SELECTION PANE **** */

        /////////////////////////////// Setting up welcome label

        Label welcomeLabel = new Label("Welcome to the Travelling Salesman Problem visualization tool!");
        welcomeLabel.setStyle("-fx-font-size: 32");
        welcomeLabel.setLayoutX(50);
        welcomeLabel.setLayoutY(32);
        selectionRoot.getChildren().add(welcomeLabel);

        /////////////////////////////// Setting up Number of Nodes Field and label

        Label numberOfNodesLabel = new Label("Enter number of nodes: ");
        numberOfNodesLabel.setLayoutX(120);
        numberOfNodesLabel.setLayoutY(110);
        numberOfNodesLabel.setFont(Font.font(16));

        TextField numberOfNodesField = new TextField();
        numberOfNodesField.setText("" + DEFAULT_NUM_NODES);
        numberOfNodesField.setTranslateX(305);
        numberOfNodesField.setTranslateY(110);

        selectionRoot.getChildren().add(numberOfNodesLabel);
        selectionRoot.getChildren().add(numberOfNodesField);

        /////////////////////////////// Setting up Text Field for population selection and label

        Label populationLabel = new Label("Enter population size: ");
        populationLabel.setLayoutX(120);
        populationLabel.setLayoutY(150);
        populationLabel.setFont(Font.font(16));

        TextField populationField = new TextField();
        populationField.setText("" + DEFAULT_POP_SIZE);
        populationField.setTranslateX(305);
        populationField.setTranslateY(150);

        selectionRoot.getChildren().add(populationLabel);
        selectionRoot.getChildren().add(populationField);

        /////////////////////////////// Button to recalculate node layout

        Button recalcNodesButton = new Button("Recalculate Nodes");
        recalcNodesButton.setFont(Font.font(14));
        recalcNodesButton.setLayoutX(520);
        recalcNodesButton.setLayoutY(130);
        selectionRoot.getChildren().add(recalcNodesButton);

        EventHandler<ActionEvent> recalcNodesEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                boolean validPopulation = false;
                boolean validNodesNumber = false;
                int populationSize = DEFAULT_POP_SIZE;
                int numberOfNodes = DEFAULT_NUM_NODES;

                try {

                    populationSize = Integer.valueOf(populationField.getText());
                    validPopulation = true;

                } catch (NumberFormatException ex) {
                    Alert populationError = new Alert(AlertType.ERROR);
                    populationError.setTitle("Error");
                    populationError.setContentText("Please enter a valid integer value for population size.");
                    populationError.show();
                }

                try {
                    numberOfNodes = Integer.valueOf(numberOfNodesField.getText());

                    if (numberOfNodes < 2) {
                        throw new IllegalArgumentException();
                    }

                    validNodesNumber = true;

                } catch (NumberFormatException ex) {
                    Alert populationError = new Alert(AlertType.ERROR);
                    populationError.setTitle("Error");
                    populationError.setContentText("Please enter a valid integer value for the number of nodes.");
                    populationError.show();
                } catch (IllegalArgumentException ex1) {
                    Alert populationError = new Alert(AlertType.ERROR);
                    populationError.setTitle("Error");
                    populationError.setContentText("Please enter a value greater than 1 for the number of nodes");
                    populationError.show();
                }

                if (validPopulation && validNodesNumber) {

                    nodes.clear(); //Declaring and generating nodes
                    generateNodes(nodes, numberOfNodes);

                    // Algorithm object modifications
                    geneticAlgo.modifyParameters(populationSize, nodes);
                    nearestAlgo.setNodes(nodes);
                    greedyAlgo.modifyNodes(nodes);

                    // Remove previous circles
                    for (Circle currCir : circles) {
                        if (visualizationRoot.getChildren().contains(currCir)) {
                            visualizationRoot.getChildren().remove(currCir);
                        }
                    }
                    circles.clear();



                    for (int i = 0; i < numberOfNodes; i++) { // Printing nodes as circles

                        Circle circle = new Circle(nodes.get(i).getX(), nodes.get(i).getY(), 10);
                        circle.setFill(Color.WHITE);
                        circles.add(circle);
                    }

                    for (Circle currCir : circles) {
                        visualizationRoot.getChildren().add(currCir);
                    }
                }
            }
        };

        recalcNodesButton.setOnAction(recalcNodesEvent);

        // Setting up "select algorithm" label
        Label selectLabel = new Label("Please select one of the following algorithms: ");
        selectLabel.setStyle("-fx-font-size: 22");
        selectLabel.setLayoutX(90);
        selectLabel.setLayoutY(210);
        selectionRoot.getChildren().add(selectLabel);


        ////////////////////////////////////////////////////
        ////////////// Setting up genetic button
        Button GeneticAlgorithmButton = new Button("Genetic Algorithm");

        EventHandler<ActionEvent> GeneticAlgorithmEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                    window.setScene(visualization);
                    window.setWidth(windowWidth);
                    window.setHeight(windowHeight);
                    buttonSelected.set(0, true); // genetic
                    buttonSelected.set(1, false); // nearest
                    buttonSelected.set(2, false); // greedy
                    buttonSelected.set(3, false); // manual

                    geneticAlgo.resetBestRecordedDistance();
                    bestDistance.set(0, Double.POSITIVE_INFINITY);
            }
        };

        GeneticAlgorithmButton.setOnAction(GeneticAlgorithmEvent);
        selectionRoot.getChildren().add(GeneticAlgorithmButton);
        GeneticAlgorithmButton.setLayoutX(120);
        GeneticAlgorithmButton.setLayoutY(280);
        GeneticAlgorithmButton.setStyle("-fx-font-size:20");

        ///////////////////////////////////////////////////
        //////////////Setting up nearest button
        Button nearestAlgorithmButton = new Button("Nearest neighbor Algorithm");

        EventHandler<ActionEvent> nearestAlgorithmEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                window.setScene(visualization);
                window.setWidth(windowWidth);
                window.setHeight(windowHeight);
                buttonSelected.set(0, false); // genetic
                buttonSelected.set(1, true); // nearest
                buttonSelected.set(2, false); // greedy
                buttonSelected.set(3, false); // manual
            }

        };

        nearestAlgorithmButton.setOnAction(nearestAlgorithmEvent);
        selectionRoot.getChildren().add(nearestAlgorithmButton);
        nearestAlgorithmButton.setLayoutX(120);
        nearestAlgorithmButton.setLayoutY(350);
        nearestAlgorithmButton.setStyle("-fx-font-size:20");

        ///////////////////////////////////////////////////
        //////////////Setting up greedy button
        Button greedyAlgorithmButton = new Button("Greedy Heuristic Algorithm");

        EventHandler<ActionEvent> greedyAlgorithmEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                window.setScene(visualization);
                window.setWidth(windowWidth);
                window.setHeight(windowHeight);
                buttonSelected.set(0, false); // genetic
                buttonSelected.set(1, false); // nearest
                buttonSelected.set(2, true); // greedy
                buttonSelected.set(3, false); // manual
            }
        };

        greedyAlgorithmButton.setOnAction(greedyAlgorithmEvent);
        selectionRoot.getChildren().add(greedyAlgorithmButton);
        greedyAlgorithmButton.setLayoutX(120);
        greedyAlgorithmButton.setLayoutY(420);
        greedyAlgorithmButton.setStyle("-fx-font-size:20");

        ///////////////////////////////////////////////////
        //////////////Setting up manual path button
        Button manualPathButton = new Button("Manual Path");

        EventHandler<ActionEvent> manualPathButtonEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {

                window.setScene(visualization);
                window.setWidth(windowWidth);
                window.setHeight(windowHeight);
                buttonSelected.set(0, false); // genetic
                buttonSelected.set(1, false); // nearest
                buttonSelected.set(2, false); // greedy
                buttonSelected.set(3, true);
            }
        };

        manualPathButton.setOnAction(manualPathButtonEvent);
        selectionRoot.getChildren().add(manualPathButton);
        manualPathButton.setLayoutX(120);
        manualPathButton.setLayoutY(490);
        manualPathButton.setStyle("-fx-font-size:20");

        ///////////////////////////////////////////////////
        /////////////////////////////////////// Setting up best distance text
        Text bestDistanceText = new Text();
        bestDistanceText.setFill(Color.WHITE);
        bestDistanceText.setX(20);
        bestDistanceText.setY(20);
        bestDistanceText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
        visualizationRoot.getChildren().add(bestDistanceText);

        ////////////////////////////////////// Setting up population count text
        Text populationCountText = new Text();
        populationCountText.setFill(Color.WHITE);
        populationCountText.setX(20);
        populationCountText.setY(40);
        populationCountText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));


        ///////////////////////////////////////////////////
        //////////////Setting up back button
        Button backButton = new Button("Go Back");

        EventHandler<ActionEvent> backEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                window.setScene(algorithmSelection);
                window.setWidth(windowWidth);
                window.setHeight(windowHeight - 200);
                buttonSelected.set(0, false); // genetic
                buttonSelected.set(1, false); // nearest
                buttonSelected.set(2, false); // greedy
                buttonSelected.set(3, false); // manual

                // Reset everything from the algorithms:

                populationCountText.setText("");
                bestDistanceText.setText("");

                // Clear labels from the manual path example
                if (manualPathStarted) {
                    for (Label label : circleLabels){
                        if (visualizationRoot.getChildren().contains(label)) {
                            visualizationRoot.getChildren().remove(label);
                        }
                    }
                    circleLabels.clear();
                }

                nearestPathFinished = false;
                greedyPathFinished = false;
                manualPathStarted = false;
                manualPathDistance = 0;
                manualPathOrder.clear();

                // Clear all lines from previous algorithms.
                for (Line line : lines) {
                    if (visualizationRoot.getChildren().contains(line)) {
                        visualizationRoot.getChildren().remove(line);
                    }
                }
                lines.clear();

                for (Line line : relativeLines) {
                    if (visualizationRoot.getChildren().contains(line)) {
                        visualizationRoot.getChildren().remove(line);
                    }
                }
                relativeLines.clear();
            }
        };

        backButton.setOnAction(backEvent);
        visualizationRoot.getChildren().add(backButton);
        backButton.setLayoutX(880);
        backButton.setLayoutY(715);
        backButton.setStyle("-fx-font-size:20");

        //////////////////////////////// Event Handler for the Manual Path
        EventHandler manualPathEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (manualPathStarted) {
                    for (int i = 0; i < nodes.size(); i++) {
                        // If the mouse click was on this node
                        if (event.getSceneX() >= nodes.get(i).getX() - 8 &&
                            event.getSceneX() <= nodes.get(i).getX() + 8 &&
                            event.getSceneY() >= nodes.get(i).getY() - 8 &&
                            event.getSceneY() <= nodes.get(i).getY() + 8)
                        {
                            manualPathOrder.add(nodes.get(i));
                            if (manualPathOrder.size() > 1) {  // If this isn't the first node selected
                                Node node1 = manualPathOrder.get(manualPathOrder.size() - 2), // Second to last node
                                     node2 = manualPathOrder.get(manualPathOrder.size() - 1); // Last node

                                Line line = new Line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
                                line.setStroke(Color.DARKBLUE);
                                line.setStrokeWidth(5);
                                lines.add(line);

                                manualPathDistance += manualPath.calcEdgeLength(node1, node2);
                            }
                        }
                    }
                }
            }
        };


        ///////////////////////////////////////////////////////////////////// Event handler for algorithms
        EventHandler event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (buttonSelected.get(0) == true) { // Genetic Algorithm was selected

                    int order[] = geneticAlgo.runGeneticAlgorithm();
                    int relativeOrder[] = geneticAlgo.getRelativePath();

                    if (!visualizationRoot.getChildren().contains(populationCountText)) {
                        visualizationRoot.getChildren().add(populationCountText);
                    }
                    populationCountText.setText("Populations generated: " + geneticAlgo.getPopulationCount());

                    if (geneticAlgo.getBestRecordedDistance() < bestDistance.get(0)) {

                        bestDistance.set(0, geneticAlgo.getBestRecordedDistance());

                        bestDistanceText.setText("Best Distance: " + String.format("%.2f", geneticAlgo.getBestRecordedDistance()));

                        for (Line curr : lines) {
                            if (visualizationRoot.getChildren().contains(curr)) {
                                visualizationRoot.getChildren().remove(curr);
                            }
                        }

                        lines.clear();

                        for (int i = 0; i < geneticAlgo.getNumberOfNodes(); i++) {

                            if ((i + 1) >= geneticAlgo.getNumberOfNodes()) {
                                break;
                            }

                            Line line = new Line(geneticAlgo.getNodes().get(order[i]).getX(), geneticAlgo.getNodes().get(order[i]).getY(),
                                    geneticAlgo.getNodes().get(order[i + 1]).getX(), geneticAlgo.getNodes().get(order[i + 1]).getY());
                            line.setStroke(Color.DARKBLUE);
                            line.setStrokeWidth(7);

                            lines.add(line);
                        }

                        for (Line curr : lines) {
                            visualizationRoot.getChildren().add(curr);
                        }
                    }

                    for (Line curr : relativeLines) {
                        if (visualizationRoot.getChildren().contains(curr)) {
                            visualizationRoot.getChildren().remove(curr);
                        }
                    }

                    relativeLines.clear();

                    for (int i = 0; i < geneticAlgo.getNumberOfNodes(); i++) {

                        if ((i + 1) >= geneticAlgo.getNumberOfNodes()) {
                            break;
                        }

                        Line line = new Line(geneticAlgo.getNodes().get(relativeOrder[i]).getX(), geneticAlgo.getNodes().get(relativeOrder[i]).getY(),
                                geneticAlgo.getNodes().get(relativeOrder[i + 1]).getX(), geneticAlgo.getNodes().get(relativeOrder[i + 1]).getY());
                        line.setStroke(Color.DARKBLUE);
                        line.setStrokeWidth(2);
                        line.setOpacity(0.6);

                        relativeLines.add(line);
                    }

                    for (Line curr : relativeLines) {
                        visualizationRoot.getChildren().add(curr);
                    }

                } else if (buttonSelected.get(1) == true) { // nearest Algorithm was selected

                    if (nearestPathFinished == false) {
                        ArrayList<Integer> tspPath = nearestAlgo.calcPath();

                        int pathDistance = 0;

                        for (int i = 0; i < tspPath.size() - 1; i++) {
                            Node node1 = nodes.get(tspPath.get(i)),
                                 node2 = nodes.get(tspPath.get(i + 1));

                            pathDistance += nearestAlgo.calcEdgeLength(node1, node2);

                            Line line = new Line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
                            line.setStroke(Color.DARKBLUE);
                            line.setStrokeWidth(5);

                            lines.add(line);
                        }

                        bestDistanceText.setText("Nearest Neighbour Algorithm distance: " + pathDistance);

                        nearestPathFinished = true;
                    }
                    for (Line line : lines) {
                        if (!visualizationRoot.getChildren().contains(line)) {
                            visualizationRoot.getChildren().add(line);
                            break;
                        }
                    }

                } else if (buttonSelected.get(2) == true) { // greedy algorithm was selected

                    if (greedyPathFinished == false) {
                        ArrayList<GreedyAlgorithm.Edge> tspPath = greedyAlgo.calcPath();

                        System.out.println(greedyAlgo.getNodes().size() + "!!");

                        int pathDistance = 0; // updated as each edge is added

                        for (int i = 0; i < tspPath.size(); i++) {
                            // Access the nodes referenced in the edge
                            Node node1, node2;
                            node1 = greedyAlgo.getNodes().get(tspPath.get(i).getNode1Index());
                            node2 = greedyAlgo.getNodes().get(tspPath.get(i).getNode2Index());

                            pathDistance += tspPath.get(i).getLength();

                            Line line = new Line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
                            line.setStroke(Color.DARKBLUE);
                            line.setStrokeWidth(5);

                            lines.add(line);
                        }

                        bestDistanceText.setText("Greedy algorithm distance: " + pathDistance);

                        greedyPathFinished = true;
                    }

                    for (Line line : lines) {
                        if (!visualizationRoot.getChildren().contains(line)) {
                            visualizationRoot.getChildren().add(line);
                            break;
                        }
                    }
                }
                else if (buttonSelected.get(3) == true) { // Manual path selected
                    if (manualPathStarted == false) {
                        for (int i = 0; i < circles.size(); i++) {
                            circles.get(i).setOnMouseClicked(manualPathEvent);
                            Label label = new Label(String.valueOf(i));
                            label.setLayoutX(circles.get(i).getCenterX() + 10);
                            label.setLayoutY(circles.get(i).getCenterY());
                            label.setTextFill(Paint.valueOf("WHITE"));

                            circleLabels.add(label);
                            visualizationRoot.getChildren().add(circleLabels.get(i));
                        }

                        manualPathStarted = true;
                    }

                    for (Line line : lines) {
                        if (!visualizationRoot.getChildren().contains(line)) {
                            visualizationRoot.getChildren().add(line);
                            break;
                        }
                    }

                    bestDistanceText.setText("Manual distance: " + manualPathDistance);
                }
            }
        };



        KeyFrame frame = new KeyFrame(Duration.millis(100), event);
        Timeline theTimeline = new Timeline(frame);

        theTimeline.setCycleCount(Timeline.INDEFINITE);
        theTimeline.play();

        window.setWidth(windowWidth);
        window.setHeight(windowHeight - 200);
        //primaryStage.setResizable(false);
        window.setTitle("Travelling Salesman");
        window.setScene(algorithmSelection);
        window.show();
    }

    public static void generateNodes(ArrayList<Node> nodes, int numberOfNodes) {

        Random rand = new Random();

        for (int i = 0; i < numberOfNodes; i++) {

            int randomNumberX = rand.nextInt(windowWidth - 80);
            int randomNumberY = rand.nextInt(windowHeight - 80);

            if (randomNumberX < 50) // If number is too close to window width border adds some points
            {
                randomNumberX += 50;
            }

            if (randomNumberY < 50) // If number is too close to window height border adds some points
            {
                randomNumberY += 50;
            }

            nodes.add(new Node(randomNumberX, randomNumberY));
        }
    }
}
