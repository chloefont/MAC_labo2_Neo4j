package ch.heig.mac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> possibleSpreadCounts() {
        String query =
                "MATCH (sickPer:Person{healthstatus:'Sick'})-[:VISITS]->(:Place)<-[visitHealthy:VISITS]-(healthyPer:Person{healthstatus:'Healthy'})" + "\n" +
                        "WHERE visitHealthy.starttime > sickPer.confirmedtime" + "\n" +
                        "RETURN sickPer.name AS sickName, size(collect(healthyPer.name)) AS nbHealthy";

        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
    }

    public List<Record> carelessPeople() {
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    public List<Record> sociallyCareful() {
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
        
        String query = 
            "MATCH (:Person{healthstatus: 'Sick'})-[v:VISITS]->(p:Place)" + "\n" +
            "WITH p.type AS placeType, count(v) AS nbOfSickVisits" + "\n" +
            "RETURN placeType, nbOfSickVisits" + "\n" +
            "ORDER BY nbOfSickVisits DESC" + "\n" +
            "LIMIT 1;";

        try (var session = driver.session()) {
            var result = session.run(query);
            return result.peek();
        }
    }

    public List<Record> sickFrom(List<String> names) {
        Map<String, Object> params = new HashMap<>();
        params.put("list", names);

        String query = 
            "MATCH (sick:Person{healthstatus: 'Sick'})" + "\n" +
            "WHERE sick.name IN $list" + "\n" +
            "RETURN sick.name AS sickName;";

        try (var session = driver.session()) {
            var result = session.run(query, params);
            return result.list();
        }
    }
}
