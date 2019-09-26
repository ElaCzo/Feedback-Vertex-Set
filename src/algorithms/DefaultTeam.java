package algorithms;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {

  public boolean estArete(Point a, Point b, int edgeThreshold){
    return a.distance(b)<edgeThreshold;
  }

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> fvs = new ArrayList<Point>();

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

    /*int degre=-1;
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

    Point p = null;
    while(!e.isValid(points, fvs, edgeThreshold)) {
      al = degresTries.get(degresTries.lastKey());
      while(al.isEmpty()) {
        degresTries.remove(degresTries.lastKey());
        al = degresTries.get(degresTries.lastKey());
      }
      if(Math.random()<0.70)
        fvs.add(p=al.remove(al.size()-1));
    }*/

    Evaluation e = new Evaluation();
    int rand;
    Point p;
    while(!e.isValid(points, fvs, edgeThreshold)){
      rand=(int)(Math.random()*points.size());
      p=points.get(rand);
      if(!fvs.contains(p))
        fvs.add(p);
    }

    /* Local searching naïf */
    ArrayList<Point> reste = new ArrayList<Point>();
    reste.addAll(points);
    reste.removeAll(fvs);
    boolean continuer = true;
    Point a, b, c;
    int i, j, k;

    /* Local searching naïf trois pour deux. */
    continuer=true;
    int r, s;
    Point rr, ss;
    while(continuer) {
      // shuffle ici
      Collections.shuffle(fvs);
      continuer=false;
      for(i=0; i< fvs.size() && !continuer; i++) {
        Collections.shuffle(fvs);
        a=fvs.remove(i);
        for (j = i + 1; j < fvs.size() && !continuer; j++) {
          b = fvs.remove(j);

          for (k = j + 1; k < fvs.size() && !continuer; k++) {
            c = fvs.remove(k);
            if((estArete(a, b, edgeThreshold) && (estArete(b, c, edgeThreshold) || estArete(a, c, edgeThreshold)))
                    || (estArete(b, c, edgeThreshold)) && estArete(a, c, edgeThreshold)) {
              Collections.shuffle(reste);
              for (r = 0; r < reste.size(); r++) {
                Collections.shuffle(reste);
                rr = reste.get(r);
                fvs.add(rr);
                for (s = r + 1; s < reste.size(); s++) {
                  ss = reste.get(s);
                  fvs.add(ss);
                  if (e.isValid(points, fvs, edgeThreshold)) {
                    continuer = true;
                    reste.remove(rr);
                    reste.remove(ss);
                    break;
                  } else
                    fvs.remove(ss);
                }
                if (continuer)
                  break;
                else if (!continuer)
                  fvs.remove(rr);
              }
            }
            if(!continuer)
              fvs.add(c);
          }
          if(!continuer)
            fvs.add(b);
        }
        if(!continuer)
          fvs.add(a);
      }
    }

    System.out.println("Local searching 3->2 : "+fvs.size());

    continuer=true;
    while (continuer) {
      // shuffle ici
      Collections.shuffle(fvs);
      continuer = false;
      for (i = 0; i < fvs.size() && !continuer; i++) {
        a = fvs.remove(i);
        for (j = i + 1; j < fvs.size() && !continuer; j++) {
          b = fvs.remove(j);

          // shuffle reste aussi si ça prend pas trop de temps
          Collections.shuffle(reste);
          for (Point u : reste) {
            fvs.add(u);
            if (e.isValid(points, fvs, edgeThreshold)) {
              continuer = true;
              reste.remove(u);
              break;
            }
            fvs.remove(u);
          }
          if (!continuer)
            fvs.add(b);
        }
        if (!continuer)
          fvs.add(a);
      }
    }

    System.out.println("Local searching 1->2 : "+fvs.size());


    return fvs;
  }
}
