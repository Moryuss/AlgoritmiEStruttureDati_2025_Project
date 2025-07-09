package main;

import static nicolas.StatoCella.*;
import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CompitoTreImpl_NoRequisitiFunzionali;
import matteo.ICammino;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import nicolas.Utils;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class AppletMain extends PApplet {
	
	public static void main(String[] args) {
		PApplet.main(AppletMain.class, args);
	}
	
	
	int w,h,s;
	int[] palette;
	IGriglia<?> griglia;
	
	int COLORE_OSTACOLO,COLORE_DESTINAZIONE,COLORE_LANDMARK,COLORE_FRONTIERA,
		COLORE_COMPLEMENTO,COLORE_ORIGINE,COLORE_REGINA,COLORE_CONTESTO,COLORE_BASE;
	
	
	@Override
	public void settings() {
		var file = new File("config.json");
		if (file.exists()==false) {
			System.err.println("Error: file %s not found".formatted(file.toString()));
			return;
		}
		
		var config = PApplet.loadJSONObject(file);
		JSONObject json = config.getJSONObject("applet");
		
		size(json.getInt("width", 1200), json.getInt("height", 600));
		
		
		palette = Stream.of(json.getStringList("palette").toArray())
		.mapToInt(e -> Utils.parseHex(e).orElse(0))
		.map(n->n|0xff000000)
		.toArray();
		
		COLORE_OSTACOLO		 = palette[6];
		COLORE_DESTINAZIONE	 = palette[7];
		COLORE_LANDMARK		 = palette[8];
		COLORE_FRONTIERA	 = palette[4];
		COLORE_COMPLEMENTO	 = palette[3];
		COLORE_ORIGINE		 = palette[0];
		COLORE_REGINA		 = palette[1];
		COLORE_CONTESTO		 = palette[2];
		COLORE_BASE			 = palette[5];
		
		
		griglia = Optional.ofNullable(config.getString("load", null))
		.<IGriglia<?>>map(str -> Utils.loadSimple(new File(str)))
		.orElseGet(()->new LettoreGriglia().crea(file.toPath()));
		
		w = griglia.width();
		h = griglia.height();
		s = width/w;
		
		println("w=%d, h=%d".formatted(w, h));
		
		
		if (config.hasKey("load")==false && json.getBoolean("demo", false)) {
//			griglia = GrigliaConOrigineFactory.creaV0(griglia, 2, 2);
//			LANDMARK.addTo(griglia.getCellaAt(14, 9));
//			DESTINAZIONE.addTo(griglia.getCellaAt(w-1, h-1));
			
			griglia = GrigliaConOrigineFactory.creaV0(griglia, 6, 4);
		}
		
		griglia.print();
		
	}
	
	
	@Override
	public void setup() {
		textAlign(CENTER, CENTER);
		ellipseMode(CORNER);
		textSize(20);
	}
	
	@Override
	public void draw() {
		
		background(255);
		fill(0);
		noStroke();
		
		griglia.forEach((j,i) -> {
			int n = griglia.getCellaAt(j, i).stato();
			
			if (OSTACOLO.check(n)) fill(COLORE_OSTACOLO);
			else if (DESTINAZIONE.check(n)) fill(COLORE_DESTINAZIONE);
			else if (LANDMARK.check(n)) fill(COLORE_LANDMARK);
			//else if (FRONTIERA.check(n)) fill(COLORE_FRONTIERA);
			else if (COMPLEMENTO.check(n)) fill(COLORE_COMPLEMENTO);
			else if (ORIGINE.check(n)) fill(COLORE_ORIGINE);
			else if (REGINA.check(n)) fill(COLORE_REGINA);
			else if (CONTESTO.check(n)) fill(COLORE_CONTESTO);
			else fill(COLORE_BASE);
			
			rect(j*s, i*s, s, s);
			
			if (FRONTIERA.check(n)) {
				fill(100, 100);
				circle(j*s, i*s, s);
			}
			
			textAlign(CENTER, CENTER);
			fill(150);
			text(n, (0.5f+j)*s, (0.5f+i)*s);
			
			if (griglia.getCellaAt(j, i) instanceof ICella2 c2) {
				textAlign(LEFT, TOP);
				if (c2.isUnreachable()) text("+∞", (0f+j)*s, (+i)*s);
				else {
					//text("%.2f".formatted(c2.distanzaDaOrigine()), j*s, i*s);
					text(c2.distanzaTorre()+":"+c2.distanzaAlfiere(), j*s, i*s);
				}
			}
			
		});
		
		if (cammino!=null) {
			stroke(0, 150, 0);
			noFill();
			pushMatrix();
			scale(s);
			translate(0.5f, 0.5f);
			strokeWeight(0.1f);
			
			int[] prev = {0,0,0};
			cammino.landmarks().forEach(lm -> {
				if (prev[2]>0) {
					dashedLine(prev[0], prev[1], lm.x(), lm.y(), 0.2f, 0.4f);
				}
				prev[0] = lm.x();
				prev[1] = lm.y();
				prev[2] = 1;
			});
			popMatrix();
		}
		
	}
	
	ICella2 O,D;
	ICammino cammino;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		
		switch(e.getButton()) {
		case LEFT:
			clearNonOStacoli(e.isControlDown());
			O = D = null;
			cammino = null;
			var g = GrigliaConOrigineFactory.creaV0(griglia, x, y);
			O = g.getCellaAt(x, y);
			griglia = g;
			break;
		
		case RIGHT:
			if (O==null) return;
			griglia.forEach((j,i) -> {
				var cella = griglia.getCellaAt(j, i);
				if (LANDMARK.matches(cella.stato())) {
					LANDMARK.removeTo(cella);
				}
			});
			maskGriglia((DESTINAZIONE.mask()-1) | OSTACOLO.mask());
			cammino = null;
			
			DESTINAZIONE.toggleTo(griglia.getCellaAt(x, y));
			D = (ICella2)griglia.getCellaAt(x, y);
			D.setStato(DESTINAZIONE.value());
			
			try {
				var compitoTreImpl = new CompitoTreImpl_NoRequisitiFunzionali();
				cammino = compitoTreImpl.camminoMin(griglia, O, D);
				cammino.landmarks().forEach(lm -> {
					LANDMARK.addTo(griglia.getCellaAt(lm.x(), lm.y()));
				});
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			break;
		
		case CENTER:
			OSTACOLO.toggleTo(griglia.getCellaAt(x, y));
			break;
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//if (e.isControlDown()==false) return;
		
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		x = constrain(x, 0, w-1);
		y = constrain(y, 0, h-1);
		
		switch(e.getButton()) {
		case LEFT:
			griglia.getCellaAt(x, y).setStato(OSTACOLO.value());
			break;
		case RIGHT:
			OSTACOLO.removeTo(griglia.getCellaAt(x, y));
			break;
		case CENTER:
			break;
		}
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case 'P':			
			if (e.isShiftDown()) {
			
				var str = griglia.collect(
						c -> c.is(OSTACOLO) ? "1" : " ", 
						Collectors.joining(",", "[", ",]"), 
						Collectors.joining(",\n", "[\n", "\n]"));
				
				println(str);
				
			} else {
				var str = griglia.collect(
						c -> "%02x".formatted(c.stato()), 
						Collectors.joining(",", "[", ",]"), 
						Collectors.joining(",\n", "[\n", "\n]"));
				
				println(str);
			}
			
			break;
		case 'C':
			clearNonOStacoli(e.isControlDown());
			O = D = null;
			cammino = null;
			break;
		case 'K':
			if (griglia instanceof IGrigliaConOrigine gco) {
				griglia = gco.addObstacle(gco.convertiChiusuraInOstacolo());
			}
			break;
		}
	}


	private void clearNonOStacoli(boolean isControlDown) {
		var mask = isControlDown ? 0 : OSTACOLO.value();
		maskGriglia(mask);
	}
	
	private void maskGriglia(int mask) {
		griglia.forEach((x,y) -> {
			var cella = griglia.getCellaAt(x, y);
			cella.setStato(cella.stato()&mask);
		});
	}
	
	public void dashedLine(float x1, float y1, float x2, float y2, float dl, float ds) {
		beginShape(LINES);
		
		var start = new PVector(x1, y1);
		var end = new PVector(x2, y2);
		var delta = PVector.sub(end, start);
		var n = delta.mag() / (dl+ds);
		var delta2 = delta.copy().setMag(ds);
		delta.setMag(dl);
		
		for (; n>0; n--) {
			vertex(start.x, start.y);
			start.add(delta);
			vertex(start.x, start.y);
			start.add(delta2);
		}
		
		endShape();
	}
	
}
