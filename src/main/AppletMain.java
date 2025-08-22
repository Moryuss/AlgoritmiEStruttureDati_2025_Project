package main;

import static nicolas.StatoCella.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import francesco.*;
import francesco.implementazioni.Cella2D;
import francesco.implementazioni.LettoreGriglia;
import matteo.*;
import matteo.Riassunto.*;
import nicolas.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;
import processing.event.Event;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import utils.Utils;

public class AppletMain extends PApplet {
	
	public static void main(String[] args) {
		PApplet.main(AppletMain.class, args);
	}
	
	
	int w,h,s;
	int[] palette, colors;
	boolean mouseEnabled = true;
	IGrigliaMutabile<?> griglia;
	CompitoDueImpl compitoDue = CompitoDueImpl.V0;
	CamminoConfiguration camminoConfiguration = ConfigurationMode.DEFAULT.toCamminoConfiguration();
	int tipiRiassunto = TipoRiassunto.VERBOSE.mask;
	
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
		
		
		
		colors = Stream.of(json.getString("colors", "\"0x404040,0x808080,0xff0000,0xff6a00,0xffd800,0xb6ff00,0x4cff00,0x00ff21,0x00ff90,0x00ffff,0x0094ff,0x0026ff,0x4800ff,0xb200ff,0xff00dc,0xff006e\"").split("[\\[\",\\]\\s]+"))
		.skip(1)
		.mapToInt(e -> Utils.parseHex(e).orElse(0))
		.map(n->n|0xff000000)
		.toArray();
		
		
		var map = Utils.toMap(json.getJSONObject("palette"));
		COLORE_OSTACOLO     = getColor(map, StatoCella.OSTACOLO); 
		COLORE_DESTINAZIONE = getColor(map, StatoCella.DESTINAZIONE);
		COLORE_LANDMARK     = getColor(map, StatoCella.LANDMARK); 
		COLORE_FRONTIERA    = getColor(map, StatoCella.FRONTIERA); 
		COLORE_COMPLEMENTO  = getColor(map, StatoCella.COMPLEMENTO); 
		COLORE_ORIGINE      = getColor(map, StatoCella.ORIGINE); 
		COLORE_REGINA       = getColor(map, StatoCella.REGINA); 
		COLORE_CONTESTO     = getColor(map, StatoCella.CONTESTO); 
		COLORE_BASE         = getColor(map, StatoCella.VUOTA);
		
		
		if (config.hasKey("load")) {
			griglia = loadGriglia(config);
		} else if (config.hasKey("loadThis")) {
			griglia = Utils.loadSimple(config.getJSONArray("loadThis")).toGrigliaMutabile();
		}
		
		if (griglia==null) {
			griglia = new LettoreGriglia().crea(file.toPath()).toGrigliaMutabile();
		}
		
		w=griglia.width(); h=griglia.height();
		width = json.getInt("width", 1200);
		s = width/w;
		height = min(s*h, json.getInt("height", 600));
		width = height*w/h;
		size(width, height);
		
		
		showText = json.getBoolean("showText", false);
		pixelDensity(json.getInt("pixelDensity", 1));
		
