package schulscheduler.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;

public abstract class Serialization {

    private Serialization() {
    }

    /**
     * Serialisiert einen Objektbaum in einen Stream, mit normaler Java-Serialisierung.
     *
     * @param data Das zu serialisierende Objekt bzw. die Wurzel des Objektbaums.
     * @param stream Der Stream, auf den die Serialisierung geschrieben wird.
     * @param <T> Der Typ des zu serialisierenden Objekts.
     * @throws IOException Wenn ein Fehler beim Serialisieren auftritt.
     */
    public static <T extends Serializable> void javaSerialize(T data, OutputStream stream) throws IOException {
        try (ObjectOutputStream objectStream = new ObjectOutputStream(stream)) {
            objectStream.writeObject(data);
            objectStream.flush();
        }
    }

    /**
     * Deserialisiert einen Objektbaum aus einem Stream, mit normaler Java-Serialisierung.
     *
     * @param stream Der Stream, aus dem die serialisierten Daten gelesen werden.
     * @param <T> Der Typ des zu deserialisierenden Objekts.
     * @return Das deserialisierte Objekt.
     * @throws IOException Wenn ein Fehler beim Deserialisieren auftritt.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T javaDeserialize(InputStream stream) throws IOException {
        try (ObjectInputStream objectStream = new ObjectInputStream(stream)) {
            return (T) objectStream.readObject();
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Klont das gegebene Objekt und den gesamten damit verbundenen Objektbaum
     * durch Serialisieren und anschließendes Deserialisieren mit Java- Object Serialization (JOS).
     *
     * @param data Das zu klonende Objekt.
     * @param <T> Der Typ des zu klonenden Objekts.
     * @return Ein Klon des Objekt.
     * @throws IOException Wenn ein Serialisierungs-Fehler auftritt.
     */
    public static <T extends Serializable> T javaClone(T data) throws IOException {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            javaSerialize(data, outStream);
            try (ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray())) {
                return javaDeserialize(inStream);
            }
        }
    }

    /**
     * Serialisiert einen Objektbaum in einen Stream, mit JAXB XML Serialisierung.
     *
     * @param data Das zu serialisierende Objekt bzw. die Wurzel des Objektbaums.
     * @param stream Der Stream, auf den die Serialisierung geschrieben wird.
     * @param <T> Der Typ des zu serialisierenden Objekts.
     * @throws JAXBException Wenn ein Fehler beim Serialisieren auftritt.
     */
    public static <T> void xmlSerialize(T data, OutputStream stream) throws JAXBException {
        JAXBContext outContext = JAXBContext.newInstance(data.getClass());
        Marshaller mOut = outContext.createMarshaller();
        mOut.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mOut.marshal(data, stream);
    }

    /**
     * Deserialisiert einen Objektbaum aus einem Stream, mit JAXB XML Serialisierung.
     *
     * @param stream Der Stream, aus dem die serialisierten Daten gelesen werden.
     * @param type Der Typ des zu deserialisierenden Objekts.
     * @param <T> Der Typ des zu deserialisierenden Objekts.
     * @return Das deserialisierte Objekt.
     * @throws JAXBException Wenn ein Fehler beim Deserialisieren auftritt.
     */
    public static <T> T xmlDeserialize(InputStream stream, Class<T> type) throws JAXBException {
        JAXBContext inContext = JAXBContext.newInstance(type);
        Unmarshaller mIn = inContext.createUnmarshaller();
        return type.cast(mIn.unmarshal(stream));
    }

    /**
     * Klont das gegebene Objekt und den gesamten damit verbundenen Objektbaum
     * durch Serialisieren und anschließendes Deserialisieren mit JAXB XML Serialisierung.
     *
     * @param data Das zu klonende Objekt.
     * @param <T> Der Typ des zu klonenden Objekts.
     * @return Ein Klon des Objekt.
     */
    public static <T> T xmlClone(T data) {
        if (data == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Class<? extends T> resultType = (Class<? extends T>) data.getClass();

        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            xmlSerialize(data, outStream);
            try (ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray())) {
                return xmlDeserialize(inStream, resultType);
            }
        } catch (JAXBException|IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Konvertiert das gegebene Objekt in den gewünschten Zieltyp durch Serialisierung und
     * Deserialisierung mit JAXB. Die beiden Typen müssen zum selben JAXB-XML-Code serialisiert werden können.
     *
     * @param source Das zu konvertierende Objekt.
     * @param targetType Der Typ des gewünschten Objekts.
     * @param <S> Der Typ des zu konvertierenden (Quell-)Objekts.
     * @param <T> Der Typ des gewünschten (Ziel-)Objekts.
     * @return Das konvertierte Objekt, vom Typ <tt>targetType</tt>.
     * @throws IOException Wenn bei der Konvertierung ein Fehler aufgetreten ist.
     */
    public static <S, T> T convertBySerialization(S source, Class<T> targetType) throws IOException {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            xmlSerialize(source, outStream);
            try (ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray())) {
                return xmlDeserialize(inStream, targetType);
            }
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }


    /**
     * Schreibt ein Objekt mit JAXB XML Serialisierung in eine Datei.
     *
     * @param data Das zu speichernde Objekt.
     * @param file Die Datei, in die das Objekt gespeichert werden soll.
     * @param <T> Der Typ des zu speichernden Objekts.
     * @throws JAXBException Wenn bei der Serialisierung ein Fehler auftritt.
     * @throws IOException Wenn ein Dateisystemfehler auftritt.
     */
    public static <T> void xmlWriteFile(T data, Path file) throws JAXBException, IOException {
        try (FileOutputStream stream = new FileOutputStream(file.toFile())) {
            xmlSerialize(data, stream);
        }
    }

    /**
     * Liest ein mit JAXB serialisiertes Objekt aus einer Datei.
     *
     * @param file Die Datei, aus der das Objekt eingelesen soll.
     * @param type Der Typ des zu lesenden Objekts.
     * @param <T> Der Typ des zu lesenden Objekts.
     * @return Das gelesene und deserialisierte Objekt.
     * @throws JAXBException Wenn bei der Deserialisierung ein Fehler auftritt.
     * @throws IOException Wenn ein Dateisystemfehler auftritt.
     */
    public static <T> T xmlReadFile(Path file, Class<T> type) throws JAXBException, IOException {
        try (FileInputStream stream = new FileInputStream(file.toFile())) {
            return xmlDeserialize(stream, type);
        }
    }

}
