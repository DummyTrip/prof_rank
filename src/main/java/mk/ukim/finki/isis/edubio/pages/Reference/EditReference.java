package mk.ukim.finki.isis.edubio.pages.Reference;

import mk.ukim.finki.isis.edubio.annotations.InstructorPage;
import mk.ukim.finki.isis.edubio.entities.Attribute;
import mk.ukim.finki.isis.edubio.entities.AttributeReference;
import mk.ukim.finki.isis.edubio.entities.Reference;
import mk.ukim.finki.isis.edubio.services.AttributeHibernate;
import mk.ukim.finki.isis.edubio.services.PersonHibernate;
import mk.ukim.finki.isis.edubio.services.ReferenceHibernate;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.jbibtex.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Created by Aleksandar on 09-Oct-16.
 */
@InstructorPage
public class EditReference {
    @Persist
    @Property
    private Long referenceId;

    @Persist
    @Property
    private Long oldreferenceId;

    @Persist
    @Property
    private Reference reference;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @InjectPage
    private mk.ukim.finki.isis.edubio.pages.Index index;

    @InjectPage
    private ShowReference showReference;

    @Property
    private Attribute attribute;

    @Persist
    @Property
    List<Attribute> attributes;

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    public boolean isNumAttributes() {
        return attributes.size() > 0 ? true :false;
    }

    @Property
    private String testVal;

    @Property
    private Integer loopIndex;

    @Persist
    @Property
    private Map<String, String> testMap;

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        if (!referenceId.equals(oldreferenceId)) {
            resetPersistedVariables();
            oldreferenceId = referenceId;
        }

        this.reference = referenceHibernate.getById(referenceId);

