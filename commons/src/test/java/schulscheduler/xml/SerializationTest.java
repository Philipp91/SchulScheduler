package schulscheduler.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schulscheduler.model.eingabe.Eingabedaten;
import schulscheduler.model.ergebnis.Ergebnisdaten;
import schulscheduler.testutils.FakeBean;
import schulscheduler.testutils.FakeSubBean;
import schulscheduler.testutils.TestData;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class SerializationTest {

    @Test
    public void testSaveAndLoadEingabedaten(@TempDir Path folder) throws IOException, JAXBException {
        Path file = folder.resolve("temp.xml");
        Eingabedaten original = TestData.readTestdataset();
        Serialization.xmlWriteFile(original, file);
        Eingabedaten deserialized = Serialization.xmlReadFile(file, Eingabedaten.class);
        assertThat(deserialized, equalTo(original));
    }

    @Test
    public void testSaveAndLoadErgebnisdaten(@TempDir Path folder) throws JAXBException {
        try {
            Path file = folder.resolve("temp.xml");
            Ergebnisdaten original = TestData.readTestresults();
            Serialization.xmlWriteFile(original, file);
            Ergebnisdaten deserialized = Serialization.xmlReadFile(file, Ergebnisdaten.class);
            assertThat(deserialized, equalTo(original));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOverwriteEingabedaten(@TempDir Path folder) throws IOException, JAXBException {
        Path file = folder.resolve("temp.xml");
        Eingabedaten dataset = TestData.readTestdataset();
        Eingabedaten dataset_small = TestData.readSmallTestdataset();

        Serialization.xmlWriteFile(dataset, file);
        Serialization.xmlWriteFile(dataset_small, file); // überschreibe
        Eingabedaten deserialized = Serialization.xmlReadFile(file, Eingabedaten.class);

        assertThat(deserialized, equalTo(dataset_small));
    }

    @Test
    public void shouldThrowOnFileNotFound(@TempDir Path folder) {
        Path file = folder.resolve("noFile.xml");
        assertThat(file.toFile().exists(), is(false));
        Assertions.assertThrows(FileNotFoundException.class, () -> Serialization.xmlReadFile(file, Eingabedaten.class));
    }

    @Test
    public void shouldThrowOnReadOnlySave(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("temp.xml");
        assertThat(file.toFile().createNewFile(), is(true));
        assertThat(file.toFile().setWritable(false), is(true));
        Eingabedaten data = TestData.readTestdataset();
        try {
            Assertions.assertThrows(FileNotFoundException.class, () -> Serialization.xmlWriteFile(data, file));
        } finally {
            assertThat(file.toFile().setWritable(true), is(true));
        }
    }

    @Test
    public void shouldThrowOnDefectFileLoad(@TempDir Path folder) throws IOException {
        Path file = folder.resolve("temp.xml");
        FileWriter writer = new FileWriter(file.toFile());
        writer.append("piauhsd0auw92"); // schreibe irgendwas in die datei
        writer.close();

        Assertions.assertThrows(JAXBException.class, () -> Serialization.xmlReadFile(file, Eingabedaten.class));
    }

    @Test
    public void shouldCloneFakeBean() throws IOException {
        FakeBean bean1 = new FakeBean();
        bean1.getTestList().add(new FakeSubBean("Sub1"));
        bean1.setTestString("Test");

        FakeBean clone = Serialization.xmlClone(bean1);
        assertThat(clone.getTestString(), is("Test"));
        assertThat(clone.getTestList(), hasSize(1));

        bean1.setTestString("Other");
        assertThat(bean1.getTestString(), is("Other"));
        assertThat(clone.getTestString(), is("Test"));
    }

}
