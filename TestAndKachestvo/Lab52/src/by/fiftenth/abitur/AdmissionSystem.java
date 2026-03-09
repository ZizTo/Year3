package by.fiftenth.abitur;

import java.util.ArrayList;
import java.util.List;

public class AdmissionSystem {
    public List<Abiturient> getAdmittedList(List<Abiturient> applicants, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("Количество мест не может быть отрицательным.");
        }
        if (applicants == null) {
            throw new IllegalArgumentException("Список абитуриентов не может быть null.");
        }
        if (places == 0 || applicants.isEmpty()) {
            return new ArrayList<>();
        }

        List<Abiturient> sortedApplicants = new ArrayList<>(applicants);

        sortedApplicants.sort((a1, a2) -> Integer.compare(a2.getTotalScore(), a1.getTotalScore()));

        int limit = Math.min(places, sortedApplicants.size());

        return sortedApplicants.subList(0, limit);
    }
}
