package ri.propagation.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class Scenario1 implements Scenario {

    private SIS sis;
    private Collection<Node> nodes;

    public Scenario1(Graph graph) {
        Collection<Node> susceptibleNodes = this.nodes = graph.getNodeSet();
        this.sis = new SIS(1 / 7D, 1 / 14D, new ArrayList<>(susceptibleNodes));
    }

    public List<Node> getNodes() {
        return new ArrayList<>(this.nodes);
    }

    @Override
    public List<Collection<Node>> propagation(int days) {
        return this.sis.propagation(days);
    }
}