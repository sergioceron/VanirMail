package net.underserver.mail.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: sergio
 * Date: 23/07/12
 * Time: 06:51 PM
 */
public class ObjectFormat {  // TODO: optimizar para no repetir nombres de campos (creo que ya no es necesario)
	
	public static String format(String format, Object object){
		Pattern p = Pattern.compile("\\{[^}]+}");
		Matcher m = p.matcher(format);

		List<String> fields = new ArrayList<String>();
		StringBuilder formatBuilder = new StringBuilder();
		int pos = 0;
		while(m.find()){
			String field = format.substring(m.start() + 1, m.end() - 1);
			formatBuilder.append(format.substring(pos, m.start()));
			pos = m.end();
			fields.add(field);
		}
		formatBuilder.append(format.substring(pos));

		String[] fieldNames = fields.toArray(new String[]{ });

		List<Object> values = new ArrayList<Object>();
        for( String fieldName : fieldNames ) {
			try {
				PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, fieldName);
				Method getMethod = descriptor.getReadMethod();
				Object value = getMethod.invoke(object, (Object[]) null);
				//if( getMethod.isAnnotationPresent( AccessFormat.class ) ){
					values.add(value);
				//} else {
				//	throw new Exception( "Forbidden access to field " + fieldName );
				//}
			} catch (Exception e) {
				e.printStackTrace();
				values.add("");
			}
		}

		Object[] fieldValues = values.toArray();
		String formatted = "";
		try{
			formatted = String.format(formatBuilder.toString(), fieldValues);
		}catch(Exception e){
			e.printStackTrace();
		}
        return formatted;
	}
}
