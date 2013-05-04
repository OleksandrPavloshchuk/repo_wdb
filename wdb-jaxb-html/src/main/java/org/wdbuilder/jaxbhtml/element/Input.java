package org.wdbuilder.jaxbhtml.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.wdbuilder.jaxbhtml.HtmlElement;


@XmlRootElement(name="input")
public class Input extends HtmlElement {
    @XmlAttribute
	private String value;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String type;
	
	private Input() {
		super();
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public void setValue( String value ) {
		this.value = value;
	}
	
	protected void setType( String type ) {
		this.type = type;
	}
	
	public static class Text extends Input {
		public Text() {
			super();
			setType("text");
		}
	}
	
	public static class Hidden extends Input {
		public Hidden() {
			super();
			setType("hidden");
		}
	}
}
