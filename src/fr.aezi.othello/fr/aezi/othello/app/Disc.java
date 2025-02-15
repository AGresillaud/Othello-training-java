package fr.aezi.othello.app;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Disc extends Group{
	private double zPos ;
	private boolean whiteUp = false;

	public Disc(double diameter, double thickness, boolean white) {
		zPos = -thickness;
		Cylinder faceNoire = new Cylinder(diameter / 2, thickness / 2);
		
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.DARKBLUE);
		material.setDiffuseColor(Color.BLACK);
		faceNoire.setMaterial(material);
		faceNoire.setRotationAxis(Rotate.X_AXIS);
		faceNoire.setRotate(90);
		faceNoire.setAccessibleText("FACE NOIRE");
		
		Cylinder faceBlanche = new Cylinder(diameter / 2, thickness / 2);
		material = new PhongMaterial();
		material.setDiffuseColor(Color.WHITE);
		material.setSpecularColor(Color.WHITESMOKE);
		faceBlanche.setMaterial(material);
		faceBlanche.setRotationAxis(Rotate.X_AXIS);
		faceBlanche.setRotate(90);
		faceBlanche.setAccessibleText("FACE BLANCHE");

		this.getChildren().addAll(faceNoire, faceBlanche);
		
		faceBlanche.setTranslateZ(thickness / 2);
		this.setTranslateZ(zPos);
		this.setRotationAxis(new Point3D(1, 1, 0));
		
		setWhiteUp(white);
		
		EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
            	if(t.getButton() == MouseButton.SECONDARY){
                    turnDisc();
            	}
            }
        };
		
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
	}
	
	public Disc(double diameter, double thickness, double x, double y, boolean white) {
		this(diameter, thickness, white);
		this.setTranslateX(x);
		this.setTranslateY(y);
	}
	
	public void setWhiteUp(boolean white) {
		this.whiteUp = white;
		setRotate(getRotationAngle());
	}
	
	private double getRotationAngle() {
		if(whiteUp) {
			return 180.0;
		}
		else {
			return 0.0;
		}
	}
	private Tuple<Timeline> animationToRun ;
	public void setDiscToBeTurned(double delay) {
		System.out.println("delay: "+delay);
		whiteUp = !whiteUp;
		animationToRun = runDiscRotation(getRotationAngle());
		
		animationToRun.first.setDelay(Duration.seconds(delay));
		animationToRun.second.setDelay(Duration.seconds(delay + 0.1));

		System.out.println(animationToRun.first.getDelay());
		System.out.println(animationToRun.second.getDelay());
	}
	
	public void runItNow() {
		if(animationToRun != null) {
			animationToRun.first.play();
			animationToRun.second.play();
		}
	}
	
	public void turnDisc() {
		whiteUp = !whiteUp;
		
		Tuple<Timeline> myTls = runDiscRotation(getRotationAngle());
		myTls.first.play();
		myTls.second.play();
	}
	
	private static final double TL_FALL= 4.0;
	private static final double TL_LIFT= 1.0;

	private Tuple<Timeline> runDiscRotation(double newAngle) {
		setRotationAxis(new Point3D(1, 1, 0));

		KeyValue plusTranslate = new KeyValue(translateZProperty(), -50, Interpolator.EASE_OUT);
		KeyValue minusTranslate = new KeyValue(translateZProperty(), zPos, Interpolator.EASE_IN);
		KeyValue kvAxis = new KeyValue(rotationAxisProperty(), new Point3D(1, 0, 0));
		Timeline tl = new Timeline();
		tl.setRate(TL_LIFT);
		tl.getKeyFrames().add(new KeyFrame(Duration.seconds(0.3), plusTranslate));

		KeyValue kvValue = new KeyValue(rotateProperty(), newAngle);
		Timeline tlTurn = new Timeline();
		tlTurn.setRate(TL_LIFT);
		tlTurn.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2), kvValue, kvAxis));
		Timeline tlFall = new Timeline();
		tlFall.setRate(TL_FALL);
		tlFall.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), minusTranslate));

		tlTurn.setDelay(Duration.seconds(0.1));
		tl.setOnFinished((event)->{
			tlFall.play();
		});
		return new Tuple<Timeline>(tl, tlTurn);
	}
	class Tuple<T> {
		Tuple(T first, T second ){
			this.first = first;
			this.second = second;
		}
		T first ;
		T second ;
	}
}
