package com.github.pkrysztofiak.rxjavafx.rxjavafxdemo.concurrency;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ParallelTasksApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Button button = new Button("Start");
        Label label = new Label();
        HBox hBox = new HBox(button, label);
        stage.setScene(new Scene(hBox));
        stage.show();

        JavaFxObservable.actionEventsOf(button)
        .flatMap(actionEvent -> Observable.range(1, 4))
        .flatMap(i -> Observable.just(i)
                .subscribeOn(Schedulers.newThread())
                .map(this::runLongProcess))
        .observeOn(JavaFxScheduler.platform())
        .scan(0, (aggregator, next) -> ++aggregator)
        .map(String::valueOf)
        .subscribe(label::setText);
    }

    private int runLongProcess(int i) {
        try {
            Thread.sleep(new Random().nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " i=" + i);
        return i;
    }
}