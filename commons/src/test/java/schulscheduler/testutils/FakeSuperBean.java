package schulscheduler.testutils;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;
import schulscheduler.model.base.BaseElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Dieser Bean ist dazu da, verschiedene Testfälle zu unterstützen.
 * FakeSuperBean und FakeBean bilden eigentlich eine Klasse. Um die Unterstützung von verschiedenen
 * Vererbungsstrukturen zu testen, wurden sie aber aufgeteilt.
 */
@XmlRootElement
public class FakeSuperBean extends BaseElement {

    private SimpleStringProperty testString = new SimpleStringProperty(this, "testString");

    public SimpleStringProperty getTestStringProperty() {
        return testString;
    }

    @XmlElement
    public String getTestString() {
        return testString.get();
    }

    public void setTestString(String testString) {
        this.testString.set(testString);
    }

    private SimpleStringProperty testPublicString = new SimpleStringProperty(this, "testPublicString");

    public SimpleStringProperty getTestPublicStringProperty() {
        return testPublicString;
    }

    @XmlElement
    public String getTestPublicString() {
        return testPublicString.get();
    }

    public void setTestPublicString(String testPublicString) {
        this.testPublicString.set(testPublicString);
    }

    private SimpleBooleanProperty testBoolean = new SimpleBooleanProperty(this, "testBoolean");

    public BooleanProperty getTestBooleanProperty() {
        return testBoolean;
    }

    @XmlElement
    public boolean isTestBoolean() {
        return testBoolean.get();
    }

    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean.set(testBoolean);
    }


    private SimpleObjectProperty<FakeSubBean> trackingDisabledObject = new SimpleObjectProperty<>(this, "trackingDisabledObject");

    @NoUndoTracking
    public ObjectProperty<FakeSubBean> getTrackingDisabledObjectProperty() {
        return trackingDisabledObject;
    }

    @XmlElement
    public FakeSubBean getTrackingDisabledObject() {
        return trackingDisabledObject.get();
    }

    public void setTrackingDisabledObject(FakeSubBean trackingDisabledObject) {
        this.trackingDisabledObject.set(trackingDisabledObject);
    }


    private ListProperty<FakeSubBean> testList = new SimpleListProperty<FakeSubBean>(this, "testList", FXCollections.observableList(new ArrayList<FakeSubBean>()));

    public ListProperty<FakeSubBean> getTestListProperty() {
        return testList;
    }

    @XmlElement
    public ObservableList<FakeSubBean> getTestList() {
        return testList.get();
    }

}
