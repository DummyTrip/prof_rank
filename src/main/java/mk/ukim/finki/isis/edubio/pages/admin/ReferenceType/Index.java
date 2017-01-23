package mk.ukim.finki.isis.edubio.pages.admin.ReferenceType;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.ReferenceType;
import mk.ukim.finki.isis.edubio.services.ExcelWorkbook;
import mk.ukim.finki.isis.edubio.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class Index {
    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private ExcelWorkbook excelWorkbook;

    @Property
    private ReferenceType referenceType;

    @Property
    private BeanModel<ReferenceType> referenceTypeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<ReferenceType> getReferenceTypes() {
        return referenceTypeHibernate.getAll();
    }

    void setupRender() {
        referenceTypeBeanModel = beanModelSource.createDisplayModel(ReferenceType.class, messages);
        referenceTypeBeanModel.include("name", "points");
        referenceTypeBeanModel.add("referenceInputTemplate", pcs.create(ReferenceType.class, "referenceInputTemplate"));
        referenceTypeBeanModel.add("show", null);
        referenceTypeBeanModel.add("edit", null);
        referenceTypeBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long referenceTypeId) {
        referenceType = referenceTypeHibernate.getById(referenceTypeId);
        referenceTypeHibernate.delete(referenceType);
    }

    @CommitAfter
    void onActionFromReadExcel() throws Exception {
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 1);
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 2);
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 3);
    }
}
