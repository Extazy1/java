import java.util.List;

public class Country {
    private String CountryName;
    private List<Athlete> Participations;

    // getters and setters
    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public List<Athlete> getParticipations() {
        return Participations;
    }

    public void setParticipations(List<Athlete> participations) {
        Participations = participations;
    }
}

class Athlete {


    private int Gender; // 0 for male, 1 for female
    private String PreferredLastName;
    private String PreferredFirstName;

    // getters and setters
    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getPreferredLastName() {
        return PreferredLastName;
    }

    public void setPreferredLastName(String preferredLastName) {
        PreferredLastName = preferredLastName;
    }

    public String getPreferredFirstName() {
        return PreferredFirstName;
    }

    public void setPreferredFirstName(String preferredFirstName) {
        PreferredFirstName = preferredFirstName;
    }
}
