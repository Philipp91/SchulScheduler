package schulscheduler.model.eingabe;

/**
 * Die Gewichtungen werden in den Eingabedaten einer Berechnung dazu genutzt, um die Dauer einer Berechnung und die
 * Priorität von Optimierungen anzugeben.
 */
public enum EnumGewichtung {

    NULL, NIEDRIG, MITTEL, HOCH, SEHR_HOCH, MAXIMAL;

    private static final EnumGewichtung[] BERECHNUNG_PRIORITAET_VALUES = {NULL, NIEDRIG, MITTEL, HOCH};

    /**
     * @return Die Werte {@link #NULL}, {@link #NIEDRIG}, {@link #MITTEL} und {@link #HOCH}, die üblicherweise für
     * Berechnungs-Prioritäten verwendet werden.
     */
    public static EnumGewichtung[] berechnungPrioritaetValues() {
        return BERECHNUNG_PRIORITAET_VALUES;
    }

}
