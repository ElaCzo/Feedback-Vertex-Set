package algorithms;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {

    public ArrayList<Point> improve(ArrayList<Point> points, ArrayList<Point> fvs, int threshold) {
        ArrayList<Point> retour = (ArrayList<Point>) fvs.clone();
        Evaluation eval = new Evaluation();
        for (Point p : fvs) {
            retour.remove(p);
            if (!eval.isValid(points, retour, threshold)) retour.add(p);
        }
        return retour;
    }

    public ArrayList<Point> cleanUp(ArrayList<Point> points) {
        ArrayList<Point> clone = (ArrayList<Point>) points.clone();
        ArrayList<Point> neigh;
        boolean stillAVertex = true;
        while (stillAVertex) { // tant qu'il reste un vertex de degrés 1
            stillAVertex = false;
            ArrayList<Point> clone2 = (ArrayList<Point>) clone.clone();
            for (Point p : clone2) { // pour chaque point on recherche les voisins
                neigh = voisins(p, clone);
                if (neigh.size() <= 1) { // si un seul voisin -> degré 1
                    stillAVertex = true;
                    clone.remove(p);
                }
            }
        }
        ArrayList<Point> retour = (ArrayList<Point>) clone.clone();
        return retour;
    }

    private ArrayList<Point> voisins(Point p, ArrayList<Point> vertices) {
        ArrayList<Point> result = new ArrayList<Point>();

        for (Point point : vertices) if (point.distance(p) < 100 && !point.equals(p)) result.add((Point) point.clone());

        return result;
    }

    public boolean estArete(Point a, Point b, int edgeThreshold) {
        return a.distance(b) < edgeThreshold;
    }


    public int degre(Point p, ArrayList<Point> points, int edgeThreshold) {
        int degree = -1;
        for (Point q : points)
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

        for (int i = 0; i < 6000; i++) {
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
        System.out.println("Taille fvs après méthode 1 : " + fvs.size());

        points = (ArrayList<Point>) pointsIn.clone();

        for (int i = 0; i < 6000; i++) {
            Collections.shuffle(points, new Random(System.nanoTime() + i));
            rest = (ArrayList<Point>) points.clone();
            fvs = new ArrayList<Point>();

            while (!e.isValid(points, fvs, edgeThreshold)) {
                Point choosenOne = rest.get(0);
                for (Point p3 : rest)
                    if (degre(p3, rest, edgeThreshold) > degre(choosenOne, rest, edgeThreshold))
                        choosenOne = p3;

                if ((d = degre(choosenOne, rest, edgeThreshold)) > degreMax)
                    degreMax = d;

                fvs.add(choosenOne);
                rest.remove(choosenOne);
            }
            System.out.println("GR. Current sol: " + result.size() + ". Found next sol: " + fvs.size());

            if (fvs.size() < result.size())
                result = fvs;
        }

        fvs=result;

        System.out.println("Taille fvs après méthode 2 : " + fvs.size());

        int i;
        ArrayList<Point> fvs_tmp = null;
        for(int ite = 0 ; ite < 2; ite++) {
            fvs = (ArrayList<Point>) points.clone();
            Point maxi;
            maxi = pointsIn.parallelStream().max(Comparator.comparingInt(element -> voisins(element, pointsIn).size())).get();
            ArrayList<Point> test = (ArrayList<Point>) points.clone();
            if(ite == 1){
                test.remove(maxi);
                maxi = test.parallelStream().max(Comparator.comparingInt(element -> voisins(element, test).size())).get();
            }
            ArrayList<Point> neigh = voisins(maxi, pointsIn);

            i = 0;
            int high = neigh.size();
            while (i <= high) {
                for (Point p2 : pointsIn) {
                    if (voisins(p2, pointsIn).size() <= i) {
                        fvs.remove(p2);
                        if (!e.isValid(pointsIn, fvs, edgeThreshold)) fvs.add(p2);
                    }
                }
                i++;
            }
            int score = fvs.size();
            int scoreTmp = Integer.MAX_VALUE;
            while (score < scoreTmp) {
                scoreTmp = fvs.size();
                improve(points, fvs, edgeThreshold);
                cleanUp(fvs);
                score = fvs.size();
            }
            if(ite==0){
                fvs_tmp = (ArrayList<Point>) fvs.clone();
            } else {
                if(fvs.size() > fvs_tmp.size()){
                    fvs = (ArrayList<Point>) fvs_tmp.clone();
                }
            }
            if (fvs.size() < result.size())
                result = fvs;
        }
        fvs = result;

        System.out.println("Taille fvs après méthode 3 : " + fvs.size());

        // Local searching naïf
        ArrayList<Point> reste = new ArrayList<Point>();
        reste.addAll(points);
        reste.removeAll(fvs);
        boolean continuer = true;
        Point a, b, c;
        int j, k;

        // Local searching naïf trois pour deux.
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

        // shuffle ici
        Collections.shuffle(fvs, new Random(seed));
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

    private class Arete {
        public Point a;
        public Point b;

        public Arete(Point a, Point b){
            this.a = a;
            this.b = b;
        }
    }

    public ArrayList<Arete> createAreteList(ArrayList<Point> points){
        ArrayList<Arete> retour = new ArrayList<>();
        ArrayList<Point> pointsTmp = (ArrayList<Point>) points.clone();
        while(pointsTmp.size() > 1){
            Point p = pointsTmp.get(0);
            ArrayList<Point> voisins = voisins(p,pointsTmp);
            for (Point q : voisins){
                Arete arete = new Arete(p, q);
                retour.add(arete);
            }
            pointsTmp.remove(p);
        }
        return retour;
    }

    public ArrayList<Point> newAlgo(ArrayList<Point> points) {
        ArrayList<Point> fvs = new ArrayList<>();
        ArrayList<Arete> aretes = createAreteList(points);

        while(aretes.size()>0){
            Collections.shuffle(aretes,new Random(System.nanoTime()));
            Arete arete = aretes.remove(0);
            fvs.add(arete.a);
            fvs.add(arete.b);
            ArrayList<Arete> toRemove = new ArrayList<>();
            for (Arete tmp: aretes) {
                if(tmp.a.equals(arete.a)||tmp.b.equals(arete.a)){
                    toRemove.add(tmp);
                } else if (tmp.a.equals(arete.b)||tmp.b.equals(arete.b)){
                    toRemove.add(tmp);
                }
            }
            aretes.removeAll(toRemove);
        }
        return fvs;
    }
}
