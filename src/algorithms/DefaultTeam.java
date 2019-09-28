package algorithms;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {

    public boolean estArete(Point a, Point b, int edgeThreshold){
        return a.distance(b)<edgeThreshold;
    }


    public int degre(Point p, ArrayList<Point> points, int edgeThreshold){
        int degree=-1;
        for (Point q: points)
            if (estArete(p, q, edgeThreshold) && !p.equals(q))
                degree++;
        return degree;
    }

    public ArrayList<Point> calculFVS(ArrayList<Point> pointsIn, int edgeThreshold) {
        ArrayList<Point> fvs;

        Evaluation e = new Evaluation();

        long seed = System.nanoTime();
        int degreMax = 0, d;

        ArrayList<Point> points = (ArrayList<Point>) pointsIn.clone();
        ArrayList<Point> result = (ArrayList<Point>) pointsIn.clone();
        ArrayList<Point> rest;

        Point p;

        for (int i = 0; i < 100; i++) {
            Collections.shuffle(points, new Random(System.nanoTime() + i));
            fvs = (ArrayList<Point>) points.clone();
            rest = new ArrayList<Point>();

            for(int j=0 ; j<fvs.size(); j++) {
                Point choosenOne = fvs.get(0);
                for (int k = 0; k<fvs.size(); k++) {
                    p = fvs.get(k);
                    if (degre(p, rest, edgeThreshold) < degre(choosenOne, rest, edgeThreshold))
                        choosenOne = p;
                }

                if ((d = degre(choosenOne, rest, edgeThreshold)) > degreMax)
                    degreMax = d;

                fvs.remove(choosenOne);
                if(!e.isValid(points, fvs, edgeThreshold))
                    fvs.add(choosenOne);
                else
                    rest.add(choosenOne);
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
        int r=0, s=0;
        Point rr, ss;
        while (continuer) {
            // shuffle ici
            Collections.shuffle(fvs, new Random(seed));
            continuer = false;
            for (i = 0; i < fvs.size() && !continuer; i++) {
                a = fvs.remove(i);
                for (j = i + 1; j < fvs.size() && !continuer; j++) {
                    b = fvs.remove(j);

                    for (k = j + 1; k < fvs.size() && !continuer; k++) {
                        c = fvs.remove(k);
                        if ((estArete(a, b, edgeThreshold) && (estArete(b, c, edgeThreshold) || estArete(a, c, edgeThreshold)))
                                || (estArete(b, c, edgeThreshold)) && estArete(a, c, edgeThreshold)) {
                            Collections.shuffle(reste, new Random(seed));
                            for (r = 0; r < reste.size(); r++) {
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

        reste = (ArrayList<Point>) points.clone();
        reste.removeAll(fvs);

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
                Collections.shuffle(fvs, new Random(seed));
                continuer = false;
                for (i = 0; i < fvs.size() && !continuer; i++) {
                    a = fvs.remove(i);
                    for (j = i + 1; j < fvs.size() && !continuer; j++) {
                        b = fvs.remove(j);

                        if (estArete(a, b, edgeThreshold)) {
                            // shuffle reste aussi si ça prend pas trop de temps
                            Collections.shuffle(reste, new Random(seed));
                            for (Point u : reste) {
                                fvs.add(u);
                                if (e.isValid(points, fvs, edgeThreshold)) {
                                    continuer = true;
                                    reste.remove(u);
                                    break;
                                }
                                fvs.remove(u);
                            }
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

        reste = (ArrayList<Point>) points.clone();
        reste.removeAll(fvs);

        result = (ArrayList<Point>) fvs.clone();
        rest = (ArrayList<Point>) reste.clone();
        fvsClone = (ArrayList<Point>) fvs.clone();

        for (int t = 0; t < 100; t++) {
            reste = (ArrayList<Point>) rest.clone();
            fvs = (ArrayList<Point>) fvsClone.clone();

            Collections.shuffle(fvs, new Random(seed + t));
            for (i = 0; i < degreMax; i++) {
                for (j = 0; j < fvs.size(); j++) {
                    p = points.get(j);
                    if (degre(p, reste, edgeThreshold) <= i) {
                        fvs.remove(p);
                        if (!e.isValid(points, fvs, edgeThreshold)) {
                            fvs.add(p);
                        }
                        reste.add(p);
                    }
                }
            }
            System.out.println("GR. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

            if (fvs.size() < result.size())
                result = fvs;
        }

        fvs = result;

        System.out.println("After deleting min degrees : " + fvs.size());

        return fvs;
    }
}
