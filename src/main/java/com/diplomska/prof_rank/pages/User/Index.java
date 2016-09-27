package com.diplomska.prof_rank.pages.User;

import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.ExcelWorkbook;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class Index {
    @Inject
    private UserHibernate userHibernate;

    @Inject
    ExcelWorkbook excelWorkbook;

    @Property
    private User user;

    @Property
    private BeanModel<User> userBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<User> getUsers() {
        return userHibernate.getAll();
    }

    @Property
    private List<String> po;

    public String getP() {
        String tmp = "";
        for (String pop : po) {
            tmp += " " + pop;
        }

        return tmp;
    }

    public List<List<String>> getPoi() throws  Exception{
        return excelWorkbook.readCategorySpreadsheet("poi_test.xlsx", 4);
    }

    void setupRender() {
        userBeanModel = beanModelSource.createDisplayModel(User.class, messages);
        userBeanModel.include("firstName", "fatherName", "lastName", "email");
        userBeanModel.add("role", pcs.create(User.class, "role"));
        userBeanModel.add("show", null);
        userBeanModel.add("edit", null);
        userBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long userId) throws Exception{
        user = userHibernate.getById(userId);
        userHibernate.delete(user);
    }


}
