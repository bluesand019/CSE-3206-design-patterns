import java.util.*;

// Problem: many aircraft share one runway. If every pilot had to talk to every
// other pilot to decide who lands first, each plane would need references to all
// the others and the coordination logic would be duplicated everywhere.
// Solution: planes only talk to a ControlTower (the mediator), which decides.

interface Tower {
    void requestLanding(Aircraft a);
    void landed(Aircraft a);
}

class Aircraft {
    final String name;
    private final Tower tower; // the ONLY thing a plane knows about

    Aircraft(String name, Tower tower) { this.name = name; this.tower = tower; }

    void requestLanding() { System.out.println(name + ": requesting landing"); tower.requestLanding(this); }
    void finishLanding()  { System.out.println(name + ": landed, runway clear"); tower.landed(this); }

    // commands the tower sends back
    void land() { System.out.println("  Tower -> " + name + ": cleared to land"); }
    void hold() { System.out.println("  Tower -> " + name + ": hold, circle the airport"); }
}

class ControlTower implements Tower {
    private Aircraft onRunway;
    private final Queue<Aircraft> holding = new ArrayDeque<>();

    public void requestLanding(Aircraft a) {
        if (onRunway == null) { onRunway = a; a.land(); }
        else { holding.add(a); a.hold(); }
    }

    public void landed(Aircraft a) {
        onRunway = null;
        Aircraft next = holding.poll();
        if (next != null) requestLanding(next); // give the runway to the next in line
    }
}

public class MediatorPatternDemo {
    public static void main(String[] args) {
        Tower tower = new ControlTower();
        Aircraft a = new Aircraft("Flight A1", tower);
        Aircraft b = new Aircraft("Flight B2", tower);
        Aircraft c = new Aircraft("Flight C3", tower);

        a.requestLanding();
        b.requestLanding();
        c.requestLanding();

        a.finishLanding();
        b.finishLanding();
        c.finishLanding();
    }
}
