package app.controllers;

import agents.interfaces.AgentInterface;
import agents.rl.RLAgent;
import game.controllers.BoardController;
import game.interfaces.BoardInterface;
import game.models.Board;
import game.helpers.GameType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static game.helpers.GameType.DIAMOND;
import static game.helpers.GameType.TRIANGLE;


public class MainMenuController {

    @FXML ToggleGroup diamond_or_triangle;
    @FXML Spinner<Integer> sizeSpinner;
    @FXML VBox holesBox;
    @FXML Pane boardPaneOuter;
    @FXML Button run;
    @FXML TextField episodes, actorLR, actorEDR, actorDF, epsilon, epsilonDecayRate, criticLR, criticEDR, criticDF, frameRate, nnText;
    @FXML CheckBox includeED, useVA;


    Pane board;
    private BoardInterface boardController;
    private AgentInterface agent;
    private int size = 1;
    private GameType gameType;
    private List<String> checkBoxes = new ArrayList<>();


    @FXML
    public void train() {

        if(!useVA.isSelected()) {
            agent = new RLAgent(boardController, Integer.valueOf(episodes.getText()), Double.valueOf(actorLR.getText()),
                    Double.valueOf(criticLR.getText()), Double.valueOf(actorEDR.getText()),
                    Double.valueOf(criticEDR.getText()), Double.valueOf(actorDF.getText()),
                    Double.valueOf(criticDF.getText()), Double.valueOf(epsilon.getText()),
                    Double.valueOf(epsilonDecayRate.getText()), new ArrayList<>());
        } else {
            List<Integer> layers = Arrays.stream(nnText.getText().split(",")).map(Integer::valueOf).collect(Collectors.toList());
            agent = new RLAgent(boardController, Integer.valueOf(episodes.getText()), Double.valueOf(actorLR.getText()),
                    Double.valueOf(criticLR.getText()), Double.valueOf(actorEDR.getText()),
                    Double.valueOf(criticEDR.getText()), Double.valueOf(actorDF.getText()),
                    Double.valueOf(criticDF.getText()), Double.valueOf(epsilon.getText()),
                    Double.valueOf(epsilonDecayRate.getText()), layers);
        }


        boolean finished = agent.train();

    }

    @FXML
    public void show() {
        System.out.println(frameRate.getText());
        agent.show(Integer.valueOf(frameRate.getText()));
    }

    @FXML
    public void initialize() {
        initSpinner();
    }

    private void initSpinner() {
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
    }


    @FXML
    public void spinnerChange() {
        int newValue = sizeSpinner.getValue();
        holesBox.getChildren().clear();
        this.size = newValue;
        int totalCells = gameType == DIAMOND ? size*size : (size*size + size) / 2;
        for (int i = 0; i<totalCells; i++){
            String name = Board.NAMES.get(i);

            CheckBox btn = new CheckBox();
            btn.setText(name);
            btn.setOnAction((ActionEvent e) -> checkboxClick());
            holesBox.getChildren().add(btn);
        }

        checkboxClick();
    }

    @FXML
    public void checkboxClick() {
        List<String> checkBoxes = new ArrayList<>();
        for (Node box : holesBox.getChildren()){
            if (((CheckBox) box).isSelected()){
                checkBoxes.add(((CheckBox) box).getText());
            }
        }
        this.checkBoxes = checkBoxes;
        renderBoard();
    }


    public void handleGameTypeToggle() {
        RadioButton selectedToggle = (RadioButton) diamond_or_triangle.getSelectedToggle();
        String selectedToggleText = selectedToggle.getText();
        this.gameType = selectedToggleText.equals("diamond")? DIAMOND : TRIANGLE;
        spinnerChange();
    }


    public void renderBoard() {
        try{
            FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/views/board.fxml"));
            board = boardLoader.load();
            this.boardController = boardLoader.getController();
            boardController.generateBoard(gameType, size, checkBoxes);
            boardPaneOuter.getChildren().add(board);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}

