package com.company.aws.dashboard;

import java.net.URL;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Runner extends Application {

	private Scene scene;
	MyBrowser myBrowser;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("IAV Automotive Engineering");
		myBrowser = new MyBrowser();
		scene = new Scene(myBrowser, 1350, 700, Color.web("#666970"));
		scene.getStylesheets().add("BrowserToolbar.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args
	 * the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	class MyBrowser extends Region {
		final String hellohtml = "hello.html";
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();

		public MyBrowser() {
			ClassLoader classLoader = getClass().getClassLoader();
			URL urlHello = classLoader.getResource("hello.html");
			webEngine.load(urlHello.toExternalForm());
			getChildren().add(webView);
		}

		private Node createSpacer() {
			Region spacer = new Region();
			HBox.setHgrow(spacer, Priority.ALWAYS);
			return spacer;
		}

		@Override
		protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(webView, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
		}

		@Override
		protected double computePrefWidth(double height) {
			return 750;
		}

		@Override
		protected double computePrefHeight(double width) {
			return 500;
		}
	}
}
