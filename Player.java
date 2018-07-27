package myPlayer;

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;

import javafx.scene.media.MediaPlayer;

import javafx.stage.FileChooser;

import javafx.stage.Stage;

import javafx.util.Duration;

public class Player extends Application {

	private Double endTime;

	private Double currentTime;

	private Media media;

	private MediaPlayer mplayer;
	private ListView<String> list = new ListView<>();

	ObservableList<String> data = FXCollections.observableArrayList();

	Label current_Time = new Label();
	Label volume = new Label("音量");
	Label filename = new Label();
	Slider slTime = new Slider();
	Slider sVolume = new Slider(); // 音量
	FileChooser fileChooser = new FileChooser();
	BorderPane pane = new BorderPane();

	Button btnPlay = new Button("播放");
	Button btnReplay = new Button("停止");

	Button lastbtn = new Button("上一曲");
	Button nextbtn = new Button("下一曲");

	Button btnOpen = new Button("添加歌曲"); // 实际初始化在这里完成
	Button del = new Button("删除歌曲");
	Button delall = new Button("清空列表");

	@Override

	public void start(Stage primaryStage)

	{

		list.setPrefHeight(565);
		list.setPrefWidth(400);
		list.setStyle("-fx-background-color: #98FB98");
		filename.setStyle("-fx-text-fill: #BF3EFF");
		volume.setStyle("-fx-text-fill: #BF3EFF");
		current_Time.setStyle("-fx-text-fill: #BF3EFF");
		slTime.setPrefWidth(200);
		sVolume.setPrefWidth(100);
		sVolume.setValue(50);

		btnPlay.setOnAction(e -> {
			if (list.getSelectionModel().getSelectedItem() != null) {
				if (btnPlay.getText().equals("播放")) {
					btnPlay.setText("暂停");
					filename.setText("正在播放:" + list.getSelectionModel().getSelectedItem());
					mplayer.play();
				}
				else {
					filename.setText("");
					btnPlay.setText("播放");
					mplayer.pause();
				}
			}
		});

		btnReplay.setOnAction(e -> {
			if (list.getSelectionModel().getSelectedItem() != null) {
				mplayer.stop();
				btnPlay.setText("播放");
				filename.setText("");
			}
		});

		lastbtn.setOnAction(e -> {
			list.getSelectionModel().selectPrevious();
		});

		nextbtn.setOnAction(e -> {
			list.getSelectionModel().selectNext();
		});

		del.setOnAction(e -> {
			if (list.getSelectionModel().getSelectedItem() != null) {
				list.getItems().remove(list.getSelectionModel().getSelectedIndex());
				filename.setText("");
				mplayer.stop();
				current_Time.setText(toTime(0.0) + "/" + toTime(0.0));
				btnPlay.setText("播放");
			}
		});

		delall.setOnAction(e -> {
			if (list.getSelectionModel().getSelectedItem() != null) {
				mplayer.stop();
				current_Time.setText(toTime(0.0) + "/" + toTime(0.0));
				btnPlay.setText("播放");
			}
			try {
				list.getItems().removeAll(list.getItems());

			} catch (Exception e2) {
			}

		});

		btnOpen.setOnAction(e -> {
			List<File> audiofile = fileChooser.showOpenMultipleDialog(new Stage()); // 在一个新Stage里放FileChooser
			if (audiofile != null) {
				audiofile.stream().forEach((file) -> {
					String[] ss = file.toURI().toString().split("\\/");
					data.add(ss[ss.length - 1]);
				});
				list.setItems(data);
			}
			list.getSelectionModel().selectedItemProperty()
					.addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
						try {
							if (audiofile != null) {
								audiofile.stream().forEach((file) -> {
									filename.setText("正在播放:" + new_val);
									String str = file.toURI().toString();
									String[] ss = str.split("\\/");
									if (ss[ss.length - 1].endsWith(new_val)) {
										play(str);
									}
								});
							}

						} catch (Exception e2) {
						}
					});

		});

		BorderPane left = new BorderPane();
		HBox bottom = new HBox(10);
		HBox top = new HBox();
		HBox left_bottom = new HBox(10);
		VBox center = new VBox();

		bottom.setPadding(new Insets(5));
		bottom.setAlignment(Pos.BASELINE_CENTER);
		bottom.setStyle("-fx-background-color: #00FFFF");
		top.setAlignment(Pos.CENTER);

		top.setStyle("-fx-background-color: #00FFFF");
		left.setPadding(new Insets(3));
		left.setStyle("-fx-background-color: #00FFFF");
		left_bottom.setAlignment(Pos.BASELINE_CENTER);
		center.setAlignment(Pos.BASELINE_LEFT);
		center.setPadding(new Insets(5)); // 内边距
		center.setSpacing(10); // 节点间距
		center.setStyle("-fx-background-color: #00FFFF");

		bottom.getChildren().addAll(lastbtn, btnReplay, btnPlay, nextbtn, volume, sVolume);
		top.getChildren().addAll(btnOpen, filename);

		center.getChildren().addAll(btnOpen, del, delall);
		left_bottom.getChildren().addAll(current_Time, slTime);
		left.setRight(list);
		left.setBottom(left_bottom);

		pane.setTop(top);
		pane.setCenter(center);
		pane.setLeft(left);
		pane.setBottom(bottom);

		Scene scene = new Scene(pane, 500, 600);

		primaryStage.setTitle("音频播放器");

		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public static void main(String[] args) {

		launch(args);

	}

	private void play(String s) {
		if (btnPlay.getText().equals("暂停")) {
			mplayer.stop();
		}
		btnPlay.setText("暂停");
		media = new Media(s);
		mplayer = new MediaPlayer(media);

		mplayer.setOnReady(() -> {

			endTime = mplayer.getStopTime().toSeconds();

		});

		mplayer.setOnEndOfMedia(() -> {

			mplayer.stop();

			mplayer.seek(Duration.ZERO);

			btnPlay.setText("停止");

		});

		mplayer.currentTimeProperty().addListener(ov -> {

			currentTime = mplayer.getCurrentTime().toSeconds();

			current_Time.setText(toTime(currentTime) + "/" + toTime(endTime));

			slTime.setValue(currentTime / endTime * 100);

		});

		slTime.valueProperty().addListener(ov -> {

			if (slTime.isValueChanging()) {

				mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));
			}
		});

		mplayer.volumeProperty().bind(sVolume.valueProperty().divide(100)); // 音量调节

		mplayer.play();

	}

	private String toTime(Double seconds) {

		Integer count = seconds.intValue();

		Integer Hours = count / 3600;

		count = count % 3600;

		Integer Minutes = count / 60;

		count = count % 60;

		String str = Hours.toString() + ":" + Minutes.toString() + ":" + count.toString();

		return str;

	}

}