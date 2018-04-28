package app2;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *	@author ranjan.sarma
 *	@since java 1.1
 *	
 *	(deprecated).
 */

public class CSVBuilder {
	
	private CSVBuilderHelper helper;
	private List list;
	private StringWriter writer;
	
        /**
         *
         */
        public CSVBuilder() {
		this.writer = new StringWriter();
		this.helper = new CSVBuilderHelper(writer);
		this.list = new ArrayList();
	}
	
        /**
         *
         * @param text
         */
        public void append(String text) {
                if(text == null) return;
		if(text.startsWith(""+CSVBuilderHelper.default_quote_character) && text.endsWith(""+CSVBuilderHelper.default_quote_character)) {
			text = text.substring(1, text.length()-1);
		}
		text = text.replace((char)13, (char)0x00);
		try {
			if(text == ""+CSVBuilderHelper.default_line_end) {
				helper.writeNext(getString(list));
				list.clear();
			} else if(text!=""+CSVBuilderHelper.default_separator) {
				list.add((String)text);
			}
		} catch(IOException exception) {
			System.err.println("CSVBuilder.append(String) Error: \n" + exception.getMessage());
		}
	}
	
	private String[] getString(List list) {
		String[] retval = new String[list.size()];
		Object[] object = list.toArray();
		for(int i = 0; i<object.length; i++) {
			retval[i] = object[i].toString();
		}
		return retval;
	}
	
	//override delegate method toString().
	public String toString() {
		return (this.writer).toString();
	}
	
	//override delegate method finalize().
	public void finalize() {
		try {
			this.helper.close();
			this.writer.close();
		} catch(IOException exception) {
			System.err.println("CSVBuilder.append(String) Error: \n" + exception.getMessage());
		}
	}
	
	class CSVBuilderHelper {
		public static final char default_separator = ',';
		public static final char default_escape_character = '"';
		public static final char default_quote_character = '"';
		public static final char default_no_quote_character = '\u0000';
		public static final char default_no_escape_character = '\u0000';
		public static final char default_line_end = '\n';
		public static final int initial_string_size = 128;
		private Writer writer;
		
		public CSVBuilderHelper(Writer writer) {
			this.writer = writer;
		}

		
		public void writeNext(String[] nextline) throws IOException {
			if(nextline == null) return;
			StringBuffer buffer = new StringBuffer(initial_string_size);
			for(int i = 0; i<nextline.length; i++) {
				if(i!=0) {
					buffer.append(default_separator);
				}
				String nextelement = nextline[i];
				if(nextelement == null) continue;
				if(default_quote_character != default_no_quote_character) buffer.append(default_quote_character);
				String rowdata = containsSpecialCharacters(nextelement)?processline(nextelement).toString():nextelement;
				buffer.append(rowdata);
				if(default_quote_character != default_no_quote_character) buffer.append(default_quote_character);
			}
			buffer.append(default_line_end);
			writer.write(buffer.toString());
		}

		private boolean containsSpecialCharacters(String text) {
			return ((text.indexOf(default_quote_character)!= -1) || (text.indexOf(default_escape_character) != -1));
		}

		private StringBuffer processline(String text) {
			StringBuffer buffer = new StringBuffer(initial_string_size);
			for(int i = 0; i<text.length(); i++) {
				char nextchar = text.charAt(i);
				if(default_escape_character!=default_no_escape_character && nextchar==default_quote_character) {
					buffer.append(default_escape_character).append(nextchar);
				} else if(default_escape_character!=default_no_escape_character && nextchar==default_escape_character) {
					buffer.append(default_escape_character).append(nextchar);
				} else {
					buffer.append(nextchar);
				}
			}
			return buffer;
		}

		public void close() {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
