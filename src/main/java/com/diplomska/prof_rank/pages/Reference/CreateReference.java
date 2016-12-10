package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.jbibtex.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private Long referenceTypeId;

    @Persist
    @Property
    private Long oldreferenceTypeId;

    @Persist
    @Property
    private ReferenceType referenceType;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Property
    private Reference reference;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @InjectPage
    private com.diplomska.prof_rank.pages.Index index;

    public List<ReferenceType> getReferences() {
        return referenceTypeHibernate.getAll();
    }

    @Property
    private Attribute attribute;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

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

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void setupRender() throws Exception {
        if (!referenceTypeId.equals(oldreferenceTypeId)) {
            resetPersistedVariables();
            oldreferenceTypeId = referenceTypeId;
        }

        this.referenceType = referenceTypeHibernate.getById(referenceTypeId);

        if (attributes == null) {
            attributes = referenceTypeHibernate.getAttributeValues(this.referenceType);
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
            String authorName = "Автор 1";
            authors.add(authorName);
            testMap.put(authorName, "");
        }

        if (phrase != null) {
            readScholarBibtex();
            onActionFromParseBibtex();
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

        reference = new Reference();
        reference.setReferenceType(referenceType);
        referenceHibernate.store(reference);
        referenceHibernate.updateAttributeReferences(reference, testMap, attributes);

        for (String authorName : authorNames) {
            personHibernate.setReference(reference, authorName, authorNames.indexOf(authorName));
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
    public void onDeleteAttribute(Long attributeId) {
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
            phrase = null;
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

    public void onDeleteAuthor(String author) {
        for (Iterator<String> iterator = authors.iterator(); iterator.hasNext(); ) {
            if (author.equals(iterator.next())) {
                iterator.remove();
            }
        }

        authors.remove(author);
        testMap.remove(author);
    }

    @Persist
    @Property
    boolean missingAuthors;

    public boolean isPapersReferenceType() {
        return referenceType.getName().equals("Papers") ? true : false;
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
        phrase = null;
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

    @ActivationRequestParameter(value = "bibtex")
    private String phrase;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    public Link setPhrase(Long referenceTypeId, String paperTitle) {
        resetPersistedVariables();
        phrase = paperTitle;
        this.referenceTypeId = referenceTypeId;

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    void readScholarBibtex() throws Exception{
        String command = "py scholar.py -c 1 --author \"vangel ajanovski\" --citation bt --phrase " + phrase;

        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;

        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            if (line.trim().equals("")) {
                continue;
            }
//            line = line.trim();

            bibtexString += line + "\n";
        }

//        showBibtexImport = true;
    }

}
