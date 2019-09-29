package algorithms;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class DefaultTeam {
    private class Arete {
        public Point a;
        public Point b;

        public Arete(Point a, Point b){
            this.a = a;
            this.b = b;
        }
    }

    public ArrayList<Arete> creeListeDAretes(ArrayList<Point> points){
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

    public ArrayList<Point> algoMethode4(ArrayList<Point> points) {
        ArrayList<Point> fvs = new ArrayList<>();
        ArrayList<Arete> aretes = creeListeDAretes(points);

        while(aretes.size()>0){
            Collections.shuffle(aretes, new Random(System.nanoTime()));
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

    /* La première méthode */
    private ArrayList<Point> methode1(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> result){
        ArrayList<Point> fvs = (ArrayList<Point>) points.clone();

        ArrayList<Point> rest;

        for (int i = 0; i < 100; i++) {
            Collections.shuffle(points, new Random(System.nanoTime() + i));
            fvs = (ArrayList<Point>) points.clone();
            result = (ArrayList<Point>) points.clone();
            rest = new ArrayList<Point>();
            Point p;
            int d, degreMax=0;
            Evaluation e = new Evaluation();

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

            if (fvs.size() < result.size())
                result = fvs;
        }

        fvs = result;

        return fvs;
    }

    private ArrayList<Point> methode2(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> result){
        ArrayList<Point> rest = (ArrayList<Point>) points.clone();
        ArrayList<Point> fvs = new ArrayList<Point>();
        Evaluation e = new Evaluation();
        int d, degreMax=0;

        for (int i = 0; i < 100; i++) {
            Collections.shuffle(points, new Random(System.nanoTime() + i));
            rest = (ArrayList<Point>) points.clone();
            fvs = new ArrayList<Point>();

            while (!e.isValid(points, fvs, edgeThreshold)) {
                Point choosenOne = rest.get(0);
                for (Point p : rest)
                    if (degre(p, rest, edgeThreshold) > degre(choosenOne, rest, edgeThreshold))
                        choosenOne = p;

                if ((d = degre(choosenOne, rest, edgeThreshold)) > degreMax)
                    degreMax = d;

                fvs.add(choosenOne);
                rest.remove(choosenOne);
            }

            if (fvs.size() < result.size())
                result = fvs;
        }

        fvs=result;

        return fvs;
    }

    private ArrayList<Point> methode3(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> result){
        int i;
        ArrayList<Point> fvs;
        ArrayList<Point> fvs_tmp = null;
        Evaluation e = new Evaluation();

        for(int t=0; t<1000; t++) {
            Collections.shuffle(points, new Random(System.nanoTime() + t));
            for (int ite = 0; ite < 2; ite++) {
                fvs = (ArrayList<Point>) points.clone();
                Point maxi;
                maxi = points.parallelStream().max(Comparator.comparingInt(element -> voisins(element, points).size())).get();
                ArrayList<Point> test = (ArrayList<Point>) points.clone();
                if (ite == 1) {
                    test.remove(maxi);
                    maxi = test.parallelStream().max(Comparator.comparingInt(element -> voisins(element, test).size())).get();
                }

                ArrayList<Point> neigh = voisins(maxi, points);

                i = 0;
                int high = neigh.size();
                while (i <= high) {
                    for (Point p2 : points) {
                        if (voisins(p2, points).size() <= i) {
                            fvs.remove(p2);
                            if (!e.isValid(points, fvs, edgeThreshold)) fvs.add(p2);
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
                if (ite == 0) {
                    fvs_tmp = (ArrayList<Point>) fvs.clone();
                } else {
                    if (fvs.size() > fvs_tmp.size()) {
                        fvs = (ArrayList<Point>) fvs_tmp.clone();
                    }
                }
                if (fvs.size() < result.size())
                    result = fvs;
            }
        }

        fvs = result;

        return fvs;
    }

    private ArrayList<Point> methode4(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> result){
        ArrayList<Point> fvs;
        Evaluation e= new Evaluation();

        for(int t=0; t<10000 ; t++) {
            fvs = algoMethode4(points);
            if (fvs.size() < result.size() && e.isValid(points, fvs, edgeThreshold))
                result=fvs;
        }

        fvs=result;

        return fvs;
    }

    public ArrayList<Point> localSearching32(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> fvs){
        boolean continuer = true;
        Point a, b, c, d, e;
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        reste.removeAll(fvs);
        Evaluation evaluation = new Evaluation();
        while (continuer) {
            // shuffle ici
            Collections.shuffle(fvs, new Random(System.nanoTime()));
            continuer = false;
            for (int indiceA = 0; indiceA < fvs.size() && !continuer; indiceA++) {
                a = fvs.remove(indiceA);
                for (int indiceB = indiceA + 1; indiceB < fvs.size() && !continuer; indiceB++) {
                    b = fvs.remove(indiceB);

                    for (int indiceC = indiceB + 1; indiceC < fvs.size() && !continuer; indiceC++) {
                        c = fvs.remove(indiceC);
                        if ((estArete(a, b, edgeThreshold) && (estArete(b, c, edgeThreshold) || estArete(a, c, edgeThreshold)))
                                || (estArete(b, c, edgeThreshold)) && estArete(a, c, edgeThreshold)) {
                            Collections.shuffle(reste, new Random(System.nanoTime()));
                            for (int indiceD = 0; indiceD < reste.size(); indiceD++) {
                                d = reste.get(indiceD);
                                fvs.add(d);
                                for (int indiceE = indiceD + 1; indiceE < reste.size(); indiceE++) {
                                    e = reste.get(indiceE);
                                    fvs.add(e);
                                    if (evaluation.isValid(points, fvs, edgeThreshold)) {
                                        continuer = true;
                                        reste.remove(d);
                                        reste.remove(e);
                                        break;
                                    } else
                                        fvs.remove(e);
                                }
                                if (continuer)
                                    break;
                                else if (!continuer)
                                    fvs.remove(d);
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

        return fvs;
    }

    public ArrayList<Point> localSearching21(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> fvs){
        boolean continuer = true;
        Point a, b, c;
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        reste.removeAll(fvs);
        Evaluation evaluation = new Evaluation();

        // shuffle ici
        Collections.shuffle(fvs, new Random(System.nanoTime()));
        for (int i = 0; i < fvs.size() && !continuer; i++) {
            a = fvs.remove(i);
            for (int j = i + 1; j < fvs.size() && !continuer; j++) {
                b = fvs.remove(j);

                if (estArete(a, b, edgeThreshold)) {
                    // shuffle reste aussi si ça prend pas trop de temps
                    Collections.shuffle(reste, new Random(System.nanoTime()));
                    for (int indiceC=0; indiceC<reste.size(); indiceC++) {
                        c=reste.get(indiceC);
                        fvs.add(c);
                        if (evaluation.isValid(points, fvs, edgeThreshold)) {
                            continuer = true;
                            reste.remove(c);
                            break;
                        }
                        fvs.remove(c);
                    }
                }
                if (!continuer)
                    fvs.add(b);
            }
            if (!continuer)
                fvs.add(a);
        }

        return fvs;
    }

    public ArrayList<Point> suppressionDegresFaibles(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> fvs){
        ArrayList<Point> reste = (ArrayList<Point>) points.clone();
        reste.removeAll(fvs);
        ArrayList<Point> fvsDeDepart = (ArrayList<Point>) fvs.clone();
        ArrayList<Point> rest = (ArrayList<Point>) reste.clone();
        ArrayList<Point> result = (ArrayList<Point>) fvs.clone();

        Point pointDegreMax = points.parallelStream().max(Comparator.comparingInt(element -> voisins(element, points).size())).get();;
        int degreMax = voisins(pointDegreMax, points).size();

        Point p;
        Evaluation e = new Evaluation();
        for (int t = 0; t < 100; t++) {
            reste = (ArrayList<Point>) rest.clone();
            fvs = (ArrayList<Point>) fvsDeDepart.clone();

            Collections.shuffle(fvs, new Random(System.nanoTime() + t));
            for (int i = 0; i < degreMax; i++) {
                for (int j = 0; j < fvs.size(); j++) {
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
            if (fvs.size() < result.size())
                result = fvs;
        }

        fvs = result;
        return fvs;
    }

    public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
        ArrayList<Point> fvs;

        Evaluation e = new Evaluation();

        int degreMax = 0, d;

        ArrayList<Point> result = (ArrayList<Point>) points.clone();
        ArrayList<Point> rest;

        Point p;

        /* Quatre méthodes de création naïves de l'ensemble FVS. */

        fvs=methode1(points, edgeThreshold, points);

        System.out.println("Taille fvs après méthode 1 : " + fvs.size());

        fvs=methode2(points, edgeThreshold, fvs);

        System.out.println("Taille fvs après méthode 2 : " + fvs.size());

        fvs= methode3(points, edgeThreshold, fvs);

        System.out.println("Taille fvs après méthode 3 : " + fvs.size());

        fvs=methode4(points, edgeThreshold, fvs);

        System.out.println("Taille fvs après méthode 4 : " + fvs.size());

        /*********** Local searching naïfs ************/
        // Local searching naïf trois pour deux.
        fvs=localSearching32(points, edgeThreshold, fvs);
        System.out.println("Local searching 3->2 : " + fvs.size());
        // Local searching naïf deux pour un.
        fvs=localSearching21(points, edgeThreshold, fvs);
        System.out.println("Local searching 1->2 : " + fvs.size());

        fvs=suppressionDegresFaibles(points, edgeThreshold, fvs);
        System.out.println("Suppression des degrés faibles : " + fvs.size());

        return fvs;
    }
}
