package schulscheduler.testutils;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.NoUndoTracking;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Dieser Bean ist dazu da, verschiedene Testfälle zu unterstützen.
 */
@XmlRootElement
public class FakeBean extends FakeSuperBean {

    private SimpleIntegerProperty testInteger = new SimpleIntegerProperty(this, "testInteger");

    public SimpleIntegerProperty getTestIntegerProperty() {
        return testInteger;
    }

    @XmlElement
    public int getTestInteger() {
        return testInteger.get();
    }

    public void setTestInteger(int testInteger) {
        this.testInteger.set(testInteger);
    }

    private SimpleObjectProperty<EnumFake> testEnum = new SimpleObjectProperty<>(this, "testEnum");

    public ObjectProperty<EnumFake> getTestEnumProperty() {
        return testEnum;
    }

    @XmlElement
    public EnumFake getTestEnum() {
        return testEnum.get();
    }

    public void setTestEnum(EnumFake testEnum) {
        this.testEnum.set(testEnum);
    }

    private SimpleObjectProperty<FakeSubBean> testObject = new SimpleObjectProperty<>(this, "testObject");

    public ObjectProperty<FakeSubBean> getTestObjectProperty() {
        return testObject;
    }

    @XmlElement
    public FakeSubBean getTestObject() {
        return testObject.get();
    }

    public void setTestObject(FakeSubBean testObject) {
        this.testObject.set(testObject);
    }


    private ListProperty<FakeSubBean> trackingDisabledList = new SimpleListProperty<FakeSubBean>(this, "trackingDisabledList", FXCollections.observableList(new ArrayList<FakeSubBean>()));

    @NoUndoTracking
    public ListProperty<FakeSubBean> getTrackingDisabledListProperty() {
        return trackingDisabledList;
    }

    @XmlElement
    public ObservableList<FakeSubBean> getTrackingDisabledList() {
        return trackingDisabledList.get();
    }

}
