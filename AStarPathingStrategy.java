import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{
//    private List<Point> openList = new LinkedList<>();
//    private List<Point> closedList = new LinkedList<>();

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {

        List<Point> path = new LinkedList<>();
        List<Point> openList = new LinkedList<>();
//        PriorityQueue<Point> openList1 = new PriorityQueue<>();
        List<Point> closedList = new LinkedList<>();
        Point curr = start;
        openList.add(start);
        while (!withinReach.test(curr, end)) {

            // returns filtered list for unevaluated and valid neighbors with calculated values
            // not in open list
            openList = computeVals(potentialNeighbors.apply(curr)
                    .filter(canPassThrough)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end))
                    .filter(pt -> pt.h == -1)
                    .filter(pt -> !closedList.contains(pt))
                    .collect(Collectors.toList()), end, curr, openList);

            // Sort to find smallest F value
            Comparator<Point> comp1 = Comparator.comparing(Point::getF);
            Collections.sort(openList, comp1);

            // add current to closedList from openList then to path, change current
            if(openList.size() != 0) {
                closedList.add(curr);
                curr = openList.get(0);
                openList.remove(curr);
            }
//            if(openList.size() != 0) {
//                closedList1.add(curr);
//                curr = openList1.poll();
//            }
            else
                return path;

        }

        while (curr != start){
            path.add(0, curr);
            curr = curr.prev;
        }
        return path;
    }


    //computes F,G,H values and adds to the openList and declares previous node
    public List<Point> computeVals(List<Point> validNeighbors, Point end, Point start, List<Point> openList) {
        List<Point> res = new LinkedList<>();
        for (Point p : validNeighbors){
            p.setPrev(start);
            p.setH(Math.abs((end.x - p.x)) + Math.abs((end.y- p.y)));
            p.setG(start.g + 1);
            p.setF(p.g + p.h);
            res.add(p);
            if (!openList.contains(p))
                openList.add(p);
        }
        return openList;
    }

//    private static boolean neighbors(Point p1, Point p2)
//    {
//        return p1.x+1 == p2.x && p1.y == p2.y ||
//                p1.x-1 == p2.x && p1.y == p2.y ||
//                p1.x == p2.x && p1.y+1 == p2.y ||
//                p1.x == p2.x && p1.y-1 == p2.y;
//    }
}
