package algorithms;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs = new ArrayList<Point>();

    //fvs.addAll(points);

    ArrayList<Integer> degres = new ArrayList<>();
    TreeMap<Integer, ArrayList<Point>> degresTries = new TreeMap<>();

    int cpt;

    for(int i=0; i<points.size(); i++) {
      Point p = points.get(i);
      cpt=0;
      for (int j=i+1; j<points.size(); j++) {
        Point q = points.get(j);
        if (p.distance(q) < edgeThreshold) {
          cpt++;
        }
      }
      degres.add(points.indexOf(p), cpt);
    }

    int degre=-1;
    ArrayList<Point> al;
    for(int i=0; i<points.size(); i++) {
      degre=degres.get(i);
      if(degresTries.containsKey(degre))
        degresTries.get(degre).add(points.get(i));
      else {
        al=new ArrayList<Point>();
        al.add(points.get(i));
        degresTries.put(degre, al);
      }
    }

    Evaluation e = new Evaluation();

    while(!e.isValid(points, fvs, edgeThreshold)) {
      al = degresTries.get(degresTries.size() - 1);
      while(al.isEmpty()) {
        degresTries.remove(degresTries.size() - 1);
        al = degresTries.get(degresTries.size() - 1);
      }
      fvs.add(al.remove(al.size()-1));
    }
    return fvs;
  }
}
