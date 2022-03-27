package ch.heig.mac;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class Requests {
    private static final  Logger LOGGER = Logger.getLogger(Requests.class.getName());
    private final Driver driver;

    public Requests(Driver driver) {
        this.driver = driver;
    }

    public List<String> getDbLabels() {
        var dbVisualizationQuery = "CALL db.labels";

        try (var session = driver.session()) {
            var result = session.run(dbVisualizationQuery);
            return result.list(t -> t.get("label").asString());
        }
    }

    public List<Record> possibleSpreaders() {
        var request = "MATCH (p:Person{healthstatus: \"Sick\"}) -[v:VISITS]-> (pl:Place) <- [v2:VISITS]- (p2:Person{healthstatus: \"Healthy\"}) WHERE p.confirmedtime >= v.starttime AND p.confirmedtime > v2.starttime AND p.confirmedtime < v2.endtime RETURN p.name";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> possibleSpreadCounts() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> carelessPeople() {
        var request = "MATCH (p:Person{healthstatus:\"Sick\"})-[v:VISITS]->(pl:Place) WHERE p.confirmedtime > v.starttime WITH p,count(DISTINCT pl) as rels WHERE rels > 10 RETURN p, rels ORDER BY rels DESC";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> sociallyCareful() {
        var request = "MATCH(p:Person{healthstatus:\"Sick\"}) WHERE NOT EXISTS((p)-[v:VISITS]->(pl:Place) WHERE v.starttime > p.confirmedtime) RETURN p";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> peopleToInform() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> setHighRisk() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> healthyCompanionsOf(String name) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public Record topSickSite() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> sickFrom(List<String> names) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }
}
