package main;

import static nicolas.StatoCella.*;
import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import francesco.IGriglia;
import francesco.implementazioni.LettoreGriglia;
import matteo.CompitoTreImplementation;
import matteo.ICammino;
import matteo.IProgressoMonitor;
import nicolas.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;
import processing.event.Event;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class AppletMain extends PApplet {
	
	public static void main(String[] args) {
		PApplet.main(AppletMain.class, args);
	}
	
	
	int w,h,s;
	int[] palette, colors;
	IGriglia<?> griglia;
	
	int COLORE_OSTACOLO,COLORE_DESTINAZIONE,COLORE_LANDMARK,COLORE_FRONTIERA,
		COLORE_COMPLEMENTO,COLORE_ORIGINE,COLORE_REGINA,COLORE_CONTESTO,COLORE_BASE;
	boolean showText;
	
	
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
		
		colors = Stream.of(json.getString("colors", "\"0x404040,0x808080,0xff0000,0xff6a00,0xffd800,0xb6ff00,0x4cff00,0x00ff21,0x00ff90,0x00ffff,0x0094ff,0x0026ff,0x4800ff,0xb200ff,0xff00dc,0xff006e\"").split("[\\[\",\\]\\s]+"))
		.skip(1)
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
		
		
		if (config.hasKey("load")) {
			griglia = loadGriglia(config);
		}
		
		if (griglia==null) {
			griglia = new LettoreGriglia().crea(file.toPath());
		}
		
		w = griglia.width();
		h = griglia.height();
		s = width/w;
		showText = json.getBoolean("showText", false);
		
		println("w=%d, h=%d".formatted(w, h));
		
		griglia.print();
		
	}
	
	
	private static IGriglia<?> loadGriglia(JSONObject config) {
		var load = config.getJSONObject("load");
		if (!load.hasKey("path")) {
			System.err.println("config.load non ha l'attributo \"path\"");
			return null;
		}
		if (!load.hasKey("name")) {
			System.err.println("config.load non ha l'attributo \"name\"");
			return null;
		}
		var src = PApplet.loadJSONObject(new File(load.getString("path")));
		var nomeGriglia = load.getString("name");
		var toLoad = src.getJSONArray(nomeGriglia);
		
		if (toLoad==null) {
			System.err.println("Non è stata trovata la griglia dal nome "+nomeGriglia);
			return null;
		}
		
		return Utils.loadSimple(toLoad);
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
		rectMode(CORNER);
		
		griglia.forEach((j,i) -> {
			int n = griglia.getCellaAt(j, i).stato();
			
			if (OSTACOLO.check(n)) fill(COLORE_OSTACOLO);
			else if (ORIGINE.check(n)) fill(COLORE_ORIGINE);
			else if (DESTINAZIONE.check(n)) fill(COLORE_DESTINAZIONE);
			else if (LANDMARK.check(n)) fill(COLORE_LANDMARK);
			//else if (FRONTIERA.check(n)) fill(COLORE_FRONTIERA);
			else if (COMPLEMENTO.check(n)) fill(COLORE_COMPLEMENTO);
			else if (REGINA.check(n)) fill(COLORE_REGINA);
			else if (CONTESTO.check(n)) fill(COLORE_CONTESTO);
			else fill(COLORE_BASE);
			
			rect(j*s, i*s, s, s);
			
			if (FRONTIERA.check(n)) {
				fill(100, 100);
				circle(j*s, i*s, s);
			}
			
			if (showText) {
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
			}
			
			
		});
		
		if (grigliaNazione!=null) {
			int i=0;
			for (var regione : grigliaNazione.regioni()) {
				fill(getColor(i));
				regione.celle().forEach(c -> {
					rect(c.x()*s, c.y()*s, s, s);
				});
				i++;
			}
			
			int x = mouseX * w / width;
			int y = mouseY * h / height;
			Optional<Regione> res = grigliaNazione.getRegioneContenente(x, y);
			if (res.isPresent()) {
				stroke(0);
				res.get().frontiera().forEach(c -> {
					line(c.x()*s, c.y()*s, c.x()*s+s, c.y()*s+s);
				});
			}
		}
		
		stroke(100);
		noFill();
		griglia.forEach((j,i) -> {
			line(0, i*s, width, i*s);
			line(j*s, 0, j*s, height);
		});
		
		
		if (monitor!=null) {
			var cammino = monitor.getCammino();
			if (cammino!=null) {
				stroke(200);
				drawCammino(cammino);
			}
		}
		
		if (monitorMin!=null) { // 48+21√2=77,698485
			var cammino = monitorMin.getCammino();
			if (cammino!=null) {
				stroke(0, 150, 0);
				drawCammino(cammino);
				textAlign(CENTER, CENTER);
				var msg = "%.2f".formatted(cammino.lunghezza(), cammino.landmarks().size());
				if (Double.isInfinite(cammino.lunghezza())) msg="+∞";
				translate((O.x()+0.5f)*s, (O.y()+0.5f)*s);
				rectMode(CENTER);
				fill(255);
				rect(0, 0, textWidth(msg)+s/2, s, s/2);
				fill(0);
				text(msg, 0, 0);
			}
		}
		
	}
	
	private int getColor(int index) {
		return colors[index%colors.length];
	}
	
	
	private void drawCammino(ICammino cammino) {
		noFill();
		pushMatrix();
		scale(s);
		translate(0.5f, 0.5f);
		strokeWeight(0.1f);
		float dl = 0.2f, ds=0.4f;
		Utils.forEachPair(cammino.landmarks(), (a,b) -> {
			if (b.is(COMPLEMENTO)) {
				camminoLibero2(a.x(), a.y(), b.x(), b.y(), dl, ds);
			} else {
				camminoLibero1(a.x(), a.y(), b.x(), b.y(), dl, ds);
			}
		});
		popMatrix();
	}
	
	ICella2 O,D;
	IProgressoMonitor monitor,monitorMin;
	CompitoTreImplementation compitoTreImpl;
	GrigliaConRegioni<ICella2> grigliaNazione;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		
		grigliaNazione = null;
		
		switch(e.getButton()) {
		case LEFT:
			clearNonOStacoli(e.isControlDown());
			D = null;
			monitor = monitorMin = null;
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
			griglia = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y());
			O = (ICella2) griglia.getCellaAt(O.x(), O.y());
			
			DESTINAZIONE.toggleTo(griglia.getCellaAt(x, y));
			D = (ICella2)griglia.getCellaAt(x, y);
			D.setStato(DESTINAZIONE.value());
			
			try {
				compitoTreImpl = new CompitoTreImplementation();
				monitor = compitoTreImpl.getProgress();
				monitorMin = compitoTreImpl.getProgressMin();
				new Thread(()->{
					System.out.println("inizio camminoMin");
					var cammino = compitoTreImpl.camminoMin(griglia, O, D);
					System.out.println("(%d)".formatted(cammino.landmarks().size()));
					cammino.landmarks().forEach(lm -> {
						LANDMARK.addTo(griglia.getCellaAt(lm.x(), lm.y()));
						System.out.println("(%d,%d)".formatted(lm.x(), lm.y()));
					});
					System.out.print("finito: ");
					System.out.printf("%d+%d√2=%f\n",cammino.lunghezzaTorre(),
							cammino.lunghezzaAlfiere(), cammino.lunghezza());
					monitor=null;
					System.out.println(compitoTreImpl.getReport());
				}).start();
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			break;
		
		case CENTER:
			switch(e.getModifiers()) {
			case 0 -> OSTACOLO.toggleTo(griglia.getCellaAt(x, y));
			case Event.SHIFT|Event.CTRL|Event.ALT ->{
				if (x+3>=w || y+3<0) return;
				OSTACOLO.addTo(griglia.getCellaAt(x+1, y));
				OSTACOLO.addTo(griglia.getCellaAt(x+3, y));
				OSTACOLO.addTo(griglia.getCellaAt(x, y-1));
				OSTACOLO.addTo(griglia.getCellaAt(x+1, y-1));
				OSTACOLO.addTo(griglia.getCellaAt(x+2, y-1));
				OSTACOLO.addTo(griglia.getCellaAt(x+3, y-1));
				OSTACOLO.addTo(griglia.getCellaAt(x, y-2));
				OSTACOLO.addTo(griglia.getCellaAt(x+1, y-2));
				OSTACOLO.addTo(griglia.getCellaAt(x+1, y-3));
				OSTACOLO.addTo(griglia.getCellaAt(x+2, y-3));
				OSTACOLO.addTo(griglia.getCellaAt(x+3, y-3));
			}
			}
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
			monitorMin = monitor = null;
			grigliaNazione = null;
			O = D = null;
			break;
		case 'I':
			if (compitoTreImpl!=null) {
				compitoTreImpl.interrupt();
			}
			break;
		case 'M':
			int x = mouseX * w / width;
			int y = mouseY * h / height;
			println("(%d,%d)".formatted(x, y));
			break;
		case 'U':
			if (griglia instanceof IGrigliaConOrigine gco) {
				grigliaNazione = RegioneFactory.from(gco);
				System.out.println(grigliaNazione.regioni().length);
				var str = grigliaNazione.collect(c2d->grigliaNazione.getRegioneIndexContenente(c2d)
						.map("%2d"::formatted)
						.orElse("  "),
						Collectors.joining("|"), Collectors.joining("\n"));
				System.out.println(str);
				var regione = grigliaNazione.getRegioneContenente(28, 19);
				System.out.println(regione);
			}
			break;
		case 'F':
			if (griglia instanceof IGrigliaConOrigine gco) {
				gco.getFrontiera().forEach(System.out::println);
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
	
	private void camminoLibero1(int x1, int y1, int x2, int y2, float dl, float ds) {
		var dx = (x2-x1);
		var dy = (y2-y1);
		if (abs(dx) > abs(dy)) {
			dashedLine(x1, y1, x1+abs(dy)*Math.signum(dx), y2, dl, ds);
			dashedLine(x1+abs(dy)*Math.signum(dx), y2, x2, y2, dl, ds);
		} else {
			dashedLine(x1, y1, x2, y1+abs(dx)*Math.signum(dy), dl, ds);
			dashedLine(x2, y1+abs(dx)*Math.signum(dy), x2, y2, dl, ds);
		}
	}
	private void camminoLibero2(int x1, int y1, int x2, int y2, float dl, float ds) {
		var dx = (x1-x2);
		var dy = (y1-y2);
		if (abs(dx) > abs(dy)) {
			dashedLine(x1, y1, x2+abs(dy)*Math.signum(dx), y1, dl, ds);
			dashedLine(x2+abs(dy)*Math.signum(dx), y1, x2, y2, dl, ds);
		} else {
			dashedLine(x1, y1, x1, y2+abs(dx)*Math.signum(dy), dl, ds);
			dashedLine(x1, y2+abs(dx)*Math.signum(dy), x2, y2, dl, ds);
		}
	}
	
	
	private void dashedLine(float x1, float y1, float x2, float y2, float dl, float ds) {
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
