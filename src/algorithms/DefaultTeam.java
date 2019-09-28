package algorithms;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;


//< 82,4 +1 point  1,5 point possible si inférieur à je sais pas combien. 2 points de bonus. touche g pour rendre le travail. ressucite 3 et je tue 2 à la place.
public class DefaultTeam {


   /* public ArrayList<Point> calculFVS(ArrayList<Point> pointsIn, int edgeThreshold){
        ArrayList<Point> fvs = new ArrayList<Point>();

        fvs = gloutonV3(pointsIn, edgeThreshold);

        return fvs;
    }

    private ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int e){
        ArrayList<Point> result = new ArrayList<Point>();

        for (Point point:vertices) if (point.distance(p)<e && !point.equals(p)) result.add((Point)point.clone());

        return result;
    }

    public ArrayList<Point> gloutonV3(ArrayList<Point> points, int e){
        ArrayList<Point> retour = (ArrayList<Point>) points.clone();
        Evaluation eval  = new Evaluation();
        Point maxi = points.parallelStream().max(Comparator.comparingInt(element ->  neighbor(element,points, e).size())).get();
        ArrayList<Point> neigh = neighbor(maxi,points, e);
        int i = 0,high = neigh.size();
        while(i<=high){
            for(Point p : points){
                if(neighbor(p,points, e).size()<=i){
                    retour.remove(p);
                    if(!eval.isValid(points,retour, e)) retour.add(p);
                }
            }
            i++;
        }
        return retour;
    }*/

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

    public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) {
        ArrayList<Point> fvs;

        Evaluation e = new Evaluation();

        int degreMax=0, d;
        long seed = System.nanoTime();

        ArrayList<Point> result = (ArrayList<Point>)points.clone();
        ArrayList<Point> rest;

        for (int i=0;i<100;i++) {
            Collections.shuffle(points, new Random(System.nanoTime()+i));
            rest = (ArrayList<Point>)points.clone();
            fvs = new ArrayList<Point>();

            while (!e.isValid(points, fvs, edgeThreshold)) {
                Point choosenOne=rest.get(0);
                for (Point p: rest)
                    if (degre(p,rest,edgeThreshold)>degre(choosenOne,rest,edgeThreshold))
                        choosenOne=p;

                if((d=degre(choosenOne, rest, edgeThreshold))>degreMax)
                    degreMax=d;

                fvs.add(choosenOne);
                rest.remove(choosenOne);
            }
            System.out.println("GR. Current sol: " + result.size() + ". Found next sol: "+fvs.size());

            if (fvs.size()<result.size())
                result = fvs;
        }

        fvs=result;
        System.out.println("Taille fvs : "+fvs.size());


        ArrayList<Point> reste = new ArrayList<Point>();
        reste.addAll(points);
        reste.removeAll(fvs);
        int i, j;


        result = (ArrayList<Point>)fvs.clone();
        rest = (ArrayList<Point>)reste.clone();
        ArrayList<Point> fvsClone= (ArrayList<Point>)fvs.clone();

        for(int t=0; t<100; t++) {
            reste = (ArrayList<Point>)rest.clone();
            fvs = (ArrayList<Point>)fvsClone.clone();

            Collections.shuffle(fvs, new Random(seed));
            Point p;
            for (i = 0; i < degreMax; i++) {
                for (j = 0; j<fvs.size(); j++) {
                    p=points.get(j);
                    if (degre(p, reste, edgeThreshold) <= i) {
                        fvs.remove(p);
                        if (!e.isValid(points, fvs, edgeThreshold)) {
                            fvs.add(p);
                        }
                        reste.add(p);
                    }
                }
            }
            System.out.println("GR. Current sol: " + result.size() + ". Found next sol: "+fvs.size());

            if (fvs.size()<result.size())
                result = fvs;
        }

        fvs=result;

        System.out.println("After deleting min degrees : "+fvs.size());

        return fvs;
    }
}
