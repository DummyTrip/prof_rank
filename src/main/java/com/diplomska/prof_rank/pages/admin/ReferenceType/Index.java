package com.diplomska.prof_rank.pages.admin.ReferenceType;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.services.ExcelWorkbook;
import com.diplomska.prof_rank.services.ReferenceHibernate;
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
    private ReferenceHibernate referenceHibernate;

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
        return referenceHibernate.getAll();
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
        referenceType = referenceHibernate.getById(referenceTypeId);
        referenceHibernate.delete(referenceType);
    }

    @CommitAfter
    void onActionFromReadExcel() throws Exception {
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 1);
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 2);
//        excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 3);
    }
}
