package com.example.shop.common.util;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Properties;

public class DatePrefixedSequenceIdGenerator extends SequenceStyleGenerator {
    public static final String DATE_FORMAT_PARAMETER = "dateFormat";
    public static final String DATE_FORMAT_DEFAULT = "%tY%tm%td";

    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%05d";

    public static final String DATE_NUMBER_SEPARATOR_PARAMETER = "dateNumberSeparator";
    public static final String DATE_NUMBER_SEPARATOR_DEFAULT = "_";

    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";
    public static final String VALUE_PREFIX_DEFAULT = "";

    private String valuePrefix;
    private String format;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return valuePrefix + String.format(format, LocalDate.now(), super.generate(session, object));
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);

        this.valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER, params, VALUE_PREFIX_DEFAULT);

        String dateFormat = ConfigurationHelper.getString(DATE_FORMAT_PARAMETER, params, DATE_FORMAT_DEFAULT).replace("%", "%1$");
        String numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, NUMBER_FORMAT_DEFAULT).replace("%", "%2$");
        String dateNumberSeparator = ConfigurationHelper.getString(DATE_NUMBER_SEPARATOR_PARAMETER, params, DATE_NUMBER_SEPARATOR_DEFAULT);
        this.format = dateFormat+dateNumberSeparator+numberFormat;
    }
}
