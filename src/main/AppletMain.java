package main;

import static nicolas.StatoCella.*;
import java.io.File;
import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import nicolas.GrigliaConOrigineFactory;
import nicolas.ICella2;
import nicolas.IGrigliaConOrigine;
import processing.core.PApplet;
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
		COLORE_COMPLEMENTO,COLORE_ORIGINE,COLORE_REGINA,COLORE_CONTESTO;
	
	
	@Override
	public void settings() {
		var file = new File("config.json");
		if (file.exists()==false) {
			System.err.println("Error: file %s not found".formatted(file.toString()));
			return;
		}
		
		JSONObject json = PApplet.loadJSONObject(file).getJSONObject("applet");
		
		size(json.getInt("width", 1200), json.getInt("height", 600));
		
		
		palette = Stream.of(json.getStringList("palette").toArray())
		.map(e -> e.substring(2))
		.mapToInt(e -> (int)Long.parseLong(e, 16)|0xff000000)
		.toArray();
		
		COLORE_OSTACOLO		 = palette[6];
		COLORE_DESTINAZIONE	 = palette[7];
		COLORE_LANDMARK		 = palette[8];
		COLORE_FRONTIERA	 = palette[4];
		COLORE_COMPLEMENTO	 = palette[3];
		COLORE_ORIGINE		 = palette[0];
		COLORE_REGINA		 = palette[1];
		COLORE_CONTESTO		 = palette[2];
		
		
		
		griglia = new LettoreGriglia().crea(file.toPath());
		w = griglia.width();
		h = griglia.height();
		s = width/w;
		
		
		if (json.getBoolean("demo", false)) {
			griglia = GrigliaConOrigineFactory.creaV0(griglia, 2, 2);
			LANDMARK.addTo(griglia.getCellaAt(14, 9));
			DESTINAZIONE.addTo(griglia.getCellaAt(w-1, h-1));
		}
		
		//griglia.print();
		
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
			else fill(palette[5]);
			
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
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		
		switch(e.getButton()) {
		case LEFT:
			OSTACOLO.toggleTo(griglia.getCellaAt(x, y));
			break;
		case RIGHT:
			griglia = GrigliaConOrigineFactory.creaV0(griglia, x, y);
			break;
		case CENTER:
			DESTINAZIONE.toggleTo(griglia.getCellaAt(x, y));
			break;
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//if (e.isControlDown()==false) return;
		
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		
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
			if (griglia instanceof IGrigliaConOrigine gco) {
				System.out.println(gco.toJSON());
			}
			break;
		case 'C':
			griglia.forEach((x,y) -> {
				var cella = griglia.getCellaAt(x, y);
				if (cella.isNot(OSTACOLO)) {
					cella.setStato(0);
				}
			});
			break;
		}
	}
	
}
