package schulscheduler.testutils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import schulscheduler.model.base.BaseElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Dieser Bean ist dazu da, verschiedene Testfälle zu unterstützen.
 */
@XmlRootElement
public class FakeSubBean extends BaseElement {

    public FakeSubBean() {
    }

    public FakeSubBean(String testString) {
        setTestString(testString);
    }

    private SimpleStringProperty testString = new SimpleStringProperty(this, "testString");

    public StringProperty getTestStringProperty() {
        return testString;
    }

    @XmlElement
    public String getTestString() {
        return testString.get();
    }

    public void setTestString(String testString) {
        this.testString.set(testString);
    }

}