		println("w=%d, h=%d".formatted(w, h));
		printGriglia(griglia);
		
	}

	private static int getColor(Map<String, Object> map, StatoCella stato) {
		return Utils.parseHex(map.get(stato.name())+"").orElse(0)|0xff000000;
	}
	
	private static void printGriglia(IGriglia<?> griglia) {
		System.out.println(griglia.collect(c->c.is(OSTACOLO)?"██":".'", Collectors.joining(), Collectors.joining("\n")));
	}
	
	
	private static IGrigliaMutabile<?> loadGriglia(JSONObject config) {
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
		
		return Utils.loadSimple(toLoad).toGrigliaMutabile();
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
			
			if (OSTACOLO.is(n)) fill(COLORE_OSTACOLO);
			else if (ORIGINE.is(n)) fill(COLORE_ORIGINE);
			else if (DESTINAZIONE.is(n)) fill(COLORE_DESTINAZIONE);
			else if (LANDMARK.is(n)) fill(COLORE_LANDMARK);
			//else if (FRONTIERA.is(n)) fill(COLORE_FRONTIERA);
			else if (COMPLEMENTO.is(n)) fill(COLORE_COMPLEMENTO);
			else if (REGINA.is(n)) fill(COLORE_REGINA);
			else if (CONTESTO.is(n)) fill(COLORE_CONTESTO);
			else fill(COLORE_BASE);
			
			rect(j*s, i*s, s, s);
			
			if (FRONTIERA.is(n)) {
				fill(100, 100);
				circle(j*s, i*s, s);
			}
			
			if (showText) {
				textAlign(CENTER, CENTER);
				fill(150);
				text(n, (0.5f+j)*s, (0.5f+i)*s);
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
		
		if (monitorMin!=null) {
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
		return colors[(index+5)%colors.length];
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
	
	ICella2D O,D;
	IProgressoMonitor monitor,monitorMin;
	IGrigliaConOrigine grigliaConOrigine;
	CompitoTreImplementation compitoTreImpl;
	IStatisticheEsecuzione statisticheEsecuzione;
	String report = "";
	GrigliaConRegioni<ICella2D> grigliaNazione;
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!mouseEnabled) return;
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		
		grigliaNazione = null;
		
		switch(e.getButton()) {
		case LEFT:
			if (griglia.getCellaAt(x, y).is(OSTACOLO)) return;
			clearNonOStacoli(e.isControlDown());
			D = null;
			monitor = monitorMin = null;
			var g = GrigliaConOrigineFactory.creaV0(griglia, x, y);
			grigliaConOrigine = g;
			O = g.getCellaAt(x, y);
			griglia = g.toGrigliaMutabile();
			break;
		
		case RIGHT:
			if (O==null || griglia.getCellaAt(x, y).is(OSTACOLO)) return;
			griglia.forEach((j,i) -> {
				var cella = griglia.getCellaAt(j, i);
				if (LANDMARK.is(cella.stato())) {
					LANDMARK.removeTo(griglia, x, y);
				}
			});
			maskGriglia((DESTINAZIONE.mask()-1) | OSTACOLO.mask());
			griglia = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y()).toGrigliaMutabile();
			
			D = new Cella2D(griglia.getCellaAt(x, y).stato(), x, y);
			griglia.setStato(x, y, DESTINAZIONE.value());
			
			try {
				compitoTreImpl = new CompitoTreImplementation(camminoConfiguration);
				monitor = compitoTreImpl.getProgress();
				monitorMin = compitoTreImpl.getProgressMin();
				mouseEnabled = false;
				
				new Thread(()->{
					System.out.println("inizio camminoMin");
					var cammino = compitoTreImpl.camminoMin(griglia, O, D, compitoDue);
					cammino.landmarks().forEach(lm -> {
						LANDMARK.addTo(griglia, lm.x(), lm.y());
					});
					System.out.printf("finito: %s\n", cammino.distanzaLibera());
					
					monitor=null;
					report = compitoTreImpl.getReport();
					statisticheEsecuzione = compitoTreImpl.getStatisticheEsecuzione();
					
					var timeStamp = Utils.getCurrentTimestamp();
					for (var tr : TipoRiassunto.from(tipiRiassunto)) {
						statisticheEsecuzione.generaRiassunto(tr)
						.salvaFile(tr.name().toLowerCase(), "output/"+timeStamp);
					}
					
					mouseEnabled = true;
				}).start();
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			break;
		
		case CENTER:
			switch(e.getModifiers()) {
			case 0 -> OSTACOLO.toggleTo(griglia, x, y);
			case Event.SHIFT|Event.CTRL|Event.ALT ->{
				if (x+3>=w || y+3<0) return;
				OSTACOLO.setTo(griglia, x+1, y);
				OSTACOLO.setTo(griglia, x+3, y);
				OSTACOLO.setTo(griglia, x, y-1);
				OSTACOLO.setTo(griglia, x+1, y-1);
				OSTACOLO.setTo(griglia, x+2, y-1);
				OSTACOLO.setTo(griglia, x+3, y-1);
				OSTACOLO.setTo(griglia, x, y-2);
				OSTACOLO.setTo(griglia, x+1, y-2);
				OSTACOLO.setTo(griglia, x+1, y-3);
				OSTACOLO.setTo(griglia, x+2, y-3);
				OSTACOLO.setTo(griglia, x+3, y-3);
			}
			}
			if (O!=null) {
				griglia = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y()).toGrigliaMutabile();
			}
			break;
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (!mouseEnabled) return;
		if (abs(e.getX()-pmouseX)+abs(e.getY()-pmouseY)<2.5) return;
		
		int x = e.getX() * w / width;
		int y = e.getY() * h / height;
		x = constrain(x, 0, w-1);
		y = constrain(y, 0, h-1);
		int stato = griglia.getCellaAt(x, y).stato();
		
		switch(e.getButton()) {
		case LEFT:
			OSTACOLO.setTo(griglia, x, y);
			break;
		case RIGHT:
			OSTACOLO.removeTo(griglia, x, y);
			break;
		case CENTER:
			break;
		}
		
		if (O!=null && griglia.getCellaAt(x, y).stato()!=stato) {
			griglia = GrigliaConOrigineFactory.creaV0(griglia, O.x(), O.y()).toGrigliaMutabile();
		}
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case TAB:
			handleOtherWindow();
			break;
		case 'R':
			System.out.println(report);
			break;
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
			clearNonOStacoli(e.isShiftDown());
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
			if (grigliaConOrigine != null) {
				grigliaNazione = RegioneFactory.from(grigliaConOrigine);
				System.out.printf("Numero regioni: %d\n", grigliaNazione.regioni().length);
				var str = grigliaNazione.collect(c2d->grigliaNazione.getRegioneIndexContenente(c2d)
						.map("%2d"::formatted)
						.orElse("  "),
						Collectors.joining("|"), Collectors.joining("\n"));
				if (e.isShiftDown()) System.out.println(str);
			}
			break;
		case 'F':
			grigliaConOrigine.getFrontiera().forEach(System.out::println);
			break;
		}
	}
	
	
	
	private void clearNonOStacoli(boolean isControlDown) {
		var mask = isControlDown ? 0 : OSTACOLO.value();
		maskGriglia(mask);
	}
	
	private void maskGriglia(int mask) {
		griglia.forEach((x,y) -> {
			griglia.setStato(x, y, griglia.getCellaAt(x, y).stato()&mask);
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
	
	PApplet otherWindow;
	
	private void handleOtherWindow() {
		if (otherWindow!=null) {
			otherWindow.exit();
			otherWindow = null;
			return;
		}
		
		PApplet parent = this;
		println(parent.pixelDensity);
		
		otherWindow = new PApplet() {
			
			List<ConfigurationFlag> flags = ConfigurationFlag.allFlags();
			
			@Override
			public void settings() {
				size(400, max(flags.size(), TipoRiassunto.values().length+2)*30);
				pixelDensity(parent.pixelDensity);
			}
			@Override
			public void setup() {
				windowTitle("Settings");
			}
			@Override
			public void draw() {
				background(240);
				fill(0);
				textSize(20);
				textAlign(LEFT, CENTER);
				for (int i=0; i<flags.size(); i++) {
					var f = flags.get(i);
					var bool = camminoConfiguration.hasFlag(f);
					fill(bool ? 0 : 200);
					text("%s".formatted(f.name(), bool), 10, 20+i*28);
				}
				
				fill(0);
				text("CompitoDue: %s".formatted(compitoDue.name()), 260, 20);
				
				textAlign(RIGHT, CENTER);
				for (int i=0, n=TipoRiassunto.LENGTH; i<n; i++) {
					var f = TipoRiassunto.values()[i];
					var bool = f.isIn(tipiRiassunto);
					fill(bool ? 0 : 200);
					text(f.name(), width-10, 20+(i+2)*28);
				}
				
			}
			public void keyPressed(KeyEvent e) {
				parent.keyPressed(e);
			}
			public void mouseClicked(MouseEvent e) {
				if (e.getX() <= 250) {
					int y = (e.getY()-16)/27;
					if (y<0 || y>=flags.size()) return;
					var flag = flags.get(y);
					camminoConfiguration = camminoConfiguration.toggle(flag);
					return;
				}
				if (e.getY()>10 && e.getY()<30) {
					compitoDue = compitoDue.next();
				} else if (e.getY() > 59) {
					int y = (e.getY()-60)/27;
					if (y<0 || y>=TipoRiassunto.LENGTH) return;
					int n = tipiRiassunto ^ TipoRiassunto.values()[y].mask;
					if (n>0) tipiRiassunto = n;
				}
			}
			@Override
			public void exitActual() {
				((processing.awt.PSurfaceAWT.SmoothCanvas)
				((processing.awt.PSurfaceAWT)getSurface())
				.getNative()).getFrame().dispose();
				otherWindow = null;
			}
		};
		
		
		PApplet.runSketch(new String[] {otherWindow.getClass().getSimpleName()}, otherWindow);
	}
	
}
