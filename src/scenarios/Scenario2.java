

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class Scenario2 implements Scenario {

    private SIS sis;
    private List<Node> nodes;

    public Scenario2(Graph graph) {
        List<Node> susceptibleNodes = this.nodes = Toolkit.randomNodeSet(graph, (graph.getNodeCount() / 2));
        this.sis = new SIS(1 / 7D, 1 / 14D, susceptibleNodes);
    }

    public List<Node> getNodes() {
        return new ArrayList<>(this.nodes);
    }

    @Override
    public List<Collection<Node>> propagation(int days) {
        return this.sis.propagation(days);
    }
}