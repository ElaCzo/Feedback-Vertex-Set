package algorithms;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {

  public boolean estArete(Point a, Point b, int edgeThreshold) {
    return a.distance(b) < edgeThreshold;
  }

  public int degre(Point p, ArrayList<Point> points, int edgeThreshold) {
    int degree = -1;
    for (Point q : points)
      if (estArete(p, q, edgeThreshold))
        degree++;
    return degree;
  }

  public ArrayList<Point> calculFVS(ArrayList<Point> pointsIn, int edgeThreshold) {
    ArrayList<Integer> degres = new ArrayList<>();
    TreeMap<Integer, ArrayList<Point>> degresTries = new TreeMap<>();
    ArrayList<Point> fvs = new ArrayList<Point>();

    Evaluation e = new Evaluation();

    int cpt;

    ArrayList<Point> points = (ArrayList<Point>) pointsIn.clone();
    ArrayList<Point> result = (ArrayList<Point>) pointsIn.clone();
    ArrayList<Point> rest;

    for (int i = 0; i < 100; i++) {
      Collections.shuffle(points, new Random(System.nanoTime()));
      rest = (ArrayList<Point>) points.clone();
      fvs = new ArrayList<Point>();

      while (!e.isValid(points, fvs, edgeThreshold)) {
        Point choosenOne = rest.get(0);
        for (Point p : rest)
          if (degre(p, rest, edgeThreshold) > degre(choosenOne, rest, edgeThreshold))
            choosenOne = p;

        fvs.add(choosenOne);
        rest.remove(choosenOne);
      }
      System.out.println("GR. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

      if (fvs.size() < result.size())
        result = fvs;
    }

    fvs = result;
    System.out.println("Taille fvs : " + fvs.size());

    /* Local searching naïf */
    ArrayList<Point> reste = new ArrayList<Point>();
    reste.addAll(points);
    reste.removeAll(fvs);
    boolean continuer = true;
    Point a, b, c;
    int i, j, k;

    /* Local searching naïf trois pour deux. */
    continuer = true;
    int r, s;
    Point rr, ss;
    while (continuer) {
      // shuffle ici
      Collections.shuffle(fvs);
      continuer = false;
      for (i = 0; i < fvs.size() && !continuer; i++) {
        Collections.shuffle(fvs);
        a = fvs.remove(i);
        for (j = i + 1; j < fvs.size() && !continuer; j++) {
          b = fvs.remove(j);

          for (k = j + 1; k < fvs.size() && !continuer; k++) {
            c = fvs.remove(k);
            if ((estArete(a, b, edgeThreshold) && (estArete(b, c, edgeThreshold) || estArete(a, c, edgeThreshold)))
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
            if (!continuer)
              fvs.add(c);
          }
          if (!continuer)
            fvs.add(b);
        }
        if (!continuer)
          fvs.add(a);
      }
    }

    System.out.println("Local searching 3->2 : " + fvs.size());

    result = (ArrayList<Point>) fvs.clone();
    rest = (ArrayList<Point>) reste.clone();
    ArrayList<Point> fvsClone = (ArrayList<Point>) fvs.clone();

    for (int t = 0; t < 20; t++) {
      reste = (ArrayList<Point>) rest.clone();
      fvs = (ArrayList<Point>) fvsClone.clone();

      continuer = true;
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

      System.out.println("GR. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

      if (fvs.size() < result.size())
        result = fvs;
    }

    System.out.println("Local searching 1->2 : " + fvs.size());

    fvs = result;

    // rajouter la suppression sans rajout. Vérifier que ça sert à quelque chose

    result = (ArrayList<Point>) fvs.clone();
    rest = (ArrayList<Point>) reste.clone();
    fvsClone = (ArrayList<Point>) fvs.clone();

    for (int t = 0; t < 100; t++) {
      reste = (ArrayList<Point>) rest.clone();
      fvs = (ArrayList<Point>) fvsClone.clone();

      continuer = true;
      while (continuer) {
        // shuffle ici
        Collections.shuffle(fvs);
        continuer = false;
        for (i = 0; i < fvs.size() && !continuer; i++) {
          a = fvs.remove(i);
          if (e.isValid(points, fvs, edgeThreshold)) {
            continuer = true;
            break;
          }
          if (!continuer)
            fvs.add(a);
        }
      }

      System.out.println("GR. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

      if (fvs.size() < result.size())
        result = fvs;
    }

    fvs=result;

    return fvs;
  }
}
