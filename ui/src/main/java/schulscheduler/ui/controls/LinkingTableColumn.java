package schulscheduler.ui.controls;

import javafx.beans.InvalidationListener;

/**
 * Eine Tabellenspalte, die aus {@link LinkingTableCell}s besteht, sodass zwischen je zwei Zeilen ein Button angezeigt
 * wird, mit dem die beiden Zeilen verbunden werden können.
 *
 * @param <S> Der Typ der TableView (d.h. S == TableView&lt;S&gt;)
 */
public class LinkingTableColumn<S> extends BasePropertyTableColumn<S, Boolean> {

    /**
     * JavaFX 8 hat einen "Bug" bzw. eine Eigenheit: Wenn eine TableCell nicht mehr gebraucht wird, wird sie in einen
     * Cache gepackt, sodass sie später recycelt werden kann. Dabe wird aber manchmal vergessen, den Index der Zelle auf
     * -1 zu setzen. Wenn die Zelle später als normale Zelle (mit sinnvollem Index) verwendet wird, ist das kein
     * Problem, denn dann bekommt sie einen neuen Index. Wenn sie aber als "Füll"-Zelle verwendet wird, d.h. außerhalb
     * des Index-Bereichs der TableView, dann bekommt sie keinen neuen Index (wozu auch) und es wird updateItem(null,
     * true) aufgerufen. Das wäre kein Problem, *aber* getIndex() ist dann nicht auf -1. Wenn man also von außen
     * updateItem() aufruft und sich auf getIndex() verlässt, funktioniert das nicht mehr.<br>
     * => Es ist nicht ohne Seiteneffekte möglich, updateItem() einfach so aufzurufen.<br>
     * <br>
     * Da Zellen aber nicht nur geupdated werden müssen, wenn sich ihr eigener Inhalt ändert (dafür sorgt die TableView)
     * sondern auch wenn sich ein Nachbar ändert (insbesondere wenn dieser verschwindet), muss man die Zellen bei jeder
     * Änderung der zugrundeliegenden Liste updaten. Die Möglichkeit, die hier implementiert ist (etwas pfuschig und
     * nicht sehr performant) basiert darauf, einfach alle alten Zellen auch aus dem Cache zu löschen, sodass neue
     * generiert werden müssen. Dafür muss <tt>VirtualFlow.needsRecreateCells</tt> gesetzt werden. Als Spalte kann man
     * das am einfachsten auslösen indem man seine CellFactory ändert.<br>
     * => Neue CellFactory (die dasselbe tut wie die alte) generieren und setzen.
     */
    private final InvalidationListener baseListChanged = observable -> updateCellFactory();

    /**
     * Erstellt eine LinkingTableColumn.
     */
    public LinkingTableColumn() {
        updateCellFactory();

        tableViewProperty().addListener((observable, oldTableView, newTableView) -> {
            if (oldTableView != null) {
                oldTableView.itemsProperty().removeListener(baseListChanged);
            }
            if (newTableView != null) {
                newTableView.itemsProperty().addListener(baseListChanged);
            }
        });
    }

    /**
     * Erstelt eine neue CellFactory und gibt sie an die Spalte weiter.
     */
    private void updateCellFactory() {
        this.setCellFactory(param -> new LinkingTableCell<>());
    }

}
