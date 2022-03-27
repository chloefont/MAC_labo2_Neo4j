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
        var request = "MATCH(p:Person{healthstatus:\"Sick\"})-[v:VISITS]->(pl:Place{type:\"Bar\"})\n" +
                "WHERE p.confirmedtime > v.starttime AND p.confirmedtime < v.endtime\n" +
                "WITH collect(distinct p) as badpeople\n" +
                "MATCH(p2:Person{healthstatus:\"Sick\"})\n" +
                "WHERE NOT p2 IN badpeople\n" +
                "RETURN p2";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> peopleToInform() {
        var request = "MATCH(sickP:Person{healthstatus:\"Sick\"})-[v:VISITS]-(pl:Place)-[v2:VISITS]-(maybeSickP:Person{healthstatus:\"Healthy\"})\n" +
                "WITH *, apoc.coll.min([v.endtime, v2.endtime]) AS minTime, apoc.coll.max([v.starttime, v2.starttime]) AS maxTime\n" +
                "WHERE duration.between(maxTime, minTime).hours > 2\n" +
                "RETURN sickP.name as sickName, collect(maybeSickP.name) as peopleToInform";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> setHighRisk() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> healthyCompanionsOf(String name) {
        var request = "MATCH (p:Person{name:\"Skyla Hardin\"})-[:VISITS*1..3]-(p2:Person{healthstatus:\"Healthy\"})\n" +
                "RETURN p2.name as healthyName";
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public Record topSickSite() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> sickFrom(List<String> names) {
        throw new UnsupportedOperationException("Not implemented, yet");
    }
}
