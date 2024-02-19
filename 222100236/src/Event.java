import java.util.List;

public class Event {
    private List<Sport> Sports;

    public List<Sport> getSports() {
        return Sports;
    }

    public void setSports(List<Sport> sports) {
        this.Sports = sports;
    }

    public static class Sport {
        private List<Discipline> DisciplineList;

        public List<Discipline> getDisciplineList() {
            return DisciplineList;
        }

        public void setDisciplineList(List<Discipline> disciplineList) {
            this.DisciplineList = disciplineList;
        }
    }

    public static class Discipline {
        private String Id;
        private String DisciplineName;
        public String getId() {
            return Id;
        }

        public void setId(String id) {
            this.Id = id;
        }

        public String getDisciplineName() {
            return DisciplineName;
        }

        public void setDisciplineName(String disciplineName) {
            DisciplineName = disciplineName;
        }
    }
}
