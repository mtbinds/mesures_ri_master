
import java.util.Collection;
import java.util.List;

import org.graphstream.graph.Node;

public interface Scenario {
    
    List<Collection<Node>> propagation(int days);
}