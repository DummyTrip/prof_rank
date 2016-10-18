package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.pages.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.jbibtex.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
@InstructorPage
public class CreateReference {
    @Persist
    @Property
    private Long referenceId;

    @Persist
    @Property
    private Long oldReferenceId;

    @Persist
    @Property
    private Reference reference;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Property
    private ReferenceInstance referenceInstance;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @InjectPage
    private com.diplomska.prof_rank.pages.Index index;

    public List<Reference> getReferences() {
        return referenceHibernate.getAll();
    }

    @Property
    private Attribute attribute;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

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
        if (!referenceId.equals(oldReferenceId)) {
            resetPersistedVariables();
            oldReferenceId = referenceId;
        }

        this.reference = referenceHibernate.getById(referenceId);

        if (attributes == null) {
            attributes = referenceHibernate.getAttributeValues(this.reference);
        }

        if (testMap == null) {
            testMap = new HashMap<String, String>();

            for (Attribute attribute : attributes) {
                testMap.put(String.valueOf(attribute.getId()), "");
            }
        }

        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(getNewAttributes(), "name");
        }

        bibtexString = "";

        if (authors == null) {
            authors = new ArrayList<String>();
            String authorName = "author 1";
            authors.add(authorName);
            testMap.put(authorName, "");
        }
    }


    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    Object saveReference() {
        List<String> authorNames = new ArrayList<String>();

        for (String author : authors) {
            authorNames.add(testMap.get(author));
            testMap.remove(author);
        }

        for (String authorName : authorNames) {
            referenceInstance = new ReferenceInstance();
            referenceInstance.setReference(reference);
            referenceInstanceHibernate.store(referenceInstance);

            personHibernate.setReferenceInstance(referenceInstance, authorName, authorNames.indexOf(authorName));

            referenceInstanceHibernate.updateAttributeReferenceInstances(referenceInstance, testMap, attributes);
        }

        resetPersistedVariables();

        return index;
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
    @OnEvent(component = "deleteAttribute", value = "selected")
    public void delete(Long attributeId) {
        for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
            Long id = iterator.next().getId();
            if (id.equals(attributeId)) {
                iterator.remove();
            }
        }

        testMap.remove(String.valueOf(attributeId));
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

    private void readBibtexValues(Key key, Value value) {
        try {
            String attributeValue = value.toUserString().trim();
            String attributeName = key.toString().trim();

            if (attributeName.equals("author")) {
                addBibtexAuthors(attributeName, attributeValue);
            } else {
                Attribute attribute = attributeHibernate.getOrCreateAttribute(attributeName);
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
            String authorKey = "author "+ (i+1);

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
    @OnEvent(component = "deleteAuthor", value = "selected")
    public void deleteAuthor(String author) {
        for (Iterator<String> iterator = authors.iterator(); iterator.hasNext(); ) {
            if (author.equals(iterator.next())) {
                iterator.remove();
            }
        }

        testMap.remove(author);
    }

    @Persist
    @Property
    boolean missingAuthors;

    public boolean isPapersReference() {
        return reference.getName().equals("Papers") ? true : false;
    }

    @OnEvent(component = "addAuthor", value = "selected")
    void addAuthor() {
        String lastAuthor = authors.get(authors.size() - 1);
        Integer newAuthorNumber = Integer.valueOf(lastAuthor.split(" ")[1]) + 1;
        String authorName = "author " + newAuthorNumber;
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
