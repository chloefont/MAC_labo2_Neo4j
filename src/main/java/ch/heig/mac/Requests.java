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
        var query = "MATCH (p:Person{healthstatus: \"Sick\"}) -[v:VISITS]-> (pl:Place) <- [v2:VISITS]- (p2:Person{healthstatus: \"Healthy\"}) WHERE p.confirmedtime >= v.starttime AND p.confirmedtime > v2.starttime AND p.confirmedtime < v2.endtime RETURN p.name AS sickName";
        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
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
        var query = "MATCH (p:Person{healthstatus:\"Sick\"})-[v:VISITS]->(pl:Place) WHERE p.confirmedtime > v.starttime WITH p,count(DISTINCT pl) as rels WHERE rels > 10 RETURN p.name AS sickName, rels AS nbPlaces ORDER BY rels DESC";
        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
    }

    public List<Record> sociallyCareful() {
        var query = "MATCH(p:Person{healthstatus:\"Sick\"})-[v:VISITS]->(pl:Place{type:\"Bar\"})\n" +
                "WHERE p.confirmedtime > v.starttime AND p.confirmedtime < v.endtime\n" +
                "WITH collect(distinct p) as badpeople\n" +
                "MATCH(p2:Person{healthstatus:\"Sick\"})\n" +
                "WHERE NOT p2 IN badpeople\n" +
                "RETURN p2.name as sickName";

        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
    }

    public List<Record> peopleToInform() {
        var query = "MATCH(sickP:Person{healthstatus:\"Sick\"})-[v:VISITS]-(pl:Place)-[v2:VISITS]-(maybeSickP:Person{healthstatus:\"Healthy\"})\n" +
                "WITH *, apoc.coll.min([v.endtime, v2.endtime]) AS minTime, apoc.coll.max([v.starttime, v2.starttime]) AS maxTime\n" +
                "WHERE duration.between(maxTime, minTime).hours > 2\n" +
                "RETURN sickP.name as sickName, collect(maybeSickP.name) as peopleToInform";
        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
    }

    public List<Record> setHighRisk() {
        var query =
                "MATCH(sickP:Person{healthstatus:\"Sick\"})-[v:VISITS]-(pl:Place)-[v2:VISITS]-(maybeSickP:Person{healthstatus:\"Healthy\"})\n" +
                "WITH *, apoc.coll.min([v.endtime, v2.endtime]) AS minTime, apoc.coll.max([v.starttime, v2.starttime]) AS maxTime\n" +
                "WHERE duration.between(maxTime, minTime).hours > 2\n" +
                "SET maybeSickP.risk = \"high\"\n" +
                "RETURN DISTINCT  maybeSickP.name AS highRiskName;";
        try (var session = driver.session()) {
            var result = session.run(query);
            return result.list();
        }
    }

    public List<Record> healthyCompanionsOf(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        var query = "MATCH (p:Person{name:$name})-[:VISITS*1..3]-(p2:Person{healthstatus:'Healthy'})\n" +
                "RETURN p2.name as healthyName";

        try (var session = driver.session()) {
            var result = session.run(query, params);
            return result.list();
        }
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
