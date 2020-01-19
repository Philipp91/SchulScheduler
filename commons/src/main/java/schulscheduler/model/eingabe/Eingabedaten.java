package schulscheduler.model.eingabe;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.i18n.DefaultValues;
import schulscheduler.model.schule.*;
import schulscheduler.model.unterricht.*;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.*;

@XmlRootElement(name = "eingabedaten")
public class Eingabedaten {

    @XmlElement(name = "modelVersion")
    public final int modelVersion = 1;

    private final SimpleListProperty<Stunde> stunden = new SimpleListProperty<>(this, "stunden", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Zeitslot> zeitslots = new SimpleListProperty<>(this, "zeitslots", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Lehrer> lehrer = new SimpleListProperty<>(this, "lehrer", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Fach> faecher = new SimpleListProperty<>(this, "faecher", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Klasse> klassen = new SimpleListProperty<>(this, "klassen", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Profil> profile = new SimpleListProperty<>(this, "profile", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Zuweisung> zuweisungen = new SimpleListProperty<>(this, "zuweisungen", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleListProperty<Kopplung> kopplungen = new SimpleListProperty<>(this, "kopplungen", FXCollections.observableList(new java.util.ArrayList<>()));
    private final SimpleObjectProperty<BerechnungsParameter> parameter = new SimpleObjectProperty<>(this, "parameter");

    /**
     * @return A deep-copy of this using Java Object Serialization (JOS).
     */
    public Eingabedaten copy() {
//        try {
            return null; // TODO Serialization.javaClone(this);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Erstellt einen Eingabedatensatz mit 10 Schulstunden und dazugehörigen Zeitslots.
     *
     * @return Der Eingabedatensatz, der in einem neuen/leeren Fenster angezeigt werden soll.
     */
    @Nonnull
    public static Eingabedaten createDefault() {
        Eingabedaten eingabedaten = new Eingabedaten();
        eingabedaten.addStunden(10);
        eingabedaten.setParameter(BerechnungsParameter.createDefault());
        return eingabedaten;
    }

    /**
     * Entfernt alles aus dem Eingabedatensatz, außer den Stunden und Zeitslots.
     * Die Daten müssen "rückwärts" gelöscht werden, sodass nie etwas im Datenmodell enthalten ist, das auf etwas
     * verweist, was schon gelöscht wurde.
     */
    public void clear() {
        kopplungen.clear();
        zuweisungen.clear();
        profile.clear();
        klassen.clear();
        lehrer.clear();
        faecher.clear();
    }

    /**
     * @return True wenn das Datenmodell (bis auf Stunden und Zeitslots, was ja die initialen Daten sind) leer ist.
     */
    public boolean isEmpty() {
        // Es genügt, diese zu prüfen, der Rest hängt sowieso davon ab.
        return faecher.isEmpty() && lehrer.isEmpty() && klassen.isEmpty();
    }

    /**
     * Fügt am Ende so lange Stunden hinzu, bis die gewünschte Anzahl erreicht ist. Außerdem werden entsprechende
     * Zeitslots sowie LehrerVerfügbarkeits-Einträge eingefügt.
     *
     * @param newStundenCount Die Anzahl gewünschter Stunden, muss größer sein als die derzeitige Anzahl!
     */
    public void addStunden(int newStundenCount) {
        while (stunden.size() < newStundenCount) {
            int nummer = stunden.size() + 1;
            Stunde stunde = new Stunde(nummer);
            if (nummer <= 10) {
                try {
                    stunde.setBeginn(DefaultValues.getInstance().getString("beginn_" + nummer));
                    stunde.setEnde(DefaultValues.getInstance().getString("ende_" + nummer));

                    if (nummer < 5) {
                        stunde.setUnterrichtsprioritaet(EnumGewichtung.SEHR_HOCH);
                    } else if (nummer < 7) {
                        stunde.setUnterrichtsprioritaet(EnumGewichtung.HOCH);
                    } else if (nummer < 9) {
                        stunde.setUnterrichtsprioritaet(EnumGewichtung.MITTEL);
                    } else {
                        stunde.setUnterrichtsprioritaet(EnumGewichtung.NIEDRIG);
                    }
                } catch (MissingResourceException e) {
                    throw new RuntimeException(e);
                }
            }
            stunden.add(stunde);

            for (EnumWochentag tag : EnumWochentag.values()) {
                addZeitslot(new Zeitslot(stunde, tag));
            }
        }
    }

    /**
     * Fügt den Zeitslot zu {@link #zeitslotsProperty()} hinzu. Zusätzlich werden für alle Lehrer
     * entsprechende Verfügbarkeits-Einträge generiert.
     *
     * @param zeitslot Der hinzuzufügende Zeitslot.
     */
    public void addZeitslot(@Nonnull Zeitslot zeitslot) {
        zeitslots.add(zeitslot);
        for (Lehrer lehrer : this.lehrer) {
            LehrerVerfuegbarkeit verfuegbarkeit = new LehrerVerfuegbarkeit(zeitslot, EnumVerfuegbarkeit.NORMAL);
            lehrer.getVerfuegbarkeit().add(verfuegbarkeit);
        }
    }

    /**
     * Fügt einen neuen Lehrer sowie alle Verfügbarkeits-Einträge für den Lehrer hinzu.
     *
     * @return Der hinzugefügte Lehrer.
     */
    @Nonnull
    public Lehrer addLehrer() {
        Lehrer lehrer = new Lehrer();
        for (Zeitslot zeitslot : zeitslots) {
            lehrer.getVerfuegbarkeit().add(new LehrerVerfuegbarkeit(zeitslot, EnumVerfuegbarkeit.NORMAL));
        }
        this.lehrer.add(lehrer);
        return lehrer;
    }

    /**
     * Fügt ein neues Fach sowie alle Profil-Einträge für die derzeit vorhandenen Profile hinzu.
     *
     * @return Das hinzugefügte Fach.
     */
    @Nonnull
    public Fach addFach() {
        Fach fach = new Fach();
        for (Profil profil : profile) {
            profil.getEintraege().add(new ProfilEintrag(fach, 0));
        }
        faecher.add(fach);
        return fach;
    }

    /**
     * Fügt ein neues Profil sowie alle Profil-Einträge für die derzeit vorhandenen Fächer hinzu.
     *
     * @return Das hinzugefügte Profil.
     */
    @Nonnull
    public Profil addProfil() {
        Profil profil = new Profil();
        for (Fach fach : faecher) {
            profil.getEintraege().add(new ProfilEintrag(fach, 0));
        }
        profile.add(profil);
        return profil;
    }

    /**
     * Fügt eine neue Klasse hinzu.
     *
     * @return Die hinzugefügte Klasse.
     */
    @Nonnull
    public Klasse addKlasse() {
        Klasse klasse = new Klasse();
        klassen.add(klasse);
        return klasse;
    }

    /**
     * Importiert das gegebene Profil für alle gegebenen Klassen. Weil vorhandene Einträge "überschrieben" werden
     * sollen, es aber zu kompliziert wäre, alle vorhandenen Zuweisungen zu suchen und zu manipulieren, werden diese
     * einfach gelöscht, wenn sie im Konflikt stehen, und dann neue hinzugefügt.
     *
     * @param profil Das zu importierende Profil.
     * @param klassen Die Klassen, für die das Profil importiert werden soll.
     */
    public void importProfil(@Nonnull Profil profil, @Nonnull List<Klasse> klassen) {

        // Sammle alle Aktionen und führe am Schluss auf ein Mal aus.
        List<Zuweisung> zuweisungenToAdd = new ArrayList<>();
        List<Zuweisung> zuweisungenToDelete = new ArrayList<>();
        Set<Fach> faecherToBeAdded = new HashSet<>();

        for (ProfilEintrag eintrag : profil.getEintraege()) {
            if (eintrag.getWochenstunden() == 0) {
                continue;
            }

            faecherToBeAdded.add(eintrag.getFach());

            // Neue Zuweisungen für alle Klassen hinzufügen.
            for (Klasse klasse : klassen) {
                zuweisungenToAdd.add(new Zuweisung(eintrag.getWochenstunden(), null, null, eintrag.getFach(), klasse));
            }
        }

        // Und noch alle alten, in Konflikt stehenden Zuweisungen suchen und löschen.
        for (Zuweisung zuweisung : zuweisungen) {
            if (faecherToBeAdded.contains(zuweisung.getFach()) && klassen.contains(zuweisung.getKlasse())) {
                zuweisungenToDelete.add(zuweisung);
            }
        }

        // Gesammelte Aktionen ausführen.
        zuweisungen.removeAll(zuweisungenToDelete);
        zuweisungen.addAll(zuweisungenToAdd);
    }

    /**
     * Erstellt Profileinträge sodass für jedes Profil und für jedes Fach einer existiert (wobei fehlende als 0 neu
     * hinzugefügt werden).
     */
    public void createMissingProfilEintraege() {
        for (Profil profil : profile) {
            Set<Fach> faecher = new HashSet<>(this.faecher);
            for (ProfilEintrag eintrag : profil.getEintraege()) {
                faecher.remove(eintrag.getFach());
            }
            for (Fach fach : faecher) {
                profil.getEintraege().add(new ProfilEintrag(fach, 0));
            }
        }
    }

    /**
     * Erstellt Zeitslots sodass für jeden Tag und jede Stunde einer existiert.
     */
    public void createMissingZeitslots() {
        Map<Stunde, EnumSet<EnumWochentag>> stunden = new HashMap<>();
        for (Stunde stunde : this.stunden) {
            stunden.put(stunde, EnumSet.allOf(EnumWochentag.class));
        }
        for (Zeitslot zeitslot : zeitslots) {
            stunden.get(zeitslot.getStunde()).remove(zeitslot.getWochentag());
        }
        for (Map.Entry<Stunde, EnumSet<EnumWochentag>> stunde : stunden.entrySet()) {
            for (EnumWochentag tag : stunde.getValue()) {
                addZeitslot(new Zeitslot(stunde.getKey(), tag));
            }
        }
    }

    /**
     * Erstellt Verfügbarkeitseinträge sodass für jedes Lehrer und für jeden Zeitslot einer existiert (wobei fehlende
     * als {@link EnumVerfuegbarkeit#NORMAL} neu hinzugefügt werden).
     */
    public void createMissingVerfuegbarkeitsEintraege() {
        for (Lehrer lehrer : this.lehrer) {
            Set<Zeitslot> zeitslots = new HashSet<>(this.zeitslots);
            Set<LehrerVerfuegbarkeit> eintraegeToRemove = new HashSet<>();
            for (LehrerVerfuegbarkeit eintrag : lehrer.getVerfuegbarkeit()) {
                if (eintrag.getZeitslot() == null) {
                    eintraegeToRemove.add(eintrag);
                } else {
                    zeitslots.remove(eintrag.getZeitslot());
                }
            }
            lehrer.getVerfuegbarkeit().removeAll(eintraegeToRemove);
            for (Zeitslot zeitslot : zeitslots) {
                lehrer.getVerfuegbarkeit().add(new LehrerVerfuegbarkeit(zeitslot, EnumVerfuegbarkeit.NORMAL));
            }
        }
    }

    @XmlElement(name = "stunden")
    public ObservableList<Stunde> getStunden() {
        return stunden.get();
    }

    public SimpleListProperty<Stunde> stundenProperty() {
        return stunden;
    }

    public void setStunden(ObservableList<Stunde> stunden) {
        this.stunden.set(stunden);
    }

    @XmlElement(name = "zeitslots")
    public ObservableList<Zeitslot> getZeitslots() {
        return zeitslots.get();
    }

    public SimpleListProperty<Zeitslot> zeitslotsProperty() {
        return zeitslots;
    }

    public void setZeitslots(ObservableList<Zeitslot> zeitslots) {
        this.zeitslots.set(zeitslots);
    }

    @XmlElement(name = "lehrer")
    public ObservableList<Lehrer> getLehrer() {
        return lehrer.get();
    }

    public SimpleListProperty<Lehrer> lehrerProperty() {
        return lehrer;
    }

    public void setLehrer(ObservableList<Lehrer> lehrer) {
        this.lehrer.set(lehrer);
    }

    @XmlElement(name = "faecher")
    public ObservableList<Fach> getFaecher() {
        return faecher.get();
    }

    public SimpleListProperty<Fach> faecherProperty() {
        return faecher;
    }

    public void setFaecher(ObservableList<Fach> faecher) {
        this.faecher.set(faecher);
    }

    @XmlElement(name = "klassen")
    public ObservableList<Klasse> getKlassen() {
        return klassen.get();
    }

    public SimpleListProperty<Klasse> klassenProperty() {
        return klassen;
    }

    public void setKlassen(ObservableList<Klasse> klassen) {
        this.klassen.set(klassen);
    }

    @XmlElement(name = "profile")
    public ObservableList<Profil> getProfile() {
        return profile.get();
    }

    public SimpleListProperty<Profil> profileProperty() {
        return profile;
    }

    public void setProfile(ObservableList<Profil> profile) {
        this.profile.set(profile);
    }

    @XmlElement(name = "zuweisungen")
    public ObservableList<Zuweisung> getZuweisungen() {
        return zuweisungen.get();
    }

    public SimpleListProperty<Zuweisung> zuweisungenProperty() {
        return zuweisungen;
    }

    public void setZuweisungen(ObservableList<Zuweisung> zuweisungen) {
        this.zuweisungen.set(zuweisungen);
    }

    @XmlElement(name = "kopplungen")
    public ObservableList<Kopplung> getKopplungen() {
        return kopplungen.get();
    }

    public SimpleListProperty<Kopplung> kopplungenProperty() {
        return kopplungen;
    }

    public void setKopplungen(ObservableList<Kopplung> kopplungen) {
        this.kopplungen.set(kopplungen);
    }

    @XmlElement(name = "parameter")
    public BerechnungsParameter getParameter() {
        return parameter.get();
    }

    public SimpleObjectProperty<BerechnungsParameter> parameterProperty() {
        return parameter;
    }

    public void setParameter(BerechnungsParameter parameter) {
        this.parameter.set(parameter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Eingabedaten that = (Eingabedaten) o;
        return Objects.equals(getStunden(), that.getStunden()) &&
                Objects.equals(getZeitslots(), that.getZeitslots()) &&
                Objects.equals(getLehrer(), that.getLehrer()) &&
                Objects.equals(getFaecher(), that.getFaecher()) &&
                Objects.equals(getKlassen(), that.getKlassen()) &&
                Objects.equals(getProfile(), that.getProfile()) &&
                Objects.equals(getZuweisungen(), that.getZuweisungen()) &&
                Objects.equals(getKopplungen(), that.getKopplungen()) &&
                Objects.equals(getParameter(), that.getParameter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStunden(), getZeitslots(), getLehrer(), getFaecher(), getKlassen(), getProfile(), getZuweisungen(), getKopplungen(), getParameter());
    }
}
