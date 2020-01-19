package schulscheduler.testutils;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schulscheduler.model.base.BaseElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Dieser Bean ist dazu da, verschiedene Testfälle zu unterstützen.
 */
@XmlRootElement
public class FakeContainerBean extends BaseElement {

    private ListProperty<FakeBean> fakeList = new SimpleListProperty<FakeBean>(this, "fakeList", FXCollections.observableList(new ArrayList<FakeBean>()));

    public ListProperty<FakeBean> getFakeListProperty() {
        return fakeList;
    }

    @XmlElement
    public ObservableList<FakeBean> getFakeList() {
        return fakeList.get();
    }
}