        if (testMap == null) {
            testMap = new HashMap<String, String>();
            attributes = new ArrayList<Attribute>();

            for (AttributeReference ari: referenceHibernate.getSortedAttributeReferences(reference)) {
                attributes.add(ari.getAttribute());
                testMap.put(String.valueOf(ari.getAttribute().getId()), ari.getValue());
            }
        }

        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(getNewAttributes(), "name");
        }

        bibtexString = "";

        if (authors == null) {
            List<String> personIdentifiers = personHibernate.getReferenceAuthors(reference);
            authors = new ArrayList<String>();
            int i = 0;
            String authorName;

            for (String identifier : personIdentifiers) {
                authorName = "Автор " + (++i);
                authors.add(authorName);
                testMap.put(authorName, identifier);
            }
        }
    }

    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    Object saveReference() {
        referenceHibernate.deleteAuthors(reference);

        // add authors
        for (String author : authors) {
            Reference reference = referenceHibernate.getById(referenceId);
            personHibernate.setReference(reference, testMap.get(author), authors.indexOf(author));

            testMap.remove(author);
        }

        reference = referenceHibernate.getById(referenceId);

        referenceHibernate.updateAttributeReferences(reference, testMap, attributes);

        resetPersistedVariables();

        return showReference;
    }





    @Property
    Attribute newAttribute;

    public List<Attribute> getNewAttributes() {
        return attributeHibernate.getAll();
    }

    public boolean isTestMapPopulated() {
        return getSize() > 0 ? true : false;
    }

    public Integer getSize() {
        if (testMap != null) {
            return testMap.keySet().size();
        }
        return 0;
    }

    @Inject
    private Request request;

    @InjectComponent
    private Zone newAttributesZone;

    @Property
    private SelectModel attributeSelectModel;

    @Inject
    SelectModelFactory selectModelFactory;

    @Inject
    AjaxResponseRenderer ajaxResponseRenderer;

    @OnEvent(component = "addAttribute", value = "selected")
    void addAttribute() {
        if (newAttribute != null) {
            addAttributeToForm(newAttribute, "");
        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(newAttributesZone);
        }
    }

    Object onActionFromCancel() {
        resetPersistedVariables();

        return index;
    }

    @CommitAfter
    public void onDelete(Long attributeId) {
        testMap.remove(String.valueOf(attributeId));
        for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
            Long id = iterator.next().getId();
            if (id.equals(attributeId)) {
                iterator.remove();
            }
        }
    }










    @Persist
    @Property
    String bibtexString;

    @Inject
    PersonHibernate personHibernate;

    @Persist
    @Property
    List<String> authors;

    @Property
    String author;

    @CommitAfter
    @OnEvent(component = "parseBibtex", value = "selected")
    void onActionFromParseBibtex() throws Exception {
        if (bibtexString != null) {
            Collection<BibTeXEntry> entries = listBibtexEntriesFromString(bibtexString);

            parseBibtexEntries(entries);
            showBibtexImport = false;
        }
    }

    private Collection<BibTeXEntry> listBibtexEntriesFromString(String bibtexString) throws Exception{
        Reader reader = new StringReader(bibtexString);

        BibTeXParser bibTeXParser = new BibTeXParser();
        BibTeXDatabase bibTeXDatabase = bibTeXParser.parse(reader);

        Collection<BibTeXEntry> entries = (bibTeXDatabase.getEntries()).values();

        return entries;
    }

    private void parseBibtexEntries(Collection<BibTeXEntry> entries) {
        for (BibTeXEntry entry : entries) {
            Map<Key, Value> bibtexEntryMap = entry.getFields();

            for (Key key : bibtexEntryMap.keySet()) {
                Value value = bibtexEntryMap.get(key);

                // The field is not defined
                if (value == null) {
                    continue;
                }

                readBibtexValues(key, value);
            }
        }
    }

    @Inject
    private Messages messages;

    private void readBibtexValues(Key key, Value value) {
        try {
            String attributeValue = value.toUserString().trim();
            String attributeName = key.toString().trim();

            if (attributeName.equals("author")) {
                addBibtexAuthors(messages.get(attributeName), attributeValue);
            } else if (messages.get(attributeName) != null){
                Attribute attribute = attributeHibernate.getOrCreateAttribute(messages.get(attributeName));
                addAttributeToForm(attribute, attributeValue);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private void addBibtexAuthors(String attributeName, String attributeValue) {
        String[] bibtexAuthors = attributeValue.split(" and ");
        for (int i = 0; i < bibtexAuthors.length ; i++) {
            String fullName = bibtexAuthors[i];

            List<Person> persons = personHibernate.getByBibtexAuthorName(fullName);
            String authorKey = "Автор "+ (i+1);

            if (persons.size() > 0) {
                addBibtexPersonToForm(persons, fullName, authorKey);
            } else {
                addBibtexMissingPersonToForm(fullName, authorKey);
            }
        }
    }

    private void addBibtexPersonToForm(List<Person> persons, String fullName, String authorKey) {
        Person person = persons.get(0);
        String email = " (" + person.getEmail() + ")";
        fullName = person.getFirstName() + " " + person.getLastName();

        addAuthorToForm(authorKey, fullName + email);
    }

    private void addBibtexMissingPersonToForm(String fullName, String authorKey) {
        addAuthorToForm(authorKey, fullName);
        missingAuthors = true;
    }

    private void addAuthorToForm(String key, String value) {
        if (!testMap.containsKey(key)) {
            testMap.put(key, value);
            authors.add(key);
        } else {
            testMap.put(key, value);
        }
    }

    private void addAttributeToForm(Attribute attribute, String value) {
        String attributeId = String.valueOf(attribute.getId());
        if (!testMap.containsKey(attributeId)) {
            testMap.put(attributeId, value);
            attributes.add(attribute);
        } else {
            testMap.put(attributeId, value);
        }
    }

    @CommitAfter
    public void onDeleteAuthor(String author) {
        for (Iterator<String> iterator = authors.iterator(); iterator.hasNext(); ) {
            if (author.equals(iterator.next())) {
                iterator.remove();
                authors.remove(author);
            }
        }

        testMap.remove(author);
    }

    @Persist
    @Property
    boolean missingAuthors;

    public boolean isPapersReferenceType() {
        return reference.getReferenceType().getName().equals("Papers") ? true : false;
    }

    @OnEvent(component = "addAuthor", value = "selected")
    void addAuthor() {
        String lastAuthor;
        if (authors.size() > 0){
            lastAuthor = authors.get(authors.size() - 1);
        } else{
            lastAuthor = "Автор 0";
        }
        Integer newAuthorNumber = Integer.valueOf(lastAuthor.split(" ")[1]) + 1;
        String authorName = "Автор " + newAuthorNumber;
        addAuthorToForm(authorName, "");

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(newAttributesZone);
        }
    }

    private void resetPersistedVariables() {
        testMap = null;
        attributes = null;
        authors = null;
        missingAuthors = false;
        showBibtexImport = false;
    }

    @Property
    @Persist
    boolean showBibtexImport;

    @InjectComponent
    Zone bibtexZone;

    public void onActionFromToggleBibtexImport() {
        showBibtexImport = !showBibtexImport;

        ajaxResponseRenderer.addRender(bibtexZone);
    }
}
