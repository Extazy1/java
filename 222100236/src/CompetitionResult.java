import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CompetitionResult {
    List<Heat> Heats;

    public static class Heat {
        String Name;
        List<Result> Results;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public List<Result> getResults() {
            return Results;
        }

        public void setResults(List<Result> results) {
            Results = results;
        }
    }

    public static class Result {
        private String FullName;
        private int Rank;
        private String TotalPoints;
        private List<Dive> Dives;

        public String getFullName() {
            return FullName;
        }

        public void setFullName(String fullName) {
            FullName = fullName;
        }

        public int getRank() {
            return Rank;
        }

        public void setRank(int rank) {
            Rank = rank;
        }

        public String getTotalPoints() {
            return TotalPoints;
        }

        public void setTotalPoints(String totalPoints) {
            TotalPoints = totalPoints;
        }

        public List<Dive> getDives() {
            return Dives;
        }

        public void setDives(List<Dive> dives) {
            Dives = dives;
        }
    }

    public static class Dive {
        private String DivePoints;

        public String getDivePoints() {
            return DivePoints;
        }

        public void setDivePoints(String divePoints) {
            DivePoints = divePoints;
        }
    }

    public List<Heat> getHeats() {
        return Heats;
    }

    public void setHeats(List<Heat> heats) {
        Heats = heats;
    }
}
